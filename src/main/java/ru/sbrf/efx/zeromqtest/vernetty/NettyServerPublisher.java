package ru.sbrf.efx.zeromqtest.vernetty;

import com.spotify.netty4.handler.codec.zmtp.ZMTPCodec;
import com.spotify.netty4.handler.codec.zmtp.ZMTPMessage;
import com.spotify.netty4.handler.codec.zmtp.ZMTPSocketType;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import ru.sbrf.efx.zeromqtest.dto.Securities;
import ru.sbrf.efx.zeromqtest.service.SecuritiesGenerator;

import java.net.SocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NettyServerPublisher {

    public static final int PORT = 5556;

    private SecuritiesGenerator securitiesGenerator = new SecuritiesGenerator();

    public static void main(String[] args) {
        try {
            new NettyServerPublisher().startServer();
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void startServer() throws InterruptedException {
        new ServerBootstrap()
                .group(new NioEventLoopGroup(1), new NioEventLoopGroup(1))
                .channel(NioServerSocketChannel.class)
                .childHandler(createChannelInitializer())
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .bind(PORT).sync();
    }

    private ChannelHandler createChannelInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline()
                        .addLast(ZMTPCodec.of(ZMTPSocketType.PUB))
                        .addLast(channelHandler(ch));
            }
        };
    }

    private ChannelHandler channelHandler(SocketChannel ch) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(
                () -> {
                    Securities.Security security = securitiesGenerator.newSecurity();
                    byte[] topicArr = security.getSecId().getBytes();
                    ByteBuf[] bbTopic = new ByteBuf[]{Unpooled.copiedBuffer(topicArr)};
                    ZMTPMessage topic = ZMTPMessage.from(bbTopic);
                    ch.write(topic);

                    byte[] messageArr = security.toByteArray();
                    ByteBuf[] bbMessage = new ByteBuf[]{Unpooled.copiedBuffer(messageArr)};
                    ZMTPMessage message = ZMTPMessage.from(bbMessage);
                    ch.writeAndFlush(message);

                    log(security);
                },
                10,
                5,
                TimeUnit.SECONDS
        );

        return new SimpleChannelInboundHandler() {

            @Override
            public void channelActive(ChannelHandlerContext ctx) {
                log("New connection " + ctx.channel().remoteAddress());
                try {
                    super.channelActive(ctx);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) {
                log(o);
            }

            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                SocketAddress remoteAddress = ctx.channel().remoteAddress();
                log("Disconnected " + remoteAddress);
                ctx.close();
            }
        };
    }

    private void log(Object msg){
        System.out.println(msg);
    }

}
