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
package org.jflux.swing.messaging.player;

import java.util.Comparator;
import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.generic.IndexedRecord;

/**
 *
 * @author Amy Jessica Book <jgpallack@gmail.com>
 */
public class TimestampComparator implements Comparator<IndexedRecord> {
    @Override
    public int compare(IndexedRecord o1, IndexedRecord o2) {
        Schema o1Schema = o1.getSchema();
        Schema o2Schema = o2.getSchema();
        int index1 = -1;
        int index2 = -1;
        boolean useHeader1 = false;
        boolean useHeader2 = false;
        int headerIndex1 = -1;
        int headerIndex2 = -1;
        
        for(int i = 0; i < o1Schema.getFields().size(); i++) {
            String fieldName = o1Schema.getFields().get(i).name();
            if(fieldName.equals("header")) {
                useHeader1 = true;
                headerIndex1 = i;
                
                Field field = o1Schema.getFields().get(i);
                Schema schema = field.schema();
                
                for(int j = 0; j < schema.getFields().size(); j++) {
                    String headerFieldName = schema.getFields().get(i).name();
                    
                    if(headerFieldName.equals("timestamp")) {
                        index1 = j;
                        break;
                    }
                }
                
                break;
            } else if(fieldName.equals("timestampMillisecUTC")) {
                index1 = i;
                break;
            }
        }
        
        for(int i = 0; i < o2Schema.getFields().size(); i++) {
            String fieldName = o2Schema.getFields().get(i).name();
            if(fieldName.equals("header")) {
                useHeader2 = true;
                headerIndex2 = i;
                
                Field field = o2Schema.getFields().get(i);
                Schema schema = field.schema();
                
                for(int j = 0; j < schema.getFields().size(); j++) {
                    String headerFieldName = schema.getFields().get(i).name();
                    
                    if(headerFieldName.equals("timestamp")) {
                        index2 = j;
                        break;
                    }
                }
                
                break;
            } else if(fieldName.equals("timestampMillisecUTC")) {
                index2 = i;
                break;
            }
        }
        
        if(index1 < 0 || index2 < 0) {
            return 0;
        }
        
        Long o1Timestamp =
                RecordUtils.getTimestamp(o1, useHeader1, index1, headerIndex1);
        Long o2Timestamp =
                RecordUtils.getTimestamp(o2, useHeader2, index2, headerIndex2);
        
        return o1Timestamp.compareTo(o2Timestamp);
    }
}
