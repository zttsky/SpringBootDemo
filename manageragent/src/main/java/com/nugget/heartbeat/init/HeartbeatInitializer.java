package com.nugget.heartbeat.init;

import com.nugget.heartbeat.encode.HeartbeatDecoder;
import com.nugget.heartbeat.handle.HeartBeatSimpleHandle;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


/**
 * @ClassName HeartbeatInitializer
 * @Description:
 * @Author 米粒儿
 * @Date 2019/9/17
 * @Version V1.0
 **/
@Component
public class HeartbeatInitializer extends ChannelInitializer<SocketChannel> {
    @Autowired
    HeartBeatSimpleHandle heartBeatSimpleHandle;

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        channel.pipeline()
                .addLast(new IdleStateHandler(1, 0, 0,TimeUnit.SECONDS))
                //1秒没有收到消息 将IdleStateHandler 添加到 ChannelPipeline 中
                .addLast(new HeartbeatDecoder())
                .addLast(heartBeatSimpleHandle);
    }
}
