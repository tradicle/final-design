package com.xxx.animal.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxx.animal.common.Result;
import com.xxx.animal.entity.Animal;
import com.xxx.animal.service.AnimalService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/animals")
public class AnimalController {

    private final AnimalService animalService;

    public AnimalController(AnimalService animalService) {
        this.animalService = animalService;
    }

    @GetMapping("/hello")
    public Result<String> hello() {
        return Result.ok(animalService.hello());
    }

    @GetMapping
    public Result<List<Animal>> list(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String sex,
            @RequestParam(required = false) String bodySize
    ) {
        return Result.ok(animalService.listWithCategory(category, sex, bodySize));
    }

    @GetMapping("/{id}")
    public Result<Animal> getById(@PathVariable Long id) {
        return Result.ok(animalService.getAnimalWithLocations(id));
    }

    @GetMapping("/no/{animalNo}")
    public Result<Animal> getByNo(@PathVariable String animalNo) {
        return Result.ok(animalService.getAnimalByNo(animalNo));
    }

    @PostMapping
    public Result<Boolean> create(@RequestBody Animal animal) {
        return Result.ok(animalService.saveAnimal(animal));
    }

    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody Animal animal) {
        animal.setId(id);
        return Result.ok(animalService.updateById(animal));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.ok(animalService.removeById(id));
    }

    @GetMapping("/admin")
    public Result<Map<String, Object>> adminList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int pageSize
    ) {
        LambdaQueryWrapper<Animal> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(Animal::getName, keyword)
                    .or().like(Animal::getAnimalNo, keyword)
                    .or().like(Animal::getActivityScope, keyword)
                    .or().like(Animal::getLocation, keyword));
        }
        if (category != null && !category.isBlank() && !"ALL".equalsIgnoreCase(category)) {
            wrapper.eq(Animal::getCategory, category);
        }
        if (status != null) {
            wrapper.eq(Animal::getStatus, status);
        }
        wrapper.orderByDesc(Animal::getUpdateTime);
        Page<Animal> pageResult = animalService.page(new Page<>(page, pageSize), wrapper);
        return Result.ok(Map.of("records", pageResult.getRecords(), "total", pageResult.getTotal()));
    }
}
