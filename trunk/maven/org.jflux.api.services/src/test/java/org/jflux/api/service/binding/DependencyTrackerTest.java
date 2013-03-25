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

import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.LinkedBlockingQueue;
import org.jflux.api.core.util.DefaultSource;
import org.jflux.api.registry.Descriptor;
import org.jflux.api.registry.Reference;
import org.jflux.api.registry.Registry;
import org.jflux.api.service.binding.BindingSpec.BindingStrategy;
import org.jflux.api.service.binding.DependencyTracker.DepRefTracker;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.Matchers.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 *
 * @author matt
 */
public class DependencyTrackerTest {
    private DefaultSource<Boolean> createdSource;
    
    public DependencyTrackerTest() {
        createdSource = new DefaultSource<Boolean>(false);
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
     * Test of start method, of class DependencyTracker.
     */
    @Test
    public void testStart() {
        Registry reg = mock(Registry.class);
        when(reg.findAll(any(Descriptor.class))).thenReturn(Collections.EMPTY_LIST);
        Descriptor desc = mock(Descriptor.class);
        DependencyTracker instance = new DependencyTrackerImpl();
        instance.start(reg, desc);
        verify(instance.myTracker).start(reg, desc);
    }

    /**
     * Test of stop method, of class DependencyTracker.
     */
    @Test
    public void testStop() {
        Registry reg = mock(Registry.class);
        when(reg.findAll(any(Descriptor.class))).thenReturn(Collections.EMPTY_LIST);
        Descriptor desc = mock(Descriptor.class);
        DependencyTracker instance = new DependencyTrackerImpl();
        instance.start(reg, desc);
        instance.stop();
        verify(instance.myTracker).stop();
    }

    /**
     * Test of dependencyAdded method, of class DependencyTracker.
     */
    @Test
    public void testDependencyAdded() {
        Registry reg = mock(Registry.class);
        final Reference[] refs = new Reference[]{mock(Reference.class),mock(Reference.class),mock(Reference.class)};
        final List<Reference> refList = Collections.EMPTY_LIST;
        final Object[] objs = new Object[]{new Object(), new Object(), new Object()};
        when(reg.findAll(any(Descriptor.class))).thenReturn(refList);
        when(reg.retrieve(any(Reference.class))).thenAnswer(new Answer<Object>() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                int i = refList.lastIndexOf(invocation.getArguments()[0]);
                return objs[i];
            }
        });
        Descriptor desc = mock(Descriptor.class);
        DependencyTrackerImpl instance = new DependencyTrackerImpl(BindingStrategy.EAGER);
        instance.start(reg, desc);
        instance.myTracker.addReference(refs[0]);
        instance.myTracker.addReference(refs[1]);
        instance.myTracker.addReference(refs[2]);
        assertEquals(3, instance.eager.size());
        assertEquals(0, instance.lazy.size());
        assertEquals(0, instance.remove.size());
        assertEquals(refs[0], instance.eager.remove());
        assertEquals(refs[1], instance.eager.remove());
        assertEquals(refs[2], instance.eager.remove());
        
        instance = spy(new DependencyTrackerImpl(BindingStrategy.LAZY));
        instance.start(reg, desc);
        instance.myTracker.addReference(refs[0]);
        instance.myTracker.addReference(refs[1]);
        instance.myTracker.addReference(refs[2]);
        assertEquals(3, instance.lazy.size());
        assertEquals(0, instance.eager.size());
        assertEquals(0, instance.remove.size());
        assertEquals(refs[0], instance.lazy.remove());
        assertEquals(refs[1], instance.lazy.remove());
        assertEquals(refs[2], instance.lazy.remove());
    }

    public class DependencyTrackerImpl extends DependencyTracker {
        public Queue<Reference> eager = new LinkedBlockingQueue<Reference>();
        public Queue<Reference> lazy = new LinkedBlockingQueue<Reference>();
        public Queue<Reference> remove = new LinkedBlockingQueue<Reference>();
        
        public DependencyTrackerImpl(BindingStrategy bind) {
            super("dependencyName", bind, createdSource);
            DepRefTracker rt = spy(myTracker);
            myTracker = rt;
        }
        public DependencyTrackerImpl() {
            this(BindingStrategy.LAZY);
        }

        public Object getTrackedDependency() {
            return null;
        }

        public void eagerAdd(Reference ref) {
            eager.add(ref);
        }

        public void lazyAdd(Reference ref) {
            lazy.add(ref);
        }

        public void dependencyRemoved(Reference ref) {
            remove.add(ref);
        }
    }
}