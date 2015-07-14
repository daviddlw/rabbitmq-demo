package com.hupu.mq;

import java.util.concurrent.TimeUnit;

public class RoutingConsumerRun {

	public static void main(String[] args) {
		try {
			for (int i = 0; i < CommonUtils.routingKeyLs.size(); i++) {
				MessageRunnable mr = new MessageRunnable(i, CommonUtils.routingKeyLs);
				Thread t = new Thread(mr);
				t.setDaemon(true);
				TimeUnit.SECONDS.sleep(1);
				t.start();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
