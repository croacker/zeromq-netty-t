package ru.sbrf.efx.zeromqtest.var1;

import com.google.protobuf.InvalidProtocolBufferException;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZThread;
import ru.sbrf.efx.zeromqtest.dto.Securities;
import ru.sbrf.efx.zeromqtest.service.SecuritiesService;

public class ZmqSubscriber2 implements ZThread.IDetachedRunnable {

    public static final String CONNECTION_STRING = "tcp://localhost:5556";

    private String connection;
    private ZMQ.Context context;
    private ZMQ.Socket subscriber;

    private SecuritiesService securitiesService = new SecuritiesService();


    public static void main(String... args) {
        new ZmqSubscriber2(CONNECTION_STRING).run(null);
    }


    public ZmqSubscriber2(String connection) {
        this.connection = connection;
    }


    @Override
    public void run(Object[] args) {
        open();

        ZMQ.Poller poller = context.poller(1);
        poller.register(subscriber, ZMQ.Poller.POLLIN);
        while (!Thread.currentThread().isInterrupted()) {
            poller.poll(100);
            if (poller.pollin(0)) {
                String topic = subscriber.recvStr();
                System.out.println("For topic:" + topic);
                byte[] content = subscriber.recv();
                Securities.Security msg = toSecurity(content);
                System.out.println(msg);
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
        subscriber.subscribe("update".getBytes());
    }


    void close() {
        subscriber.close();
        context.close();
    }

}
