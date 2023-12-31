package com.ka.springbootinit.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ka.springbootinit.model.entity.Post;
import com.ka.springbootinit.model.entity.PostFavour;
import org.apache.ibatis.annotations.Param;


public interface PostFavourMapper extends BaseMapper<PostFavour> {

    Page<Post> listFavourPostByPage(IPage<Post> page, @Param(Constants.WRAPPER) Wrapper<Post> queryWrapper,
            long favourUserId);

}




