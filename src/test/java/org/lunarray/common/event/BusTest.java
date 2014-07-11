/* 
 * Event.
 * Copyright (C) 2013 Pal Hargitai (pal@lunarray.org)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.lunarray.common.event;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the event bus.
 * 
 * @author Pal Hargitai (pal@lunarray.org)
 */
public class BusTest {

	private Bus bus;

	@Before
	public void setup() {
		this.bus = new Bus();
	}

	@Test
	public void testFireEvent() throws EventException {
		final ListenerStub l = new ListenerStub();
		this.bus.addListener(l);
		this.bus.handleEvent(new EventStub());
		Assert.assertEquals(1, l.getCounter());
		this.bus.removeListener(l);
	}

	@Test
	public void testListenerOrdering() throws EventException {
		final IMocksControl crtl = EasyMock.createStrictControl();
		crtl.checkOrder(true);
		@SuppressWarnings("unchecked")
		final Listener<Object> l1 = crtl.createMock(Listener.class);
		@SuppressWarnings("unchecked")
		final Listener<Object> l2 = crtl.createMock(Listener.class);
		crtl.reset();
		l2.handleEvent(EasyMock.notNull());
		EasyMock.expectLastCall().times(1);
		l1.handleEvent(EasyMock.notNull());
		EasyMock.expectLastCall().times(1);
		crtl.replay();
		this.bus.addListener(l1);
		this.bus.addListenerBefore(l2);
		this.bus.handleEvent(new Object());
		crtl.verify();
	}

	@Test
	public void testRegisterListener() {
		final ListenerStub l = new ListenerStub();
		this.bus.addListener(l);
		this.bus.removeListener(l);
	}

	public static class EventStub {
		// An event!
	}

	public static class ListenerStub
			implements Listener<EventStub> {
		private int counter = 0;

		/**
		 * Gets the value for the counter field.
		 * 
		 * @return The value for the counter field.
		 */
		public int getCounter() {
			return this.counter;
		}

		/** {@inheritDoc} */
		@Override
		public void handleEvent(final EventStub event) throws EventException {
			this.counter++;
		}
	}
}
