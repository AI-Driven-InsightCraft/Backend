package com.ka.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ka.springbootinit.common.ErrorCode;
import com.ka.springbootinit.exception.ThrowUtils;
import com.ka.springbootinit.manager.GPT3Manager;
import com.ka.springbootinit.model.entity.Chart;
import com.ka.springbootinit.service.ChartService;
import com.ka.springbootinit.mapper.ChartMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
    implements ChartService{

    @Resource
    private GPT3Manager gpt3Manager;

    public void handleChartUpdateError(long chartId, String execMessage){
        Chart updateChartResult = new Chart();
        updateChartResult.setId(chartId);
        updateChartResult.setStatus("failed");
        updateChartResult.setExecMessage(execMessage);
        boolean updateResult = updateById(updateChartResult);
        ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR, "Fail to update chart status again");
    }

    public String smartChat(String input) {
        String prompt = "You are a data analyst and a front-end development expert. Moving forward, I will provide you with content following the following format:\n" +
                "goal:\n" +
                "{The requirement or objective of data analysis}\n" +
                "data:\n" +
                "{Raw data in CSV format, using \",\" as the delimiter}\n" +
                "Please generate content based on these two sections in the specified format below (do not include any extra headers, endings, or comments):\n" +
                "【【【【【\n" +
                "{Front-end Echarts v5 option configuration JavaScript code to visualize the data effectively,without generating any additional content such as comments}\n" +
                "【【【【【\n" +
                "{Clear and detailed data analysis conclusions, without comments}\n" +
                "Please note that your output should only contain 【【【【【 and \"{}\" (must include \"{\" and \"}\") along with the content inside {} and be sure to have Quotation for key and value. I first give you an response" +
                "format example for Default Chart Type \"line\" (other Chart Types are similar): {\n" +
                "  \"title\": {\n" +
                "    \"text\": \"User tre d\",\n" +
                "    \"subtext\": \"\"\n" +
                "  },\n" +
                "  \"xAxis\": {\n" +
                "    \"type\": \"category\",\n" +
                "    \"data\": [\"1\", \"2\", \"3\"]\n" +
                "  },\n" +
                "  \"yAxis\": {\n" +
                "    \"type\": \"value\"\n" +
                "  },\n" +
                "  \"series\": [{\n" +
                "    \"data\": [10, 20, 30],\n" +
                "    \"type\": \"line\"\n" +
                "  }]\n" +
                "}\n\n" +
                "\n";
        return gpt3Manager.doChat(prompt + input);
    }
}




