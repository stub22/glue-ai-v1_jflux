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
import org.jflux.api.core.Listener;
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
    
    private Source<V> myHeadSource;
    private Source<V> myTailSource;
    private Source<List<V>> myValuesSource;
    private Adapter<Integer,V> myIndexAdapter;
    private Listener<V> myAdder;

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
        myHeadSource = new Source<V>() {
            @Override
            public V getValue() {
                return getHeadValue();
            }
        };
        myTailSource = new Source<V>() {
            @Override
            public V getValue() {
                return getTailValue();
            }
        };
        myIndexAdapter = new Adapter<Integer, V>() {
            @Override
            public V adapt(Integer a) {
                return get(a);
            }
        };
        myValuesSource = new Source<List<V>>() {
            @Override
            public List<V> getValue() {
                List<V> vals = getValueList();
                reset();
                return vals;
            }
        };
        myAdder = new Listener<V>() {
            @Override
            public void handleEvent(V input) {
                add(input);
            }
        };
    }
    private void reset(){
        myHead = 0;
        myTail = 0;
        myNextIndex = 0;
    }
    /**
    * Returns the nth item from the tail of the buffer.
    * n=0 returns the tail element, n=1 returns tail-1,
    * n=size()-1 returns the head.
    * @param n item index relative to the tail
    * @return nth item from the tail of the buffer
    * @throws IllegalArgumentException if n less than 0 or n is greater than or
    * equal to the number of elements.
    */
    public V get(Integer n){
        if(n < 0 || n >= getSize()){
            throw new IllegalArgumentException(
                    "Index: " + n + " out of bounds.  "
                    + "Buffer capacity is " + myCapacity + ".  "
                    + "Number of elements is " + getSize() + ".");
        }
        int index = (myTail-n)%myCapacity;
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
        boolean empty = myHead == myTail;
        myTail = myNextIndex;
        myNextIndex = (myNextIndex+1)%myCapacity;
        if(myHead == myTail && !empty){
            myHead = myNextIndex;
        }
    }
    /**
    * Returns the head element of the buffer.
    * The head element is the most recently added element.
    * @return head element of the buffer, returns null if the buffer is empty
    */
    public V getHeadValue(){
        if(getSize() == 0){
            return null;
        }
        return myElements.get(myHead);
    }
    
    public V getTailValue(){
        if(getSize() == 0){
            return null;
        }
        return myElements.get(myTail);
    }
    
    public List<V> getValueList(){
        int size = getSize();
        if(size == 0){
            return Collections.EMPTY_LIST;
        }
        List<V> vals = new ArrayList<V>(size);
        if(myTail > myHead){
            vals.addAll(myElements.subList(myHead, myTail+1));
        }else{
            vals.addAll(myElements.subList(myHead, size));
            vals.addAll(myElements.subList(0, myTail+1));
        }
        return vals;
    }
    /**
     * Returns the number of elements in the buffer.
     * @return number of elements in the buffer
     */
    public int getSize(){
        if(myHead == myTail){
            return 0;
        }else if(myTail < myHead){
            return myCapacity - (myHead - myTail - 1);
        }
        return myTail - myHead;
    }

    @Override
    public Source<V> getHead() {
        return myHeadSource;
    }

    @Override
    public Source<V> getTail() {
        return myTailSource;
    }

    @Override
    public Adapter<Integer, V> getIndex() {
        return myIndexAdapter;
    }

    @Override
    public Source<List<V>> getValues() {
        return myValuesSource;
    }

    @Override
    public Listener<V> addValue() {
        return myAdder;
    }
}
