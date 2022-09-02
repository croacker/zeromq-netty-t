package ru.sbrf.efx.zeromqtest.official;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class ZmqSubscriber {

    public static void main(String[] args) {
        new ZmqSubscriber()
                .start();
    }

    private void start() {
        try (ZContext context = new ZContext()) {
            ZMQ.Socket subscriber = context.createSocket(SocketType.SUB);
            boolean connectResult = subscriber.connect("tcp://localhost:5556");
            System.out.println("Connection result:" + connectResult);

//            Random rand = new Random(System.currentTimeMillis());
//            String subscription = String.format("%03d", rand.nextInt(1000));
//            subscriber.subscribe(subscription.getBytes(ZMQ.CHARSET));

            while (true) {
//                String topic = subscriber.recvStr();
//                if (topic == null)
//                    break;
                String data = subscriber.recvStr();
//                assert (topic.equals(subscription));
                System.out.println(data);
            }
        }
    }

}
