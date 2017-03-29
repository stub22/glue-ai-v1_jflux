package org.jflux.api.service.util;

import org.jflux.api.registry.Registry;
import org.jflux.api.service.RegistrationStrategy;
import org.jflux.api.service.ServiceDependency;
import org.jflux.api.service.ServiceLifecycle;
import org.jflux.api.service.ServiceManager;
import org.jflux.api.service.binding.ServiceBinding;
import org.jflux.api.service.binding.ServiceBinding.BindingStrategy;

import java.util.Map;

/**
 * <h2>Usage</h2>
 *
 * <pre>
 * 	public <T> ServiceManager<T> doStuff(final ServiceLifecycle<T> lifecycle,
 * final Registry registry) {
 * return new ServiceLauncher<>(lifecycle)
 * .bindEager(CharacterClientLifecycle_theAnimDep)
 * .property("AnimationPlayer.PROP_PLAYER_ID", "animId")
 *
 * .bindEager(CharacterClientLifecycle_theSpeechDep)
 * .property("SpeechService.PROP_ID", "speechId")
 *
 * .serviceRegistration("MiloDemoPipeListenerId", "Milo")
 * .launchService(registry);
 * }
 * </pre>
 *
 * @author matt
 */


public class ServiceLauncher<T> {


	private final BindingMapBuilder myBindingMapBuilder = new BindingMapBuilder();
	private final RegistrationStrategyBuilder<T> myServiceRegistrationStrategyBuilder = new RegistrationStrategyBuilder<>();
	private final RegistrationStrategyBuilder<ServiceManager<T>> myManagerRegistrationStrategyBuilder = new RegistrationStrategyBuilder<>();
	private final LauncherBindingMap myLauncherBindingMap = new LauncherBindingMap();
	private final LauncherServiceRegistration myLauncherServiceRegistration = new LauncherServiceRegistration();
	private final LauncherManagerRegistration myLauncherManagerRegistration = new LauncherManagerRegistration();
	private final ServiceLifecycle<T> myLifecycle;

	public ServiceLauncher(final ServiceLifecycle<T> lifecycle) {
		myLifecycle = lifecycle;
	}

	public LauncherBindingMap bind(final ServiceDependency dependency) {
		return myLauncherBindingMap.bind(dependency);
	}

	public LauncherBindingMap bindEager(final ServiceDependency dependency) {
		return myLauncherBindingMap.bindEager(dependency);
	}

	public LauncherBindingMap bindLazy(final ServiceDependency dependency) {
		return myLauncherBindingMap.bindLazy(dependency);
	}

	public LauncherServiceRegistration serviceRegistration() {
		return myLauncherServiceRegistration;
	}

	public LauncherServiceRegistration serviceRegistration(final String className) {
		return myLauncherServiceRegistration.className(className);
	}

	public LauncherServiceRegistration serviceRegistration(final String key, final String value) {
		return myLauncherServiceRegistration.property(key, value);
	}

	public LauncherManagerRegistration managerRegistration() {
		return myLauncherManagerRegistration;
	}

	public LauncherManagerRegistration managerRegistration(final String className) {
		return myLauncherManagerRegistration.className(className);
	}

	public LauncherManagerRegistration managerRegistration(final String key, final String value) {
		return myLauncherManagerRegistration.property(key, value);
	}

	public ServiceManager<T> buildServiceManager() {
		return _buildServiceManager();
	}

	public ServiceManager<T> launchService(final Registry registry) {
		return _launchService(registry);
	}

	private ServiceManager<T> _buildServiceManager() {
		final Map<String, ServiceBinding> bindings = myBindingMapBuilder.getBindingMap();
		final RegistrationStrategy svcRegStrat = myLauncherServiceRegistration.getRegistrationStrategy();
		final RegistrationStrategy mngrRegStrat = myLauncherManagerRegistration.getRegistrationStrategy();
		return new ServiceManager<>(myLifecycle, bindings, svcRegStrat, mngrRegStrat);
	}

	private ServiceManager<T> _launchService(final Registry registry) {
		final ServiceManager<T> serviceManager = buildServiceManager();
		serviceManager.start(registry);
		return serviceManager;
	}

	public final class LauncherBindingMap {
		private LauncherBindingMap() {
		}

		public LauncherBindingMap bind(final ServiceDependency serviceDependency) {
			myBindingMapBuilder.bind(serviceDependency);
			return this;
		}

		public LauncherBindingMap bindEager(final ServiceDependency serviceDependency) {
			myBindingMapBuilder.bind(serviceDependency).bindingStrategy(BindingStrategy.EAGER);
			return this;
		}

		public LauncherBindingMap bindLazy(final ServiceDependency serviceDependency) {
			myBindingMapBuilder.bind(serviceDependency).bindingStrategy(BindingStrategy.LAZY);
			return this;
		}

		public LauncherBindingMap className(final String className) {
			myBindingMapBuilder.className(className);
			return this;
		}

		public LauncherBindingMap bindingStrategy(final BindingStrategy bindingStrategy) {
			myBindingMapBuilder.bindingStrategy(bindingStrategy);
			return this;
		}

		public LauncherBindingMap updateStrategy(final ServiceDependency.UpdateStrategy updateStrategy) {
			myBindingMapBuilder.updateStrategy(updateStrategy);
			return this;
		}

		public LauncherBindingMap property(final String key, final String value) {
			myBindingMapBuilder.property(key, value);
			return this;
		}

		public LauncherServiceRegistration serviceRegistration() {
			return myLauncherServiceRegistration;
		}

		public LauncherServiceRegistration serviceRegistration(final String className) {
			return myLauncherServiceRegistration.className(className);
		}

		public LauncherServiceRegistration serviceRegistration(final String key, final String value) {
			return myLauncherServiceRegistration.property(key, value);
		}

		public LauncherManagerRegistration managerRegistration() {
			return myLauncherManagerRegistration;
		}

		public LauncherManagerRegistration managerRegistration(final String className) {
			return myLauncherManagerRegistration.className(className);
		}

		public LauncherManagerRegistration managerRegistration(final String key, final String value) {
			return myLauncherManagerRegistration.property(key, value);
		}

		public ServiceManager<T> buildServiceManager() {
			return _buildServiceManager();
		}

		public ServiceManager<T> launchService(final Registry registry) {
			return _launchService(registry);
		}
	}

	public final class LauncherServiceRegistration {
		private LauncherServiceRegistration() {
		}

		public LauncherServiceRegistration className(final String className) {
			myServiceRegistrationStrategyBuilder.className(className);
			return this;
		}

		public LauncherServiceRegistration property(final String key, final String value) {
			myServiceRegistrationStrategyBuilder.property(key, value);
			return this;
		}

		private RegistrationStrategy<T> getRegistrationStrategy() {
			if (myServiceRegistrationStrategyBuilder.getClassNames().isEmpty()) {
				for (final String className : myLifecycle.getServiceClassNames()) {
					className(className);
				}
			}
			return myServiceRegistrationStrategyBuilder.getRegistrationStrategy();
		}

		public LauncherManagerRegistration managerRegistration() {
			return myLauncherManagerRegistration;
		}

		public LauncherManagerRegistration managerRegistration(final String className) {
			return myLauncherManagerRegistration.className(className);
		}

		public LauncherManagerRegistration managerRegistration(final String key, final String value) {
			return myLauncherManagerRegistration.property(key, value);
		}

		public ServiceManager<T> buildServiceManager() {
			return _buildServiceManager();
		}

		public ServiceManager<T> launchService(final Registry registry) {
			return _launchService(registry);
		}
	}

	public final class LauncherManagerRegistration {
		private LauncherManagerRegistration() {
		}

		public LauncherManagerRegistration className(final String className) {
			myManagerRegistrationStrategyBuilder.className(className);
			return this;
		}

		public LauncherManagerRegistration property(final String key, final String value) {
			myManagerRegistrationStrategyBuilder.property(key, value);
			return this;
		}

		private RegistrationStrategy<ServiceManager<T>> getRegistrationStrategy() {
			if (myManagerRegistrationStrategyBuilder.getClassNames().isEmpty()) {
				className(ServiceManager.class.getName());
			}
			return myManagerRegistrationStrategyBuilder.getRegistrationStrategy();
		}

		public ServiceManager<T> buildServiceManager() {
			return _buildServiceManager();
		}

		public ServiceManager<T> launchService(final Registry registry) {
			return _launchService(registry);
		}
	}


}
