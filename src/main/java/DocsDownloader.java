import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;


import java.io.IOException;

import java.util.concurrent.TimeoutException;
import util.PdfCreator;

public class DocsDownloader {

    private final static String EXCHANGE_NAME = "docs";
    private final static String EXCHANGE_TYPE = "fanout";

    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        PdfCreator pdfCreator = new PdfCreator();

        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            channel.basicQos(3);

            channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE);
            // создаем временную очередь со случайным названием
            String queue = channel.queueDeclare().getQueue();

            // привязали очередь к EXCHANGE_NAME
            channel.queueBind(queue, EXCHANGE_NAME, "");

            DeliverCallback deliverCallback = (consumerTag, message) -> {
                String data = new String(message.getBody());
                System.out.println("Start processing docs for " + data);

                try {

                    pdfCreator.createTemplate(consumerTag, data);
                    channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
                } catch (IOException e) {
                    System.err.println("FAILED");
                    channel.basicReject(message.getEnvelope().getDeliveryTag(), false);
                }
            };

            channel.basicConsume(queue, false, deliverCallback, consumerTag -> {});
        } catch (IOException | TimeoutException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
