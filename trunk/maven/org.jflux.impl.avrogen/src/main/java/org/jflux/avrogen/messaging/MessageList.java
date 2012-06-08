package org.jflux.avrogen.messaging;

import java.nio.ByteBuffer;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericArray;
import org.apache.avro.specific.SpecificRecord;
import org.apache.avro.specific.SpecificRecordBase;

@SuppressWarnings("all")
public class MessageList extends SpecificRecordBase implements SpecificRecord {
    public static final Schema SCHEMA$ = Schema.parse("{\"type\":\"record\",\"name\":\"MessageList\",\"namespace\":\"org.jflux.avrogen.messaging\",\"fields\":[{\"name\":\"header\",\"type\":\"bytes\"},{\"name\":\"messages\",\"type\":{\"type\":\"array\",\"items\":\"bytes\"}}]}");
    public ByteBuffer header;
    public GenericArray<ByteBuffer> messages;

    @Override
    public Object get(int field$) {
        switch (field$) {
            case 0: return header;
            case 1: return messages;
            default: throw new AvroRuntimeException("Bad index");
        }
    }

    @SuppressWarnings(value = "unchecked")
    @Override
    public void put(int field$, Object value$) {
        switch (field$) {
            case 0: header = (ByteBuffer) value$; break;
            case 1: messages = (GenericArray<ByteBuffer>) value$; break;
            default: throw new AvroRuntimeException("Bad index");
        }
    }

    @Override
    public Schema getSchema() {
        return SCHEMA$;
    }
}
