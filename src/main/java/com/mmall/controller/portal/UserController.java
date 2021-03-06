package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import com.mmall.util.RedisShardedPoolUtil;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.spring.web.json.Json;

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
@RequestMapping("/user/")
@Api(value = "用户操作", description = "前端用户接口",tags = "用户")
public class UserController {


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
            CookieUtil.writeLoginToken(servletResponse,session.getId());
            RedisShardedPoolUtil.setEx(session.getId(),JsonUtil.obj2String(response.getData()),Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
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
    public ServerResponse<String> logout(@ApiIgnore HttpServletRequest request,@ApiIgnore HttpServletResponse response) {
        CookieUtil.delLoginToken(request,response);
        RedisShardedPoolUtil.del(CookieUtil.readLoginToken(request));
        return ServerResponse.createBySuccess();
    }

    /**
     * 用户注册
     *
     * @param user
     * @return
     */
    @RequestMapping(value = "register.do", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("注册")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true, paramType = "query"),
            @ApiImplicitParam(value = "密码", name = "password", required = true, paramType = "query"),
            @ApiImplicitParam(value = "邮箱", name = "email", required = true, paramType = "query"),
            @ApiImplicitParam(value = "电话", name = "phone", required = true, paramType = "query"),
            @ApiImplicitParam(value = "问题", name = "question", required = true, paramType = "query"),
            @ApiImplicitParam(value = "答案", name = "answer", required = true, paramType = "query"),
    })
    public ServerResponse<String> register(@ApiIgnore User user) {
        return iUserService.register(user);
    }

    /**
     * 检查可用
     *
     * @param str
     * @param type 类型
     * @return
     */
    @RequestMapping(value = "check_calid.do", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("检测邮箱或用户名是否可用")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "邮箱或用户名", name = "str", required = true, paramType = "query"),
            @ApiImplicitParam(value = "类型", name = "type", required = true, allowableValues = "email,username", paramType = "query")
    })
    public ServerResponse<String> checkValid(String str, String type) {
        return iUserService.checkValid(str, type);
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
    public ServerResponse<User> getUserInfo(@ApiIgnore HttpServletRequest request) {
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc());
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr,User.class);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc());
        }
        return iUserService.getInformation(user.getId());
    }

    /**
     * 获取忘记密码问题
     *
     * @param username
     * @return
     */
    @RequestMapping(value = "forget_get_question.do", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("获取忘记密码问题")
    @ApiImplicitParam(value = "用户名", name = "username", required = true, paramType = "query")
    public ServerResponse<String> forgetGetQuestion(String username) {
        return iUserService.selectQuestion(username);
    }


    /**
     * 验证忘记密码答案
     *
     * @param username
     * @param question
     * @param answer
     * @return
     */
    @RequestMapping(value = "forget_check_answer.do", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("验证忘记密码答案")
    @ApiImplicitParams(
            {@ApiImplicitParam(value = "用户名", name = "username", required = true, paramType = "query"),
            @ApiImplicitParam(value = "问题", name = "question", required = true, paramType = "query"),
            @ApiImplicitParam(value = "答案", name = "answer", required = true, paramType = "query")}
    )
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer) {
        return iUserService.checkAnswer(username, question, answer);
    }

    /**
     * 修改忘记密码
     * @param username
     * @param passwordNew
     * @param forgetToken
     * @return
     */
    @RequestMapping(value = "forget_reset_password.do", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("修改忘记密码")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "用户名",name = "username",required = true,paramType = "query"),
            @ApiImplicitParam(value = "新密码",name = "password",required = true,paramType = "query"),
            @ApiImplicitParam(value = "token",name = "token",required = true,paramType = "query"),
    })
    public ServerResponse<String> forgetRestPassword(String username, @RequestParam("password") String passwordNew, @RequestParam("token") String forgetToken) {
        return iUserService.forgetRestPassword(username, passwordNew, forgetToken);
    }


    /**
     *  重置密码
     * @param request
     * @param passwordOld
     * @param passwordNew
     * @return
     */
    @RequestMapping(value = "reset_password.do", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("重置密码")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "旧密码",name = "old",required = true,paramType = "query"),
            @ApiImplicitParam(value = "旧密码",name = "new",required = true,paramType = "query")

    })
    public ServerResponse<String> resetPassword(@ApiIgnore HttpServletRequest request,@RequestParam("old") String passwordOld,@RequestParam("new") String passwordNew) {
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc());
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr,User.class);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc());
        }
        return iUserService.resetPassword(passwordOld, passwordNew, user);
    }


    /**
     * 更新用户信息
     * @param request
     * @param user
     * @return
     */
    @RequestMapping(value = "update_infomation.do", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("更新用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "邮箱", name = "email",  paramType = "query"),
            @ApiImplicitParam(value = "电话", name = "phone",  paramType = "query"),
            @ApiImplicitParam(value = "问题", name = "question",  paramType = "query"),
            @ApiImplicitParam(value = "答案", name = "answer",  paramType = "query"),
    })
    public ServerResponse<User> updateInformation(@ApiIgnore HttpServletRequest request,@ApiIgnore User user) {
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc());
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User currentUser = JsonUtil.string2Obj(userJsonStr,User.class);
        if (currentUser == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc());
        }

        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerResponse<User> response = iUserService.updateInformation(user);
        if (response.isSuccess()) {
            response.getData().setUsername(currentUser.getUsername());
            RedisShardedPoolUtil.setEx(loginToken,JsonUtil.obj2String(response.getData()),Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
        }

        return response;
    }

    /**
     * 获取用户信息
     * @param request
     * @return
     */
    @RequestMapping(value = "get_infomation.do", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("获取用户信息")
    public ServerResponse<User> getInfomation(@ApiIgnore HttpServletRequest request) {
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(), "未登录，需要登录");
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User currentUser = JsonUtil.string2Obj(userJsonStr,User.class);
        if (currentUser == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(), "未登录，需要登录");

        }
        return iUserService.getInformation(currentUser.getId());
    }
}
