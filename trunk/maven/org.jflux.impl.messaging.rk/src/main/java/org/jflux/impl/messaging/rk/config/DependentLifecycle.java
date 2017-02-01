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
package org.jflux.impl.messaging.rk.config;

import org.jflux.api.core.Adapter;
import org.jflux.api.core.Listener;
import org.jflux.api.core.config.Configuration;
import org.jflux.api.core.util.MapAdapter.MapValueAdapter;
import org.jflux.api.core.util.Repeater;
import org.jflux.impl.services.rk.lifecycle.ManagedService;
import org.jflux.impl.services.rk.lifecycle.utils.ManagedServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static org.jflux.impl.services.rk.lifecycle.config.RKDependencyConfigUtils.buildLifecycleDependencyConfig;
import static org.jflux.impl.services.rk.lifecycle.config.RKLifecycleConfigUtils.buildGenericLifecycle;
import static org.jflux.impl.services.rk.lifecycle.config.RKLifecycleConfigUtils.buildGenericLifecycleConfig;

/**
 * @author Matthew Stevenson
 */
public class DependentLifecycle {
	private static final Logger theLogger = LoggerFactory.getLogger(DependentLifecycle.class);

	private final static String theDependency = "a";

	public static void createDependencyListener(
			String idKey, String idStr, Class depClass,
			final Listener<Configuration<String>> listener,
			ManagedServiceFactory factory) {

		Configuration<String> depConf = buildLifecycleDependencyConfig(
				theDependency, depClass, idKey, idStr, null, null);
		final Repeater<Configuration<String>> repeater = new Repeater() {

			@Override
			public void notifyListeners(Object e) {
				super.notifyListeners(e);
				//myListeners.clear();
				//myListeners = null;
			}

		};
		repeater.addListener(listener);
		final ManagedService ms = factory.createService(buildGenericLifecycle(
				buildGenericLifecycleConfig(
						new String[]{Object.class.getName()}, null,
						Arrays.asList(depConf),
						new MapValueAdapter(theDependency,
								new Adapter<Configuration<String>, Configuration<String>>() {
									@Override
									public Configuration<String> adapt(Configuration<String> a) {
										repeater.handleEvent(a);
										return a;
									}
								}))), null);
		repeater.addListener(new Listener<Configuration<String>>() {
			@Override
			public void handleEvent(Configuration<String> event) {
				//ms.dispose();
			}
		});
		ms.setRegistrationEnabled(false);
		ms.start();
	}
}
