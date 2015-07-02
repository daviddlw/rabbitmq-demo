package com.hupu.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.hupu.service.CommonUtils;

public class TestApp {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testCommonUtils() {
		
		System.out.println(CommonUtils.RABBITMQ_SERVER);
		System.out.println(CommonUtils.RABBITMQ_PORT);
		System.out.println(CommonUtils.EXCHANGE_NAME);
		System.out.println(CommonUtils.EXCHANGE_TYPE);
		System.out.println(CommonUtils.INTERVEL_TIME);
	}

}
