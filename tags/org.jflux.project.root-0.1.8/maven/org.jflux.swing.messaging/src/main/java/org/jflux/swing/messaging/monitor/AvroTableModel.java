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
package org.jflux.swing.messaging.monitor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;

/**
 *
 * @author Matthew Stevenson <www.robokind.org>
 */
public class AvroTableModel extends AbstractTableModel {
    private Schema mySchema;
    private List<IndexedRecord> myRecords;
    
    public AvroTableModel(Schema schema, List<IndexedRecord> records) {
        setSchema(schema);
        setRecords(records);
    }
    
    public AvroTableModel(Schema schema) {
        this(schema, null);
    }
    
    public AvroTableModel() {
        this(null, null);
    }
    
    public final void setSchema(Schema schema){
        mySchema = schema;
    }
    
    public final void setRecords(List<IndexedRecord> records){
        if(records != null) {
            myRecords = records;
        } else {
            myRecords = new ArrayList<IndexedRecord>();
        }
    }
    
    @Override
    public int getRowCount() {
        return myRecords.size();
    }

    @Override
    public int getColumnCount() {
        if(mySchema == null) {
            return 0;
        }
        
        return mySchema.getFields().size();
    }

    @Override
    public String getColumnName(int columnIndex) {
        if(mySchema == null) {
            return null;
        } else if(columnIndex >= mySchema.getFields().size()) {
            return null;
        }
        
        return mySchema.getFields().get(columnIndex).name();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if(mySchema == null) {
            return null;
        } else if(columnIndex >= mySchema.getFields().size()) {
            return null;
        }
        
        return mySchema.getFields().get(columnIndex).getClass();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if(rowIndex >= myRecords.size()) {
            return null;
        }
        
        if(getColumnName(columnIndex).equals("timestampMillisecUTC")) {
            SimpleDateFormat sdf =
                    new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
            return sdf.format(myRecords.get(rowIndex).get(columnIndex));
        }
        
        return myRecords.get(rowIndex).get(columnIndex);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if(rowIndex < myRecords.size()) {
            myRecords.get(rowIndex).put(columnIndex, aValue);
        }
    }
}
