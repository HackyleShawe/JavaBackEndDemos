package com.ks.minio.controller;

import com.alibaba.fastjson2.JSONObject;
import com.ks.minio.service.MinioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 注意：歌曲、图片等数据不适合放于resources/static，因为程序一旦运行起来，static目录下的文件是不会变化的。
 * 只适合放置静态的代码数据：JS、CSS、HTML等
 * 歌曲、图片的资源适合放置于专门的服务器、存储桶
 */
@RestController
@RequestMapping("/minio")
public class MinioController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MinioController.class);
    @Autowired
    private MinioService minioService;

    /**
     * 上传文件
     */
    @PostMapping(value = "/upload")
    public JSONObject uploadFile(@RequestParam(value = "file", required = false) MultipartFile[] multipartFiles) {
        if(multipartFiles == null || multipartFiles.length < 1) {
            //return ApiResponse.error(ResponseEnum.FRONT_END_ERROR.getCode(), ResponseEnum.FRONT_END_ERROR.getMessage());
            return errorRequest();
        }
        try {
            List<String> fileObjects = minioService.fileUpload(multipartFiles);

            JSONObject object = new JSONObject();
            object.put("url", fileObjects);
            return object;

            //return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage(), fileObjects);
        } catch (Exception e) {
            LOGGER.error("上传文件出现异常：", e);
            //return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
            return errorResponse();
        }
    }

    /**
     * 文件删除
     */
    @GetMapping("/del")
    public JSONObject fileDelete(@RequestParam("fileURL") String fileURL) {
        if(null == fileURL || "".equals(fileURL.trim())) {
            return errorRequest();
        }

        try {
            boolean delFlag = minioService.fileDelete(fileURL);
            if(delFlag) {
                return success();
            } else {
                return errorRequest();
            }
        }catch (Exception e) {
            LOGGER.error("文件删除出现异常：", e);
            return errorResponse();
        }
    }


    private JSONObject errorRequest() {
        JSONObject obj = new JSONObject();
        obj.put("code", "4000");
        obj.put("message", "前端请求出现错误");

        return obj;
    }

    private JSONObject errorResponse() {
        JSONObject obj = new JSONObject();
        obj.put("code", "5000");
        obj.put("message", "后端出现错误");

        return obj;
    }

    private JSONObject success() {
        JSONObject obj = new JSONObject();
        obj.put("code", "2000");
        obj.put("message", "操作成功");

        return obj;
    }
}
