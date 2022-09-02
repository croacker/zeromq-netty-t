package ru.sbrf.efx.zeromqtest.var1;

import com.google.protobuf.InvalidProtocolBufferException;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZThread;
import ru.sbrf.efx.zeromqtest.dto.Securities;
import ru.sbrf.efx.zeromqtest.service.SecuritiesService;

public class ZmqSubscriber implements ZThread.IDetachedRunnable {

    public static final String CONNECTION_STRING = "tcp://localhost:5556";

    private String connection;
    private ZMQ.Context context;
    private ZMQ.Socket subscriber;

    private SecuritiesService securitiesService = new SecuritiesService();


    public static void main(String... args) {
        new ZmqSubscriber(CONNECTION_STRING).run(null);
    }


    public ZmqSubscriber(String connection) {
        this.connection = connection;
    }


    @Override
    public void run(Object[] args) {
        open();

        ZMQ.Poller poller = context.poller(1);
        poller.register(subscriber, ZMQ.Poller.POLLIN);
        while (!Thread.currentThread().isInterrupted()) {
            int count = poller.poll(-1);
            if (poller.pollin(0)) {
//                String topic = subscriber.recvStr();
//                System.out.println("For topic:" + topic);
//                byte[] content = subscriber.recv();
//                Securities.Security msg = toSecurity(content);
//                System.out.println(msg);
                String topic = subscriber.recvStr();
                System.out.println(topic);
                byte[] content = subscriber.recv();
                System.out.println(content);
            }
        }
        close();
    }

    private Securities.Security toSecurity(byte[] content) {
        return securitiesService.toSecurity(content);
    }


    private void open() {
        context = ZMQ.context(1);
        subscriber = context.socket(SocketType.SUB);
        subscriber.connect(connection);
        subscriber.subscribe("".getBytes());
//        subscriber.subscribe("JPY/RUB".getBytes());
//        subscriber.subscribe("GBP/RUB".getBytes());
    }


    void close() {
        subscriber.close();
        context.close();
    }

}
