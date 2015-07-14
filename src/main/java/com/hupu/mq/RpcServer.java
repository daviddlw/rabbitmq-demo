package com.hupu.mq;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

public class RpcServer
{
	private Connection conn;
	private Channel channel;
	private String requestQueueName = CommonUtils.RPC_QUEUE_NAME;
	private QueueingConsumer qc;

	public RpcServer() throws Exception
	{
		super();
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(CommonUtils.HOST_SERVER);
		factory.setPort(CommonUtils.HOST_PORT);

		conn = factory.newConnection();
		channel = conn.createChannel();

		channel.queueDeclare(requestQueueName, false, false, false, null);
		channel.basicQos(1);

		qc = new QueueingConsumer(channel);
		channel.basicConsume(requestQueueName, false, qc);

		System.out.println("await for rpc request...");

		while (true)
		{
			QueueingConsumer.Delivery delivery = qc.nextDelivery();
			BasicProperties props = delivery.getProperties();
			BasicProperties replayProps = new BasicProperties().builder().correlationId(props.getCorrelationId())
					.build();

			String message = new String(delivery.getBody());

			int i = Integer.parseInt(message);
			System.out.println("fib(" + message + ")");
			int response = fib(i);

			channel.basicPublish("", props.getReplyTo(), replayProps, String.valueOf(response).getBytes());
			channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
		}
	}

	private int fib(int n)
	{
		if (n == 0)
		{
			return 0;
		}
		if (n == 1)
		{
			return 1;
		}

		return fib(n - 1) + fib(n - 2);
	}
}
