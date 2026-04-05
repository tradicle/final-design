package com.xxx.animal.controller;

import com.xxx.animal.common.Result;
import com.xxx.animal.entity.Animal;
import com.xxx.animal.service.AnimalService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
