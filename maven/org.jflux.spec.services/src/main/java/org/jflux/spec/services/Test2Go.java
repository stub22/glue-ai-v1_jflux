package org.jflux.spec.services;



import org.jflux.spec.services.rdf2go.SMEManager;
import org.ontoware.rdf2go.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import java.util.ArrayList;
import org.ontoware.rdf2go.model.node.Resource;
import org.jflux.impl.services.rk.lifecycle.ManagedService;
import org.jflux.impl.services.rk.lifecycle.utils.SimpleLifecycle;
import org.jflux.impl.services.rk.osgi.OSGiUtils;
import org.osgi.framework.BundleContext;
import org.jflux.impl.services.rk.osgi.lifecycle.OSGiComponent;
import org.jflux.api.registry.Registry;
import org.jflux.impl.registry.OSGiRegistry;

/**
 *
 * @author Major Jacquote II <mjacquote@gmail.com>
 *							 <major@robokindrobots.com>
 * 
 */


public class Test2Go {
	
	
	
	public ArrayList<ManagedService> registerServiceManagerEntites(String modelPath) {
		
		
		com.hp.hpl.jena.rdf.model.Model jenaModel = RDFDataMgr.loadModel(modelPath);
		Model model2go = new org.ontoware.rdf2go.impl.jena.ModelImplJena(jenaModel);
		model2go.open();
		BundleContext ctx = OSGiUtils.getBundleContext(Test2Go.class);
				
				
		ClosableIterator iter= SMEManager.getAllInstances(model2go);
		ArrayList<ManagedService> services = new ArrayList<ManagedService>();
		
		while(iter.hasNext()){
			Resource smResource = (Resource)iter.next();
			SMEManager sm = SMEManager.getInstance(model2go,smResource.asURI());
			
			ManagedService ms = new OSGiComponent(ctx, new SimpleLifecycle(sm, SMEManager.class), null);
			
			ms.start();
			
			services.add(ms);
		}
		
		return services;
	}
	
	public ServiceManagerExtender2Go startSpecExtender(
            BundleContext bundleCtx, String optionalSpecFilter, String ttlPath, String ontoPath) {
        Registry reg = new OSGiRegistry(bundleCtx);
        ServiceManagerExtender2Go serviceManagerExtender2go = new ServiceManagerExtender2Go(
                bundleCtx, reg, optionalSpecFilter, ttlPath, ontoPath);
        serviceManagerExtender2go.start();
        return serviceManagerExtender2go;
    }
}
