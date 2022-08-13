package cn.xm.netty.c3;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @author 夏明
 * @version 1.0
 */
@Slf4j
public class EventLoopClient {
    public static void main(String[] args) throws InterruptedException {
        // 带有 future promise 的类型都是配合异步方法使用 用来处理结果
        ChannelFuture channelFuture = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override // 在连接建立后被调用
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                // 1. 连接到服务器
                // 异步非阻塞 main 发起了调用 真正执行 connect 的是 nio线程
                .connect(new InetSocketAddress("localhost", 8080));

        // 2.1 使用 sync 方法同步处理结束
//        channelFuture.sync(); // 阻塞住当前线程 直到nio线程连接已经建立完成
//        // 如果不加 sync 无阻塞向下获取channel 出错
//        Channel channel = channelFuture.channel();
//        // 2. 向服务器发送数据
//        channel.writeAndFlush("hello world");

        // 2.2 使用 addListener(回调对象) 方法异步处理结果
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            // 在 nio 线程连接建立好后, 会调用 operationComplete
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                Channel channel = channelFuture.channel();
                log.debug("{}", channel);
                channel.writeAndFlush("hello, world");
            }
        });
    }
}
