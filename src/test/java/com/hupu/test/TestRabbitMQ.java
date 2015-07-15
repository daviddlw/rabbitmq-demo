package com.hupu.test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import com.hupu.mq.CommonUtils;
import com.hupu.mq.MessageRunnable;
import com.hupu.mq.RpcClient;
import com.hupu.mq.RpcServer;
import com.hupu.service.RabbitMQService;
import com.rabbitmq.client.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

public class TestRabbitMQ
{

	private Logger logger = Logger.getLogger(TestRabbitMQ.class);

	private static final String QUEUE_NAME = "hello_queue";
	private static final String EXCHANGE_NAME = "hello_exchange";
	private static final String EXCHANGE_NAME_DIRECT = CommonUtils.EXCHANGE_NAME_DIRECT;
	// 一个队列两个交换机
	private static final String NEW_EXCHANGE = "new_exchange";
	private static final String QUEUE_A = "queue_a";
	private static final String QUEUE_B = "queue_b";
	private static final String DIRECT_TYPE = "direct";
	private static final String FANOUT_TYPE = "fanout";
	private static final String TOPIC_TYPE = "topic";
	private static final String ROUTE_A = "route_a";
	private static final String ROUTE_B = "route_b";

	private static final String HOST_SERVER = CommonUtils.HOST_SERVER;
	private static final int HOST_PORT = CommonUtils.HOST_PORT;
	private SimpleDateFormat sdf = CommonUtils.sdf;
	private static String HUPU_ROUTE_KEY = "hupu_test_route";
	private static String HUPU_QUEUE_KEY = "hupu_test_queue";
	private static String TASK_QUEUE_NAME = "task_queue";
	private static List<String> ls = CommonUtils.ls;
	private static List<String> routingMessageLs = CommonUtils.routingMessageLs;
	private static List<String> routingKeyLs = CommonUtils.routingKeyLs;

	@Test
	public void testStompProtocol()
	{
		RabbitMQService.publicMessage("greetings", "大虎扑");
	}

	@Test
	public void testRabbitMQServiceSend()
	{
		RabbitMQService.publicMessage(HUPU_QUEUE_KEY, HUPU_ROUTE_KEY, "我爱你中国" + sdf.format(new Date()));
		// RabbitMQService.publicMessage(HUPU_ROUTE_KEY, "我爱你中国" +
		// sdf.format(new Date()));
	}

	@Test
	public void testRabbitMQServiceRecevie()
	{
		RabbitMQService.receiveMessage(HUPU_QUEUE_KEY, HUPU_ROUTE_KEY);
		// RabbitMQService.receiveMessage(HUPU_ROUTE_KEY);
	}

	@Test
	public void testRabbitMQServiceRecevie2()
	{
		RabbitMQService.receiveMessage(HUPU_QUEUE_KEY, HUPU_ROUTE_KEY);
	}

	@Test
	public void testRabbitExchangeWithMutipleQueueProducer()
	{
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(HOST_SERVER);
		factory.setPort(HOST_PORT);

		try
		{
			Connection conn = factory.newConnection();
			Channel channel = conn.createChannel();

			channel.exchangeDeclare(NEW_EXCHANGE, DIRECT_TYPE);
			channel.queueDeclare(QUEUE_A, false, false, false, null);
			channel.queueDeclare(QUEUE_B, false, false, false, null);

			channel.queueBind(QUEUE_A, NEW_EXCHANGE, ROUTE_A);
			channel.queueBind(QUEUE_B, NEW_EXCHANGE, ROUTE_B);

			String messageA = "One Exchange Mutiple QUEUES A - " + sdf.format(new Date());
			channel.basicPublish(NEW_EXCHANGE, ROUTE_A, null, messageA.getBytes());
			channel.basicPublish(NEW_EXCHANGE, ROUTE_A, null, "追加信息".getBytes());
			logger.info("启动MQ Producer, Sent message a..." + messageA);

			String messageB = "One Exchange Mutiple QUEUES B - " + sdf.format(new Date());
			channel.basicPublish(NEW_EXCHANGE, ROUTE_B, null, messageB.getBytes());
			logger.info("启动MQ Producer, Sent message b..." + messageB);

			channel.close();
			conn.close();

		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testRabbitExchangeWithMutipleQueueConsumerA()
	{
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(HOST_SERVER);
		factory.setPort(HOST_PORT);
	
		try
		{
			Connection conn = factory.newConnection();
			Channel channel = conn.createChannel();
	
			channel.exchangeDeclare(NEW_EXCHANGE, DIRECT_TYPE);
			channel.queueBind(QUEUE_A, NEW_EXCHANGE, ROUTE_A);
	
			QueueingConsumer qc = new QueueingConsumer(channel);
			channel.basicConsume(QUEUE_A, true, qc);
	
			while (true)
			{
				QueueingConsumer.Delivery delivery = qc.nextDelivery();
				String message = new String(delivery.getBody());
				logger.info("message： " + message);
			}
	
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ShutdownSignalException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConsumerCancelledException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testRabbitExchangeWithMutipleQueueConsumerB()
	{
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(HOST_SERVER);
		factory.setPort(HOST_PORT);

		try
		{
			Connection conn = factory.newConnection();
			Channel channel = conn.createChannel();

			channel.exchangeDeclare(NEW_EXCHANGE, DIRECT_TYPE);
			channel.queueBind(QUEUE_B, NEW_EXCHANGE, ROUTE_B);

			QueueingConsumer qc = new QueueingConsumer(channel);
			channel.basicConsume(QUEUE_B, true, qc);

			while (true)
			{
				QueueingConsumer.Delivery delivery = qc.nextDelivery();
				String message = new String(delivery.getBody());
				logger.info("message： " + message);
			}

		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ShutdownSignalException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConsumerCancelledException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testRabbitExchangeMQProducer()
	{
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(HOST_SERVER);
		factory.setPort(HOST_PORT);

		try
		{
			Connection conn = factory.newConnection();
			Channel channel = conn.createChannel();

			channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

			String message = "hello world! 你好Exchange - " + sdf.format(new Date());
			channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());
			logger.info("启动MQ Producer, Sent message..." + message);

			channel.close();
			conn.close();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testRabbitExchangeMQConsumer()
	{
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(HOST_SERVER);
		factory.setPort(HOST_PORT);

		try
		{
			Connection conn = factory.newConnection();
			Channel channel = conn.createChannel();

			channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
			channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");
			logger.info("Waiting for message...");
			QueueingConsumer qc = new QueueingConsumer(channel);
			channel.basicConsume(QUEUE_NAME, true, qc);

			while (true)
			{
				QueueingConsumer.Delivery delivery = qc.nextDelivery(); // 阻塞知道介绍一条消息
				String result = new String(delivery.getBody());
				logger.info(result);
			}

		} catch (Exception e)
		{
			// TODO: handle exception
		}
	}

	@Test
	public void testRabbitSimpleMQProducer()
	{
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(HOST_SERVER);
		factory.setPort(HOST_PORT);
		try
		{
			Connection conn = factory.newConnection();
			Channel channel = conn.createChannel();
			channel.queueDeclare(QUEUE_NAME, false, false, false, null); // (如果没有就)创建Queue

			String message = "hello world! 你好中国 - " + sdf.format(new Date());
			channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
			logger.info("启动MQ Producer, Sent message..." + message);

			channel.close();
			conn.close();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testRabbitSimpleMQConsumer()
	{
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(HOST_SERVER);
		factory.setPort(HOST_PORT);
		try
		{
			Connection conn = factory.newConnection();
			Channel channel = conn.createChannel();

			channel.queueDeclare(QUEUE_NAME, false, false, false, null); // 看一下Queue是否存在
			System.out.println("启动消费者...");
			QueueingConsumer qc = new QueueingConsumer(channel);
			channel.basicConsume(QUEUE_NAME, true, qc);

			while (true)
			{
				QueueingConsumer.Delivery delivery = qc.nextDelivery(); // 阻塞知道介绍一条消息
				String result = new String(delivery.getBody());
				logger.info(result);
			}
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ShutdownSignalException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConsumerCancelledException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String getMessage(int i)
	{
		return ls.get(i);
	}

	@Test
	public void testNewTaskMQProducer()
	{
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(HOST_SERVER);
		factory.setPort(HOST_PORT);

		try
		{
			Connection conn = factory.newConnection();
			Channel channel = conn.createChannel();

			// durable=true保证mq重启后任务不会消失
			channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);

			for (int i = 0; i < ls.size(); i++)
			{
				String message = getMessage(i);

				// MessageProperties.PERSISTENT_TEXT_PLAIN持久化文本
				channel.basicPublish("", TASK_QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
				System.out.println("sent message: " + message);
				TimeUnit.SECONDS.sleep(1);
			}

			channel.close();
			conn.close();

		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testNewWorkMQConsumer()
	{
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(HOST_SERVER);
		factory.setPort(HOST_PORT);

		try
		{
			Connection conn = factory.newConnection();
			Channel channel = conn.createChannel();

			channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);

			System.out.println("Waiting for message..." + sdf.format(new Date()));
			// 保证消费者处理完一条消息后才能继续处理下一条消息
			channel.basicQos(1);

			QueueingConsumer qc = new QueueingConsumer(channel);
			channel.basicConsume(TASK_QUEUE_NAME, false, qc);

			while (true)
			{
				QueueingConsumer.Delivery delivery = qc.nextDelivery();
				String message = new String(delivery.getBody());

				System.out.println("Received: " + message);
				for (int i = 0; i < message.toCharArray().length; i++)
				{
					Thread.sleep(1000);
				}
				System.out.println("Done...");
				// 保证消息的转发如果当前consumer进程挂了
				channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Test
	public void testPubAndSubProducer()
	{
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(HOST_SERVER);
		factory.setPort(HOST_PORT);

		try
		{
			Connection conn = factory.newConnection();
			Channel channel = conn.createChannel();

			channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
			for (int i = 0; i < ls.size(); i++)
			{
				String message = getMessage(i);
				channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());
				System.out.println("sent message: " + message);
				TimeUnit.SECONDS.sleep(1);
			}

			channel.close();
			conn.close();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testPubAndSubConsumer()
	{
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(HOST_SERVER);
		factory.setPort(HOST_PORT);

		try
		{
			Connection conn = factory.newConnection();
			Channel channel = conn.createChannel();

			channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
			String queue = channel.queueDeclare().getQueue();

			channel.queueBind(queue, EXCHANGE_NAME, "");

			System.out.println("Waiting for message..." + sdf.format(new Date()));

			QueueingConsumer qc = new QueueingConsumer(channel);
			channel.basicConsume(queue, true, qc);

			while (true)
			{
				QueueingConsumer.Delivery delivery = qc.nextDelivery();
				String message = new String(delivery.getBody());
				System.out.println("Received: " + message);
			}
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ShutdownSignalException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConsumerCancelledException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testRoutingProducer()
	{
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(HOST_SERVER);
		factory.setPort(HOST_PORT);

		try
		{
			Connection conn = factory.newConnection();
			Channel channel = conn.createChannel();

			channel.exchangeDeclare(EXCHANGE_NAME_DIRECT, "direct");
			String routingKey = StringUtils.EMPTY;
			String message = StringUtils.EMPTY;
			for (int i = 0; i < routingKeyLs.size(); i++)
			{
				routingKey = routingKeyLs.get(i);
				message = routingMessageLs.get(i);

				channel.basicPublish(EXCHANGE_NAME_DIRECT, routingKey, null, message.getBytes());
				System.out.println("sent message: " + message);
			}

			channel.basicPublish(EXCHANGE_NAME_DIRECT, "info", null, "info_new".getBytes());

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Test
	public void testTopicProducer()
	{
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(HOST_SERVER);
		factory.setPort(HOST_PORT);

		try
		{
			Connection conn = factory.newConnection();
			Channel channel = conn.createChannel();

			Random r = new Random();

			channel.exchangeDeclare(CommonUtils.EXCHANGE_NAME_TOPIC, "topic");
			String routingKey = StringUtils.EMPTY;
			String message = StringUtils.EMPTY;

			channel.basicPublish(CommonUtils.EXCHANGE_NAME_TOPIC, routingKey, null, message.getBytes());

			for (int i = 0; i < CommonUtils.topicKeysLs.size(); i++)
			{
				message = CommonUtils.topicMessageLs.get(r.nextInt(CommonUtils.topicMessageLs.size()));
				System.out.println("message: " + message);
				if (message.contains("orange"))
				{
					routingKey = "*.orange.*";
				} else if (message.contains("rabbit"))
				{
					routingKey = "*.*.rabbit";
				} else if (message.contains("lazy"))
				{
					routingKey = "lazy.#";
				}
				channel.basicPublish(CommonUtils.EXCHANGE_NAME_TOPIC, routingKey, null, message.getBytes());

			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Test
	public void testRpc()
	{
		RpcClient client;
		try
		{
			client = new RpcClient();
			String result = client.call("10");
			System.out.println(result);

			client.close();
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	
	@Test
	public void startServer() throws Exception
	{
		RpcServer server = new RpcServer();
		System.out.println(server);
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
	
	@Test
	public void testFib()
	{
		System.out.println(fib(10));
	}
}
