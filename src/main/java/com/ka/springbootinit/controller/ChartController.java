package com.ka.springbootinit.controller;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.ka.springbootinit.annotation.AuthCheck;
import com.ka.springbootinit.bizmq.BIMessageProducer;
import com.ka.springbootinit.common.BaseResponse;
import com.ka.springbootinit.common.DeleteRequest;
import com.ka.springbootinit.common.ErrorCode;
import com.ka.springbootinit.common.ResultUtils;
import com.ka.springbootinit.constant.CommonConstant;
import com.ka.springbootinit.constant.UserConstant;
import com.ka.springbootinit.exception.BusinessException;
import com.ka.springbootinit.exception.ThrowUtils;
import com.ka.springbootinit.manager.AIManager;
import com.ka.springbootinit.manager.GPT3Manager;
import com.ka.springbootinit.manager.RedisLimiterManager;
import com.ka.springbootinit.model.dto.chart.*;
import com.ka.springbootinit.model.entity.Chart;
import com.ka.springbootinit.model.entity.User;
import com.ka.springbootinit.model.vo.BiRensponse;
import com.ka.springbootinit.service.ChartService;
import com.ka.springbootinit.service.UserService;
import com.ka.springbootinit.utils.ExcelUtils;
import com.ka.springbootinit.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;


@RestController
@RequestMapping("/chart")
@Slf4j
public class ChartController {

    @Resource
    private ChartService chartService;

    @Resource
    private UserService userService;

    @Resource
    private AIManager aiManager;

    @Resource
    private GPT3Manager gpt3Manager;

    @Resource
    private RedisLimiterManager redisLimiterManager;

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @Resource
    private BIMessageProducer biMessageProducer;


    // region 增删改查

    /**
     * 创建
     *
     * @param chartAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addChart(@RequestBody ChartAddRequest chartAddRequest, HttpServletRequest request) {
        if (chartAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartAddRequest, chart);
        User loginUser = userService.getLoginUser(request);
        chart.setUserId(loginUser.getId());
        boolean result = chartService.save(chart);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newChartId = chart.getId();
        return ResultUtils.success(newChartId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteChart(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldChart.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = chartService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param chartUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateChart(@RequestBody ChartUpdateRequest chartUpdateRequest) {
        if (chartUpdateRequest == null || chartUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartUpdateRequest, chart);
        long id = chartUpdateRequest.getId();
        // 判断是否存在
        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = chartService.updateById(chart);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/")
    public BaseResponse<Chart> getChartVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = chartService.getById(id);
        if (chart == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(chart);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param chartQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<Chart>> listChartByPage(@RequestBody ChartQueryRequest chartQueryRequest,
            HttpServletRequest request) {
        long current = chartQueryRequest.getCurrent();
        long size = chartQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Chart> chartPage = chartService.page(new Page<>(current, size),
                getQueryWrapper(chartQueryRequest));
        return ResultUtils.success(chartPage);
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param chartQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<Chart>> listMyChartVOByPage(@RequestBody ChartQueryRequest chartQueryRequest,
            HttpServletRequest request) {
        if (chartQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        chartQueryRequest.setUserId(loginUser.getId());
        long current = chartQueryRequest.getCurrent();
        long size = chartQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Chart> chartPage = chartService.page(new Page<>(current, size),
                getQueryWrapper(chartQueryRequest));
        return ResultUtils.success(chartPage);
    }

    // endregion


    /**
     * 编辑（用户）
     *
     * @param chartEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editChart(@RequestBody ChartEditRequest chartEditRequest, HttpServletRequest request) {
        if (chartEditRequest == null || chartEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartEditRequest, chart);
        User loginUser = userService.getLoginUser(request);
        long id = chartEditRequest.getId();
        // 判断是否存在
        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldChart.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = chartService.updateById(chart);
        return ResultUtils.success(result);
    }

    private QueryWrapper<Chart> getQueryWrapper(ChartQueryRequest chartQueryRequest) {
        QueryWrapper<Chart> queryWrapper = new QueryWrapper<>();
        if (chartQueryRequest == null) {
            return queryWrapper;
        }

        Long id = chartQueryRequest.getId();
        String name = chartQueryRequest.getName();
        String goal = chartQueryRequest.getGoal();
        String chartType = chartQueryRequest.getChartType();
        Long userId = chartQueryRequest.getUserId();
        String sortField = chartQueryRequest.getSortField();
        String sortOrder = chartQueryRequest.getSortOrder();

        queryWrapper.eq(id != null && id > 0, "id",id);
        queryWrapper.like(StringUtils.isNotBlank(name),"name",name);
        queryWrapper.eq(StringUtils.isNotBlank(goal),"goal",goal);
        queryWrapper.eq(StringUtils.isNotBlank(chartType),"chartType",chartType);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);


        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }


    /**
     * @param multipartFile
     * @param getChartByAiRequest
     * @param request
     * @return
     */
    @PostMapping("/gen")
    public BaseResponse<BiRensponse> genChartByAi(@RequestPart("file") MultipartFile multipartFile,
                                                  GenChartByAiRequest getChartByAiRequest, HttpServletRequest request) {
        String name = getChartByAiRequest.getName();
        String goal = getChartByAiRequest.getGoal();
        String chartType = getChartByAiRequest.getChartType();

        // Input Check
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "goal is empty");
        ThrowUtils.throwIf(StringUtils.isBlank(name), ErrorCode.PARAMS_ERROR, "name is empty");
        // Size Check, File Size should < 1MB
        long size = multipartFile.getSize();
        String originalFilename = multipartFile.getOriginalFilename();
        final long ONE_MB = 1024 * 1024L;
        ThrowUtils.throwIf(ONE_MB < size, ErrorCode.PARAMS_ERROR, "file size exceed upperbound");

        // File Suffix Check
        String suffix = FileUtil.getSuffix(originalFilename);
        final List<String> validFileSuffixList = Arrays.asList("xlsx");
        ThrowUtils.throwIf(!validFileSuffixList.contains(suffix), ErrorCode.PARAMS_ERROR, "invalid file suffix");


        User loginUser = userService.getLoginUser(request);


        // Limiter Test
        redisLimiterManager.doRateLimit("genChartByAi_v" + String.valueOf(loginUser.getId()));

//        StringBuilder userInput = new StringBuilder();
//        userInput.append("You are a data analyst. I will give you my analysis goals and data. Please help me analyze the data and inform me of the conclusions.\n");
//        userInput.append("goals: ").append(goal).append("\n");
//        String res = ExcelUtils.excelToCsv(multipartFile);
//        userInput.append("data: ").append(res).append("\n");

        long biModelId = 1659171950288818178L;
        StringBuilder userInput = new StringBuilder();
        userInput.append("Goal: ");

        String userGoal = goal;
        if (StringUtils.isNotBlank(chartType)){
            userGoal += ", Please use chart type: " + chartType;
        }
        userInput.append(userGoal);
        String res = ExcelUtils.excelToCsv(multipartFile);
        userInput.append(", Data: ").append(res).append("\n");
        // Invoke AI API
        // Choices:
        //      Yucongming API uses Chinese LLM AI ERNIE Bot. Free & VPN required & Prompt Not required
        //      OpenAI API. Not Free & VPN not Required
        // Todo: Adding an interface for AIManager
        //String result = aiManager.doChat(biModelId,userInput.toString());
        String result = chartService.smartChat(userInput.toString());
        String[] splits = result.split("【【【【【");
        if (splits.length < 3) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI generate error");
        }
        String genChart = splits[1].trim();
        String genResult = splits[2].trim();
        // insert to DB
        Chart chart = new Chart();
        chart.setName(name);
        chart.setGoal(goal);
        chart.setChartData(res);
        chart.setChartType(chartType);

        chart.setGenChart(genChart);
        chart.setGenResult(genResult);
        chart.setUserId(loginUser.getId());
        chart.setStatus("succeed");
        boolean saveResult = chartService.save(chart);
        ThrowUtils.throwIf(!saveResult,ErrorCode.SYSTEM_ERROR,"chart save error");


        BiRensponse biRensponse = new BiRensponse();
        biRensponse.setChartId(chart.getId());
        biRensponse.setGenChart(genChart);
        biRensponse.setGenResult(genResult);
        return ResultUtils.success(biRensponse);
    }

    /**
     * @param multipartFile
     * @param getChartByAiRequest
     * @param request
     * @return
     */
    @PostMapping("/gen/async")
    public BaseResponse<BiRensponse> genChartByAiAsync(@RequestPart("file") MultipartFile multipartFile,
                                                  GenChartByAiRequest getChartByAiRequest, HttpServletRequest request) {
        String name = getChartByAiRequest.getName();
        String goal = getChartByAiRequest.getGoal();
        String chartType = getChartByAiRequest.getChartType();

        // Input Check
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "goal is empty");
        ThrowUtils.throwIf(StringUtils.isBlank(name), ErrorCode.PARAMS_ERROR, "name is empty");
        // Size Check, File Size should < 1MB
        long size = multipartFile.getSize();
        String originalFilename = multipartFile.getOriginalFilename();
        final long ONE_MB = 1024 * 1024L;
        ThrowUtils.throwIf(ONE_MB < size, ErrorCode.PARAMS_ERROR, "file size exceed upperbound");

        // File Suffix Check
        String suffix = FileUtil.getSuffix(originalFilename);
        final List<String> validFileSuffixList = Arrays.asList("xlsx");
        ThrowUtils.throwIf(!validFileSuffixList.contains(suffix), ErrorCode.PARAMS_ERROR, "invalid file suffix");


        User loginUser = userService.getLoginUser(request);


        // Limiter Test
        redisLimiterManager.doRateLimit("genChartByAi_v" + String.valueOf(loginUser.getId()));

//        StringBuilder userInput = new StringBuilder();
//        userInput.append("You are a data analyst. I will give you my analysis goals and data. Please help me analyze the data and inform me of the conclusions.\n");
//        userInput.append("goals: ").append(goal).append("\n");
//        String res = ExcelUtils.excelToCsv(multipartFile);
//        userInput.append("data: ").append(res).append("\n");

        long biModelId = 1659171950288818178L;
        StringBuilder userInput = new StringBuilder();
        userInput.append("Goal: ");

        String userGoal = goal;
        if (StringUtils.isNotBlank(chartType)){
            userGoal += ", Please use Chart Type: " + chartType;
        }
        userInput.append(userGoal);
        String res = ExcelUtils.excelToCsv(multipartFile);
        userInput.append(", Data: ").append(res).append("\n");

        // insert to DB
        Chart chart = new Chart();
        chart.setName(name);
        chart.setGoal(goal);
        chart.setChartData(res);
        chart.setChartType(chartType);
        chart.setStatus("wait");

        chart.setUserId(loginUser.getId());
        boolean saveResult = chartService.save(chart);
        ThrowUtils.throwIf(!saveResult,ErrorCode.SYSTEM_ERROR,"chart save error");

        // Todo: Try catch
        CompletableFuture.runAsync(()->{
            // Mark the working chart
            Chart updateChart = new Chart();
            updateChart.setId(chart.getId());
            updateChart.setStatus("running");
            boolean b = chartService.updateById(updateChart);
            if (!b) {
                chartService.handleChartUpdateError(chart.getId(), "Fail to update chart status");
                return;
            }
            // Invoke AI API
            //String result = aiManager.doChat(biModelId,userInput.toString());
            String result = chartService.smartChat(userInput.toString());
            String[] splits = result.split("【【【【【");
            if (splits.length < 3) {
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
                chartService.handleChartUpdateError(chart.getId(), "Fail to update chart status when finished");
                return;
            }
        }, threadPoolExecutor);

        BiRensponse biRensponse = new BiRensponse();
        biRensponse.setChartId(chart.getId());
        return ResultUtils.success(biRensponse);
    }

    @PostMapping("/gen/async/mq")
    public BaseResponse<BiRensponse> genChartByAiAsyncMq(@RequestPart("file") MultipartFile multipartFile,
                                                       GenChartByAiRequest getChartByAiRequest, HttpServletRequest request) {
        String name = getChartByAiRequest.getName();
        String goal = getChartByAiRequest.getGoal();
        String chartType = getChartByAiRequest.getChartType();

        // Input Check
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "goal is empty");
        ThrowUtils.throwIf(StringUtils.isBlank(name), ErrorCode.PARAMS_ERROR, "name is empty");
        // Size Check, File Size should < 1MB
        long size = multipartFile.getSize();
        String originalFilename = multipartFile.getOriginalFilename();
        final long ONE_MB = 1024 * 1024L;
        ThrowUtils.throwIf(ONE_MB < size, ErrorCode.PARAMS_ERROR, "file size exceed upperbound");

        // File Suffix Check
        String suffix = FileUtil.getSuffix(originalFilename);
        final List<String> validFileSuffixList = Arrays.asList("xlsx");
        ThrowUtils.throwIf(!validFileSuffixList.contains(suffix), ErrorCode.PARAMS_ERROR, "invalid file suffix");


        User loginUser = userService.getLoginUser(request);


        // Limiter Test
        redisLimiterManager.doRateLimit("genChartByAi_v" + String.valueOf(loginUser.getId()));

//        StringBuilder userInput = new StringBuilder();
//        userInput.append("You are a data analyst. I will give you my analysis goals and data. Please help me analyze the data and inform me of the conclusions.\n");
//        userInput.append("goals: ").append(goal).append("\n");
//        String res = ExcelUtils.excelToCsv(multipartFile);
//        userInput.append("data: ").append(res).append("\n");

        long biModelId = 1659171950288818178L;

        StringBuilder userInput = new StringBuilder();
        userInput.append("Goal: ");

        String userGoal = goal;
        if (StringUtils.isNotBlank(chartType)){
            userGoal += ", Please use Chart Type: " + chartType;
        }
        userInput.append(userGoal);
        String res = ExcelUtils.excelToCsv(multipartFile);
        userInput.append(", Data: ").append(res).append("\n");

        // insert to DB
        Chart chart = new Chart();
        chart.setName(name);
        chart.setGoal(goal);
        chart.setChartData(res);
        chart.setChartType(chartType);
        chart.setStatus("wait");

        chart.setUserId(loginUser.getId());
        boolean saveResult = chartService.save(chart);
        ThrowUtils.throwIf(!saveResult,ErrorCode.SYSTEM_ERROR,"chart save error");

        long newChartId = chart.getId();
        biMessageProducer.sendMessage(String.valueOf(newChartId));

        BiRensponse biRensponse = new BiRensponse();
        biRensponse.setChartId(newChartId);
        return ResultUtils.success(biRensponse);
    }


}
