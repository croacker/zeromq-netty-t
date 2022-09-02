package ru.sbrf.efx.zeromqtest.official;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class ZmqPublusher2 {

    public static void main(String[] args) throws InterruptedException {
        new ZmqPublusher2()
                .start();
    }

    private void start() throws InterruptedException {
        try (ZContext context = new ZContext()) {
            ZMQ.Socket publisher = context.createSocket(SocketType.PUB);
            publisher.bind("tcp://localhost:5556");

            //  Ensure subscriber connection has time to complete
            Thread.sleep(1000);

            long counter = 0;
            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(1000);
                String data = "Test message-" + counter;
                publisher.send(data);
                System.out.println("Send: " + data);
                counter++;
            }
        }
    }

}
