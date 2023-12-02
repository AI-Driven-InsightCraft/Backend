package com.ka.springbootinit.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ka.springbootinit.model.dto.user.UserQueryRequest;
import com.ka.springbootinit.model.entity.User;
import com.ka.springbootinit.model.vo.LoginUserVO;
import com.ka.springbootinit.model.vo.UserVO;
import java.util.List;
import javax.servlet.http.HttpServletRequest;


public interface UserService extends IService<User> {

    long userRegister(String userAccount, String userPassword, String checkPassword);

    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);


    User getLoginUser(HttpServletRequest request);

    User getLoginUserPermitNull(HttpServletRequest request);

    boolean isAdmin(HttpServletRequest request);

    boolean isAdmin(User user);

    boolean userLogout(HttpServletRequest request);

    LoginUserVO getLoginUserVO(User user);

    UserVO getUserVO(User user);

    List<UserVO> getUserVO(List<User> userList);

    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

}
