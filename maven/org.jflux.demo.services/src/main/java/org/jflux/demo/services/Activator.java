package org.jflux.demo.services;

import org.osgi.framework.BundleContext;
import org.appdapter.xload.rspec.URLRepoSpec;  // Using this class requires us to import  appdapter.xload
import org.appdapter.osgi.core.BundleActivatorBase;
import org.jflux.spec.services.Test2Go;
import org.jflux.impl.services.rk.lifecycle.ManagedService;
import org.jflux.impl.services.rk.osgi.lifecycle.OSGiComponent;
import org.jflux.impl.services.rk.lifecycle.utils.SimpleLifecycle;
import java.util.Properties;

public class Activator extends BundleActivatorBase {
    //private final static String MERGED_MODEL_MANAGER_QN = "csi:merged_manager_1001";
    
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
	   props.put("bundleContextSpec", "http://www.w3.org/2002/07/owl#bundleContextSpec");
	   
	   ManagedService ms = new OSGiComponent(context, new SimpleLifecycle(context, BundleContext.class), props);
	   ms.start();
	   
	   test.registerServiceManagersThing();
	   test.startSpecExtender(context, null);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
    }
}


// List<ClassLoader> classloaders =
//                ClassLoaderUtils.getFileResourceClassLoaders(
//                context, ClassLoaderUtils.ALL_RESOURCE_CLASSLOADER_TYPES);
//        RepoSpec repoSpec =
//             new URLRepoSpec("HeadlessMessaging/dir.ttl", classloaders);
//        
//        String QUERY_SOURCE_GRAPH_QN = "ccrt:qry_sheet_77";
//        String TGT_GRAPH_SPARQL_VAR = "qGraph";
//        Repo.WithDirectory bmcMemoryRepoHandle = repoSpec.makeRepo();
//        EnhancedRepoClient enhancedRepoSpec = 
//                new EnhancedLocalRepoClient(repoSpec, bmcMemoryRepoHandle,
//                        TGT_GRAPH_SPARQL_VAR, QUERY_SOURCE_GRAPH_QN);
//
//        RegisterWiring.loadAndRegisterSpec(context, enhancedRepoSpec, MERGED_MODEL_MANAGER_QN);
//        RegisterWiring.startSpecExtender(context, null);