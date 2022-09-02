package ru.sbrf.efx.zeromqtest.vernetty;

import com.spotify.netty4.handler.codec.zmtp.ZMTPMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class ZMTPMessageToByteArrayDecoder extends MessageToMessageDecoder<ZMTPMessage> {
    @Override
    protected void decode(ChannelHandlerContext context, ZMTPMessage msg, List<Object> out) throws Exception {
        for (int i = 0; i < msg.size(); i++){
            byte[] bytes = new byte[msg.frame(i).readableBytes()];
            msg.frame(0).readBytes(bytes);
            out.add(bytes);
        }
    }

}
