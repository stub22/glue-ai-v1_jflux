/*
 * Copyright 2013 The JFlux Project (www.jflux.org).
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
package org.jflux.api.service.util;

import org.jflux.api.registry.Registry;
import org.jflux.api.service.ServiceDependency;
import org.jflux.api.service.ServiceLifecycle;
import org.jflux.api.service.ServiceManager;

import static org.mockito.Mockito.mock;

/**
 * Examples of how to register a lifecycle easily using the {@link ServiceLifecycle}.
 *
 * @author ben
 * @since 3/28/2017.
 */
public class ServiceLauncherExamples {

	/**
	 * Examples of dependencies that the MiloDemoPipeListener would need to fulfill it's lifecycle.
	 */
	private final ServiceDependency CharacterClientLifecycle_theAnimDep = mock(ServiceDependency.class);
	private final ServiceDependency CharacterClientLifecycle_theSpeechDep = mock(ServiceDependency.class);

	/**
	 * Corresponds to AnimationPlayer.PROP_PLAYER_ID, which isn't accessible from this project.
	 */
	private final String ANIMATION_PLAYER_PROPERTY_ID = "animationPlayerId";

	/**
	 * Corresponds to SpeechService.PROP_ID, which isn't accessible from this project.
	 */
	private final String SPEECH_SERVICE_PROPERTY_ID = "speechServiceId";


	/*
	 * TODO(ben): Add reasons for why you would use these formats.
	 */

	public <T> ServiceManager<T> launchLifecycleWithDefaultBindings(
			final ServiceLifecycle<T> lifecycle,
			final Registry registry) {

		return new ServiceLauncher<>(lifecycle)
				.launchService(registry);
	}

	public <T> ServiceManager<T> launchLifecycleWithExplicitProperties(
			final ServiceLifecycle<T> lifecycle,
			final Registry registry) {

		return new ServiceLauncher<>(lifecycle)
				.bindEager(CharacterClientLifecycle_theAnimDep)
				.property(ANIMATION_PLAYER_PROPERTY_ID, "animId")

				.bindEager(CharacterClientLifecycle_theSpeechDep)
				.property(SPEECH_SERVICE_PROPERTY_ID, "speechId")

				.serviceRegistration()
				.property("miloDemoPipeListenerId", "miloId")
				.launchService(registry);
	}


	public <T> ServiceManager<T> launchLifecycleWithExplicitPropertiesAndManagerRegistration(
			final ServiceLifecycle<T> lifecycle,
			final Registry registry) {

		return new ServiceLauncher<>(lifecycle)
				.bindEager(CharacterClientLifecycle_theAnimDep)
				.property(ANIMATION_PLAYER_PROPERTY_ID, "animId")

				.bindEager(CharacterClientLifecycle_theSpeechDep)
				.property(SPEECH_SERVICE_PROPERTY_ID, "speechId")

				.serviceRegistration()
				.property("miloDemoPipeListenerId", "miloId")
				.managerRegistration()
				.launchService(registry);
	}
}