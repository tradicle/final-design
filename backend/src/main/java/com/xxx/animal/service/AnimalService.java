package com.xxx.animal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxx.animal.entity.Animal;
import java.util.List;

public interface AnimalService extends IService<Animal> {
    String hello();
    boolean saveAnimal(Animal animal);
    Animal getAnimalWithLocations(Long id);
    Animal getAnimalByNo(String animalNo);
    List<Animal> listWithCategory(String category, String sex, String bodySize);
}
