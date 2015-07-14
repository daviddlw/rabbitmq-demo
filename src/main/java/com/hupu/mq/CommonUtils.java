package com.hupu.mq;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

public class CommonUtils
{

	private static Logger logger = Logger.getLogger(CommonUtils.class);

	public static String HOST_SERVER = "192.168.9.74";

	public static int HOST_PORT = 5672;

	public static final String EXCHANGE_NAME_DIRECT = "hello_exchange_direct";

	public static final String EXCHANGE_NAME_TOPIC = "hello_exchange_topic";

	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static List<String> ls = Arrays.asList(new String[] { "daviddai", "mongodb", "we are family", "running man",
			"rabbitmq", "zabbix", "nginx" });

	public static List<String> routingKeyLs = Arrays.asList(new String[] { "error", "warn", "info" });

	public static List<String> routingMessageLs = Arrays.asList(new String[] { "error_message", "warn_message",
			"info_message" });

	public static List<String> topicKeysLs = Arrays.asList(new String[] { "*.orange.*", "*.*.rabbit", "lazy.#" });

	public static List<String> topicMessageLs = Arrays.asList(new String[] { "quick.orange.rabbit",
			"lazy.orange.elephant", "quick.orange.fox", "lazy.pink.rabbit", "quick.brown.fox", "orange",
			"quick.orange.male.rabbit" });

	public static final String RPC_QUEUE_NAME = "rpc_queue";

	static
	{
		try
		{
			PropertiesConfiguration propConf = new PropertiesConfiguration("config.properties");
			HOST_SERVER = propConf.getString("hupu.rabbitmq.server", HOST_SERVER);
			HOST_PORT = propConf.getInt("hupu.rabbitmq.port", HOST_PORT);
			logger.info(String.format("init config.properties, server: %s, port: %d", HOST_SERVER, HOST_PORT));
		} catch (ConfigurationException e)
		{
			logger.error(e.getMessage(), e);
		}
	}

}
