package com.ka.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ka.springbootinit.model.entity.Chart;
import com.ka.springbootinit.service.ChartService;
import com.ka.springbootinit.mapper.ChartMapper;
import org.springframework.stereotype.Service;

/**
* @author wangzhiyuan
* @description 针对表【chart】的数据库操作Service实现
* @createDate 2023-09-27 02:00:30
*/
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
    implements ChartService{

}




