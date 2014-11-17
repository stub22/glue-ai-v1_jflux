/*
 * Copyright 2011 Hanson Robokind LLC.
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


package org.jflux.impl.services.rk.property;

import java.beans.PropertyChangeSupport;
import javax.swing.event.SwingPropertyChangeSupport;

/**
 * Helper methods for PropertyChangeSupport
 * 
 * @author Matthew Stevenson <www.robokind.org>
 */
public class PropertyChangeUtils {
    private static boolean theSwingFlag = false;

    /**
     * Sets the Swing flag.  If false, <code>buildPropertyChangeSupport</code>
     * creates standard PropertyChangeSupport.  If true, it creates
     * SwingPropertyChangeSupport.
     * 
     * @param val the value for the Swing flag
     */
    public static void setSwing(boolean val){
        theSwingFlag = val;
    }

    /**
     * Returns the Swing flag value
     * @return the Swing flag.
     */
    public static boolean getSwingFlag(){
        return theSwingFlag;
    }

    /**
     * Creates a new PropertyChangeSupport.  If the Swing flag is set to true,
     * this creates a SwingPropertyChangeSupport.
     * 
     * @param sourceBean source for the PropertyChangeSupport
     * @return new PropertyChangeSupport
     */
    public static PropertyChangeSupport buildPropertyChangeSupport(Object sourceBean){
        if(theSwingFlag){
            return new SwingPropertyChangeSupport(sourceBean);
        }else{
            return new PropertyChangeSupport(sourceBean);
        }
    }
}
