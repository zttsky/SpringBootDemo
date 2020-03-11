package com.nugget.agent.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName CustomProtocol
 * @Description: 资源
 * @Author 米粒儿
 * @Date 2019/9/18
 * @Version V1.0
 **/
@Data
@Builder
@NoArgsConstructor
public class Resource implements Serializable {

    private static final long serialVersionUID = 290429819350651974L;

    private int deleteCount; //删除数
    private String type;  //类型
    private String serverName;  //服务名称
    private int count; //条目最小数
    private String ip; //IP
    private String key; //通道
    private List<Subscriber> subscribers; //接收者集合

    public Resource(int deleteCount, String type, String serverName, int count, String ip, String key, List<Subscriber> subscribers) {
        this.deleteCount = deleteCount;
        this.type = type;
        this.serverName = serverName;
        this.count = count;
        this.ip = ip;
        this.key = key;
        this.subscribers = subscribers;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"deleteCount\":")
                .append(deleteCount);
        sb.append(",\"type\":\"")
                .append(type).append('\"');
        sb.append(",\"serverName\":\"")
                .append(serverName).append('\"');
        sb.append(",\"count\":")
                .append(count);
        sb.append(",\"ip\":\"")
                .append(ip).append('\"');
        sb.append(",\"key\":\"")
                .append(key).append('\"');
        sb.append(",\"subscribers\":")
                .append(subscribers);
        sb.append('}');
        return sb.toString();
    }
}
