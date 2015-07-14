package com.hupu.service;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

public class CommonUtils {
	public static String RABBITMQ_SERVER = "192.168.9.74";
	public static int RABBITMQ_PORT = 5672;
	public static String EXCHANGE_NAME = "hupu_mq_exchange";
	public static String EXCHANGE_TYPE = "topic";
	public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
	public static int INTERVEL_TIME = 3;

	private static Logger logger = Logger.getLogger(CommonUtils.class);

	static {
		try {

			Configuration propConfig = new PropertiesConfiguration("config.properties");

			RABBITMQ_SERVER = propConfig.getString("hupu.rabbitmq.server", RABBITMQ_SERVER);
			RABBITMQ_PORT = propConfig.getInt("hupu.rabbitmq.port", RABBITMQ_PORT);
			EXCHANGE_NAME = propConfig.getString("hupu.rabbitmq.exchangename", EXCHANGE_NAME);
			EXCHANGE_TYPE = propConfig.getString("hupu.rabbitmq.exchangetype", EXCHANGE_TYPE);
			INTERVEL_TIME = propConfig.getInt("hupu.rabbitmq.interval", INTERVEL_TIME);

		} catch (ConfigurationException e) {
			logger.error(e.getMessage(), e);
		}

	}
}
