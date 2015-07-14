package com.hupu.conf;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.config.StompBrokerRelayRegistration;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import com.hupu.filter.WebSocketFilter;
import com.hupu.service.CommonUtils;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

	private Logger logger = Logger.getLogger(WebSocketConfig.class);

	@Bean
	public WebSocketFilter connectFilter() {
		return new WebSocketFilter();
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		// Send
		StompBrokerRelayRegistration relayRegistration = config.enableStompBrokerRelay("/topic");
		logger.info("server: " + CommonUtils.RABBITMQ_SERVER + ", port: " + CommonUtils.RABBITMQ_PORT);
		relayRegistration.setRelayHost(CommonUtils.RABBITMQ_SERVER);
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/mq");//.withSockJS();
	}
}