/*
 * Copyright 2013 Hanson Robokind LLC.
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
package org.jflux.swing.messaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import org.apache.avro.Schema;
import org.jflux.api.core.Listener;

/**
 *
 * @author Amy Jessica Book <jgpallack@gmail.com>
 */
public class AvroComboBoxModel extends DefaultComboBoxModel implements Listener {
    private List<Schema> mySchemas;
    private String selected;
    private OSGiSchemaSelector mySelector;
    
    public AvroComboBoxModel(OSGiSchemaSelector selector) {
        mySelector = selector;
        mySchemas = new ArrayList<Schema>();
        setSortedSchemas();
        mySelector.getChangeNotifier().addListener(this);
    }

    @Override
    public void setSelectedItem(Object o) {
        for(Schema schema: mySchemas) {
            if(schema.getName().equals(o.toString())) {
                selected = schema.getName();
            }
        }
    }

    @Override
    public Object getSelectedItem() {
        return selected;
    }

    @Override
    public int getSize() {
        return mySchemas.size();
    }

    @Override
    public Object getElementAt(int i) {
        if(mySchemas.isEmpty()) {
            return null;
        } else {
            return mySchemas.get(i).getName();
        }
    }

    @Override
    public synchronized void handleEvent(Object t) {
        updateSchemaList();
    }
    
    private void setSortedSchemas(){
        List<Schema> newList = mySelector.getSchemas();
        Collections.sort(newList, new Comparator<Schema>() {
            @Override
            public int compare(Schema t, Schema t1) {
                return t.getName().compareTo(t1.getName());
            }
        });
        mySchemas.clear();
        mySchemas.addAll(newList);
        if(selected == null){
            if(!mySchemas.isEmpty()){
                selected = mySchemas.get(0).getName();
            }
        }else{
            boolean selFound = false;
            for(Schema s : mySchemas){
                if(selected.equals(s.getName())){
                    selFound = true;
                    break;
                }
            }
            if(!selFound){
                if(!mySchemas.isEmpty()){
                    selected = mySchemas.get(0).getName();
                }else{
                    selected = null;
                }
            }
        }
    }
    
    private synchronized void updateSchemaList(){
        setSortedSchemas();
        fireContentsChanged(this, 0, mySchemas.size() - 1);
    }
}
