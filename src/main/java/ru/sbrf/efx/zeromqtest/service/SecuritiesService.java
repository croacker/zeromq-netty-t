package ru.sbrf.efx.zeromqtest.service;

import com.google.protobuf.InvalidProtocolBufferException;
import ru.sbrf.efx.zeromqtest.dto.Securities;
import ru.sbrf.efx.zeromqtest.util.Result;

public class SecuritiesService {

    public Securities.Security toSecurity(byte[] content) {
        Securities.Security security = null;
        try {
            security = Securities.Security.parseFrom(content);
        } catch (InvalidProtocolBufferException e) {
            System.out.println("Error unmarshal: " + content);
            //e.printStackTrace();
        }
        return security;
    }

}
