package com.hupu.mq;

import java.util.concurrent.TimeUnit;

public class ConsumerRun
{

	public static void main(String[] args)
	{
		 runRoutingKeyDemo();
		// runTopicDemo();
	}

	private static void runRoutingKeyDemo()
	{
		try
		{
			for (int i = 0; i < CommonUtils.routingKeyLs.size(); i++)
			{
				MessageRunnable mr = new MessageRunnable(i, CommonUtils.routingKeyLs);
				Thread t = new Thread(mr);
				TimeUnit.SECONDS.sleep(1);
				t.start();
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private static void runTopicDemo()
	{
		try
		{
			String queueName = "";
			String key = "";
			for (int i = 0; i < CommonUtils.topicKeysLs.size(); i++)
			{
				key = CommonUtils.topicKeysLs.get(i);

				if (key.contains("rabbit") || key.contains("lazy"))
				{
					queueName = "rabbit_lazy_queue";
				} else
				{
					queueName = "orange_queue";
				}

				TopicMessageRunnable tmr = new TopicMessageRunnable(i, queueName, CommonUtils.topicKeysLs);
				Thread t = new Thread(tmr);
				TimeUnit.SECONDS.sleep(1);
				t.start();
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
