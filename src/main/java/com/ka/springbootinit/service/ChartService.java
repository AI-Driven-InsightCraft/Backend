package com.ka.springbootinit.service;

import com.ka.springbootinit.model.entity.Chart;
import com.baomidou.mybatisplus.extension.service.IService;


public interface ChartService extends IService<Chart> {

    public String smartChat(String input);

    public void handleChartUpdateError(long chartId, String execMessage);
}
