package com.ka.springbootinit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ka.springbootinit.model.entity.Post;
import java.util.Date;
import java.util.List;


public interface PostMapper extends BaseMapper<Post> {

    List<Post> listPostWithDelete(Date minUpdateTime);

}




