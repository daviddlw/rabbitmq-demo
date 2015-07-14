package com.hupu.mq;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

public class CommonUtils {
	
	public static final String HOST_SERVER = "192.168.9.74";
	
	public static final int HOST_PORT = 5672;
	
	public static final String EXCHANGE_NAME_DIRECT = "hello_exchange_direct";
	
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static List<String> ls = Arrays.asList(new String[] { "daviddai", "mongodb", "we are family", "running man", "rabbitmq", "zabbix", "nginx" });
	
	public static List<String> routingMessageLs = Arrays.asList(new String[] { "error_message", "warn_message", "info_message" });
	
	public static List<String> routingKeyLs = Arrays.asList(new String[] { "error", "warn", "info" });
	
}
