package com.xxx.animal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("clue")
public class Clue {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String content;
    private String location;
    private String contact;
    private String image;
    private Integer status; // 0: Pending, 1: Processed
    private Long userId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
