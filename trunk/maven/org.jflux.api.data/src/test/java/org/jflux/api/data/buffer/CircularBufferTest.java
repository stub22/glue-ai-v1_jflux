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

import java.util.Collections;
import java.util.List;
import org.junit.*;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

/**
 *
 * @author Jason G. Pallack <jgpallack@gmail.com>
 */
public class CircularBufferTest {
    
    private CircularBuffer<Integer> emptyInstance;
    private CircularBuffer<Integer> partialInstance;
    private CircularBuffer<Integer> fullInstance;
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
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
        emptyInstance = new CircularBuffer<Integer>(32);
        
        partialInstance = new CircularBuffer<Integer>(32);
        partialInstance.add(64);
        partialInstance.add(128);
        partialInstance.add(256);
        
        fullInstance = new CircularBuffer<Integer>(3);
        fullInstance.add(64);
        fullInstance.add(128);
        fullInstance.add(256);
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of get method, of class CircularBuffer.
     */
    @Test
    public void testGetPartial() {
        System.out.println("get");
        
        Integer n = 1;
        Integer expResult = 128;
        Integer result = partialInstance.get(n);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of get method, of class CircularBuffer.
     */
    @Test
    public void testGetFull() {
        System.out.println("get");
        
        Integer n = 1;
        Integer expResult = 128;
        Integer result = fullInstance.get(n);

        assertEquals(expResult, result);
    }
    
    /**
     * Test of get method, of class CircularBuffer.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testGetEmpty() throws IllegalArgumentException {
        System.out.println("get");
        
        Integer n = 1;
        emptyInstance.get(n);
    }
    
    @Test
    public void testGetEmpty2(){
        System.out.println("get");
        
        thrown.expect(IllegalArgumentException.class);
        emptyInstance.get(-2);
    }

    /**
     * Test of add method, of class CircularBuffer.
     */
    @Test
    public void testAddEmpty() {
        System.out.println("add");
        
        Integer n = 0;
        Integer expResult = 64;
        emptyInstance.add(expResult);
        Integer result = emptyInstance.get(n);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of add method, of class CircularBuffer.
     */
    @Test
    public void testAddPartial() {
        System.out.println("add");

        Integer n = 2;
        Integer m = 0;
        Integer expResultN = 512;
        Integer expResultM = 2048;
        
        partialInstance.add(expResultN);
        partialInstance.add(1024);
        partialInstance.add(expResultM);
        
        Integer result = partialInstance.get(n);

        assertEquals(expResultN, result);

        result = partialInstance.get(m);
        
        assertEquals(expResultM, result);
    }
    
    /**
     * Test of add method, of class CircularBuffer.
     */
    @Test
    public void testAddFull() {
        System.out.println("add");
        
        Integer expResult = 2048;
        Integer n = 0;
        
        fullInstance.add(512);
        fullInstance.add(1024);
        fullInstance.add(expResult);
        
        Integer result = fullInstance.get(n);
        assertEquals(expResult, result);
    }

    /**
     * Test of getHeadValue method, of class CircularBuffer.
     */
    @Test
    public void testGetHeadValueEmpty() {
        System.out.println("getHeadValue");
        
        Integer result = emptyInstance.getHeadValue();
        assertNull(result);
    }
    
    /**
     * Test of getHeadValue method, of class CircularBuffer.
     */
    @Test
    public void testGetHeadValuePartial() {
        System.out.println("getHeadValue");
        
        Integer expResult = 256;
        Integer result = partialInstance.getHeadValue();
        assertEquals(expResult, result);
    }
    
    /**
     * Test of getHeadValue method, of class CircularBuffer.
     */
    @Test
    public void testGetHeadValueFull() {
        System.out.println("getHeadValue");
        
        Integer expResult = 256;
        Integer result = fullInstance.getHeadValue();
        assertEquals(expResult, result);
    }

    /**
     * Test of getTailValue method, of class CircularBuffer.
     */
    @Test
    public void testGetTailValueEmpty() {
        System.out.println("getTailValue");
        
        Integer result = emptyInstance.getTailValue();
        assertNull(result);
    }
    
    /**
     * Test of getTailValue method, of class CircularBuffer.
     */
    @Test
    public void testGetTailValuePartial() {
        System.out.println("getTailValue");
        
        Integer expResult = 64;
        Integer result = partialInstance.getTailValue();
        assertEquals(expResult, result);
    }
    
    /**
     * Test of getTailValue method, of class CircularBuffer.
     */
    @Test
    public void testGetTailValueFull() {
        System.out.println("getTailValue");
        
        Integer expResult = 64;
        Integer result = fullInstance.getTailValue();
        assertEquals(expResult, result);
    }

    /**
     * Test of getValueList method, of class CircularBuffer.
     */
    @Test
    public void testGetValueListEmpty() {
        System.out.println("getValueList");
        
        List<Integer> expResult = Collections.EMPTY_LIST;
        List<Integer> result = emptyInstance.getValueList();
        
        assertEquals(expResult, result);
    }
    
    /**
     * Test of getValueList method, of class CircularBuffer.
     */
    @Test
    public void testGetValueListPartial() {
        System.out.println("getValueList");
        
        Integer expResultSize = 3;
        Integer expResult0 = 64;
        Integer expResult1 = 128;
        Integer expResult2 = 256;
        List<Integer> result = partialInstance.getValueList();
        Integer resultSize = result.size();
        assertEquals(expResultSize, resultSize);
        assertTrue(result.contains(expResult0));
        assertTrue(result.contains(expResult1));
        assertTrue(result.contains(expResult2));
    }
    
    /**
     * Test of getValueList method, of class CircularBuffer.
     */
    @Test
    public void testGetValueListFull() {
        System.out.println("getValueList");
        
        Integer expResultSize = 3;
        Integer expResult0 = 64;
        Integer expResult1 = 128;
        Integer expResult2 = 256;
        List<Integer> result = fullInstance.getValueList();
        Integer resultSize = result.size();
        assertEquals(expResultSize, resultSize);
        assertTrue(result.contains(expResult0));
        assertTrue(result.contains(expResult1));
        assertTrue(result.contains(expResult2));
    }

    /**
     * Test of getSize method, of class CircularBuffer.
     */
    @Test
    public void testGetSizeEmpty() {
        System.out.println("getSize");
        
        Integer expResult = 0;
        Integer result = emptyInstance.getSize();
        
        assertEquals(expResult, result);
    }
    
    /**
     * Test of getSize method, of class CircularBuffer.
     */
    @Test
    public void testGetSizePartial() {
        System.out.println("getSize");
        
        Integer expResult = 3;
        Integer result = partialInstance.getSize();
        assertEquals(expResult, result);
    }
    
    /**
     * Test of getSize method, of class CircularBuffer.
     */
    @Test
    public void testGetSizeFull() {
        System.out.println("getSize");
        
        Integer expResult = 3;
        Integer result = fullInstance.getSize();
        assertEquals(expResult, result);
    }

    /**
     * Test of getHead method, of class CircularBuffer.
     */
    @Test
    public void testGetHeadEmpty() {
        System.out.println("getHead");
        
        Integer result = emptyInstance.getHead().getValue();
        assertNull(result);
    }
    
    /**
     * Test of getHead method, of class CircularBuffer.
     */
    @Test
    public void testGetHeadPartial() {
        System.out.println("getHead");
        
        Integer expResult = 256;
        Integer result = partialInstance.getHead().getValue();
        assertEquals(expResult, result);
    }
    
    /**
     * Test of getHead method, of class CircularBuffer.
     */
    @Test
    public void testGetHeadFull() {
        System.out.println("getHead");
        
        Integer expResult = 256;
        Integer result = fullInstance.getHead().getValue();
        assertEquals(expResult, result);
    }

    /**
     * Test of getTail method, of class CircularBuffer.
     */
    @Test
    public void testGetTailEmpty() {
        System.out.println("getTail");
        
        Integer result = emptyInstance.getTail().getValue();
        assertNull(result);
    }
    
    /**
     * Test of getTail method, of class CircularBuffer.
     */
    @Test
    public void testGetTailPartial() {
        System.out.println("getTail");
        
        Integer expResult = 64;
        Integer result = partialInstance.getTail().getValue();
        assertEquals(expResult, result);
    }
    
    /**
     * Test of getTail method, of class CircularBuffer.
     */
    @Test
    public void testGetTailFull() {
        System.out.println("getTail");
        
        Integer expResult = 64;
        Integer result = fullInstance.getTail().getValue();
        assertEquals(expResult, result);
    }

    /**
     * Test of getIndex method, of class CircularBuffer.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testGetIndexEmpty() throws IllegalArgumentException {
        System.out.println("getIndex");
        
        Integer n = 1;
        emptyInstance.getIndex().adapt(n);
    }
    
    @Test
    public void testGetIndexEmpty2(){
        System.out.println("getIndex");
        
        thrown.expect(IllegalArgumentException.class);
        emptyInstance.getIndex().adapt(-2);
    }
    
    /**
     * Test of getIndex method, of class CircularBuffer.
     */
    @Test
    public void testGetIndexPartial() {
        System.out.println("getIndex");
        
        Integer n = 1;
        Integer expResult = 128;
        Integer result = partialInstance.getIndex().adapt(n);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of getIndex method, of class CircularBuffer.
     */
    @Test
    public void testGetIndexFull() {
        System.out.println("getIndex");
        
        Integer n = 1;
        Integer expResult = 128;
        Integer result = fullInstance.getIndex().adapt(n);
        assertEquals(expResult, result);
    }

    /**
     * Test of getValues method, of class CircularBuffer.
     */
    @Test
    public void testGetValuesEmpty() {
        System.out.println("getValues");
        
        List<Integer> expResult = Collections.EMPTY_LIST;
        List<Integer> result = emptyInstance.getValues().getValue();
        
        assertEquals(expResult, result);
    }
    
    /**
     * Test of getValues method, of class CircularBuffer.
     */
    @Test
    public void testGetValuesPartial() {
        System.out.println("getValues");
        
        Integer expResultSize = 3;
        Integer expResult0 = 64;
        Integer expResult1 = 128;
        Integer expResult2 = 256;
        List<Integer> result = partialInstance.getValues().getValue();
        Integer resultSize = result.size();
        assertEquals(expResultSize, resultSize);
        assertTrue(result.contains(expResult0));
        assertTrue(result.contains(expResult1));
        assertTrue(result.contains(expResult2));
    }
    
    /**
     * Test of getValues method, of class CircularBuffer.
     */
    @Test
    public void testGetValuesFull() {
        System.out.println("getValues");
        
        Integer expResultSize = 3;
        Integer expResult0 = 64;
        Integer expResult1 = 128;
        Integer expResult2 = 256;
        List<Integer> result = fullInstance.getValues().getValue();
        Integer resultSize = result.size();
        assertEquals(expResultSize, resultSize);
        assertTrue(result.contains(expResult0));
        assertTrue(result.contains(expResult1));
        assertTrue(result.contains(expResult2));
    }

    /**
     * Test of addValue method, of class CircularBuffer.
     */
    @Test
    public void testAddValueEmpty() {
        System.out.println("addValue");
        
        Integer n = 0;
        Integer expResult = 64;
        emptyInstance.addValue().handleEvent(expResult);
        Integer result = emptyInstance.get(n);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of addValue method, of class CircularBuffer.
     */
    @Test
    public void testAddValuePartial() {
        System.out.println("addValue");
        
        Integer n = 2;
        Integer m = 0;
        Integer expResultN = 512;
        Integer expResultM = 2048;
        
        partialInstance.addValue().handleEvent(expResultN);
        partialInstance.addValue().handleEvent(1024);
        partialInstance.addValue().handleEvent(expResultM);
        
        Integer result = partialInstance.get(n);

        assertEquals(expResultN, result);

        result = partialInstance.get(m);
        
        assertEquals(expResultM, result);
    }
    
    /**
     * Test of addValue method, of class CircularBuffer.
     */
    @Test
    public void testAddValueFull() {
        System.out.println("addValue");
        
        Integer expResult = 2048;
        Integer n = 0;
        
        fullInstance.addValue().handleEvent(512);
        fullInstance.addValue().handleEvent(1024);
        fullInstance.addValue().handleEvent(expResult);
        
        Integer result = fullInstance.get(n);
        assertEquals(expResult, result);
    }
}
