package ru.sbrf.efx.zeromqtest.vernetty;

import com.spotify.netty4.handler.codec.zmtp.ZMTPCodec;
import com.spotify.netty4.handler.codec.zmtp.ZMTPHandshakeSuccess;
import com.spotify.netty4.handler.codec.zmtp.ZMTPMessage;
import com.spotify.netty4.handler.codec.zmtp.ZMTPSocketType;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import ru.sbrf.efx.zeromqtest.dto.Securities;
import ru.sbrf.efx.zeromqtest.service.SecuritiesService;

import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NettyClientSubscriber {

    public static final int PORT = 5556;

    public static final String HOST = "127.0.0.1";

    private SecuritiesService securitiesService = new SecuritiesService();

    public static void main(String[] args) {
        try {
            new NettyClientSubscriber().startClient();
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void startClient() throws InterruptedException {
        new Bootstrap()
                .group(new NioEventLoopGroup(1))
                .channel(NioSocketChannel.class)
                .handler(createChannelInitializer())
                .connect(HOST, PORT)
                .channel().closeFuture()
                .sync();
    }

    private ChannelHandler createChannelInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline()
                        .addLast(ZMTPCodec.of(ZMTPSocketType.SUB))
                        .addLast(new ZMTPMessageToByteArrayDecoder())
                        .addLast(new ByteArrayToProtoMessageDecoder())
                        .addLast(channelHandler(ch));
            }
        };
    }

    private ChannelHandler channelHandler(SocketChannel ch) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(
                () -> {
                    byte[] b = new byte[]{1};
                    ByteBuf[] bb = new ByteBuf[]{Unpooled.copiedBuffer(b)};
                    ZMTPMessage message = ZMTPMessage.from(bb);
                    ch.writeAndFlush(message);
                },
                10,
                TimeUnit.SECONDS
        );
        return new SimpleChannelInboundHandler() {

            @Override
            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                super.channelActive(ctx);
                log("New connection " + ctx.channel().remoteAddress());
                try {
                    super.channelActive(ctx);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

//            @Override
//            protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) {
//                byte[] bytes = new byte[((ZMTPMessage) o).frame(0).readableBytes()];
//                ((ZMTPMessage) o).frame(0).readBytes(bytes);
//                Securities.Security security = toSecurity(bytes);
//                log(security);
//            }

            @Override
            protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) {
//                byte[] bytes = (byte[]) o;
//                ((ZMTPMessage) o).frame(0).readBytes(bytes);
                Securities.Security security = (Securities.Security) o;
                log(security);
            }

//            @Override
//            public void channelRead(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
//                super.channelRead(channelHandlerContext, o);
//                if(o instanceof ZMTPMessage) {
//                    ZMTPMessage message = (ZMTPMessage) o;
//                    log(message);
//                }
//            }

            @Override
            public void userEventTriggered(final ChannelHandlerContext ctx, final Object evt)
                    throws Exception {
                super.userEventTriggered(ctx, evt);
                log(evt);
                if (evt instanceof ZMTPHandshakeSuccess) {
                    log("ZMTPHandshakeSuccess");
//                    byte[] b = new byte[]{1};
//                    ByteBuf[] bb = new ByteBuf[]{Unpooled.copiedBuffer(b)};
//                    ZMTPMessage message = ZMTPMessage.from(bb);
//                    ch.writeAndFlush(message);
                }
            }

            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                cause.printStackTrace();
                ctx.close();
            }
        };
    }

    private void log(Object msg){
        System.out.println(msg);
    }

    private Securities.Security toSecurity(byte[] content) {
        return securitiesService.toSecurity(content);
    }
}
