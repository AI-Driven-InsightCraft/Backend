package com.ka.springbootinit.model.dto.chart;

import lombok.Data;

import java.io.Serializable;


@Data
public class GenChartByAiRequest implements Serializable {


    private String name;

    private String goal;

    private String chartType;

    private static final long serialVersionUID = 1L;
}