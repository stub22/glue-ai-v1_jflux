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
import java.util.List;


/**
 * Caches data in a circular buffer with a fixed size.
 *
 * @author Matthew Stevenson <www.jflux.org>
 */
public class CircularBufferCache<V> implements IndexedCache<Integer, V>{
    private List<V> myElements;
    private int next;
    private int head;
    private int tail;
    private int capacity;

    /**
     * Creates a new CircularBufferCache with the given capacity.
     * @param capacity capacity of the buffer, must be greater than 0
     * @throws IllegalArgumentException if capacity is not greater than 0
     */
    public CircularBufferCache(int capacity){
        if(capacity <= 0){
            throw new IllegalArgumentException(
                    "Capacity must be greater than 0.");
        }
        myElements = new ArrayList<V>(capacity);
        next = 0;
        head = 0;
        tail = 0;
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
    @Override
    public V adapt(Integer n){
        if(n < 0 || n >= myElements.size()){
            throw new IllegalArgumentException(
                    "Index: " + n + " out of bounds.  "
                    + "Buffer capacity is " + capacity + ".  "
                    + "Number of elements is " + myElements.size() + ".");
        }
        int index = (head-n)%capacity;
        return myElements.get(index);
    }
    /**
    * Adds the value to the buffer.
    * If the buffer is at its capacity, the oldest element is removed.
    * @param value data to add to the buffer
    */
    @Override
    public void handleEvent(V value){
        if(next < capacity && next >= myElements.size()){
            myElements.add(value);
        }else{
            myElements.set(next, value);
        }
        head = next;
        next = (next+1)%capacity;
        if(head == tail && myElements.size() > 1){
            tail = next;
        }
    }
    /**
    * Returns the head element of the buffer.
    * The head element is the most recently added element.
    * @return head element of the buffer, returns null if the buffer is empty
    */
    @Override
    public V getValue(){
        if(myElements.isEmpty()){
            return null;
        }
        return myElements.get(head);
    }
    /**
     * Returns the number of elements in the buffer.
     * @return number of elements in the buffer
     */
    public int getSize(){
        return myElements.size();
    }
}
