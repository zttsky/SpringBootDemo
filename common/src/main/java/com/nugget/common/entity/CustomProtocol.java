package com.nugget.common.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

/**
 * @ClassName CustomProtocol
 * @Description:
 * @Author 米粒儿
 * @Date 2019/9/17
 * @Version V1.0
 **/
@Configuration
@Data
@Builder
@NoArgsConstructor
public class CustomProtocol implements Serializable {

    private String key; //通道
    private int type;  //类型（发送者、接收者） 在服务中可快速定位，更改条目数（发送、接收的数量）
    private String name;  //服务名称
    private int count; //条目数(最小值)
    private Long id;//通道id
    private String ip;//ip(就近原则)

    private static final long serialVersionUID = 290429819350651974L;

    public CustomProtocol(String key, int type, String name, int count, Long id, String ip) {
        this.key = key;
        this.type = type;
        this.name = name;
        this.count = count;
        this.id = id;
        this.ip = ip;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"key\":\"")
                .append(key).append('\"');
        sb.append(",\"type\":")
                .append(type);
        sb.append(",\"name\":\"")
                .append(name).append('\"');
        sb.append(",\"count\":")
                .append(count);
        sb.append(",\"id\":")
                .append(id);
        sb.append(",\"ip\":\"")
                .append(ip).append('\"');
        sb.append('}');
        return sb.toString();
    }
}
