package com.crm.controller;


import com.crm.common.result.Result;
import com.crm.service.OssService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "图片上传")
@RequestMapping("/oss")
public class OssController {
    @Resource
    private OssService ossService;
    @PostMapping("/upload")
    public Result<Map<String, String>> upload(MultipartFile  file){
        String imageUrl = ossService.uploadFile(file);
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("fileUrl", imageUrl); // 键必须是 fileUrl
        return Result.ok(resultMap);
    }
}
