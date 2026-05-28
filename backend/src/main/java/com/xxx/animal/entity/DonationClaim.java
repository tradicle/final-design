package com.xxx.animal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("donation_claim")
public class DonationClaim {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String needName;
    private String needGap;
    private String quantity;
    private String contactName;
    private String phone;
    private String wechat;
    private String pickupDate;
    private String remark;
    // 0-待确认 1-已通过 2-已拒绝
    private Integer status;
    private String reviewNote;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
