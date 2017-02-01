/*
 * Copyright 2012 Hanson Robokind LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jflux.impl.messaging.rk.common;

import org.jflux.api.core.Adapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Matthew Stevenson <www.robokind.org>
 */
public class PolymorphicAdapter<A, B> implements Adapter<A, B> {
	private static final Logger theLogger = LoggerFactory.getLogger(PolymorphicAdapter.class);
	private Map<String, Adapter<A, B>> myAdapterMap;
	private AdapterKeyMap<A> myKeyMap;

	public PolymorphicAdapter(AdapterKeyMap<A> keyMap) {
		if (keyMap == null) {
			throw new NullPointerException();
		}
		myKeyMap = keyMap;
		myAdapterMap = new HashMap<>();
	}

	public void addAdapter(String adaptClass, Adapter<A, B> adapter) {
		if (adaptClass == null || adapter == null) {
			throw new NullPointerException();
		}
		myAdapterMap.put(adaptClass, adapter);
	}

	@Override
	public B adapt(A a) {
		return polymorphicAdapt(a);
	}

	private <C extends A> B polymorphicAdapt(C a) {
		String type = myKeyMap.getKey(a);
		if (type == null) {
			theLogger.warn("Unable to adapt, KeyMap returned null key.");
			return null;
		}
		Adapter<? extends A, B> adapter = myAdapterMap.get(type);
		if (adapter != null) {
			B b = null;
			try {
				b = ((Adapter<C, ? extends B>) adapter).adapt(a);
				if (b != null) {
					return b;
				}
			} catch (ClassCastException ex) {
				theLogger.warn("Found incompatible adapter for given class.  Class: {}"
						+ ", Adapter: {}", a.getClass(), adapter, ex);
				return null;
			}
			theLogger.info(
					"Unable to adapt with specific adapter, returned null.");
		} else {
			theLogger.info("No specifc adapter, using default adapter.");
		}
		return null;
	}

	public static interface AdapterKeyMap<T> {
		public String getKey(T t);
	}
}
