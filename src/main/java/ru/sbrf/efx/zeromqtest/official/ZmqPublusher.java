package ru.sbrf.efx.zeromqtest.official;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.Random;

public class ZmqPublusher {

    public static void main(String[] args) throws InterruptedException {
        new ZmqPublusher()
                .start();
    }

    private void start() throws InterruptedException {
        try (ZContext context = new ZContext()) {
            ZMQ.Socket publisher = context.createSocket(SocketType.PUB);
            publisher.bind("tcp://*:5556");

            //  Ensure subscriber connection has time to complete
            Thread.sleep(1000);

            //  Send out all 1,000 topic messages
            int topicNbr;
            for (topicNbr = 0; topicNbr < 1000; topicNbr++) {
                String data = String.format("%03d", topicNbr);
//                publisher.send(data, ZMQ.SNDMORE);
                publisher.send(data);
                publisher.send("Save Roger");
                System.out.println("Send: " + data);
            }
            //  Send one random update per second
            Random rand = new Random(System.currentTimeMillis());
            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(1000);
                String data = String.format("%03d", rand.nextInt(1000));
//                publisher.send(data, ZMQ.SNDMORE);
                publisher.send(data);
                publisher.send("Off with his head!");
                System.out.println("Send: " + data);
            }
        }
    }

}
