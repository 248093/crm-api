package com.crm.service.impl;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;
import com.crm.common.config.OssConfig;
import com.crm.service.OssService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Service
@Slf4j
public class OssServiceImpl implements OssService {
    @Resource
    private OssConfig ossConfig;

    public String endpoint;
    public String accessKeyId;
    public String accessKeySecret;
    public String bucketName;
    public String dir;

    @PostConstruct
    public void init() {
        this.endpoint = ossConfig.getEndpoint();
        this.accessKeyId = ossConfig.getAccessKeyId();
        this.accessKeySecret = ossConfig.getAccessKeySecret();
        this.bucketName = ossConfig.getBucketName();
        this.dir = ossConfig.getDir();
    }

    @Override
    public String uploadFile(MultipartFile file) {
        if (file!=null) {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String fileOriginalFilename = file.getOriginalFilename();
            String suffix = fileOriginalFilename.substring(fileOriginalFilename.lastIndexOf("."));
            String newFileName = timestamp + suffix;
            log.info("上传文件名：{}", newFileName);
            String uploadpath = dir + newFileName;
            OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
            ObjectMetadata objectMedata = new ObjectMetadata();
            objectMedata.setContentType("image/jpg");
            objectMedata.setContentDisposition("inline");
            try {
                ossClient.putObject(bucketName, uploadpath, file.getInputStream(), objectMedata);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            ossClient.shutdown();
            return "https://" + bucketName + "." + endpoint + "/" + uploadpath;
        }
        else {
            return "上传失败";
        }
    }
}
