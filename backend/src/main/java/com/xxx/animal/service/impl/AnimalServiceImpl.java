package com.xxx.animal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxx.animal.entity.Animal;
import com.xxx.animal.entity.AnimalLocation;
import com.xxx.animal.mapper.AnimalLocationMapper;
import com.xxx.animal.mapper.AnimalMapper;
import com.xxx.animal.service.AnimalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AnimalServiceImpl extends ServiceImpl<AnimalMapper, Animal> implements AnimalService {

    private final AnimalLocationMapper animalLocationMapper;

    public AnimalServiceImpl(AnimalLocationMapper animalLocationMapper) {
        this.animalLocationMapper = animalLocationMapper;
    }

    @Override
    public String hello() {
        return "hello animal";
    }

    @Override
    @Transactional
    public boolean saveAnimal(Animal animal) {
        if (animal.getAnimalNo() == null || animal.getAnimalNo().isEmpty()) {
            animal.setAnimalNo(UUID.randomUUID().toString());
        }
        boolean success = this.save(animal);
        if (success && animal.getLatitude() != null && animal.getLongitude() != null) {
            AnimalLocation location = new AnimalLocation();
            location.setAnimalId(animal.getId());
            location.setLatitude(animal.getLatitude());
            location.setLongitude(animal.getLongitude());
            location.setRecordTime(LocalDateTime.now());
            animalLocationMapper.insert(location);
        }
        return success;
    }

    @Override
    public Animal getAnimalWithLocations(Long id) {
        Animal animal = this.getById(id);
        if (animal != null) {
            loadLocations(animal);
        }
        return animal;
    }

    @Override
    public Animal getAnimalByNo(String animalNo) {
        LambdaQueryWrapper<Animal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Animal::getAnimalNo, animalNo);
        Animal animal = this.getOne(wrapper);
        if (animal != null) {
            loadLocations(animal);
        }
        return animal;
    }

    private void loadLocations(Animal animal) {
        LambdaQueryWrapper<AnimalLocation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AnimalLocation::getAnimalId, animal.getId());
        wrapper.orderByAsc(AnimalLocation::getRecordTime);
        List<AnimalLocation> locations = animalLocationMapper.selectList(wrapper);
        animal.setLocations(locations);
    }

    @Override
    public List<Animal> listWithCategory(String category, String sex, String bodySize) {
        LambdaQueryWrapper<Animal> wrapper = new LambdaQueryWrapper<>();
        if (category != null && !category.isEmpty() && !"ALL".equalsIgnoreCase(category)) {
            wrapper.eq(Animal::getCategory, category);
        }
        if (sex != null && !sex.isEmpty() && !"ALL".equalsIgnoreCase(sex)) {
            wrapper.eq(Animal::getSex, sex);
        }
        if (bodySize != null && !bodySize.isEmpty() && !"ALL".equalsIgnoreCase(bodySize)) {
            wrapper.eq(Animal::getBodySize, bodySize);
        }
        wrapper.orderByDesc(Animal::getCreateTime);
        return this.list(wrapper);
    }
}
