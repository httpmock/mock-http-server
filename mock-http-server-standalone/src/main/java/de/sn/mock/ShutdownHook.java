package de.sn.mock;

import org.apache.tomee.embedded.Container;

final class ShutdownHook extends Thread {

	private Container container;

	public ShutdownHook(Container container) {
		this.container = container;
	}

	@Override
	public void run() {
		try {
			container.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}