package com.xxx.animal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("animal")
public class Animal {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String animalNo; // Unique Business Number
    
    private String name;
    
    private String category; // DOG, CAT

    private String sex; // MALE, FEMALE

    private String bodySize; // SMALL, MEDIUM, LARGE
    
    private Integer age;
    
    private String avatar;
    
    private Boolean isSterilized;
    
    private String activityScope;
    
    private Integer status; // 1: Available, 0: Adopted
    
    private String description;
    
    private String detailContent; // Rich text detail
    
    private BigDecimal latitude;
    
    private BigDecimal longitude;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
    
    @TableField(exist = false)
    private List<AnimalLocation> locations;
}
