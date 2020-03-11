package com.nugget.common.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName Address
 * @Description: 数据承载容器类
 * @Author 米粒儿
 * @Date 2019/9/19
 * @Version V1.0
 **/
@Configuration
@Data
@Builder
@NoArgsConstructor
public class Address {
    private String ip;
    private int count;

    public Address(String ip, int count) {
        this.ip = ip;
        this.count = count;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"ip\":\"")
                .append(ip).append('\"');
        sb.append(",\"count\":")
                .append(count);
        sb.append('}');
        return sb.toString();
    }
}
