/*
 * Copyright 2012 The JFlux Project (www.jflux.org).
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
package org.jflux.api.encode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import org.jflux.api.core.Adapter;

/**
 *
 * @author Matthew Stevenson
 */
public class BytesUtils {
    
    public static Adapter<ByteArrayOutputStream,ByteBuffer> 
            outputStreamToBuffer(){
        return new OutputStreamToBuffer();
    }
    
    public static Adapter<ByteBuffer,ByteArrayInputStream> 
            bufferToInputStream(){
        return new BufferToInputStream();
    }
    
    public static Adapter<ByteArrayOutputStream,ByteArrayInputStream> 
            outputStreamToInputStream(){
        return new OutputStreamToInputStream();
    }
    
    public static class OutputStreamToBuffer implements 
            Adapter<ByteArrayOutputStream,ByteBuffer> {
        @Override public ByteBuffer adapt(ByteArrayOutputStream a) {
            return ByteBuffer.wrap(a.toByteArray());
        }        
    }
    public static class BufferToInputStream implements 
            Adapter<ByteBuffer,ByteArrayInputStream> {
        @Override public ByteArrayInputStream adapt(ByteBuffer a) {
            return new ByteArrayInputStream(a.array());
        }
    }
    public static class OutputStreamToInputStream implements 
            Adapter<ByteArrayOutputStream,ByteArrayInputStream> {
        @Override public ByteArrayInputStream adapt(ByteArrayOutputStream a) {
            return new ByteArrayInputStream(a.toByteArray());
        }
    }
}
