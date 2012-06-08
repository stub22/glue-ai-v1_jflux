/*
 * Copyright 2012 by The JFlux Project (www.jflux.org).
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

package org.jflux.api.data.buffer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jflux.api.core.Adapter;
import org.jflux.api.core.Source;


/**
 * Caches data in a circular buffer with a fixed size.
 *
 * @author Matthew Stevenson <www.jflux.org>
 */
public class CircularBuffer<V> implements Buffer<Integer, V>{
    private List<V> myElements;
    private int myNextIndex;
    private int myHead;
    private int myTail;
    private int myCapacity;

    /**
     * Creates a new CircularBufferCache with the given capacity.
     * @param capacity capacity of the buffer, must be greater than 0
     * @throws IllegalArgumentException if capacity is not greater than 0
     */
    public CircularBuffer(int capacity){
        if(capacity <= 0){
            throw new IllegalArgumentException(
                    "Capacity must be greater than 0.");
        }
        myCapacity = capacity;
        myElements = new ArrayList<V>(myCapacity);
        myNextIndex = 0;
        myHead = 0;
        myTail = 0;
    }
    /**
    * Returns the nth item from the head of the buffer.
    * n=0 returns the head element, n=1 returns head-1,
    * n=size()-1 returns the tail.
    * @param n item index relative to the head
    * @return nth item from the head of the buffer
    * @throws IllegalArgumentException if n less than 0 or n is greater than or
    * equal to the number of elements.
    */
    public V get(Integer n){
        if(n < 0 || n >= myElements.size()){
            throw new IllegalArgumentException(
                    "Index: " + n + " out of bounds.  "
                    + "Buffer capacity is " + myCapacity + ".  "
                    + "Number of elements is " + myElements.size() + ".");
        }
        int index = (myHead-n)%myCapacity;
        return myElements.get(index);
    }
    /**
    * Adds the value to the buffer.
    * If the buffer is at its capacity, the oldest element is removed.
    * @param value data to add to the buffer
    */
    public void add(V value){
        if(myNextIndex < myCapacity && myNextIndex >= myElements.size()){
            myElements.add(value);
        }else{
            myElements.set(myNextIndex, value);
        }
        myHead = myNextIndex;
        myNextIndex = (myNextIndex+1)%myCapacity;
        if(myHead == myTail && myElements.size() > 1){
            myTail = myNextIndex;
        }
    }
    /**
    * Returns the head element of the buffer.
    * The head element is the most recently added element.
    * @return head element of the buffer, returns null if the buffer is empty
    */
    public V getHeadValue(){
        if(myElements.isEmpty()){
            return null;
        }
        return myElements.get(myHead);
    }
    
    public V getTailValue(){
        if(myElements.isEmpty()){
            return null;
        }
        return myElements.get(myHead);
    }
    
    public List<V> getValueList(){
        if(myElements.isEmpty()){
            return Collections.EMPTY_LIST;
        }
        List<V> vals = new ArrayList<V>(myElements.size());
        if(myHead > myTail){
            vals.addAll(myElements.subList(myTail, myHead+1));
        }else{
            vals.addAll(myElements.subList(myTail, myElements.size()));
            vals.addAll(myElements.subList(0, myHead+1));
        }
        return vals;
    }
    /**
     * Returns the number of elements in the buffer.
     * @return number of elements in the buffer
     */
    public int getSize(){
        return myElements.size();
    }

    @Override
    public Source<V> getHead() {
        return new Source<V>() {
            @Override
            public V getValue() {
                return getHeadValue();
            }
        };
    }

    @Override
    public Source<V> getTail() {
        return new Source<V>() {
            @Override
            public V getValue() {
                return getTailValue();
            }
        };
    }

    @Override
    public Adapter<Integer, V> getIndexAdapter(Integer index) {
        return new Adapter<Integer, V>() {
            @Override
            public V adapt(Integer a) {
                return get(a);
            }
        };
    }

    @Override
    public Source<List<V>> getValues() {
        return new Source<List<V>>() {
            @Override
            public List<V> getValue() {
                return getValueList();
            }
        };
    }
}
