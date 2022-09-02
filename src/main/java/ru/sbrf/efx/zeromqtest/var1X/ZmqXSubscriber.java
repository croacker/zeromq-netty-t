package ru.sbrf.efx.zeromqtest.var1X;

import com.google.protobuf.InvalidProtocolBufferException;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZThread;
import ru.sbrf.efx.zeromqtest.dto.Securities;
import ru.sbrf.efx.zeromqtest.dto.Subscribes;
import ru.sbrf.efx.zeromqtest.service.SecuritiesService;
import ru.sbrf.efx.zeromqtest.service.SubscribesGenerator;
import zmq.socket.pubsub.Sub;

import java.nio.ByteBuffer;

public class ZmqXSubscriber implements ZThread.IDetachedRunnable {

    public static final String CONNECTION_STRING = "tcp://localhost:5556";

    private String connection;
    private ZMQ.Context context;
    private ZMQ.Socket subscriber;

    private int i;

    private SubscribesGenerator subscribeGenerator = new SubscribesGenerator();

    private SecuritiesService securitiesService = new SecuritiesService();

    public static void main(String... args) {
        new ZmqXSubscriber(CONNECTION_STRING).run(null);
    }


    public ZmqXSubscriber(String connection) {
        this.connection = connection;
    }


    @Override
    public void run(Object[] args) {
        open();

        ZMQ.Poller poller = context.poller(1);
        poller.register(subscriber, ZMQ.Poller.POLLIN);
        byte[] b = new byte[]{1};
        ByteBuffer bb = ByteBuffer.wrap(b);
        subscriber.sendByteBuffer(bb, 0);
        while (!Thread.currentThread().isInterrupted()) {
            poller.poll();
            if (poller.pollin(0)) {
                String topic = subscriber.recvStr();
                System.out.println("< Receive for topic:" + topic);
                byte[] content = subscriber.recv();
                Securities.Security security = toSecurity(content);
                if (security != null){
                    System.out.println(security);
                }
            }
            Subscribes.Subscribe subscribe = subscribeGenerator.newSubscribe("x-sub-1");
            System.out.println("> Send:\n" + subscribe);
            subscriber.send(subscribe.toByteArray());
        }

        close();
    }

    private void open() {
        context = ZMQ.context(1);
        subscriber = context.socket(SocketType.XSUB);
        subscriber.connect(connection);
    }


    void close() {
        subscriber.close();
        context.close();
    }

    private Securities.Security toSecurity(byte[] content) {
        return securitiesService.toSecurity(content);
    }

}
