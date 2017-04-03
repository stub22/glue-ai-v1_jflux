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

import com.google.common.base.Optional;

/**
 * A {@link QpidBrokerStatus} can be registered into a {@link org.osgi.framework.BundleContext} when
 * the Qpid Broker's status changes.
 *
 * @author ben
 * @since 3/29/2017.
 */
public interface QpidBrokerStatus {

	/**
	 * Used for registering an {@link QpidBrokerStatus} to OSGI.
	 */
	String PROPERTY_ID = "qpidBrokerStatusId";

	/**
	 * Return the class that created this status message.
	 *
	 * @return the class that created this status message.
	 */
	Class getSourceClass();

	/**
	 * Return an optional message when creating the status.
	 *
	 * @return an optional message when creating the status.
	 */
	Optional<String> getMessage();
}
