package com.hupu.controller;

import org.apache.log4j.Logger;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;

public class GreetingController {

	private Logger logger = Logger.getLogger(GreetingController.class);

	@MessageMapping("/mq")
	@SendTo("/topic/greetings")
	public void greeting(String name) {
		logger.info(name);
	}

}
