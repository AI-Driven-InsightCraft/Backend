package com.ka.springbootinit.model.dto.chart;


import com.ka.springbootinit.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChartQueryRequest extends PageRequest implements Serializable {

    private Long id;

    private String name;

    private String goal;

    private String chartType;

    private Long userId;

    private static final long serialVersionUID = 1L;
}