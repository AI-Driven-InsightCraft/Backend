package com.ka.springbootinit.bizmq;

import com.ka.springbootinit.common.ErrorCode;
import com.ka.springbootinit.exception.BusinessException;
import com.ka.springbootinit.model.entity.Chart;
import com.ka.springbootinit.service.ChartService;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
@Slf4j
public class BIMessageConsumer {

    @Resource
    private ChartService chartService;

    @SneakyThrows
    @RabbitListener(queues = {BiMqConstant.BI_QUEUE_NAME}, ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliverTag){
        // Mark the working chart
        if (StringUtils.isBlank(message)) {
            channel.basicNack(deliverTag,false,false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "message empty");
        }
        long chartId = Long.parseLong(message);
        Chart chart = chartService.getById(chartId);
        if (chart == null) {
            channel.basicNack(deliverTag,false,false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "chart empty");
        }
        Chart updateChart = new Chart();
        updateChart.setId(chart.getId());
        updateChart.setStatus("running");
        boolean b = chartService.updateById(updateChart);
        if (!b) {
            channel.basicNack(deliverTag,false,false);
            chartService.handleChartUpdateError(chart.getId(), "Fail to update chart status");
            return;
        }
        // Invoke AI API
        //String result = aiManager.doChat(biModelId,userInput.toString());
        String result = chartService.smartChat(buildUserInput(chart));
        String[] splits = result.split("【【【【【");
        if (splits.length < 3) {
            channel.basicNack(deliverTag,false,false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI generate error");
        }
        String genChart = splits[1].trim();
        String genResult = splits[2].trim();
        Chart updateChartResult = new Chart();
        updateChartResult.setId(chart.getId());
        updateChartResult.setGenResult(genResult);
        updateChartResult.setGenChart(genChart);
        updateChartResult.setStatus("succeed");

        boolean r= chartService.updateById(updateChartResult);
        if (!r) {
            channel.basicNack(deliverTag,false,false);
            chartService.handleChartUpdateError(chart.getId(), "Fail to update chart status when finished");
            return;
        }

        // message ack
        channel.basicAck(deliverTag, false);
    }

    private String buildUserInput(Chart chart){
        StringBuilder userInput = new StringBuilder();
        String goal = chart.getGoal();
        String chartType = chart.getChartType();
        String csvData = chart.getChartData();
        userInput.append("Goal: ");

        String userGoal = goal;
        if (StringUtils.isNotBlank(chartType)){
            userGoal += ", Please use Chart Type: " + chartType;
        }
        userInput.append(userGoal);

        userInput.append(", Data: ").append(csvData).append("\n");
        return userInput.toString();
    }
}
