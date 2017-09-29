package com.github.fabriziocucci.jersey.issue;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

public class ApplicationTest extends JerseyTest {

	private static final CountDownLatch COUNT_DOWN_LATCH_FOR_1ST_BINDER = new CountDownLatch(1);
	private static final CountDownLatch COUNT_DOWN_LATCH_FOR_2ND_BINDER = new CountDownLatch(1);
	
	
	@Override
	protected Application configure() {
		return new ResourceConfig()
				.register(new MyBinder(COUNT_DOWN_LATCH_FOR_1ST_BINDER))
				.register(new MyBinder(COUNT_DOWN_LATCH_FOR_2ND_BINDER));
	}
	
	@Test
	public void testThatTwoInstancesOfTheSameBinderClassCannotBeRegistered() throws InterruptedException {
		assertTrue("1st binder not registered", COUNT_DOWN_LATCH_FOR_1ST_BINDER.await(5, TimeUnit.SECONDS));
		assertTrue("2nd binder not registered", COUNT_DOWN_LATCH_FOR_2ND_BINDER.await(5, TimeUnit.SECONDS));
	}
	
	private static class MyBinder extends AbstractBinder {
		
		private final CountDownLatch countDownLatch;
		
		public MyBinder(CountDownLatch countDownLatch) {
			this.countDownLatch = countDownLatch;
		}

		@Override
		protected void configure() {
			countDownLatch.countDown();
		}
		
	}
	
}
