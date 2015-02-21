package org.jflux.spec.discovery;

import java.util.HashMap;
import java.util.Map;
import org.appdapter.bind.rdf.jena.assembly.KnownComponentImpl;

/**
 *
 * @author Amy Jessica Book <jgpallack@gmail.com>
 */

public class SerialNumberSpec extends KnownComponentImpl {
    private String mySerialNumber;
    private Map<String, String> myProperties;
    private String myPropString;
    
    public SerialNumberSpec() {
        myProperties = new HashMap<String, String>();
        myPropString = "";
    }
    
    public String getSerialNumber() {
        return mySerialNumber;
    }
    
    public void setSerialNumber(String serialNumber) {
        mySerialNumber = serialNumber;
    }
    
    public void addProperty(String key, String value) {
        myProperties.put(key, value);
        buildPropString();
     }
     
    public void removeProperty(String key) {
        myProperties.remove(key);
        buildPropString();
    }
    
    public String getProperty(String key) {
        return myProperties.get(key);
    }
    
    public String getBroadcastId() {
        StringBuilder sb = new StringBuilder();
        String serial =
                mySerialNumber.replaceAll("%", "%25").replaceAll(" ", "%20");
        sb.append(serial);
        sb.append(myPropString);
        
        return sb.toString();   
    }
    
    private void buildPropString() {
        StringBuilder sb = new StringBuilder();
        
        for(String key: myProperties.keySet()) {
            String value = myProperties.get(key);
            String tmpKey =
                    key.replaceAll(";", "-").replaceAll(",", ".")
                            .replaceAll("%", "%25").replaceAll(" ", "%20");
            String tmpValue =
                    value.replaceAll(";", "-").replaceAll(",", ".")
                            .replaceAll("%", "%25").replaceAll(" ", "%20");
            
            sb.append(";");
            sb.append(tmpKey);
            sb.append(",");
            sb.append(tmpValue);
        }
        
        myPropString = sb.toString();
    }
}
