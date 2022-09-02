package ru.sbrf.efx.zeromqtest.var1X;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZThread;

import java.nio.ByteBuffer;

public class ZmqXSubscriber2 implements ZThread.IDetachedRunnable {

    public static final String CONNECTION_STRING = "tcp://localhost:5556";

    private String connection;
    private ZMQ.Context context;
    private ZMQ.Socket subscriber;

    private int i;

    public static void main(String... args) {
        new ZmqXSubscriber2(CONNECTION_STRING).run(null);
    }


    public ZmqXSubscriber2(String connection) {
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
                String content = subscriber.recvStr();
                System.out.println(content);
            }
            String msg = "Message from subscriber:" + i++;
            subscriber.send(msg);
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

}
