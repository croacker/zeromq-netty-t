package ru.sbrf.efx.zeromqtest.service;

import ru.sbrf.efx.zeromqtest.dto.Securities;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SecuritiesGenerator {

    private AtomicInteger idGenerator = new AtomicInteger(0);

    private List<String> secIds = List.of("CAD/RUB", "CHF/RUB", "GBP/RUB", "JPY/RUB", "TRY/RUB", "USD/RUB");

    public Securities.Security newSecurity(){
        return Securities.Security.newBuilder()
                .setId(idGenerator.incrementAndGet())
                .setSecId(randomSecId())
                .setRate(randomRate())
                .build();
    }

    public Securities.Security newSecurity(String secId){
        return Securities.Security.newBuilder()
                .setId(idGenerator.incrementAndGet())
                .setSecId(secId)
                .setRate(randomRate())
                .build();
    }

    public String randomSecId(){
        return secIds.get(getRandomInt(0, 5));
    }

    private double randomRate(){
        return getRandomDouble(0.0, 99.99999);
    }

    private int getRandomInt(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    private double getRandomDouble(double min, double max) {
        return (Math.random() * (max - min)) + min;
    }
}
