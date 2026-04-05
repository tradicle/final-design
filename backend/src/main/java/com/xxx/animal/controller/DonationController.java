package com.xxx.animal.controller;

import com.xxx.animal.common.Result;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/donation")
public class DonationController {

    @GetMapping("/records")
    public Result<String> list() {
        return Result.ok("捐赠功能开发中...");
    }
}
