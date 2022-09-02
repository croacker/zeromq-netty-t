package ru.sbrf.efx.zeromqtest.service;

import com.google.protobuf.InvalidProtocolBufferException;
import ru.sbrf.efx.zeromqtest.dto.Subscribes;

public class SubscribesService {

    public Subscribes.Subscribe toSubscribe(byte[] content) {
        Subscribes.Subscribe subscribe = null;
        try {
            subscribe = Subscribes.Subscribe.parseFrom(content);
        } catch (InvalidProtocolBufferException e) {
            System.out.println("Error unmarshal: " + content);
        }
        return subscribe;
    }

}
