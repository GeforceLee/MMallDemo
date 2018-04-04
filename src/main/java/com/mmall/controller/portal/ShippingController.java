package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;
import com.mmall.pojo.User;
import com.mmall.service.IShippingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpSession;

/**
 * @author geforce
 * @date 2018/4/4
 */
@Controller
@RequestMapping("/shipping")
@Api(tags = "用户地址",description = "地址接口")
public class ShippingController {
    @Autowired
    private IShippingService iShippingService;

    @RequestMapping(value = "add.do",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("增加")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "收货人姓名" ,name = "receiverName",paramType = "query"),
            @ApiImplicitParam(value = "收货人电话" ,name = "receiverPhone",paramType = "query"),
            @ApiImplicitParam(value = "收货人手机" ,name = "receiverMobile",paramType = "query"),
            @ApiImplicitParam(value = "省" ,name = "receiverProvince",paramType = "query"),
            @ApiImplicitParam(value = "市" ,name = "receiverCity",paramType = "query"),
            @ApiImplicitParam(value = "区" ,name = "receiverDistrict",paramType = "query"),
            @ApiImplicitParam(value = "地址" ,name = "receiverAddress",paramType = "query"),
            @ApiImplicitParam(value = "邮编" ,name = "receiverZip",paramType = "query"),
    })
    public ServerResponse add(@ApiIgnore HttpSession session,@ApiIgnore Shipping shipping) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc());
        }
        return iShippingService.add(user.getId(),shipping);
    }

    @RequestMapping(value = "del.do",method = RequestMethod.DELETE)
    @ResponseBody
    @ApiOperation("删除")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "地址id" ,name = "shippingId",paramType = "query"),
    })
    public ServerResponse del(@ApiIgnore HttpSession session, Integer shippingId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc());
        }
        return iShippingService.del(user.getId(),shippingId);
    }

    @RequestMapping(value = "update.do",method = RequestMethod.PUT)
    @ResponseBody
    @ApiOperation("更新")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "地址id" ,name = "id",paramType = "query"),
            @ApiImplicitParam(value = "收货人姓名" ,name = "receiverName",paramType = "query"),
            @ApiImplicitParam(value = "收货人电话" ,name = "receiverPhone",paramType = "query"),
            @ApiImplicitParam(value = "收货人手机" ,name = "receiverMobile",paramType = "query"),
            @ApiImplicitParam(value = "省" ,name = "receiverProvince",paramType = "query"),
            @ApiImplicitParam(value = "市" ,name = "receiverCity",paramType = "query"),
            @ApiImplicitParam(value = "区" ,name = "receiverDistrict",paramType = "query"),
            @ApiImplicitParam(value = "地址" ,name = "receiverAddress",paramType = "query"),
            @ApiImplicitParam(value = "邮编" ,name = "receiverZip",paramType = "query"),
    })
    public ServerResponse update(@ApiIgnore HttpSession session,@ApiIgnore Shipping shipping) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc());
        }
        return iShippingService.update(user.getId(),shipping);
    }

    @RequestMapping(value = "select.do",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询地址")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "地址id" ,name = "shippingId",paramType = "query"),
    })
    public ServerResponse select(@ApiIgnore HttpSession session, Integer shippingId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc());
        }
        return iShippingService.select(user.getId(),shippingId);
    }

    @RequestMapping(value = "list.do",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("列表")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "页码" ,name = "pageNum",paramType = "query"),
            @ApiImplicitParam(value = "每页限制" ,name = "pageSize",paramType = "query"),
    })

    public ServerResponse<PageInfo> list(@ApiIgnore HttpSession session,
                                         @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                                         @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc());
        }
        return iShippingService.list(user.getId(),pageNum,pageSize);
    }
}
