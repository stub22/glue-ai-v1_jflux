package org.jflux.demo.services;

import org.osgi.framework.BundleContext;
import org.appdapter.osgi.core.BundleActivatorBase;
import org.jflux.spec.services.Test2Go;
import org.jflux.impl.services.rk.lifecycle.ManagedService;
import org.jflux.impl.services.rk.osgi.lifecycle.OSGiComponent;
import org.jflux.impl.services.rk.lifecycle.utils.SimpleLifecycle;
import java.util.Properties;

public class Activator extends BundleActivatorBase {

	//SERVICE_MODEL_PATH is the turtle file with the information for the services to be launched.
	//SVN_MGMT_ONTOLOGY_MODEL_PATH is the ontology that SERVICE_MODEL_PATH uses. 
	private final static String SERVICE_MODEL_PATH = "file:../org.jflux.spec.services/src/main/resources/org/jflux/spec/services/ServiceTest.ttl";
	private final static String SVN_MGMT_ONTOLOGY_MODEL_PATH = "file:../org.jflux.spec.services/src/main/resources/org/jflux/spec/services/ServiceManagement_OWL2.n3";
	
	
    @Override
    public void start(BundleContext context) throws Exception {
		// forceLog4jConfig();
		org.appdapter.fancy.log.Log4JUtils.setupScanTestLogging();
		scheduleFrameworkStartEventHandler(context);
    }

    @Override 
    protected void handleFrameworkStartedEvent(BundleContext context) {        
       Test2Go test = new Test2Go();
	   
	   Properties props = new Properties();
	   props.put("bundleContext", "http://www.w3.org/2002/07/owl#bundleContextSpec");
	   
	   ManagedService ms = new OSGiComponent(context, new SimpleLifecycle(context, BundleContext.class), props);
	   ms.start();
	   
	   //TODO: open models here and pass them to Test2Go
	   test.registerServiceManagerEntites(SERVICE_MODEL_PATH);
	   test.startSpecExtender(context, null,SERVICE_MODEL_PATH,SVN_MGMT_ONTOLOGY_MODEL_PATH);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
    }
}
