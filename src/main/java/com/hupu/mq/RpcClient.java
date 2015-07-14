package com.hupu.mq;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

public class RpcClient
{
	private Connection conn;
	private Channel channel;
	private String requestQueueName = CommonUtils.RPC_QUEUE_NAME;
	private String replyQueueName;
	private QueueingConsumer qc;

	public RpcClient() throws Exception
	{
		super();
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(CommonUtils.HOST_SERVER);
		factory.setPort(CommonUtils.HOST_PORT);

		conn = factory.newConnection();
		channel = conn.createChannel();
		replyQueueName = channel.queueDeclare().getQueue();
		qc = new QueueingConsumer(channel);
		channel.basicConsume(replyQueueName, true, qc);

	}

	public String call(String message) throws Exception
	{
		String response = null;
		String corrid = java.util.UUID.randomUUID().toString();

		BasicProperties props = new BasicProperties().builder().correlationId(corrid).replyTo(replyQueueName).build();
		channel.basicPublish("", requestQueueName, props, message.getBytes());
		
		while (true)
		{
			QueueingConsumer.Delivery delivery = qc.nextDelivery();
			if(delivery.getProperties().getCorrelationId().equals(corrid)){
				response = new String(delivery.getBody());
				break;
			}
		}
		
		return response;
	}
	
	public void close() throws Exception
	{
		conn.close();
	}

}
