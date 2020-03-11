package com.nugget.agent.serivice;

import com.nugget.agent.entity.Resource;
import com.nugget.agent.entity.Subscriber;
import com.nugget.common.entity.Address;
import com.nugget.common.entity.Role;
import com.nugget.common.util.RedisConfig;
import com.nugget.heartbeat.entity.CustomProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName Explorer
 * @Description: 资源管理器
 * @Author 米粒儿
 * @Date 2019/9/18
 * @Version V1.0
 **/
@Service
public class ExplorerImpl implements IExplorer{

    private  Map<String,Resource> resourceContainer = new HashMap<>();  //资源存储容器
    private  ArrayList addressList = new ArrayList();

    private final static Logger LOGGER = LoggerFactory.getLogger(ExplorerImpl.class);


    /**
     * 未收到心跳 删除策略
     *
     * @param
     * @param
     * @return
     */
    public  void deletionStrategy(CustomProtocol customProtocol){
            if(resourceContainer.size()>0 && resourceContainer.isEmpty()){
                resourceContainer.forEach((key, value) -> {
                    if(customProtocol.getType()==(Role.PROMULGATOR.getCode())){
                        if(customProtocol.getKey().equals(key)){
                            if(customProtocol.getName().equals(value.getServerName())){
                                if(value.getCount()==value.getDeleteCount()){

                                    Connection(value.getIp()).delete(key);
                                    resourceContainer.remove(key);
                                }
                            }
                        }
                    }else if(customProtocol.getType()==Role.SUBSCRIBER.getCode()){
                        if(customProtocol.getKey().equals(key)){
                            value.setCount(searchCount(key));
                            delete(value.getSubscribers(),customProtocol.getName());
                        }
                    }
                });
            }

    }

    /**
     * 连接特定redis
     * @param str
     * @return
     */
    public RedisTemplate Connection(String str) {
        String[] arr = str.split(":");

        RedisConfig redisConfig = new RedisConfig(arr[0], Integer.parseInt(arr[1]), "");
//        RedisConfig redisConfig = new RedisConfig("192.168.1.120",6379,"");
        RedisTemplate redisTemplate = redisConfig.redisTemplate();
        return redisTemplate;
    }

    /**
     * 接收后删除策略
     */
    public void normalDeletionStrategy(int  tailer,Resource resource) {
        lRightPop(Connection(resource.getIp()),tailer,resource.getKey());
    }


    public void lRightPop(RedisTemplate redisTemplate,int tailer,String key){
        for(int i=0;i<tailer;i++){
            redisTemplate.opsForList().rightPop(key,1,TimeUnit.SECONDS);
        }
    }
    /**
     * 接收处理策略
     */
    public void receiveStrategy(CustomProtocol customProtocol) {
        if (customProtocol.getType() == (Role.SUBSCRIBER.getCode())) {
            LOGGER.info("现在长度为："+resourceContainer.size());
            resourceContainer.forEach((key,value)->{
                if(value.getSubscribers().size()==0){
                    Subscriber subscriber =new Subscriber(customProtocol.getName(),customProtocol.getCount());
                    value.getSubscribers().add(subscriber);
                }
                value.getSubscribers().forEach(param->{
                    if(param.getServerName().equals(customProtocol.getName())){
                        param.setCount(customProtocol.getCount());
                        value.setCount(searchCount(key));
                        int temp=value.getCount()-value.getDeleteCount();
                        if(temp>0){
                            normalDeletionStrategy(temp,value);
                            value.setDeleteCount(value.getDeleteCount()+temp);
                        }
                    }
                });
            });
        }
    }
    /**
     * 根据channel(key)查找最小条目数
     * @param channel
     * @return int
     */

    public int searchCount(String channel){
         List<Integer> countList = new ArrayList();
        resourceContainer.forEach((key,value)->{
            if(channel!=null && channel.length()>0){
                if(channel.equals(key)){
                    if (value.getSubscribers() != null && value.getSubscribers().size() > 0 ) {
                        value.getSubscribers().forEach(entity->{
                            countList.add(entity.getCount());
                        });
                    } else {
                        countList.add(0);
                    }
                }
            }
        });
        return Collections.min(countList);
    }


    /**
     * 删除指定容器中 指定通道 指定的接收者
     * @param list
     * @param name
     * @return int
     */
    public void delete(List<Subscriber> list, String name){
        list.forEach(param->{
            if(param.getServerName().equals(name)){
               list.remove(param);
            }
        });
    }

    /**
     *  注册资源
     * @param customProtocol
     * @return Address
     */
    public Address registerResource(CustomProtocol customProtocol) {
        Address address = new Address();
        if(customProtocol!=null){
            //发布者
            if(customProtocol.getType()==Role.PROMULGATOR.getCode()){
                //已存在直接获取
                if(resourceContainer.get(customProtocol.getKey())!=null){
                    address.setIp(resourceContainer.get(customProtocol.getKey()).getIp());
                    address.setCount(resourceContainer.get(customProtocol.getKey()).getCount());
                    return address;
                }else{
                    //不存在创建新的
                    Resource resource = new Resource();
                    resource.setKey(customProtocol.getKey());
                    resource.setServerName(customProtocol.getName());
                    resource.setType(customProtocol.getKey());
                    resource.setDeleteCount(0);
                    resource.setSubscribers(new ArrayList<>());
                    String addressip = ipPolicy(customProtocol.getIp());
                    resource.setIp(addressip);
                    resourceContainer.put(customProtocol.getKey(),resource);
                    LOGGER.info("已经存储成功，长度为："+resourceContainer.size());
                    address.setIp(addressip);
                    address.setCount(0);
                    return address;
                }
            }
            if (customProtocol.getType()==Role.SUBSCRIBER.getCode()) {
                //订阅者
                if(resourceContainer.get(customProtocol.getKey())!=null){
                    Resource resource = resourceContainer.get(customProtocol.getKey());
                    List<Subscriber> subscriberList = resource.getSubscribers();
                    //已存在获取 ip 最小条数
                    if (subscriberList != null && subscriberList.size() > 0) {
                        address.setIp(resource.getIp());
                        address.setCount(resource.getCount());
                    } else {
                        //不存在 添加到订阅者列表中
                        subscriberList = new ArrayList<>();
                        Subscriber subscriber = new Subscriber(customProtocol.getName(),customProtocol.getCount());
                        subscriberList.add(subscriber);
                        address.setIp(resource.getIp());
                        address.setCount(resource.getCount());
                    }
                    return address;
                }else{
                    return null;
                }
            }
            return null;
        }else{
            return null;
        }
    }

    /**
     * 测试连接
     */
    public boolean testConnection(String ip) {
        if(isMatching(ip)){
            String [] str =ip.split(":");
            Jedis jedis=new Jedis(str[0],Integer.parseInt(str[1]));
            if(jedis.ping().equals("PONG")){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    /**
     *  IP获取策略
     * @param ip 调用方ip
     * @return
     */


    public String ipPolicy(String ip) {
        if(ip!=null){
            if(addressList!=null && addressList.size()>0){
             for(int i=0;i<addressList.size();i++){
                 if(ip.equals(addressList.get(i))){
                     if(testConnection(ip)){
                         return ip;
                     }
                 }
             }
             int random=(int)(Math.random()*addressList.size());
                if(testConnection(addressList.get(random).toString())){
                    return addressList.get(random).toString();
                }
            }
            return "";
        }
        return "";
    }

    /**
     * 正则匹配IP正确性
     * @param ip
     * @return
     */
    public boolean isMatching(String ip) {
        if(ip!=null && ip.length()>0){
            String [] arr = ip.split(":");
        String regex="^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
        Matcher p=Pattern.compile(regex).matcher(arr[0]);
            regex="([0-9]|[1-9]\\d{1,3}|[1-5]\\d{4}|6[0-4]\\d{4}|65[0-4]\\d{2}|655[0-2]\\d|6553[0-5])";
        Matcher p1=Pattern.compile(regex).matcher(arr[1]);
        if(p.matches() && p1.matches()){
            return true;
        }else{
           return false;
        }
        }else{
            return false;
        }
    }





    /**
     * 获取redisResource到List中
     */
    @PostConstruct
    public void getRedisResource() {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("ipConfig.properties");
        Properties p = new Properties();
        try {
            p.load(inputStream);
        } catch (IOException e1) {
            LOGGER.info("读取配置文件异常");
        }
        if(p.getProperty("redis.address")!=null && p.getProperty("redis.address").length()>0){
            String[] array = p.getProperty("redis.address").split(",");
            for (String s : array) {
                addressList.add(s);
            }
        }
    }


}
