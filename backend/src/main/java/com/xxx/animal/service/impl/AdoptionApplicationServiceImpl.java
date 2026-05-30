package com.xxx.animal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxx.animal.entity.AdoptionApplication;
import com.xxx.animal.entity.Animal;
import com.xxx.animal.mapper.AdoptionApplicationMapper;
import com.xxx.animal.service.AdoptionApplicationService;
import com.xxx.animal.service.AnimalService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AdoptionApplicationServiceImpl extends ServiceImpl<AdoptionApplicationMapper, AdoptionApplication> implements AdoptionApplicationService {

    private final AnimalService animalService;

    public AdoptionApplicationServiceImpl(AnimalService animalService) {
        this.animalService = animalService;
    }

    @Override
    public Page<AdoptionApplication> listWithPetName(Page<AdoptionApplication> page, LambdaQueryWrapper<AdoptionApplication> wrapper) {
        // First query: get paginated applications
        Page<AdoptionApplication> result = this.page(page, wrapper);

        List<AdoptionApplication> records = result.getRecords();
        if (records == null || records.isEmpty()) {
            return result;
        }

        // Collect all animal IDs
        List<Long> animalIds = records.stream()
                .map(AdoptionApplication::getAnimalId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        if (animalIds.isEmpty()) {
            return result;
        }

        // Second query: batch fetch animals
        List<Animal> animals = animalService.listByIds(animalIds);
        Map<Long, String> nameMap = animals.stream()
                .collect(Collectors.toMap(Animal::getId, Animal::getName, (a, b) -> a));

        // Set animalName on each record
        for (AdoptionApplication record : records) {
            if (record.getAnimalId() != null) {
                record.setAnimalName(nameMap.get(record.getAnimalId()));
            }
        }

        return result;
    }
}
