/*
 * Copyright 2013 The JFlux Project (www.jflux.org).
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
package org.jflux.api.service.binding;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.jflux.api.core.util.DefaultSource;
import org.jflux.api.registry.Descriptor;
import org.jflux.api.registry.Reference;
import org.jflux.api.registry.Registry;
import org.jflux.api.service.binding.BindingSpec.BindingStrategy;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 *
 * @author matt
 */
public class SingleDepencyTrackerTest {
    private DefaultSource<Boolean> createdSource = new DefaultSource<Boolean>(false);
    
    public SingleDepencyTrackerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of eagerAdd method, of class SingleDepencyTracker.
     */
    @Test
    public void testEagerAdd() {
        Registry reg = mock(Registry.class);
        final Reference[] refs = new Reference[]{mock(Reference.class),mock(Reference.class),mock(Reference.class)};
        final List<Reference> refList = Arrays.asList(refs);
        final Object[] objs = new Object[]{new Object(), new Object(), new Object()};
        when(reg.findAll(any(Descriptor.class))).thenReturn(Collections.EMPTY_LIST);
        when(reg.retrieve(any(Reference.class))).thenAnswer(new Answer<Object>() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                int i = refList.lastIndexOf(invocation.getArguments()[0]);
                return i == -1 ? null : objs[i];
            }
        });
        Descriptor desc = mock(Descriptor.class);
        createdSource.setValue(false);
        SingleDepencyTracker instance = new SingleDepencyTracker("depName", BindingStrategy.EAGER, createdSource);
        instance.start(reg, desc);
        assertNull(instance.getTrackedDependency());
        
        instance.myTracker.addReference(refs[0]);
        assertEquals(objs[0], instance.getTrackedDependency());
        
        instance.myTracker.addReference(refs[1]);
        assertEquals(objs[1], instance.getTrackedDependency());
        
        instance.myTracker.addReference(refs[2]);
        assertEquals(objs[2], instance.getTrackedDependency());
        
        instance.myTracker.removeReference(refs[2]);
        assertEquals(objs[1], instance.getTrackedDependency());
        
        instance.myTracker.removeReference(refs[0]);
        assertEquals(objs[1], instance.getTrackedDependency());
        
        instance.myTracker.addReference(refs[2]);
        assertEquals(objs[2], instance.getTrackedDependency());
        
        instance.myTracker.addReference(refs[2]);
        assertEquals(objs[2], instance.getTrackedDependency());
        
        instance.myTracker.addReference(refs[0]);
        assertEquals(objs[0], instance.getTrackedDependency());
        
        instance.myTracker.addReference(refs[1]);
        assertEquals(objs[1], instance.getTrackedDependency());
        
        instance.myTracker.removeReference(refs[1]);
        assertEquals(objs[0], instance.getTrackedDependency());
        
        instance.myTracker.removeReference(refs[0]);
        assertEquals(objs[2], instance.getTrackedDependency());
        
        instance.myTracker.removeReference(refs[2]);
        assertNull(instance.getTrackedDependency());
    }

    /**
     * Test of lazyAdd method, of class SingleDepencyTracker.
     */
    @Test
    public void testLazyAdd() {
        Registry reg = mock(Registry.class);
        final Reference[] refs = new Reference[]{mock(Reference.class),mock(Reference.class),mock(Reference.class)};
        final List<Reference> refList = Arrays.asList(refs);
        final Object[] objs = new Object[]{new Object(), new Object(), new Object()};
        when(reg.findAll(any(Descriptor.class))).thenReturn(Collections.EMPTY_LIST);
        when(reg.retrieve(any(Reference.class))).thenAnswer(new Answer<Object>() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                int i = refList.lastIndexOf(invocation.getArguments()[0]);
                return i == -1 ? null : objs[i];
            }
        });
        Descriptor desc = mock(Descriptor.class);
        createdSource.setValue(false);
        SingleDepencyTracker instance = new SingleDepencyTracker("depName", BindingStrategy.LAZY, createdSource);
        instance.start(reg, desc);
        assertNull(instance.getTrackedDependency());
        
        instance.myTracker.addReference(refs[0]);
        assertEquals(objs[0], instance.getTrackedDependency());
        
        instance.myTracker.addReference(refs[1]);
        assertEquals(objs[0], instance.getTrackedDependency());
        
        instance.myTracker.addReference(refs[2]);
        assertEquals(objs[0], instance.getTrackedDependency());
        
        instance.myTracker.removeReference(refs[2]);
        assertEquals(objs[0], instance.getTrackedDependency());
        
        instance.myTracker.removeReference(refs[0]);
        assertEquals(objs[1], instance.getTrackedDependency());
        
        instance.myTracker.addReference(refs[2]);
        assertEquals(objs[1], instance.getTrackedDependency());
        
        instance.myTracker.addReference(refs[2]);
        assertEquals(objs[1], instance.getTrackedDependency());
        
        instance.myTracker.addReference(refs[0]);
        assertEquals(objs[1], instance.getTrackedDependency());
        
        instance.myTracker.addReference(refs[1]);
        assertEquals(objs[1], instance.getTrackedDependency());
        
        instance.myTracker.removeReference(refs[1]);
        assertEquals(objs[2], instance.getTrackedDependency());
        
        instance.myTracker.removeReference(refs[2]);
        assertEquals(objs[0], instance.getTrackedDependency());
        
        instance.myTracker.removeReference(refs[0]);
        assertNull(instance.getTrackedDependency());
    }

    /**
     * Test of getTrackedDependency method, of class SingleDepencyTracker.
     */
    @Test
    public void testGetTrackedDependency_Eager() {
        Registry reg = mock(Registry.class);
        final Reference[] refs = new Reference[]{mock(Reference.class),mock(Reference.class),mock(Reference.class)};
        final List<Reference> refList = Arrays.asList(refs);
        final Object[] objs = new Object[]{new Object(), new Object(), new Object()};
        when(reg.findAll(any(Descriptor.class))).thenReturn(Collections.EMPTY_LIST);
        when(reg.retrieve(any(Reference.class))).thenAnswer(new Answer<Object>() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                int i = refList.lastIndexOf(invocation.getArguments()[0]);
                return i == -1 ? null : objs[i];
            }
        });
        Descriptor desc = mock(Descriptor.class);
        SingleDepencyTracker instance = new SingleDepencyTracker("dependencyName", BindingStrategy.EAGER, createdSource);
        instance.start(reg, desc);
        assertNull(instance.getTrackedDependency());
        
        instance.myTracker.addReference(refs[0]);
        assertEquals(objs[0], instance.getTrackedDependency());
        
        instance.myTracker.addReference(refs[1]);
        assertEquals(objs[1], instance.getTrackedDependency());
        
        instance.myTracker.removeReference(refs[1]);
        assertEquals(objs[0], instance.getTrackedDependency());
        
        instance.myTracker.addReference(refs[1]);
        instance.myTracker.removeReference(refs[0]);
        assertEquals(objs[1], instance.getTrackedDependency());
        
        instance.myTracker.removeReference(refs[1]);
        assertNull(instance.getTrackedDependency());
    }
    
    @Test
    public void testGetTrackedDependency_Lazy() {
        Registry reg = mock(Registry.class);
        final Reference[] refs = new Reference[]{mock(Reference.class),mock(Reference.class),mock(Reference.class)};
        final List<Reference> refList = Arrays.asList(refs);
        final Object[] objs = new Object[]{new Object(), new Object(), new Object()};
        when(reg.findAll(any(Descriptor.class))).thenReturn(Collections.EMPTY_LIST);
        when(reg.retrieve(any(Reference.class))).thenAnswer(new Answer<Object>() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                int i = refList.lastIndexOf(invocation.getArguments()[0]);
                return i == -1 ? null : objs[i];
            }
        });
        Descriptor desc = mock(Descriptor.class);
        SingleDepencyTracker instance = new SingleDepencyTracker("dependencyName", BindingStrategy.LAZY, createdSource);
        instance.start(reg, desc);
        assertNull(instance.getTrackedDependency());
        
        instance.myTracker.addReference(refs[0]);
        assertEquals(objs[0], instance.getTrackedDependency());
        
        instance.myTracker.addReference(refs[1]);
        assertEquals(objs[0], instance.getTrackedDependency());
        
        instance.myTracker.removeReference(refs[1]);
        assertEquals(objs[0], instance.getTrackedDependency());
        
        instance.myTracker.addReference(refs[1]);
        instance.myTracker.removeReference(refs[0]);
        assertEquals(objs[1], instance.getTrackedDependency());
        
        instance.myTracker.removeReference(refs[1]);
        assertNull(instance.getTrackedDependency());
    }
}