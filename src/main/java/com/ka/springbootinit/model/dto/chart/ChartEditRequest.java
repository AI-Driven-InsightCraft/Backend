package com.ka.springbootinit.model.dto.chart;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChartEditRequest implements Serializable {

    private Long id;

    private String name;

    private String goal;

    private String chartData;

    private String chartType;

    private static final long serialVersionUID = 1L;
}