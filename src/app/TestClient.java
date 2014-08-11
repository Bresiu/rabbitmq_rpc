package app;

import com.rabbitmq.client.*;
import utils.Constants;
import utils.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

public class TestClient {
    private Channel channel;
    private QueueingConsumer consumer;
    private String replyQueueName;

    public TestClient() {
        try {
            createConnection();
        } catch (Exception e) {
            Logger.log(e.toString());
        }
    }

    private void createConnection() throws IOException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(Constants.HOST);
        // factory.setPort(Constants.PORT);

        Connection connection = factory.newConnection();
        channel = connection.createChannel();

        replyQueueName = channel.queueDeclare().getQueue();
        consumer = new QueueingConsumer(channel);
        channel.basicConsume(replyQueueName, true, consumer);
    }

    public void sendMessage(String message) {
        try {
            String correlationId = getUUID();
            Logger.log("building message with correlation id: " + correlationId);
            AMQP.BasicProperties properties = getBasicProperties(correlationId);

            publishMessage(properties, message);
            Logger.log("sent message to server: " + message);

            waitForResponse(correlationId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void waitForResponse(String correlationId) throws InterruptedException, UnsupportedEncodingException {
        String response;
        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();

            if (correlationId.equals(delivery.getProperties().getCorrelationId())) {
                response = new String(delivery.getBody(), "UTF-8");
                Logger.log("received response from server: " + response + " with correlation id: " +
                        delivery.getProperties().getCorrelationId());
                break;
            }
        }
    }

    private void publishMessage(AMQP.BasicProperties properties, String message) throws IOException {
        channel.basicPublish("", Constants.QUERIES_QUEUE_NAME, properties, message.getBytes());
    }

    private String getUUID() {
        return UUID.randomUUID().toString();
    }

    private AMQP.BasicProperties getBasicProperties(String correlationId) {
        return new AMQP.BasicProperties.Builder()
                .correlationId(correlationId)
                .replyTo(replyQueueName)
                .contentType("application/json")
                .build();
    }
}
