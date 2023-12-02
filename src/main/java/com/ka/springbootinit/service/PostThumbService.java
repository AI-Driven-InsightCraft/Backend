package com.ka.springbootinit.service;

import com.ka.springbootinit.model.entity.PostThumb;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ka.springbootinit.model.entity.User;


public interface PostThumbService extends IService<PostThumb> {


    int doPostThumb(long postId, User loginUser);

    int doPostThumbInner(long userId, long postId);
}
