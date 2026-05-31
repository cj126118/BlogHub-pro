package com.bloghub.controller;

import com.bloghub.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private static final Logger log = LoggerFactory.getLogger(UploadController.class);

    private static final Set<String> ALLOWED_TYPES = new HashSet<>(Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/webp", "image/svg+xml"
    ));

    private static final long MAX_SIZE = 5 * 1024 * 1024; // 5MB

    @Value("${upload.dir:./uploads}")
    private String uploadDir;

    /**
     * 上传图片
     */
    @PostMapping
    public Result<Map<String, Object>> upload(@RequestParam("file") MultipartFile file,
                                               HttpServletRequest request) {
        // 校验文件类型
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            return Result.badRequest("不支持的文件类型: " + contentType);
        }

        // 校验文件大小
        if (file.getSize() > MAX_SIZE) {
            return Result.badRequest("文件大小不能超过 5MB");
        }

        // 获取原始文件名和扩展名
        String originalName = file.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }

        // UUID 重命名
        String uuidName = UUID.randomUUID().toString().replace("-", "") + extension;

        // 按日期分目录
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        String relativePath = datePath + "/" + uuidName;
        String fullPath = uploadDir + "/" + relativePath;

        try {
            Path destPath = Paths.get(fullPath).normalize();
            // 路径穿越防护
            if (!destPath.startsWith(Paths.get(uploadDir).normalize())) {
                return Result.badRequest("非法路径");
            }
            // 创建目录
            Files.createDirectories(destPath.getParent());
            // 保存文件
            file.transferTo(destPath.toFile());

            String url = "/uploads/" + relativePath.replace("\\", "/");

            log.info("文件上传成功: {} ({})", url, file.getSize());

            Map<String, Object> data = new HashMap<>();
            data.put("url", url);
            data.put("name", originalName);
            data.put("size", file.getSize());
            return Result.created(data);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }
}
