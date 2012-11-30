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

import org.junit.Rule;
import java.util.Collections;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.*;

/**
 *
 * @author Jason G. Pallack <jgpallack@gmail.com>
 */
public class CircularBufferTest {
    
    public CircularBufferTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of get method, of class CircularBuffer.
     */
    @Test
    public void testGet() {
        System.out.println("get");
        
        // Partial buffer
        
        Integer n = 1;
        CircularBuffer<Integer> instance = new CircularBuffer<Integer>(32);
        Integer expResult = 128;
        instance.add(64);
        instance.add(expResult);
        instance.add(256);
        Integer result = instance.get(n);
        assertEquals("Did not return the expected value (expected " + expResult
                + ", got " + result + ")", expResult, result);
        
        // Empty buffer
        
        instance = new CircularBuffer<Integer>(32);
        
        try {
            result = instance.get(n);
            
            fail("Should have given an IllegalArgument exception for an empty list.");
        } catch(IllegalArgumentException ex) {
            // success
        }
        
        // Full buffer
        
        instance = new CircularBuffer<Integer>(3);
        instance.add(64);
        instance.add(expResult);
        instance.add(256);
        result = instance.get(n);
        assertEquals("Did not return the expected value (expected " + expResult
                + ", got " + result + ")", expResult, result);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testGetBounds() throws IllegalArgumentException{
        CircularBuffer<Integer> instance = new CircularBuffer<Integer>(32);
        Integer result = instance.get(1);
    }
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Test
    public void testGetBounds2(){
        thrown.expect(IllegalArgumentException.class);
        CircularBuffer<Integer> instance = new CircularBuffer<Integer>(32);
        Integer result = instance.get(-2);
    }

    /**
     * Test of add method, of class CircularBuffer.
     */
    @Test
    public void testAdd() {
        System.out.println("add");
        
        // Empty buffer
        
        Integer n = 0;
        CircularBuffer<Integer> instance = new CircularBuffer<Integer>(32);
        Integer expResult = 64;
        instance.add(expResult);
        Integer result = instance.get(n);
        assertEquals("Did not return the expected value (expected " + expResult
                + ", got " + result + ")", expResult, result);
        
        // Partial buffer
        
        n = 2;
        expResult = 256;
        instance.add(128);
        instance.add(expResult);
        
        result = instance.get(n);
        assertEquals(new Integer(64), result);
        
        result = instance.get(0);
        assertEquals("Did not return the expected value (expected " + expResult
                + ", got " + result + ")", expResult, result);
        
        // Full buffer
        
        instance = new CircularBuffer<Integer>(3);
        instance.add(64);
        instance.add(128);
        instance.add(expResult);
        
        result = instance.get(0);
        assertEquals("Did not return the expected value (expected " + expResult
                + ", got " + result + ")", expResult, result);
    }

    /**
     * Test of getHeadValue method, of class CircularBuffer.
     */
    @Test
    public void testGetHeadValue() {
        System.out.println("getHeadValue");
        
        // Partial buffer
        
        CircularBuffer<Integer> instance = new CircularBuffer<Integer>(32);
        Integer expResult = 256;
        instance.add(64);
        instance.add(128);
        instance.add(expResult);
        Integer result = instance.getHeadValue();
        assertEquals("Did not return the expected value (expected " + expResult
                + ", got " + result + ")", expResult, result);
        
        // Empty buffer
        
        instance = new CircularBuffer<Integer>(32);
        
        try {
            result = instance.getHeadValue();
            
            fail("Should have given an IndexOutOfBounds exception for an empty list.");
        } catch(IndexOutOfBoundsException ex) {
            // success
        }
        
        // Full buffer
        
        instance = new CircularBuffer<Integer>(3);
        instance.add(64);
        instance.add(128);
        instance.add(expResult);
        result = instance.getHeadValue();
        assertEquals("Did not return the expected value (expected " + expResult
                + ", got " + result + ")", expResult, result);
    }

    /**
     * Test of getTailValue method, of class CircularBuffer.
     */
    @Test
    public void testGetTailValue() {
        System.out.println("getTailValue");
        
        // Partial buffer
        
        CircularBuffer<Integer> instance = new CircularBuffer<Integer>(32);
        Integer expResult = 64;
        instance.add(expResult);
        instance.add(128);
        instance.add(256);
        Integer result = instance.getTailValue();
        assertEquals("Did not return the expected value (expected " + expResult
                + ", got " + result + ")", expResult, result);
        
        // Empty buffer
        
        instance = new CircularBuffer<Integer>(32);
        //ExpectedException.none().expect(IndexOutOfBoundsException.class);
        try {
            result = instance.getTailValue();
            
            fail("Should have given an IndexOutOfBounds exception for an empty list.");
        } catch(IndexOutOfBoundsException ex) {
            // success
        }
        
        // Full buffer
        
        instance = new CircularBuffer<Integer>(3);
        instance.add(expResult);
        instance.add(128);
        instance.add(256);
        result = instance.getTailValue();
        assertEquals("Did not return the expected value (expected " + expResult
                + ", got " + result + ")", expResult, result);
    }

    /**
     * Test of getValueList method, of class CircularBuffer.
     */
    @Test
    public void testGetValueList() {
        System.out.println("getValueList");
        
        // Partuak buffer
        
        CircularBuffer<Integer> instance = new CircularBuffer<Integer>(32);
        Integer expResultSize = 3;
        Integer expResult0 = 64;
        Integer expResult1 = 128;
        Integer expResult2 = 256;
        instance.add(expResult0);
        instance.add(expResult1);
        instance.add(expResult2);
        List<Integer> result = instance.getValueList();
        Integer resultSize = result.size();
        assertEquals("List should be " + expResultSize +
                " elements, is actually " + resultSize + " elements.",
                expResultSize, resultSize);
        assertTrue(expResult0 + " is not in the list.",
                result.contains(expResult0));
        assertTrue(expResult1 + " is not in the list.",
                result.contains(expResult1));
        assertTrue(expResult2 + " is not in the list.",
                result.contains(expResult2));
        
        // Empty buffer
        
        instance = new CircularBuffer<Integer>(32);
        List<Integer> expResult = Collections.EMPTY_LIST;
        result = instance.getValueList();
        
        assertEquals("List should be empty but isn't.", expResult, result);
        
        // Full buffer
        
        instance = new CircularBuffer<Integer>(expResultSize);
        instance.add(expResult0);
        instance.add(expResult1);
        instance.add(expResult2);
        result = instance.getValueList();
        resultSize = result.size();
        assertEquals("List should be " + expResultSize +
                " elements, is actually " + resultSize + " elements.",
                expResultSize, resultSize);
        assertTrue(expResult0 + " is not in the list.",
                result.contains(expResult0));
        assertTrue(expResult1 + " is not in the list.",
                result.contains(expResult1));
        assertTrue(expResult2 + " is not in the list.",
                result.contains(expResult2));
    }

    /**
     * Test of getSize method, of class CircularBuffer.
     */
    @Test
    public void testGetSize() {
        System.out.println("getSize");
        
        // Partial buffer
        
        CircularBuffer<Integer> instance = new CircularBuffer<Integer>(32);
        Integer expResult = 3;
        instance.add(64);
        instance.add(128);
        instance.add(256);
        Integer result = instance.getSize();
        assertEquals("Size should be " + expResult + " elements, is actually "
                + result + " elements.", expResult, result);
        
        // Empty buffer
        
        instance = new CircularBuffer<Integer>(32);
        expResult = 0;
        result = instance.getSize();
        
        assertEquals("Size should be " + expResult + " elements, is actually "
                + result + " elements.", expResult, result);
        
        // Full buffer
        expResult = 3;
        instance = new CircularBuffer<Integer>(expResult);
        instance.add(64);
        instance.add(128);
        instance.add(256);
        result = instance.getSize();
        assertEquals("Size should be " + expResult + " elements, is actually "
                + result + " elements.", expResult, result);
    }

    /**
     * Test of getHead method, of class CircularBuffer.
     */
    @Test
    public void testGetHead() {
        System.out.println("getHead");
        CircularBuffer<Integer> instance = new CircularBuffer<Integer>(32);
        Integer expResult = 256;
        instance.add(64);
        instance.add(128);
        instance.add(expResult);
        Integer result = instance.getHead().getValue();
        assertEquals("Did not return the expected value (expected " + expResult
                + ", got " + result + ")", expResult, result);
        
        // Empty buffer
        
        instance = new CircularBuffer<Integer>(32);
        
        try {
            result = instance.getHead().getValue();
            
            fail("Should have given an IndexOutOfBounds exception for an empty list.");
        } catch(IndexOutOfBoundsException ex) {
            // success
        }
        
        // Full buffer
        
        instance = new CircularBuffer<Integer>(3);
        instance.add(64);
        instance.add(128);
        instance.add(expResult);
        result = instance.getHead().getValue();
        assertEquals("Did not return the expected value (expected " + expResult
                + ", got " + result + ")", expResult, result);
    }

    /**
     * Test of getTail method, of class CircularBuffer.
     */
    @Test
    public void testGetTail() {
        System.out.println("getTail");
        CircularBuffer<Integer> instance = new CircularBuffer<Integer>(32);
        Integer expResult = 64;
        instance.add(expResult);
        instance.add(128);
        instance.add(256);
        Integer result = instance.getTail().getValue();
        assertEquals("Did not return the expected value (expected " + expResult
                + ", got " + result + ")", expResult, result);
        
        // Empty buffer
        
        instance = new CircularBuffer<Integer>(32);
        
        try {
            result = instance.getTail().getValue();
            
            fail("Should have given an IndexOutOfBounds exception for an empty list.");
        } catch(IndexOutOfBoundsException ex) {
            // success
        }
        
        // Full buffer
        
        instance = new CircularBuffer<Integer>(3);
        instance.add(expResult);
        instance.add(128);
        instance.add(256);
        result = instance.getTail().getValue();
        assertEquals("Did not return the expected value (expected " + expResult
                + ", got " + result + ")", expResult, result);
    }

    /**
     * Test of getIndex method, of class CircularBuffer.
     */
    @Test
    public void testGetIndex() {
        System.out.println("getIndex");
        Integer n = 1;
        CircularBuffer<Integer> instance = new CircularBuffer<Integer>(32);
        Integer expResult = 128;
        instance.add(64);
        instance.add(expResult);
        instance.add(256);
        Integer result = instance.getIndex().adapt(n);
        assertEquals("Did not return the expected value (expected " + expResult
                + ", got " + result + ")", expResult, result);
        
        // Empty buffer
        
        instance = new CircularBuffer<Integer>(32);
        
        try {
            result = instance.getIndex().adapt(n);
            
            fail("Should have given an IllegalArgument exception for an empty list.");
        } catch(IllegalArgumentException ex) {
            // success
        }
        
        // Full buffer
        
        instance = new CircularBuffer<Integer>(3);
        instance.add(64);
        instance.add(expResult);
        instance.add(256);
        result = instance.getIndex().adapt(n);
        assertEquals("Did not return the expected value (expected " + expResult
                + ", got " + result + ")", expResult, result);
    }

    /**
     * Test of getValues method, of class CircularBuffer.
     */
    @Test
    public void testGetValues() {
        System.out.println("getValues");
        CircularBuffer<Integer> instance = new CircularBuffer<Integer>(32);
        Integer expResultSize = 3;
        Integer expResult0 = 64;
        Integer expResult1 = 128;
        Integer expResult2 = 256;
        instance.add(expResult0);
        instance.add(expResult1);
        instance.add(expResult2);
        List<Integer> result = instance.getValues().getValue();
        Integer resultSize = result.size();
        assertEquals("List should be " + expResultSize +
                " elements, is actually " + resultSize + " elements.",
                expResultSize, resultSize);
        assertTrue(expResult0 + " is not in the list.",
                result.contains(expResult0));
        assertTrue(expResult1 + " is not in the list.",
                result.contains(expResult1));
        assertTrue(expResult2 + " is not in the list.",
                result.contains(expResult2));
        
        // Empty buffer
        
        instance = new CircularBuffer<Integer>(32);
        List<Integer> expResult = Collections.EMPTY_LIST;
        result = instance.getValues().getValue();
        
        assertEquals("List should be empty but isn't.", expResult, result);
        
        // Full buffer
        
        instance = new CircularBuffer<Integer>(expResultSize);
        instance.add(expResult0);
        instance.add(expResult1);
        instance.add(expResult2);
        result = instance.getValues().getValue();
        resultSize = result.size();
        assertEquals("List should be " + expResultSize +
                " elements, is actually " + resultSize + " elements.",
                expResultSize, resultSize);
        assertTrue(expResult0 + " is not in the list.",
                result.contains(expResult0));
        assertTrue(expResult1 + " is not in the list.",
                result.contains(expResult1));
        assertTrue(expResult2 + " is not in the list.",
                result.contains(expResult2));
    }

    /**
     * Test of addValue method, of class CircularBuffer.
     */
    @Test
    public void testAddValue() {
        System.out.println("addValue");
        
        // Empty buffer
        
        Integer n = 0;
        CircularBuffer<Integer> instance = new CircularBuffer<Integer>(32);
        Integer expResult = 64;
        instance.addValue().handleEvent(expResult);
        Integer result = instance.get(n);
        assertEquals("Did not return the expected value (expected " + expResult
                + ", got " + result + ")", expResult, result);
        
        // Partial buffer
        
        n = 2;
        expResult = 256;
        instance.addValue().handleEvent(128);
        instance.addValue().handleEvent(expResult);
        
        result = instance.get(0);
        assertEquals("Did not return the expected value (expected " + expResult
                + ", got " + result + ")", expResult, result);
        
        // Full buffer
        
        instance = new CircularBuffer<Integer>(3);
        instance.addValue().handleEvent(64);
        instance.addValue().handleEvent(128);
        instance.addValue().handleEvent(expResult);
        
        result = instance.get(0);
        assertEquals("Did not return the expected value (expected " + expResult
                + ", got " + result + ")", expResult, result);
    }
}
