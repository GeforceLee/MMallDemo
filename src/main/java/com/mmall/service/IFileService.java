package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author geforce
 * @date 2018/4/3
 */
public interface IFileService {
    String upload(MultipartFile file,String path);
}
