package com.hupu.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

public class RabbitMQService {
	private static final Logger logger = LoggerFactory.getLogger(RabbitMQService.class);

	private final static String EXCHANGE_NAME = CommonUtils.EXCHANGE_NAME;
	private final static String EXCHANGE_TYPE = CommonUtils.EXCHANGE_TYPE;

	private static ConnectionFactory factory;
	private static Connection connection;
	private static Channel channel;

	static {
		initConnection();
	}

	public synchronized static void initConnection() {
		try {
			factory = new ConnectionFactory();
			factory.setHost(CommonUtils.RABBITMQ_SERVER);
			factory.setPort(CommonUtils.RABBITMQ_PORT);
			if (connection != null) {
				connection.close();
			}
		} catch (Exception ex) {
			logger.error("close", ex);
		}
		try {
			connection = factory.newConnection();
			channel = connection.createChannel();
			channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE, true);
		} catch (Exception ex) {
			logger.error("newConnection", ex);
		}
	}

	public static void publicMessage(String queue, String routingKey, String message) {

		try {
			channel.queueDeclare(queue, false, false, false, null);
			channel.queueBind(queue, EXCHANGE_NAME, routingKey);

			byte[] bytes = message.getBytes(Charsets.UTF_8);
			channel.basicPublish(EXCHANGE_NAME, routingKey, MessageProperties.PERSISTENT_TEXT_PLAIN, bytes);
			logger.info(String.format("Send message: [%s], routeKey: %s", message, routingKey));
		} catch (Exception ex) {
			logger.error("mq pub ", ex);
			initConnection();
		}
	}

	public static void publicMessage(String routingKey, String message) {
		try {
			byte[] bytes = message.getBytes(Charsets.UTF_8);
			channel.basicPublish(EXCHANGE_NAME, routingKey, MessageProperties.PERSISTENT_TEXT_PLAIN, bytes);
			logger.info(String.format("Send message: [%s], routeKey: %s", message, routingKey));
		} catch (Exception ex) {
			logger.error("mq pub ", ex);
			initConnection();
		}
	}

	public static void receiveMessage(String queue, String routingKey) {
		QueueingConsumer qc = new QueueingConsumer(channel);
		try {
			channel.basicConsume(queue, true, qc);
			while (true) {
				QueueingConsumer.Delivery delivery = qc.nextDelivery(); // 阻塞知道介绍一条消息
				String result = new String(delivery.getBody());
				logger.info(String.format("Receive message: [%s], routeKey: %s", result, routingKey));
			}
		} catch (IOException | ShutdownSignalException | ConsumerCancelledException | InterruptedException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public static void receiveMessage(String routingKey) {
		QueueingConsumer qc = new QueueingConsumer(channel);
		try {
			channel.basicConsume("", true, qc);
			while (true) {
				QueueingConsumer.Delivery delivery = qc.nextDelivery(); // 阻塞知道介绍一条消息
				String result = new String(delivery.getBody());
				logger.info(result);
			}
		} catch (IOException | ShutdownSignalException | ConsumerCancelledException | InterruptedException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public static void subscript(String routingKey, boolean autoDelete) throws IOException {
		boolean autoAck = false;

		final Channel channel = connection.createChannel();

		String queueName = routingKey;

		channel.queueDeclare(queueName, true, false, autoDelete, null);
		channel.queueBind(queueName, EXCHANGE_NAME, routingKey);

		channel.basicConsume(queueName, autoAck, "", new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
				String routingKey = envelope.getRoutingKey();
				long deliveryTag = envelope.getDeliveryTag();
				System.out.println(new String(body));
			}
		});
	}

}
