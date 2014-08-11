package app;

import com.rabbitmq.client.*;
import utils.Constants;
import utils.Logger;

import java.io.IOException;

public class Server {
    private Connection connection;
    private Channel channel;
    private QueueingConsumer consumer;

    public Server() {
        try {
            createConnection();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    private void createConnection() throws IOException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(Constants.HOST);
        // factory.setPort(Constants.PORT);

        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.queueDeclare(Constants.QUERIES_QUEUE_NAME, false, false, false, null);
        channel.basicQos(1);

        consumer = new QueueingConsumer(channel);
        channel.basicConsume(Constants.QUERIES_QUEUE_NAME, false, consumer);
    }

    public void start() {
        try {
            waitForMessages();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void waitForMessages() throws InterruptedException, IOException {
        while (connection.isOpen() && channel.isOpen()) {
            Logger.log("Waiting for messages");
            String response = "";

            QueueingConsumer.Delivery delivery = consumer.nextDelivery();

            AMQP.BasicProperties properties = delivery.getProperties();
            AMQP.BasicProperties replyProperties = new AMQP.BasicProperties.Builder()
                    .correlationId(properties.getCorrelationId()).build();
            try {
                String message = new String(delivery.getBody());
                Logger.log("received message from client: " + message +
                        " with correlation id: " + delivery.getProperties().getCorrelationId());

                response = "test " + message;
                Logger.log("sending response to client: " + response);
            } catch (Exception e) {
                System.out.println(e.toString());
                response = "";
            } finally {
                channel.basicPublish("", properties.getReplyTo(), replyProperties, response.getBytes());
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }
        }
    }
}
