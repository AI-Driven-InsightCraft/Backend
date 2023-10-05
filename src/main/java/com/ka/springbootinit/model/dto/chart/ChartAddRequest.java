package com.ka.springbootinit.model.dto.chart;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

@Data
public class ChartAddRequest implements Serializable {

    private String name;
    private String goal;

    private String chartData;

    private String chartType;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}