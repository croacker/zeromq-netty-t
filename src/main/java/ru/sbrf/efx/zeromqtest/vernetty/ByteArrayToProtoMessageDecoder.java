package ru.sbrf.efx.zeromqtest.vernetty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import ru.sbrf.efx.zeromqtest.dto.Securities;

import java.util.List;

public class ByteArrayToProtoMessageDecoder extends MessageToMessageDecoder<byte[]> {
    @Override
    protected void decode(ChannelHandlerContext context, byte[] bytes, List<Object> out) throws Exception {
        Securities.Security security = Securities.Security.parseFrom(bytes);
        out.add(security);
    }
}
