package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisShardedPoolUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Copyright (C), 2018, GeforceLee
 *
 * @author: geforce
 * @Date: 2018/4/1 下午6:29
 */
@Controller
@RequestMapping("/manage/user")
@Api(tags = "管理用户",description = "后端管理接口")
public class UserManageController {

    @Autowired
    private IUserService iUserService;

    /**
     * 管理平台登录
     * @param username
     * @param password
     * @param session
     * @param servletResponse
     * @return
     */
    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("管理平台登录")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "用户名",name = "username",required = true,paramType = "query"),
            @ApiImplicitParam(value = "密码",name = "password",required = true,paramType = "query")
    })
    public ServerResponse<User> login(String username, String password,@ApiIgnore HttpSession session,@ApiIgnore HttpServletResponse servletResponse) {
        ServerResponse<User> response = iUserService.login(username,password);
        if (response.isSuccess()){
            User user = response.getData();
            if (user.getRole()== Const.Role.ROLE_ADMIN) {
                CookieUtil.writeLoginToken(servletResponse,session.getId());
                RedisShardedPoolUtil.setEx(session.getId(),JsonUtil.obj2String(user),Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
                return response;
            }else {
                return ServerResponse.createByErrorMessage("不是管理员，无法登陆");
            }

        }
        return response;
    }


}
