package com.mmall.controller.common.interceptor;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.controller.backend.ProductManageController;
import com.mmall.controller.backend.UserManageController;
import com.mmall.pojo.User;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * @author geforce
 * @date 2018/4/11
 */
@Slf4j
public class AuthorityInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("preHandle");
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        //解析handlerMthod
        String methodName = handlerMethod.getMethod().getName();
        String className = handlerMethod.getBean().getClass().getSimpleName();

        //解析参数
        StringBuilder requestParamBuffer = new StringBuilder();

        Map paramMap = request.getParameterMap();
        Iterator it = paramMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String mapKey = (String) entry.getKey();
            String mapValue = StringUtils.EMPTY;
            //request这个参数的map,里面的value返回是一个String[]

            Object obj = entry.getValue();
            if (obj instanceof String[]) {
                String[] strs = (String[]) obj;
                mapValue = Arrays.toString(strs);
            }
            requestParamBuffer.append(mapKey).append("=").append(mapValue);
        }
        if (StringUtils.equals(className,UserManageController.class.getSimpleName()) && StringUtils.equals(methodName,"login")){
            log.info("权限拦截器拦截到请求,className:{},methodName:{}",className,methodName);
            //如果拦截到登录请求,不能打印日志.防止用户名密码泄露
            return true;
        }
        log.info(requestParamBuffer.toString());
        User user = null;
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isNotEmpty(loginToken)) {
            String userJsonStr = RedisShardedPoolUtil.get(loginToken);
            user = JsonUtil.string2Obj(userJsonStr,User.class);
        }

        if (user == null || (user.getRole().intValue() != Const.Role.ROLE_ADMIN)){
            //重置respone, 否者会报异常 getWriter() has already been called for this response.
            response.reset();
            //这里要设置编码,否者会出现乱码
            response.setCharacterEncoding("UTF-8");
            //返回值的类型
            response.setContentType("appliction/json;charset=UTF-8");

            PrintWriter out = response.getWriter();


            //富文本特殊处理
            if (user == null) {
                if (StringUtils.equals(className,ProductManageController.class.getSimpleName()) && StringUtils.equals(methodName,"richTextImgUpload")){
                    log.info("权限拦截器拦截到请求,className:{},methodName:{}",className,methodName);
                    Map resultMap = Maps.newHashMap();
                    resultMap.put("success",false);
                    resultMap.put("msg","请登录管理员");
                    out.print(JsonUtil.obj2String(resultMap));
                }else {
                    out.print(JsonUtil.obj2String(ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc())));
                }
            } else {
                if (StringUtils.equals(className,ProductManageController.class.getSimpleName()) && StringUtils.equals(methodName,"richTextImgUpload")){
                    log.info("权限拦截器拦截到请求,className:{},methodName:{}",className,methodName);
                    Map resultMap = Maps.newHashMap();
                    resultMap.put("success",false);
                    resultMap.put("msg","没有权限");
                    out.print(JsonUtil.obj2String(resultMap));
                }else {
                    out.print(JsonUtil.obj2String(ServerResponse.createByErrorMessage("没有权限")));
                }
            }
            out.flush();
            //这里要关闭
            out.close();
            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("postHandle");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.info("afterCompletion");
    }
}
