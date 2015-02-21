package org.jflux.spec.discovery;

import com.hp.hpl.jena.assembler.Assembler;
import com.hp.hpl.jena.assembler.Mode;
import com.hp.hpl.jena.rdf.model.Resource;
import java.util.List;
import org.appdapter.bind.rdf.jena.assembly.CachingComponentAssembler;
import org.appdapter.bind.rdf.jena.assembly.ItemAssemblyReader;
import org.appdapter.core.item.Item;
import org.appdapter.core.name.Ident;
import org.jflux.spec.services.PropertySpec;

/**
 *
 * @author Amy Jessica Book <jgpallack@gmail.com>
 */


public class SerialNumberSpecBuilder
    extends CachingComponentAssembler<SerialNumberSpec> {
    public SerialNumberSpecBuilder(Resource builderConfRes) {
        super(builderConfRes);
    }
    
    private final static String theSerialNumberNumber =
            "http://www.friendularity.org/discovery#serialNumberNumber";
    private final static String theHasProperty =
            "http://www.cogchar.org/schema/scene#hasProperty";
    
    @Override
    protected Class<SerialNumberSpec> decideComponentClass(
            Ident ident, Item item) {
        return SerialNumberSpec.class;
    }

    @Override
    protected void initExtendedFieldsAndLinks(
            SerialNumberSpec mkc, Item item, Assembler asmblr, Mode mode) {
        ItemAssemblyReader reader = getReader();
        
        mkc.setSerialNumber(reader.readConfigValString(
                item.getIdent(), theSerialNumberNumber, item, "000001"));
        
        // read in and build the linked properties, and storing each in the Spec
        List linkedProperties =
                reader.findOrMakeLinkedObjects(
                item, theHasProperty, asmblr, mode, null);
        
        for(Object lp: linkedProperties) {
            if(!(lp instanceof PropertySpec)) {
                continue;
            }

            PropertySpec propertySpec = (PropertySpec)lp;
            mkc.addProperty(propertySpec.getName(), propertySpec.getValue());
        }
    }
}
