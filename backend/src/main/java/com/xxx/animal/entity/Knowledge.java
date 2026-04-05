package com.xxx.animal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("knowledge")
public class Knowledge {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String content;
    private Integer sortOrder;
}
