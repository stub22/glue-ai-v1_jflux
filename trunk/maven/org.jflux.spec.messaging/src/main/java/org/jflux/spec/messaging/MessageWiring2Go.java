package org.jflux.spec.messaging;

import org.ontoware.rdf2go.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.rdf.model.InfModel;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import java.util.ArrayList;
import java.util.Properties;
import org.ontoware.rdf2go.model.node.Resource;
import org.jflux.impl.services.rk.lifecycle.ManagedService;
import org.jflux.impl.services.rk.lifecycle.utils.SimpleLifecycle;
import org.osgi.framework.BundleContext;
import org.jflux.impl.services.rk.osgi.lifecycle.OSGiComponent;
import org.jflux.spec.messaging.rdf2go.MsgSvcConf;
import org.jflux.spec.messaging.rdf2go.MSCConnection;
import org.jflux.onto.common.osgi.OSGiServPropBinding;



public class MessageWiring2Go {
	private final Model model2go;
	
//	public MessageWiring2Go(String path, String ontoPath) {
//		com.hp.hpl.jena.rdf.model.Model jenaModel = RDFDataMgr.loadModel(path);
//		com.hp.hpl.jena.rdf.model.Model ontoModel = RDFDataMgr.loadModel(ontoPath);
//		com.hp.hpl.jena.rdf.model.Model unionModel = ModelFactory.createUnion(jenaModel,ontoModel);
//		Reasoner reasoner = ReasonerRegistry.getRDFSReasoner();
//		InfModel infModel = ModelFactory.createInfModel(reasoner, unionModel);
//		model2go = new org.ontoware.rdf2go.impl.jena.ModelImplJena(infModel);
//		model2go.open();
//	}
	
	
	public MessageWiring2Go(Model inputModel){
		
		if(inputModel.isOpen()){
			model2go=inputModel;
		} else {
			model2go=inputModel;
			model2go.open();
		}
	}
	
	public ArrayList<ManagedService> loadMsgServices(BundleContext ctx){
		
		
		ClosableIterator iter= MsgSvcConf.getAllInstances(model2go);
		ArrayList<ManagedService> services = new ArrayList<ManagedService>();
		
		while(iter.hasNext()){
			Resource smResource = (Resource)iter.next();
			MsgSvcConf msg_service= MsgSvcConf.getInstance(model2go,smResource.asURI());
			Properties properties =getPropertyMap(msg_service);
			ManagedService ms = new OSGiComponent(ctx, new SimpleLifecycle(msg_service, msg_service.getClass()), properties);
			
			ms.start();
			services.add(ms);
		}
		
		return services;
	}
	
	private Properties getPropertyMap(MsgSvcConf msgSvcConf) {
		Properties properties = new Properties();
		ClosableIterator iter= msgSvcConf.getAllOSGiServPropBinding();
		while (iter.hasNext()) {
			Resource obj = (Resource) iter.next();
			OSGiServPropBinding ps = OSGiServPropBinding.getInstance(model2go, obj.asURI());
			properties.put(ps.getServPropKey().getServPropKeyName(), ps.getServPropValue());
		}

		return properties;
	}
	
}
