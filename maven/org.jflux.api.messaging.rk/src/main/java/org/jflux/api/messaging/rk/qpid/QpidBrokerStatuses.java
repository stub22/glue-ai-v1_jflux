/*
 *   Copyright 2014 by the MechIO Project. (www.mechio.org).
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.jflux.api.messaging.rk.qpid;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.util.Properties;

/**
 * Helper methods for {@link QpidBrokerStatus} objects. Created to make it easy to register a {@link
 * QpidBrokerStatus} object into a {@link org.osgi.framework.BundleContext}.
 *
 * @author ben
 * @since 4/3/2017.
 */
public class QpidBrokerStatuses {

	private QpidBrokerStatuses() {
	}

	/**
	 * Register a new {@link QpidBrokerStarted} object in {@code bundleContext} registered from the
	 * {@code sourceClass}.
	 *
	 * @param bundleContext to register the {@link QpidBrokerStarted} object in
	 * @param sourceClass   where the {@link QpidBrokerStarted} object is being registered from.
	 */
	public static void registerQpidBrokerStarted(final BundleContext bundleContext, final Class sourceClass) {
		final QpidBrokerStarted qpidBrokerStarted = new QpidBrokerStarted(sourceClass);
		registerQpidBrokerStarted(bundleContext, qpidBrokerStarted);
	}

	/**
	 * Register a new {@link QpidBrokerStarted} object in {@code bundleContext} registered from the
	 * {@code sourceClass}.
	 *
	 * @param bundleContext to register the {@link QpidBrokerStarted} object in
	 * @param sourceClass   where the {@link QpidBrokerStarted} object is being registered from.
	 * @param message       any relevant message
	 */
	public static void registerQpidBrokerStarted(final BundleContext bundleContext, final Class sourceClass, final String message) {
		final QpidBrokerStarted qpidBrokerStarted = new QpidBrokerStarted(sourceClass, message);
		registerQpidBrokerStarted(bundleContext, qpidBrokerStarted);
	}


	private static ServiceRegistration registerQpidBrokerStarted(final BundleContext context, final QpidBrokerStarted qpidBrokerStarted) {
		final Properties props = new Properties();
		props.put("QpidBrokerStatusId", "QpidBrokerStarted");
		return context.registerService(QpidBrokerStarted.class.getName(), qpidBrokerStarted, props);
	}
}
