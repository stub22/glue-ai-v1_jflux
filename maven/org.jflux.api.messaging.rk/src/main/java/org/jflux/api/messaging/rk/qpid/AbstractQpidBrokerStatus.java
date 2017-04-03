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

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Allows subclasses to easily fulfill the {@link QpidBrokerStatus} contract.
 *
 * @author ben
 * @since 3/30/2017.
 */
public abstract class AbstractQpidBrokerStatus implements QpidBrokerStatus {
	private final Class mySourceClass;
	/**
	 * Optional
	 */
	private final String myMessage;

	AbstractQpidBrokerStatus(final Class sourceClass) {
		this(sourceClass, null);
	}

	AbstractQpidBrokerStatus(final Class sourceClass, final String message) {
		mySourceClass = checkNotNull(sourceClass);
		myMessage = message;
	}

	@Override
	public final Class getSourceClass() {
		return mySourceClass;
	}

	@Override
	public final Optional<String> getMessage() {
		return Optional.fromNullable(myMessage);
	}

	@Override
	public String toString() {
		return "QpidBrokerStarted{" +
				"SourceClass=" + mySourceClass +
				", Message='" + myMessage + '\'' +
				'}';
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		final QpidBrokerStarted that = (QpidBrokerStarted) o;

		if (!mySourceClass.equals(that.getSourceClass())) return false;
		return myMessage != null ? myMessage.equals(that.getMessage().orNull()) : !that.getMessage().isPresent();

	}

	@Override
	public int hashCode() {
		int result = mySourceClass.hashCode();
		result = 31 * result + (myMessage != null ? myMessage.hashCode() : 0);
		return result;
	}
}
