package ru.sbrf.efx.zeromqtest.var1;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZThread;
import ru.sbrf.efx.zeromqtest.dto.Securities;
import ru.sbrf.efx.zeromqtest.dto.Subscribes;
import ru.sbrf.efx.zeromqtest.service.SecuritiesGenerator;

import static zmq.ZMQ.ZMQ_DONTWAIT;

public class ZmqPublisher implements ZThread.IDetachedRunnable {

    public static final String CONNECTION_STRING = "tcp://*:5556";

    private ZMQ.Context context;
    private ZMQ.Socket publisher;
    private String connection;
    private int i;

    private SecuritiesGenerator securitiesGenerator = new SecuritiesGenerator();

    public static void main(String... args) {
        new ZmqPublisher(CONNECTION_STRING).run(null);
    }

    public ZmqPublisher(String connection) {
        this.connection = connection;
    }


    @Override
    public void run(Object[] args) {
        open();
        while (!Thread.currentThread().isInterrupted()) {
            Securities.Security security = securitiesGenerator.newSecurity();
//            System.out.println("> Send:\n" + security);
//            publisher.send(security.getSecId(), zmq.ZMQ.ZMQ_SNDMORE);
            publisher.send(security.toByteArray());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        close();
    }


    public void open() {
        context = ZMQ.context(1);
        publisher = context.socket(SocketType.PUB);
        publisher.bind(connection);
    }


    public void close() {
        publisher.close();
        context.term();
    }

    private byte[] newMessage(int i){
        return Securities.Security.newBuilder()
                .setId(i)
                .setSecId("CAD/RUB")
                .setRate(48.38630)
                .build()
                .toByteArray();
    }
}
