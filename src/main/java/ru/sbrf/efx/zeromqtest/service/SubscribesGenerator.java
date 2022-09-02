package ru.sbrf.efx.zeromqtest.service;

import ru.sbrf.efx.zeromqtest.dto.Subscribes;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SubscribesGenerator {

    private AtomicInteger idGenerator = new AtomicInteger(0);

    private List<String> topicNames = List.of("CAD/RUB", "CHF/RUB", "GBP/RUB", "JPY/RUB", "TRY/RUB", "USD/RUB");

    public Subscribes.Subscribe newSubscribe(String serviceName){
        return Subscribes.Subscribe.newBuilder()
                .setId(idGenerator.incrementAndGet())
                .setServiceName(serviceName)
                .setTopicName(randomTopicName())
                .build();
    }

    public Subscribes.Subscribe newSubscribe(String serviceName, String topicName){
        return Subscribes.Subscribe.newBuilder()
                .setId(idGenerator.incrementAndGet())
                .setServiceName(serviceName)
                .setTopicName(topicName)
                .build();
    }

    public String randomTopicName(){
        return topicNames.get(getRandomInt(0, 5));
    }

    private int getRandomInt(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

}
