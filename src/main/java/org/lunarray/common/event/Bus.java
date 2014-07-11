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

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.lunarray.common.generics.GenericsUtil;

/**
 * An event bus. Allows registering of listeners.
 * 
 * @author Pal Hargitai (pal@lunarray.org)
 */
public final class Bus {

	/** The cached listeners. */
	private final transient Map<Class<?>, List<ListenerInstancePair<?>>> cachedListeners;
	/** The listeners. */
	private final transient List<ListenerInstancePair<?>> listeners;

	/**
	 * Default constructor.
	 */
	public Bus() {
		this.cachedListeners = new HashMap<Class<?>, List<ListenerInstancePair<?>>>();
		this.listeners = new LinkedList<ListenerInstancePair<?>>();
	}

	/**
	 * Adds a listener.
	 * 
	 * @param listener
	 *            The listener.
	 * @param <T>
	 *            The listener event type.
	 */
	public <T> void addListener(final Listener<T> listener) {
		this.addListener(listener, null);
	}

	/**
	 * Adds a listener.
	 * 
	 * @param listener
	 *            The listener.
	 * @param instance
	 *            The instance associated with this listener.
	 * @param <T>
	 *            The listener event type.
	 */
	public <T> void addListener(final Listener<T> listener, final Object instance) {
		final ListenerInstancePair<T> pair = new ListenerInstancePair<T>(listener, instance);
		this.listeners.add(pair);
		final Type observableType = GenericsUtil.getEntityGenericType(listener.getClass(), 0, Listener.class);
		final Class<?> observable = GenericsUtil.guessClazz(observableType);
		if (!this.cachedListeners.containsKey(observable)) {
			this.cachedListeners.put(observable, new LinkedList<ListenerInstancePair<?>>());
		}
		for (final Map.Entry<Class<?>, List<ListenerInstancePair<?>>> entry : this.cachedListeners.entrySet()) {
			final Class<?> type = entry.getKey();
			if (observable.isAssignableFrom(type)) {
				entry.getValue().add(pair);
			}
		}
	}

	/**
	 * Adds a listener.
	 * 
	 * @param listener
	 *            The listener.
	 * @param <T>
	 *            The listener event type.
	 */
	public <T> void addListenerBefore(final Listener<T> listener) {
		this.addListenerBefore(listener, null);
	}

	/**
	 * Adds a listener.
	 * 
	 * @param listener
	 *            The listener.
	 * @param instance
	 *            The instance associated with this listener.
	 * @param <T>
	 *            The listener event type.
	 */
	public <T> void addListenerBefore(final Listener<T> listener, final Object instance) {
		final ListenerInstancePair<T> pair = new ListenerInstancePair<T>(listener, instance);
		this.listeners.add(pair);
		final Type observableType = GenericsUtil.getEntityGenericType(listener.getClass(), 0, Listener.class);
		final Class<?> observable = GenericsUtil.guessClazz(observableType);
		if (!this.cachedListeners.containsKey(observable)) {
			this.cachedListeners.put(observable, new LinkedList<ListenerInstancePair<?>>());
		}
		for (final Map.Entry<Class<?>, List<ListenerInstancePair<?>>> entry : this.cachedListeners.entrySet()) {
			final Class<?> type = entry.getKey();
			if (observable.isAssignableFrom(type)) {
				entry.getValue().add(0, pair);
			}
		}
	}

	/**
	 * Handle an event.
	 * 
	 * @param event
	 *            The event to handle.
	 * @param <T>
	 *            The event type.
	 * @throws EventException
	 *             Thrown if the event could not be processed.
	 */
	public <T> void handleEvent(final T event) throws EventException {
		final Class<?> type = event.getClass();
		if (!this.cachedListeners.containsKey(event.getClass())) {
			this.cachedListeners.put(type, new LinkedList<ListenerInstancePair<?>>());
			for (final ListenerInstancePair<?> listener : this.listeners) {
				final Type observableType = GenericsUtil.getEntityGenericType(listener.listener.getClass(), 0, Listener.class);
				final Class<?> observable = GenericsUtil.guessClazz(observableType);
				if (observable.isAssignableFrom(type)) {
					this.cachedListeners.get(type).add(listener);
				}
			}
		}
		@SuppressWarnings("unchecked")
		final List<ListenerInstancePair<T>> resolvedListeners = List.class.cast(this.cachedListeners.get(event.getClass()));
		for (final ListenerInstancePair<T> listener : resolvedListeners) {
			listener.handleEvent(event, null);
		}
	}

	/**
	 * Handle an event.
	 * 
	 * @param event
	 *            The event to handle.
	 * @param instance
	 *            The instance.
	 * @param <T>
	 *            The event type.
	 * @throws EventException
	 *             Thrown if the event could not be processed.
	 */
	public <T> void handleEvent(final T event, final Object instance) throws EventException {
		final Class<?> type = event.getClass();
		if (!this.cachedListeners.containsKey(event.getClass())) {
			this.cachedListeners.put(type, new LinkedList<ListenerInstancePair<?>>());
			for (final ListenerInstancePair<?> listener : this.listeners) {
				final Type observableType = GenericsUtil.getEntityGenericType(listener.listener.getClass(), 0, Listener.class);
				final Class<?> observable = GenericsUtil.guessClazz(observableType);
				if (observable.isAssignableFrom(type)) {
					this.cachedListeners.get(type).add(listener);
				}
			}
		}
		@SuppressWarnings("unchecked")
		final List<ListenerInstancePair<T>> resolvedListeners = List.class.cast(this.cachedListeners.get(event.getClass()));
		for (final ListenerInstancePair<T> listener : resolvedListeners) {
			listener.handleEvent(event, instance);
		}
	}

	/**
	 * Remove a listener.
	 * 
	 * @param listener
	 *            The listener to remove.
	 * @param <T>
	 *            The listener event type.
	 */
	public <T> void removeListener(final Listener<T> listener) {
		this.removeListener(listener, null);
	}

	/**
	 * Remove a listener.
	 * 
	 * @param listener
	 *            The listener to remove.
	 * @param instance
	 *            The associated marker.
	 * @param <T>
	 *            The listener event type.
	 */
	public <T> void removeListener(final Listener<T> listener, final Object instance) {
		final ListenerInstancePair<T> pair = new ListenerInstancePair<T>(listener, instance);
		this.listeners.remove(pair);
		for (final List<ListenerInstancePair<?>> entry : this.cachedListeners.values()) {
			entry.remove(pair);
		}
	}

	/**
	 * The listener pair.
	 * 
	 * @author Pal Hargitai (pal@lunarray.org)
	 * @param <T>
	 *            The observable type.
	 */
	private static class ListenerInstancePair<T> {
		/** A marker instance. */
		private final transient Object instance;
		/** The listener. */
		private final transient Listener<T> listener;

		/**
		 * Default constructor.
		 * 
		 * @param listener
		 *            the listener.
		 * @param instance
		 *            The match instance.
		 */
		public ListenerInstancePair(final Listener<T> listener, final Object instance) {
			this.listener = listener;
			this.instance = instance;
		}

		/** {@inheritDoc} */
		@Override
		public boolean equals(final Object obj) {
			boolean result;
			if (obj instanceof ListenerInstancePair) {
				final ListenerInstancePair<?> other = (ListenerInstancePair<?>) obj;
				result = (other.listener == this.listener) && (other.instance == this.instance);
			} else {
				result = false;
			}
			return result;
		}

		/**
		 * Handle an event.
		 * 
		 * @param event
		 *            The event.
		 * @param instance
		 *            The instance.
		 * @throws EventException
		 *             Thrown if the event could not be handled.
		 */
		public void handleEvent(final T event, final Object instance) throws EventException {
			if ((instance == null) || (this.instance == null) || (instance == this.instance)) {
				this.listener.handleEvent(event);
			}
		}

		/** {@inheritDoc} */
		@Override
		public int hashCode() {
			return this.listener.hashCode();
		}
	}
}
