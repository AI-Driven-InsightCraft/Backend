package com.ka.springbootinit.service;

import com.ka.springbootinit.model.entity.Chart;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author wangzhiyuan
* @description 针对表【chart】的数据库操作Service
* @createDate 2023-09-27 02:00:30
*/
public interface ChartService extends IService<Chart> {

    public String smartChat(String input);

    public void handleChartUpdateError(long chartId, String execMessage);
}
