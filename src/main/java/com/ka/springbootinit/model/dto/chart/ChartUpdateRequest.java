package com.ka.springbootinit.model.dto.chart;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


@Data
public class ChartUpdateRequest implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;


    private String goal;

    private String name;


    private String chartData;

    private String chartType;

    private String genChart;

    private String genResult;

    /**
     * wait,running,succeed,failed
     */
    private String status;

    /**
     *
     */
    private String execMessage;

    /**
     *
     */
    private Long userId;

    /**
     *
     */
    private Date createTime;

    /**
     *
     */
    private Date updateTime;

    /**
     *
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}