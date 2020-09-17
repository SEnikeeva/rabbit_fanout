import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class DocsProducer {
    // есть EXCHANGE - images НЕ ОЧЕРЕДЬ
    private final static String EXCHANGE_NAME = "docs";
    // тип FANOUT
    private final static String EXCHANGE_TYPE = "fanout";

    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            // создаем exchange
            channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE);

            Scanner sc = new Scanner(System.in);
            String nextline;
            while (!(nextline = sc.nextLine()).equals("q") ) {
                channel.basicPublish(EXCHANGE_NAME, "",null, nextline.getBytes());
            }
        } catch (IOException | TimeoutException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
