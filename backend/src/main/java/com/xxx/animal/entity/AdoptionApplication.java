package com.xxx.animal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("adoption_application")
public class AdoptionApplication {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long animalId;
    private Long userId;
    private String applicantName;
    private Integer age;
    private String job;
    private String income;
    private String address;
    private String phone;
    private String wechat;
    private String housing;
    private String experience;
    private String familyMembers;
    private String reason;
    // 0-待审核 1-通过 2-拒绝
    private Integer status;
    private String reviewNote;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String animalName;
}
