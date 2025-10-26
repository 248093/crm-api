package com.crm.service.impl;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;
import com.crm.common.config.OssConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
@Service
public class OssApiService {

    private static OssConfig config;

    // 默认配置文件路径
    private static final String DEFAULT_CONFIG_FILE = "aliyun-oss.properties";

    /**
     * 初始化OSS配置 - 使用默认配置文件
     */
    public static void init() {
        try {
            Properties props = new Properties();
            InputStream inputStream = OssApiService.class.getClassLoader().getResourceAsStream(DEFAULT_CONFIG_FILE);
            if (inputStream != null) {
                props.load(inputStream);
                inputStream.close();

                OssConfig defaultConfig = new OssConfig();
                defaultConfig.setEndpoint(props.getProperty("aliyun-oss.endpoint", ""));
                defaultConfig.setAccessKeyId(props.getProperty("aliyun-oss.access-key-id", ""));
                defaultConfig.setAccessKeySecret(props.getProperty("aliyun-oss.access-key-secret", ""));
                defaultConfig.setBucketName(props.getProperty("aliyun-oss.bucket-name", ""));
                defaultConfig.setDir(props.getProperty("aliyun-oss.dir", ""));

                config = defaultConfig;
            }
        } catch (Exception e) {
            log.error("加载默认配置文件失败", e);
        }
    }

    /**
     * 初始化OSS配置 - 使用自定义配置
     * @param ossConfig OSS配置对象
     */
    public static void init(OssConfig ossConfig) {
        config = ossConfig;
    }

    /**
     * 上传文件到OSS
     * @param file 要上传的文件
     * @return 文件访问URL
     */
    public static String uploadFile(MultipartFile file) {
        // 检查配置是否已初始化
        if (config == null) {
            throw new IllegalStateException("OssService未初始化，请先调用init()方法设置配置");
        }

        // 复用原有的上传逻辑
        if (file != null) {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String fileOriginalFilename = file.getOriginalFilename();
            String suffix = fileOriginalFilename.substring(fileOriginalFilename.lastIndexOf("."));
            String newFileName = timestamp + suffix;
            log.info("上传文件名：{}", newFileName);

            String uploadpath = config.getDir() + newFileName;
            OSSClient ossClient = new OSSClient(
                    config.getEndpoint(),
                    config.getAccessKeyId(),
                    config.getAccessKeySecret()
            );

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType("image/jpg");
            objectMetadata.setContentDisposition("inline");

            try {
                ossClient.putObject(config.getBucketName(), uploadpath, file.getInputStream(), objectMetadata);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                ossClient.shutdown();
            }

            return "https://" + config.getBucketName() + "." + config.getEndpoint() + "/" + uploadpath;
        } else {
            return "上传失败";
        }
    }
}
