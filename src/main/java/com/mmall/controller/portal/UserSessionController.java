package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Copyright (C), 2018, GeforceLee
 *
 * @author: geforce
 * @Date: 2018/4/1 下午12:47
 */
@Controller
@RequestMapping("/user/springsession")
@Api(value = "用户操作", description = "前端用户接口",tags = "用户springsession")
public class UserSessionController {


    @Autowired
    private IUserService iUserService;

    /**
     * 用户登录
     *
     * @param username
     * @param password
     * @param session
     * @return
     */
    @ApiOperation("用户登录")
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true, paramType = "query"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, paramType = "query"),
    })
    public ServerResponse<User> login(String username, String password, @ApiIgnore HttpSession session, @ApiIgnore HttpServletResponse servletResponse) {

        ServerResponse<User> response = iUserService.login(username, password);
        if (response.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER,response.getData());
//            CookieUtil.writeLoginToken(servletResponse,session.getId());
//            RedisShardedPoolUtil.setEx(session.getId(),JsonUtil.obj2String(response.getData()),Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
        }
        return response;
    }

    /**
     * 登出
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "logout.do", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("登出")
    public ServerResponse<String> logout(@ApiIgnore HttpServletRequest request,@ApiIgnore HttpSession session,@ApiIgnore HttpServletResponse response) {
//        CookieUtil.delLoginToken(request,response);
//        RedisShardedPoolUtil.del(CookieUtil.readLoginToken(request));

        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }


    /**
     * 获取用户信息
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "get_user_info.do", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("获取用户信息")
    public ServerResponse<User> getUserInfo(@ApiIgnore HttpServletRequest request,@ApiIgnore HttpSession session) {
//        String loginToken = CookieUtil.readLoginToken(request);
//        if (StringUtils.isEmpty(loginToken)) {
//            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc());
//        }
//        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
//        User user = JsonUtil.string2Obj(userJsonStr,User.class);
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorMessage("用户未登录，无法获取用户信息");
    }


}
