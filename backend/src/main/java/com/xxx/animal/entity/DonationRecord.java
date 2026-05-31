package com.xxx.animal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("donation_record")
public class DonationRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String date;
    private String donor;
    private String item;
    private String quantity;
    private String unit;
    private String remark;
    private Integer sortOrder;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
