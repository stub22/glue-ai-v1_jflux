/*
 * Copyright 2014 the JFlux Project.
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

package org.jflux.api.common.rk.config;

import org.jflux.impl.services.rk.utils.GlobalIdentifier;
import org.jflux.impl.services.rk.utils.HashCodeUtil;
import org.jflux.api.common.rk.property.PropertyChangeNotifier;

/**
 * Defines a version.
 * 
 * @author Matthew Stevenson <www.jflux.org>
 */
public class VersionProperty extends PropertyChangeNotifier implements 
        Cloneable, GlobalIdentifier{
    /**
     * Property String for version name.
     */
    public final static String PROP_NAME = "name";
    /**
     * Property String for version number.
     */
    public final static String PROP_NUMBER = "number";

    private String myName;
    private String myNumber;

    /**
     * Creates a new VersionPropety with the given name and number.
     * @param name name for the new VersionProperty
     * @param number number for the new VersionProperty
     */
    public VersionProperty(String name, String number){
        if(name == null || number == null){
            throw new NullPointerException();
        }
        myName = name;
        myNumber = number;
    }

    /**
     * Returns the version name.
     * @return version name
     */
    public String getName(){
        return myName;
    }

    /**
     * Set the version name
     * @param name new version name
     */
    public void setName(String name){
        if(name == null){
            throw new NullPointerException();
        }
        String old = myName;
        myName = name;
        firePropertyChange(PROP_NAME, old, myName);
    }

    /**
     * Returns the version number.
     * @return version number
     */
    public String getNumber(){
        return myNumber;
    }

    /**
     * Set the version number
     * @param num new version number
     */
    public void setNumber(String num){
        if(num == null){
            throw new NullPointerException();
        }
        String old = myNumber;
        myNumber = num;
        firePropertyChange(PROP_NUMBER, old, myNumber);
    }

    @Override
    public boolean equals(Object obj){
        if(obj == null ||  obj.getClass() != this.getClass()){
            return false;
        }
        VersionProperty vp = (VersionProperty)obj;
        return myName.equals(vp.myName) && myNumber.equals(vp.myNumber);
    }

    @Override
    public int hashCode() {
        return HashCodeUtil.hash(HashCodeUtil.SEED, myName, myNumber);
    }

    @Override
    public String toString(){
        return String.format("Version{%s, %s}", myName, myNumber);
    }

    /**
     * Returns a string representing the version which is formatted for 
     * displaying.
     * @return string representing the version which is formatted for displaying
     */
    public String display(){
        return String.format("%s - %s", myName, myNumber);
    }
    
    @Override
    public Object clone(){
        return new VersionProperty(myName, myNumber);
    }
}
