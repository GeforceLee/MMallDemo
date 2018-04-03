<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>


<html>
<body>
<h2>Hello World!</h2>

<h1>上传</h1>
<form action="/manage/product/upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file"/>
    <input type="submit" value="上传文件">
</form>

<h1>富文本上传</h1>

<form action="/manage/product/richtext_img_upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file"/>
    <input type="submit" value="上传文件">
</form>
</body>
</html>
