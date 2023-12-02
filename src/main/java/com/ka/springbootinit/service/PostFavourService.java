package com.ka.springbootinit.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ka.springbootinit.model.entity.Post;
import com.ka.springbootinit.model.entity.PostFavour;
import com.ka.springbootinit.model.entity.User;


public interface PostFavourService extends IService<PostFavour> {

    int doPostFavour(long postId, User loginUser);

    Page<Post> listFavourPostByPage(IPage<Post> page, Wrapper<Post> queryWrapper,
            long favourUserId);

    int doPostFavourInner(long userId, long postId);
}
