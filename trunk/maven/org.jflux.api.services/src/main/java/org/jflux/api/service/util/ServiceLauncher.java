package org.jflux.api.service.util;

import java.util.Map;
import org.jflux.api.registry.Registry;
import org.jflux.api.service.RegistrationStrategy;
import org.jflux.api.service.ServiceDependency;
import org.jflux.api.service.ServiceLifecycle;
import org.jflux.api.service.ServiceManager;
import org.jflux.api.service.binding.ServiceBinding;
import org.jflux.api.service.binding.ServiceBinding.BindingStrategy;

/**
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
	
	public ServiceLauncher(ServiceLifecycle<T> lifecycle){
		myLifecycle = lifecycle;
	}
	
	public LauncherBindingMap bind(ServiceDependency dependency){
		return myLauncherBindingMap.bind(dependency);
	}
	
	public LauncherBindingMap bindEager(ServiceDependency dependency){
		return myLauncherBindingMap.bindEager(dependency);
	}
	
	public LauncherBindingMap bindLazy(ServiceDependency dependency){
		return myLauncherBindingMap.bindLazy(dependency);
	}
	
	public LauncherServiceRegistration serviceRegistration(){
		return myLauncherServiceRegistration;
	}
	
	public LauncherServiceRegistration serviceRegistration(String className){
		return myLauncherServiceRegistration.className(className);
	}
	
	public LauncherServiceRegistration serviceRegistration(String key, String value){
		return myLauncherServiceRegistration.property(key, value);
	}
	
	public LauncherManagerRegistration managerRegistration(){
		return myLauncherManagerRegistration;
	}
	
	public LauncherManagerRegistration managerRegistration(String className){
		return myLauncherManagerRegistration.className(className);
	}
	
	public LauncherManagerRegistration managerRegistration(String key, String value){
		return myLauncherManagerRegistration.property(key, value);
	}
	
	public ServiceManager<T> buildServiceManager() {
		return _buildServiceManager();
	}
	
	public ServiceManager<T> launchService(Registry registry) {
		return _launchService(registry);
	}
	
	private ServiceManager<T> _buildServiceManager() {
		Map<String, ServiceBinding> bindings = myBindingMapBuilder.getBindingMap();
        RegistrationStrategy svcRegStrat = myLauncherServiceRegistration.getRegistrationStrategy();
        RegistrationStrategy mngrRegStrat = myLauncherManagerRegistration.getRegistrationStrategy();
        return new ServiceManager<>(myLifecycle, bindings, svcRegStrat, mngrRegStrat);
	}
	
	private ServiceManager<T> _launchService(Registry registry) {
		ServiceManager<T> serviceManager = buildServiceManager();
		serviceManager.start(registry);
		return serviceManager;
	}
	
	public final class LauncherBindingMap {
		private LauncherBindingMap(){}
		
		public LauncherBindingMap bind(ServiceDependency serviceDependency){
			myBindingMapBuilder.bind(serviceDependency);
			return this;
		}
		
		public LauncherBindingMap bindEager(ServiceDependency serviceDependency){
			myBindingMapBuilder.bind(serviceDependency).bindingStrategy(BindingStrategy.EAGER);
			return this;
		}
		
		public LauncherBindingMap bindLazy(ServiceDependency serviceDependency){
			myBindingMapBuilder.bind(serviceDependency).bindingStrategy(BindingStrategy.LAZY);
			return this;
		}

		public LauncherBindingMap className(String className){
			myBindingMapBuilder.className(className);
			return this;
		}

		public LauncherBindingMap bindingStrategy(BindingStrategy bindingStrategy){
			myBindingMapBuilder.bindingStrategy(bindingStrategy);
			return this;
		}

		public LauncherBindingMap updateStrategy(ServiceDependency.UpdateStrategy updateStrategy){
			myBindingMapBuilder.updateStrategy(updateStrategy);
			return this;
		}

		public LauncherBindingMap property(String key, String value){
			myBindingMapBuilder.property(key, value);
			return this;
		}
	
		public LauncherServiceRegistration serviceRegistration(){
			return myLauncherServiceRegistration;
		}

		public LauncherServiceRegistration serviceRegistration(String className){
			return myLauncherServiceRegistration.className(className);
		}

		public LauncherServiceRegistration serviceRegistration(String key, String value){
			return myLauncherServiceRegistration.property(key, value);
		}

		public LauncherManagerRegistration managerRegistration(){
			return myLauncherManagerRegistration;
		}

		public LauncherManagerRegistration managerRegistration(String className){
			return myLauncherManagerRegistration.className(className);
		}

		public LauncherManagerRegistration managerRegistration(String key, String value){
			return myLauncherManagerRegistration.property(key, value);
		}
	
		public ServiceManager<T> buildServiceManager() {
			return _buildServiceManager();
		}

		public ServiceManager<T> launchService(Registry registry) {
			return _launchService(registry);
		}
	}
	
	public final class LauncherServiceRegistration {
		private LauncherServiceRegistration(){};
		
		public LauncherServiceRegistration className(String className) {
			myServiceRegistrationStrategyBuilder.className(className);
			return this;
		}

		public LauncherServiceRegistration property(String key, String value) {
			myServiceRegistrationStrategyBuilder.property(key, value);
			return this;
		}
		
		private RegistrationStrategy<T> getRegistrationStrategy(){
			if(myServiceRegistrationStrategyBuilder.getClassNames().isEmpty()){
				for(String className : myLifecycle.getServiceClassNames()){
					className(className);
				}
			}
			return myServiceRegistrationStrategyBuilder.getRegistrationStrategy();
		}

		public LauncherManagerRegistration managerRegistration(){
			return myLauncherManagerRegistration;
		}

		public LauncherManagerRegistration managerRegistration(String className){
			return myLauncherManagerRegistration.className(className);
		}

		public LauncherManagerRegistration managerRegistration(String key, String value){
			return myLauncherManagerRegistration.property(key, value);
		}
	
		public ServiceManager<T> buildServiceManager() {
			return _buildServiceManager();
		}

		public ServiceManager<T> launchService(Registry registry) {
			return _launchService(registry);
		}
	}
	
	public final class LauncherManagerRegistration {
		private LauncherManagerRegistration(){}
		
		public LauncherManagerRegistration className(String className) {
			myManagerRegistrationStrategyBuilder.className(className);
			return this;
		}

		public LauncherManagerRegistration property(String key, String value) {
			myManagerRegistrationStrategyBuilder.property(key, value);
			return this;
		}
		
		private RegistrationStrategy<ServiceManager<T>> getRegistrationStrategy(){
			if(myManagerRegistrationStrategyBuilder.getClassNames().isEmpty()){
				className(ServiceManager.class.getName());
			}
			return myManagerRegistrationStrategyBuilder.getRegistrationStrategy();
		}
	
		public ServiceManager<T> buildServiceManager() {
			return _buildServiceManager();
		}

		public ServiceManager<T> launchService(Registry registry) {
			return _launchService(registry);
		}
	}
	
//	ServiceDependency CharacterClientLifecycle_theAnimDep = null;
//	ServiceDependency CharacterClientLifecycle_theSpeechDep = null;
//	
//	public <T> ServiceManager<T> doStuff(ServiceLifecycle<T> lifecycle, Registry registry){
//		return new ServiceLauncher<>(lifecycle)
//				.bindEager(CharacterClientLifecycle_theAnimDep).property("AnimationPlayer.PROP_PLAYER_ID", "animId")
//				.bindEager(CharacterClientLifecycle_theSpeechDep).property("SpeechService.PROP_ID", "speechId")
//				.serviceRegistration("MiloDemoPipeListener.class.getName()", "Milo")
//			.launchService(registry);
//	}
}
