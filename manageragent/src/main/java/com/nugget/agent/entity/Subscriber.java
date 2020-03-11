package com.nugget.agent.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName Subscriber
 * @Description: 接收者资源
 * @Author 米粒儿
 * @Date 2019/9/18
 * @Version V1.0
 **/
@Data
@Builder
@NoArgsConstructor
public class Subscriber implements Serializable {

    private static final long serialVersionUID = 947594710495432423L;

    private String serverName;  //接收者名称
    private int count; //拿取数

    public Subscriber(String serverName, int count) {
        this.serverName = serverName;
        this.count = count;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"serverName\":\"")
                .append(serverName).append('\"');
        sb.append(",\"count\":")
                .append(count);
        sb.append('}');
        return sb.toString();
    }
}
