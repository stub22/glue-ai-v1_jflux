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

/**
 * Allows a {@link org.jflux.api.service.ServiceLifecycle} with a {@link
 * org.jflux.api.service.ServiceDependency} on {@link QpidBrokerStarted} to wait until the Qpid
 * Broker has been started before continuing.
 *
 * The Bundle that starts the Qpid Broker must register to a
 * {@link org.osgi.framework.BundleContext}.
 *
 * @author ben
 * @since 3/29/2017.
 */
public class QpidBrokerStarted extends AbstractQpidBrokerStatus {


	public QpidBrokerStarted(final Class sourceClass) {
		super(sourceClass);
	}

	public QpidBrokerStarted(final Class sourceClass, final String message) {
		super(sourceClass, message);
	}
}
