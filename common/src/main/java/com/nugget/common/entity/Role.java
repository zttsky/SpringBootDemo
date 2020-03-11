package com.nugget.common.entity;

/**
 * @ClassName Role
 * @Description: 数据承载容器类
 * @Author 米粒儿
 * @Date 2019/9/18
 * @Version V1.0
 **/
public enum Role {
    PROMULGATOR(0, "发布者"),
    SUBSCRIBER(100, "订阅者");

    Role(int number, String description) {
        this.code = number;
        this.description = description;
    }
    private int code;
    private String description;

    public int getCode() {
        return code;
    }
    public String getDescription() {
        return description;
    }
}