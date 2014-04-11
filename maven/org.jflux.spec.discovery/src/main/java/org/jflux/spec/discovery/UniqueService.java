 /*
 * Copyright 2014 the Friendularity Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jflux.spec.discovery;

import java.util.Map;

/**
 *
 * @author Amy Jessica Book <jgpallack@gmail.com>
 */
public class UniqueService {
    private String myIPAddress;
    private String mySerial;
    private Map<String, String> myProperties;
    
    public UniqueService(
            String ipAddress, String serial, Map<String, String> properties) {
        myIPAddress = ipAddress;
        mySerial = serial;
        myProperties = properties;
    }
    
    public String getIPAddress() {
        return myIPAddress;
    }
    
    public String getSerial() {
        return mySerial;
    }
    
    public Map<String, String> getProperties() {
        return myProperties;
    }
    
    @Override
    public boolean equals(Object other) {
        if(other instanceof UniqueService) {
            UniqueService oService = (UniqueService)other;
            
            return oService.getIPAddress().equals(myIPAddress) &&
                    oService.getSerial().equals(mySerial);
        }
        
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash +
                (this.myIPAddress != null ? this.myIPAddress.hashCode() : 0);
        hash = 47 * hash +
                (this.mySerial != null ? this.mySerial.hashCode() : 0);
        hash = 47 * hash +
                (this.myProperties != null ? this.myProperties.hashCode() : 0);
        return hash;
    }
}
