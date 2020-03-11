package com.nugget.agent.serivice;

import com.nugget.agent.entity.Subscriber;
import com.nugget.common.entity.Address;
import com.nugget.heartbeat.entity.CustomProtocol;

import java.util.List;

/**
 * @ClassName IExplorer
 * @Description: 数据承载容器类
 * @Author 米粒儿
 * @Date 2019/9/19
 * @Version V1.0
 **/
public interface IExplorer {
    /**
     * 删除策略
     * @param customProtocol
     */
    public  void deletionStrategy(CustomProtocol customProtocol);
    /**
     * 接收处理策略
     */
    public void receiveStrategy(CustomProtocol customProtocol);
    /**
     * 根据channel(key)查找最小条目数
     * @param channel
     * @return int
     */
    public int searchCount(String channel);
    /**
     * 删除指定容器中 指定通道 指定的接收者
     * @param list
     * @param name
     * @return int
     */
    public void delete(List<Subscriber> list, String name);
    /**
     *  注册资源
     * @param customProtocol
     * @return Address
     */
    public Address registerResource(CustomProtocol customProtocol);

    /**
     * 测试连接
     */
    public boolean testConnection(String ip);
    /**
     *  IP获取策略
     * @param ip 调用方ip
     * @return
     */
    public String ipPolicy(String ip);
    /**
     * 正则匹配IP正确性
     * @param ip
     * @return
     */
    public boolean isMatching(String ip);
    /**
     * 获取redisResource到List中
     */
    public void getRedisResource();
}
