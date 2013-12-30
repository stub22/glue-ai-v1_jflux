package org.jflux.swing.messaging.player;

import java.util.ArrayList;
import java.util.List;
import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.Schema.Type;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.IndexedRecord;

/**
 *
 * @author Jason G. Pallack <jgpallack@gmail.com>
 */

public class RecordUtils {
    public static Object marshal(Schema schema) {
        Type type = schema.getType();
        
        if(type == Type.STRING) {
            return "";
        } else if(type == Type.BOOLEAN) {
            return Boolean.FALSE;
        } else if(type == Type.DOUBLE) {
            return new Double(0.0);
        } else if(type == Type.FLOAT) {
            return new Float(0.0);
        } else if(type == Type.INT) {
            return new Integer(0);
        } else if(type == Type.LONG) {
            return new Long(0);
        } else if(type == Type.RECORD) {
            IndexedRecord record = new GenericData.Record(schema);
            
            List<Field> fields = schema.getFields();
            for(int i = 0; i < fields.size(); i++) {
                Field field = fields.get(i);
                Schema fieldSchema = field.schema();
                record.put(i, marshal(fieldSchema));
            }
            
            return record;
        } else if(type == Type.ARRAY) {
            return new GenericData.Array(schema, new ArrayList());
        } else {
            return new Object();
        }
    }
    
    public static Long getTimestamp(
            IndexedRecord record, boolean useHeader, int timestampIndex,
            int headerIndex) {
        if(useHeader) {
            IndexedRecord header = (IndexedRecord)record.get(headerIndex);
            return (Long)header.get(timestampIndex);
        } else {
            return (Long)record.get(timestampIndex);
        }
    }
}
