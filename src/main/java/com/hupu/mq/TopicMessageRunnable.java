package com.hupu.mq;

import java.util.Date;
import java.util.List;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

public class TopicMessageRunnable implements Runnable {

	private int index;
	private List<String> routingKeyLs;
	private String queueName;

	public TopicMessageRunnable() {
		super();
		// TODO Auto-generated constructor stub
	}

	public TopicMessageRunnable(int index, String queueName, List<String> routingKeyLs) {
		super();
		this.index = index;
		this.queueName = queueName;
		this.routingKeyLs = routingKeyLs;
	}

	@Override
	public void run() {
		getMessage(routingKeyLs.get(index));
	}

	private void getMessage(String routingKey) {
		
		System.out.println(String.format("Thread: %s has started...", Thread.currentThread().getName()));
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(CommonUtils.HOST_SERVER);
		factory.setPort(CommonUtils.HOST_PORT);

		try {
			Connection conn = factory.newConnection();
			Channel channel = conn.createChannel();

			channel.exchangeDeclare(CommonUtils.EXCHANGE_NAME_TOPIC, "topic");
			
			channel.queueDeclare(queueName, false, false, false, null);
			channel.queueBind(queueName, CommonUtils.EXCHANGE_NAME_TOPIC, routingKey);

			QueueingConsumer qc = new QueueingConsumer(channel);
			channel.basicConsume(queueName, true, qc);

			System.out.println("Waiting for message... " + CommonUtils.sdf.format(new Date()));

			while (true) {
				QueueingConsumer.Delivery delivery = qc.nextDelivery();
				String message = new String(delivery.getBody());
				System.out.println(Thread.currentThread().getName() + ", received: " + message + ", routingKey: " + routingKey);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
