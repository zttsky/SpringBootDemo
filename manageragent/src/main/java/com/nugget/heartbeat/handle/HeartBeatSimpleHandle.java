package com.nugget.heartbeat.handle;


import com.nugget.agent.serivice.ExplorerImpl;
import com.nugget.heartbeat.entity.CustomProtocol;
import com.nugget.heartbeat.util.NettySocketHolder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName HeartBeatSimpleHandle
 * @Description:
 * @Author 米粒儿
 * @Date 2019/9/17
 * @Version V1.0
 **/
@Component
@ChannelHandler.Sharable
public class HeartBeatSimpleHandle extends SimpleChannelInboundHandler<CustomProtocol> {

    @Autowired
    ExplorerImpl explorerImpl;


    private CustomProtocol customProtocol;
    private final static Logger LOGGER = LoggerFactory.getLogger(HeartBeatSimpleHandle.class);
    //private static final ByteBuf HEART_BEAT = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(new CustomProtocol("fiell","接收者",0L,123456L).toString(), CharsetUtil.UTF_8));
    int readIdleTimes = 0;
    /**
     * 取消绑定
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        NettySocketHolder.remove((NioSocketChannel) ctx.channel());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
//            //向客户端发送消息
//            ctx.writeAndFlush(HEART_BEAT).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                readIdleTimes++;
                if(readIdleTimes>5){
                    explorerImpl.deletionStrategy(customProtocol);
                    LOGGER.info("hello mister DC 这节奏不要停");

                }
            }
        }
        //super.userEventTriggered(ctx, evt);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CustomProtocol customProtocol){
        this.customProtocol = customProtocol;
        LOGGER.info("收到customProtocol={}", customProtocol.toString());
        //我们调用writeAndFlush（Object）来逐字写入接收到的消息并刷新线路
        //ctx.writeAndFlush(customProtocol);
        //保存客户端与 Channel 之间的关系
        //NettySocketHolder.put(customProtocol.getId(), (NioSocketChannel) ctx.channel());
        readIdleTimes=0;
        //ctx.flush();
       try {
           explorerImpl.receiveStrategy(customProtocol);
       }catch (Exception ex){
           LOGGER.info("接收channelRead0函数报错，信息为："+ex.getMessage());
       }


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        //super.exceptionCaught(ctx, cause);
        //ctx.close();
        System.out.println("异常信息：\r\n" + cause.getMessage());
    }

    /**
     *  客户端关闭后
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        //todo 网络断开或宕机
        LOGGER.info("宕机为"+customProtocol.getIp());
    }
}
