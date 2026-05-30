package com.xxx.animal.controller;

import com.xxx.animal.common.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file,
                                  @RequestParam("folder") String folder) {
        if (file.isEmpty()) {
            return Result.fail("文件为空");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String fileName = UUID.randomUUID() + extension;
        // Use backend/uploads/{folder} for development so it persists
        String uploadDir = System.getProperty("user.dir") + "/uploads/" + folder + "/";
        File dest = new File(uploadDir + fileName);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        try {
            file.transferTo(dest);
            // Return URL. ResourceHandler maps /uploads/** to this directory.
            return Result.ok("/uploads/" + folder + "/" + fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.fail("上传失败: " + e.getMessage());
        }
    }
}
