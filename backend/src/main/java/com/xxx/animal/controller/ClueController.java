package com.xxx.animal.controller;

import com.xxx.animal.common.Result;
import com.xxx.animal.entity.Clue;
import com.xxx.animal.service.ClueService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/clue")
public class ClueController {
    private final ClueService clueService;

    public ClueController(ClueService clueService) {
        this.clueService = clueService;
    }

    @PostMapping
    public Result<Boolean> submit(@RequestBody Clue clue) {
        clue.setStatus(0);
        return Result.ok(clueService.save(clue));
    }

    @GetMapping
    public Result<List<Clue>> list() {
        return Result.ok(clueService.list());
    }
}
