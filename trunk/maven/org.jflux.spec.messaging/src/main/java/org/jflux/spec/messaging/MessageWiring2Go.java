package org.jflux.spec.messaging;

import org.ontoware.rdf2go.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import java.util.ArrayList;
import java.util.Properties;
import org.ontoware.rdf2go.model.node.Resource;
import org.jflux.impl.services.rk.lifecycle.ManagedService;
import org.jflux.impl.services.rk.lifecycle.utils.SimpleLifecycle;
import org.jflux.impl.services.rk.osgi.OSGiUtils;
import org.osgi.framework.BundleContext;
import org.jflux.impl.services.rk.osgi.lifecycle.OSGiComponent;
import org.jflux.spec.messaging.rdf2go.MsgSvcConf;
import org.jflux.onto.common.osgi.OSGiServPropBinding;

/**
 *
 * @author Major Jacquote <mjacquote@gmail.com>
 */


public class MessageWiring2Go {
	private final Model model2go;
	
	public MessageWiring2Go() {
		com.hp.hpl.jena.rdf.model.Model jenaModel = RDFDataMgr.loadModel("");
		model2go = new org.ontoware.rdf2go.impl.jena.ModelImplJena(jenaModel);
		model2go.open();
	}
	
	public ArrayList<ManagedService> loadMsgServices(){
		
		BundleContext ctx = OSGiUtils.getBundleContext(MessageWiring2Go.class);
		
		ClosableIterator iter= MsgSvcConf.getAllInstances(model2go);
		ArrayList<ManagedService> services = new ArrayList<ManagedService>();
		
		while(iter.hasNext()){
			Resource smResource = (Resource)iter.next();
			MsgSvcConf dsi= MsgSvcConf.getInstance(model2go,smResource.asURI());
			Properties properties =getPropertyMap(dsi);
			ManagedService ms = new OSGiComponent(ctx, new SimpleLifecycle(dsi, dsi.getClass()), properties);
			
			ms.start();
			
			services.add(ms);
		}
		
		
		return services;
	}
	
	private Properties getPropertyMap(MsgSvcConf dsi) {
		Properties properties = new Properties();
		ClosableIterator iter= dsi.getAllOSGiServPropBinding();
		while (iter.hasNext()) {
			Resource obj = (Resource) iter.next();
			OSGiServPropBinding ps = OSGiServPropBinding.getInstance(model2go, obj.asURI());
			properties.put(ps.getServPropKey().getServPropKeyName(), ps.getServPropValue());
		}

		return properties;
	}
	
}
