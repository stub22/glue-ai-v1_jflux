package org.jflux.demo.services;

import java.util.List;
import org.osgi.framework.BundleContext;
import org.appdapter.core.boot.ClassLoaderUtils;
import org.appdapter.fancy.rclient.EnhancedRepoClient;
import org.appdapter.fancy.rspec.RepoSpec;
import org.appdapter.fancy.rspec.URLRepoSpec;
import org.appdapter.core.store.Repo;
import org.appdapter.osgi.core.BundleActivatorBase;
import org.jflux.spec.services.RegisterWiring;

public class Activator extends BundleActivatorBase {
    private final static String MERGED_MODEL_MANAGER_QN =
            "csi:merged_manager_1001";
    
    @Override
    public void start(BundleContext context) throws Exception {
        forceLog4jConfig();
		scheduleFrameworkStartEventHandler(context);
    }

    @Override 
    protected void handleFrameworkStartedEvent(BundleContext context) {        
        List<ClassLoader> classloaders =
                ClassLoaderUtils.getFileResourceClassLoaders(
                context, ClassLoaderUtils.ALL_RESOURCE_CLASSLOADER_TYPES);
        RepoSpec repoSpec =
             new URLRepoSpec("HeadlessMessaging/dir.ttl", classloaders);
        
        String QUERY_SOURCE_GRAPH_QN = "ccrt:qry_sheet_77";
        String TGT_GRAPH_SPARQL_VAR = "qGraph";
        Repo.WithDirectory bmcMemoryRepoHandle = repoSpec.makeRepo();
        EnhancedRepoClient enhancedRepoSpec = 
                new EnhancedRepoClient(repoSpec, bmcMemoryRepoHandle,
                        TGT_GRAPH_SPARQL_VAR, QUERY_SOURCE_GRAPH_QN);

        RegisterWiring.loadAndRegisterSpec(context, enhancedRepoSpec, MERGED_MODEL_MANAGER_QN);
        RegisterWiring.startSpecExtender(context, null);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
    }
}
