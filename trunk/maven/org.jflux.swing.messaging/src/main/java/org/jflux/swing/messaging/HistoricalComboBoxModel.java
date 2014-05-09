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

package org.jflux.swing.messaging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.Preferences;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author Amy Jessica Book <jgpallack@gmail.com>
 */


public class HistoricalComboBoxModel extends DefaultComboBoxModel {
    private List<String> myEntries;
    private String selected;
    private Preferences myPrefs;
    private String myKey;
    private String myDefVal;
    
    public HistoricalComboBoxModel(String node, String key, String defVal) {
        myPrefs = Preferences.userRoot().node(node);
        myKey = key;
        myDefVal = defVal;
        String histString = myPrefs.get(myKey, myDefVal);
        String[] history = histString.split("¤");
        myEntries = new ArrayList<String>();
        myEntries.addAll(Arrays.asList(history));
        
        if(!myEntries.isEmpty()) {
            selected = myEntries.get(0);
        } else {
            selected = null;
        }
    }
    
    private static String join(List<String> list, String sep) {
        StringBuilder sb = new StringBuilder();
        
        if(list.isEmpty()) {
            return "";
        }
        
        sb.append(list.get(0));
        
        for(int i = 1; i < list.size(); i++) {
            sb.append(sep);
            sb.append(list.get(i));
        }
        
        return sb.toString();
    }

    @Override
    public void setSelectedItem(Object anItem) {
        if(!myEntries.contains(anItem.toString())) {
            myEntries.add(anItem.toString());
            
            String history = join(myEntries, "¤");
            myPrefs.put(myKey, history);
        }
        
        selected = anItem.toString();
        
        fireContentsChanged(this, 0, myEntries.size() - 1);
    }

    @Override
    public Object getSelectedItem() {
        return selected;
    }

    @Override
    public int getSize() {
        return myEntries.size();
    }

    @Override
    public Object getElementAt(int index) {
        if(myEntries.isEmpty()) {
            return null;
        } else {
            return myEntries.get(index);
        }
    }
    
    public void clear() {
        myEntries = new ArrayList<String>();
        selected = "";
        myPrefs.put(myKey, myDefVal);
    }
    
    public void deleteSelected() {
        myEntries.remove(selected);
        
        if(!myEntries.isEmpty()) {
            selected = myEntries.get(0);
        } else {
            selected = null;
        }
        
        String history = join(myEntries, "¤");
        myPrefs.put(myKey, history);
        fireContentsChanged(this, 0, myEntries.size() - 1);
    }
}
