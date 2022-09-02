package ru.sbrf.efx.zeromqtest.var1X;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZThread;
import ru.sbrf.efx.zeromqtest.dto.Securities;
import ru.sbrf.efx.zeromqtest.dto.Subscribes;
import ru.sbrf.efx.zeromqtest.service.SecuritiesGenerator;
import ru.sbrf.efx.zeromqtest.service.SubscribesService;

import static zmq.ZMQ.ZMQ_DONTWAIT;

public class ZmqXPublisher implements ZThread.IDetachedRunnable {

    public static final String CONNECTION_STRING = "tcp://*:5556";

    private ZMQ.Context context;
    private ZMQ.Socket publisher;
    private String connection;
    private int i;

    private SecuritiesGenerator securitiesGenerator = new SecuritiesGenerator();

    private SubscribesService subscribesService = new SubscribesService();

    public static void main(String... args) {
        new ZmqXPublisher(CONNECTION_STRING).run(null);
    }

    public ZmqXPublisher(String connection) {
        this.connection = connection;
    }


    @Override
    public void run(Object[] args) {
        open();
        while (!Thread.currentThread().isInterrupted()) {
            Securities.Security security = securitiesGenerator.newSecurity();
            System.out.println("> Send:\n" + security);
            publisher.send(security.getSecId(), zmq.ZMQ.ZMQ_SNDMORE);
            publisher.send(security.toByteArray());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            byte[] msg = publisher.recv(ZMQ_DONTWAIT);
            if (msg != null){
                Subscribes.Subscribe subscribe = toSubscribe(msg);
                if (subscribe != null) {
                    System.out.println("< Receive:\n" + subscribe);
                }
            }
        }
        close();
    }


    public void open() {
        context = ZMQ.context(1);
//        publisher = context.socket(SocketType.PUB);
        publisher = context.socket(SocketType.XPUB);
        publisher.bind(connection);
//        publisher.connect(connection);
    }

    public void close() {
        publisher.close();
        context.term();
    }

    private Subscribes.Subscribe toSubscribe(byte[] content) {
        return subscribesService.toSubscribe(content);
    }
}
