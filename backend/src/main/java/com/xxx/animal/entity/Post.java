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
@TableName("post")
public class Post {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String images;
    private String location;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    @TableField(exist = false)
    private String username;
    @TableField(exist = false)
    private String avatar;
    @TableField(exist = false)
    private List<Comment> comments;
}
