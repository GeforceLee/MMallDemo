package com.mmall.controller.backend;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.util.RedisPoolUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author geforce
 * @date 2018/4/3
 */
@Controller
@RequestMapping("/manage/product")
@Api(tags = "管理产品",description = "产品相关接口")
public class ProductManageController {
    @Autowired
    private IUserService iUserService;

    @Autowired
    private IProductService iProductService;

    @Autowired
    private IFileService iFileService;

    /**
     * 产品新增或更新
     * @param request
     * @param product
     * @return
     */
    @RequestMapping(value = "save.do",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("产品新增或更新")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "产品id",name="id",paramType = "query"),
            @ApiImplicitParam(value = "类别id",name="categoryId",paramType = "query"),
            @ApiImplicitParam(value = "名称",name="name",paramType = "query"),
            @ApiImplicitParam(value = "子标题",name="subtitle",paramType = "query"),
            @ApiImplicitParam(value = "主图片",name="mainImage",paramType = "query"),
            @ApiImplicitParam(value = "字图片",name="subImages",paramType = "query"),
            @ApiImplicitParam(value = "详情",name="detail",paramType = "query"),
            @ApiImplicitParam(value = "价格",name="price",paramType = "query"),
            @ApiImplicitParam(value = "库存",name="stock",paramType = "query"),
            @ApiImplicitParam(value = "状态",name="status",paramType = "query")
    })
    public ServerResponse productSave(@ApiIgnore HttpServletRequest request,@ApiIgnore Product product) {
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc());
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr,User.class);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc());
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.saveOrUpdateUpdate(product);
        } else {
            return ServerResponse.createByErrorMessage("没有权限");
        }
    }

    /**
     * 设置产品状态
     * @param request
     * @param productId
     * @param status
     * @return
     */
    @RequestMapping(value = "set_sale_status.do",method = RequestMethod.PUT)
    @ResponseBody
    @ApiOperation("设置产品状态")
    @ApiImplicitParams({
        @ApiImplicitParam(value = "产品id",name = "productId",paramType = "query"),
        @ApiImplicitParam(value = "状态",name = "status",paramType = "query")
    })
    public ServerResponse setSaleStatus(@ApiIgnore HttpServletRequest request, Integer productId,Integer status) {
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc());
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr,User.class);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc());
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.setSaleStatus(productId,status);
        } else {
            return ServerResponse.createByErrorMessage("没有权限");
        }
    }

    /**
     * 产品详情
     * @param session
     * @param productId
     * @return
     */
    @RequestMapping(value = "detail.do",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("产品详情")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "产品id",name = "productId",paramType = "query")
    })
    public ServerResponse getDetail(@ApiIgnore HttpServletRequest request, Integer productId) {
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc());
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr,User.class);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc());
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.manageProductDetail(productId);
        } else {
            return ServerResponse.createByErrorMessage("没有权限");
        }
    }

    /**
     * 产品列表
     * @param request
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "list.do",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("产品列表")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "页码",name = "pageNum",paramType = "query"),
            @ApiImplicitParam(value = "每页数量",name = "pageSize",paramType = "query"),
    })
    public ServerResponse getList(@ApiIgnore HttpServletRequest request,
                                  @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                  @RequestParam(value = "pageSize",defaultValue = "10") int pageSize) {
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc());
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr,User.class);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc());
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.getProductList(pageNum,pageSize);
        } else {
            return ServerResponse.createByErrorMessage("没有权限");
        }
    }


    /**
     * 产品搜索
     * @param request
     * @param productName
     * @param productId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "search.do",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("搜索产品")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "产品名称",name = "productName",paramType = "query"),
            @ApiImplicitParam(value = "产品id",name = "productId",paramType = "query"),
            @ApiImplicitParam(value = "页码",name = "pageNum",paramType = "query"),
            @ApiImplicitParam(value = "每页数量",name = "pageSize",paramType = "query"),
    })
    public ServerResponse productSearch(@ApiIgnore HttpServletRequest request,
                                  String productName,
                                  Integer productId,
                                  @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                  @RequestParam(value = "pageSize",defaultValue = "10") int pageSize) {
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc());
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr,User.class);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc());
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.searchProcut(productName,productId,pageNum,pageSize);
        } else {
            return ServerResponse.createByErrorMessage("没有权限");
        }
    }

    /**
     * 图片上传
     * @param file
     * @param request
     * @return
     */
    @RequestMapping(value = "upload.do",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("图片上传")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "图片",name = "upload_file",paramType = "form",dataType = "file")
    })
    public ServerResponse upload(
                                 @RequestParam("upload_file") MultipartFile file,
                                 @ApiIgnore HttpServletRequest request) {

        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc());
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr,User.class);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEES_LOGIN.getCode(),ResponseCode.NEES_LOGIN.getDesc());
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file,path);
            if (StringUtils.isBlank(targetFileName)) {
                return ServerResponse.createByErrorMessage("上传图片失败");
            }
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;

            Map fileMap = Maps.newHashMap();
            fileMap.put("uri",targetFileName);
            fileMap.put("url",url);
            return ServerResponse.createBySuccess(fileMap);
        } else {
            return ServerResponse.createByErrorMessage("没有权限");
        }
    }



    @RequestMapping(value = "richtext_img_upload.do",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("富文本上传图片")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "图片",name = "upload_file",paramType = "form",dataType = "file")
    })
    public Map richTextImgUpload(
                                 @RequestParam("upload_file") MultipartFile file,
                                 @ApiIgnore HttpServletRequest request,
                                 @ApiIgnore HttpServletResponse response) {
        Map resultMap = Maps.newHashMap();
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            resultMap.put("success",false);
            resultMap.put("msg","请登录管理员");
            return resultMap;        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr,User.class);
        if (user == null){
            resultMap.put("success",false);
            resultMap.put("msg","请登录管理员");
            return resultMap;        }

        //富文本对于返回值有自己的要求,用是是simditor
        if (iUserService.checkAdminRole(user).isSuccess()) {
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file,path);
            if (StringUtils.isBlank(targetFileName)) {
                resultMap.put("success",false);
                resultMap.put("msg","上传失败");
                return resultMap;
            }

            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
            resultMap.put("success",true);
            resultMap.put("msg","上传成功");
            resultMap.put("file_path",url);
            response.addHeader("Access-Control-Allow-Headers","X-File-Name");

            return resultMap;
        } else {
            resultMap.put("success",false);
            resultMap.put("msg","无权限操作");
            return resultMap;
        }
    }
}
