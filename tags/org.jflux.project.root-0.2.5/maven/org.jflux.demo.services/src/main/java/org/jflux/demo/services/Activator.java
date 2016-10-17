package org.jflux.demo.services;

import org.osgi.framework.BundleContext;
import org.appdapter.osgi.core.BundleActivatorBase;
import org.jflux.spec.services.Test2Go;
import org.jflux.impl.services.rk.lifecycle.ManagedService;
import org.jflux.impl.services.rk.osgi.lifecycle.OSGiComponent;
import org.jflux.impl.services.rk.lifecycle.utils.SimpleLifecycle;
import java.util.Properties;
import org.jflux.spec.messaging.MessageWiring2Go;
import org.apache.jena.riot.RDFDataMgr;
import org.ontoware.rdf2go.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.rdf.model.InfModel;


public class Activator extends BundleActivatorBase {

	//SERVICE_MODEL_PATH is the turtle file with the information for the services to be launched.
	//SVM_MGMT_ONTOLOGY_MODEL_PATH is the ontology that SERVICE_MODEL_PATH uses. 
	//MSG_MODEL_PATH is the model for the spec messaging services. 
	private final static String SERVICE_MODEL_PATH = "file:../org.jflux.spec.services/src/main/resources/org/jflux/spec/services/ServiceTest.ttl";
	private final static String SVM_MGMT_ONTOLOGY_MODEL_PATH = "file:../org.jflux.spec.services/src/main/resources/org/jflux/spec/services/ServiceManagement_OWL2.n3";
	private final static String MSG_MODEL_PATH = "file:../org.jflux.spec.messaging/src/main/resources/org/jflux/spec/messaging/connections.ttl";
	private final static String SVC_MSG_ONTOLOGY_MODEL_PATH = "file:../org.jflux.spec.messaging/src/main/resources/org/jflux/spec/messaging/MessageSvc_OWL2.n3";

	@Override
	public void start(BundleContext context) throws Exception {
		// forceLog4jConfig();
		org.appdapter.fancy.log.Log4JUtils.setupScanTestLogging();
		scheduleFrameworkStartEventHandler(context);
	}

	@Override
	protected void handleFrameworkStartedEvent(BundleContext context) {
		com.hp.hpl.jena.rdf.model.Model jenaServiceModel = RDFDataMgr.loadModel(SERVICE_MODEL_PATH);
		Model model2go = new org.ontoware.rdf2go.impl.jena.ModelImplJena(jenaServiceModel);
		model2go.open();
		
		MessageWiring2Go msg = new MessageWiring2Go(getInfModel(MSG_MODEL_PATH, SVC_MSG_ONTOLOGY_MODEL_PATH));
		msg.loadMsgServices(context);

		Test2Go test = new Test2Go();
		Properties props = new Properties();
		props.put("bundleContext", "http://www.w3.org/2002/07/owl#bundleContextSpec");

		ManagedService ms = new OSGiComponent(context, new SimpleLifecycle(context, BundleContext.class), props);
		ms.start();

		test.registerServiceManagerEntites(model2go);
		test.startSpecExtender(context, null, model2go);
	}
	
	
	private Model getInfModel(String ttlPath, String ontoPath){
		com.hp.hpl.jena.rdf.model.Model jenaModel = RDFDataMgr.loadModel(ttlPath);
		com.hp.hpl.jena.rdf.model.Model ontoModel = RDFDataMgr.loadModel(ontoPath);
		com.hp.hpl.jena.rdf.model.Model unionModel = ModelFactory.createUnion(jenaModel,ontoModel);
		Reasoner reasoner = ReasonerRegistry.getRDFSReasoner();
		InfModel infModel = ModelFactory.createInfModel(reasoner, unionModel);
		Model model2go = new org.ontoware.rdf2go.impl.jena.ModelImplJena(infModel);
		
		model2go.open();
		
		return model2go;
	}
	
	
	@Override
	public void stop(BundleContext context) throws Exception {
	}
}
