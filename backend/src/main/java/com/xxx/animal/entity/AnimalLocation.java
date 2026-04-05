package com.xxx.animal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("animal_location")
public class AnimalLocation {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long animalId;
    
    private BigDecimal latitude;
    
    private BigDecimal longitude;
    
    private LocalDateTime recordTime;
}
