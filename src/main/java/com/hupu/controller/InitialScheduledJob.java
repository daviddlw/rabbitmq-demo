package com.hupu.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.hupu.service.CommonUtils;
import com.hupu.service.RabbitMQService;

public class InitialScheduledJob implements ServletContextListener {

	private static Logger logger = Logger.getLogger(InitialScheduledJob.class);

	private static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
	private static SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
	private static String HUPU_ROUTE_KEY = "greetings";
	private static String HUPU_QUEUE_KEY = "hupu_test_queue";

	private void startProducer() {

		ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
		Runnable r = new Runnable() {

			@Override
			public void run() {
				logger.info("start producer...");
				String message = "我爱大虎扑" + sdf.format(new Date());
				RabbitMQService.publicMessage(HUPU_ROUTE_KEY, message);
			}
		};

		exec.scheduleAtFixedRate(r, 0, CommonUtils.INTERVEL_TIME, TimeUnit.SECONDS);

	}

	private void startConsumer() {
		RabbitMQService.receiveMessage(HUPU_QUEUE_KEY, HUPU_ROUTE_KEY);
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {

		if (CommonUtils.INTERVEL_TIME > 0) {
			startProducer();
			// logger.info("consumer task will start after 5 seconds...");
			ExecutorService exec = Executors.newSingleThreadExecutor();
			exec.execute(new Runnable() {

				@Override
				public void run() {
					// startConsumer();
				}
			});
		} else {
			logger.info("close scheduled task...");
		}
	}
}
