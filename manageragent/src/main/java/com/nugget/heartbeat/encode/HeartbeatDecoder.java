package com.nugget.heartbeat.encode;

import com.google.gson.Gson;
import com.nugget.heartbeat.entity.CustomProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @ClassName HeartbeatDecoder
 * @Description: 服务端解码器
 * @Author 米粒儿
 * @Date 2019/9/17
 * @Version V1.0
 **/
public class HeartbeatDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        long id = byteBuf.readLong();
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        CustomProtocol customProtocol = new CustomProtocol();
        String content = new String(bytes);
        Gson gson = new Gson();
        list.add(gson.fromJson(content,CustomProtocol.class));
    }
}
