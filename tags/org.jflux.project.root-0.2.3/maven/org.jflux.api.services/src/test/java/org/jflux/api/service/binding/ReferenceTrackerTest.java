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
import org.jflux.api.core.Listener;
import org.jflux.api.core.util.DefaultSource;
import org.jflux.api.registry.Descriptor;
import org.jflux.api.registry.Reference;
import org.jflux.api.registry.Registry;
import org.jflux.api.registry.RegistryEvent;
import org.jflux.api.registry.basic.BasicRegistryEvent;
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
public class ReferenceTrackerTest {
    
    public ReferenceTrackerTest() {
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
     * Test of start method, of class ReferenceTracker.
     */
    @Test
    public void testStart_FindsAllAndAddsListener() {
        Registry reg = mock(Registry.class);
        when(reg.findAll(any(Descriptor.class))).thenReturn(Collections.EMPTY_LIST);
        Descriptor desc = mock(Descriptor.class);
        
        ReferenceTracker instance = new ReferenceTracker();
        instance.start(reg, desc);
        
        verify(reg).findAll(desc);
        verify(reg).addListener(eq(desc), any(Listener.class));
    }
    
    @Test
    public void testStart_CollectsReferences() {
        Registry reg = mock(Registry.class);
        List<Reference> refs = Arrays.asList(mock(Reference.class),mock(Reference.class),mock(Reference.class));
        when(reg.findAll(any(Descriptor.class))).thenReturn(refs);
        Descriptor desc = mock(Descriptor.class);
        
        ReferenceTracker instance = new ReferenceTracker();
        instance.start(reg, desc);
        
        List<Reference> availRefs = instance.getAvailableReferences();
        assertEquals(refs.size(), availRefs.size());
        for(int i=0; i<refs.size(); i++){
            assertEquals(refs.get(i), availRefs.get(i));
        }
    }
    
    @Test
    public void testStart_DoesNotRetrieveItems() {
        Registry reg = mock(Registry.class);
        List<Reference> refs = Arrays.asList(mock(Reference.class),mock(Reference.class),mock(Reference.class));
        when(reg.findAll(any(Descriptor.class))).thenReturn(refs);
        Descriptor desc = mock(Descriptor.class);
        
        ReferenceTracker instance = new ReferenceTracker();
        instance.start(reg, desc);
        
        verify(reg, never()).retrieve(any(Class.class), any(Reference.class));
        verify(reg, never()).retrieve(any(Reference.class));
        assertEquals(0, instance.getTrackedServices().size());
    }


    /**
     * Test of stop method, of class ReferenceTracker.
     */
    @Test
    public void testStop_RemovesRefsAndStopsListening() {
        Registry reg = mock(Registry.class);
        List<Reference> refs = Arrays.asList(mock(Reference.class),mock(Reference.class),mock(Reference.class));
        when(reg.findAll(any(Descriptor.class))).thenReturn(refs);
        Descriptor desc = mock(Descriptor.class);
        
        ReferenceTracker instance = new ReferenceTracker();
        instance.start(reg, desc);
        assertEquals(refs.size(), instance.getAvailableReferences().size());
        
        instance.stop();
        
        assertEquals(0, instance.getAvailableReferences().size());
        verify(reg).removeListener(any(Listener.class));
    }
    
    @Test
    public void test_TrackerListensForRegisteredServices(){
        Registry reg = mock(Registry.class);
        when(reg.findAll(any(Descriptor.class))).thenReturn(Collections.EMPTY_LIST);
        Descriptor desc = mock(Descriptor.class);
        final DefaultSource<Listener<RegistryEvent>> lSource = new DefaultSource<Listener<RegistryEvent>>(null);
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                lSource.setValue((Listener)invocation.getArguments()[1]);
                return null;
            }
        }).when(reg).addListener(eq(desc), any(Listener.class));
        
        ReferenceTracker instance = new ReferenceTracker();
        instance.start(reg, desc);
        assertEquals(0, instance.getAvailableReferences().size());
        
        Listener<RegistryEvent> listener = lSource.getValue();
        assertNotNull(listener);
        
        Reference add = mock(Reference.class);
        listener.handleEvent(new BasicRegistryEvent(RegistryEvent.REGISTERED, add));
        assertEquals(1, instance.getAvailableReferences().size());
        assertEquals(add, instance.getAvailableReferences().get(0));
        
        listener.handleEvent(new BasicRegistryEvent(RegistryEvent.MODIFIED, add));
        assertEquals(1, instance.getAvailableReferences().size());
        assertEquals(add, instance.getAvailableReferences().get(0));
        
        listener.handleEvent(new BasicRegistryEvent(RegistryEvent.MODIFIED_ENDMATCH, add));
        assertEquals(0, instance.getAvailableReferences().size());
        
        listener.handleEvent(new BasicRegistryEvent(RegistryEvent.REGISTERED, add));
        assertEquals(1, instance.getAvailableReferences().size());
        assertEquals(add, instance.getAvailableReferences().get(0));
        
        listener.handleEvent(new BasicRegistryEvent(RegistryEvent.UNREGISTERING, add));
        assertEquals(0, instance.getAvailableReferences().size());
    }

    /**
     * Test of getAvailableReferences method, of class ReferenceTracker.
     */
    @Test
    public void testGetAvailableReferences() {
        Registry reg = mock(Registry.class);
        List<Reference> refs = Arrays.asList(mock(Reference.class),mock(Reference.class),mock(Reference.class));
        when(reg.findAll(any(Descriptor.class))).thenReturn(refs);
        Descriptor desc = mock(Descriptor.class);
        
        ReferenceTracker instance = new ReferenceTracker();
        instance.start(reg, desc);
        
        List<Reference> availRefs = instance.getAvailableReferences();
        assertEquals(refs.size(), availRefs.size());
        for(int i=0; i<refs.size(); i++){
            assertEquals(refs.get(i), availRefs.get(i));
        }
    }

    /**
     * Test of getTrackedReferences method, of class ReferenceTracker.
     */
    @Test
    public void testGetTrackedReferences() {
        Registry reg = mock(Registry.class);
        final List<Reference> refs = Arrays.asList(mock(Reference.class),mock(Reference.class),mock(Reference.class));
        final List svcs = Arrays.asList(new Object(), new Object(), new Object());
        when(reg.findAll(any(Descriptor.class))).thenReturn(refs);
        when(reg.retrieve(any(Reference.class))).thenAnswer(new Answer<Object>() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                int i = refs.lastIndexOf(invocation.getArguments()[0]);
                return svcs.get(i);
            }
        });
        Descriptor desc = mock(Descriptor.class);
        
        ReferenceTracker instance = new ReferenceTracker();
        instance.start(reg, desc);
        assertEquals(refs.size(), instance.getAvailableReferences().size());
        
        assertEquals(0, instance.getTrackedReferences().size());
        Object obj0 = instance.getService(refs.get(0));
        assertEquals(svcs.get(0), obj0);
        assertEquals(1, instance.getTrackedReferences().size());
        assertEquals(refs.get(0), instance.getTrackedReferences().get(0));
        
        Object obj2 = instance.getService(refs.get(2));
        assertEquals(svcs.get(2), obj2);
        assertEquals(2, instance.getTrackedReferences().size());
        assertEquals(refs.get(2), instance.getTrackedReferences().get(1));
        
        instance.releaseReference(refs.get(0));
        assertEquals(1, instance.getTrackedReferences().size());
        assertEquals(refs.get(2), instance.getTrackedReferences().get(0));
        
        obj0 = instance.getService(refs.get(0));
        assertEquals(svcs.get(0), obj0);
        assertEquals(2, instance.getTrackedReferences().size());
        assertEquals(refs.get(2), instance.getTrackedReferences().get(0));
        assertEquals(refs.get(0), instance.getTrackedReferences().get(1));
        
        obj2 = instance.getService(refs.get(2));
        assertEquals(svcs.get(2), obj2);
        assertEquals(2, instance.getTrackedReferences().size());
        assertEquals(refs.get(2), instance.getTrackedReferences().get(0));
        assertEquals(refs.get(0), instance.getTrackedReferences().get(1));
        
        instance.releaseReference(refs.get(1));
        assertEquals(2, instance.getTrackedReferences().size());
        assertEquals(refs.get(2), instance.getTrackedReferences().get(0));
        assertEquals(refs.get(0), instance.getTrackedReferences().get(1));
        
        Object obj1 = instance.getService(refs.get(1));
        assertEquals(svcs.get(1), obj1);
        assertEquals(3, instance.getTrackedReferences().size());
        assertEquals(refs.get(2), instance.getTrackedReferences().get(0));
        assertEquals(refs.get(0), instance.getTrackedReferences().get(1));
        assertEquals(refs.get(1), instance.getTrackedReferences().get(2));
        
        instance.releaseReference(refs.get(2));
        assertEquals(2, instance.getTrackedReferences().size());
        assertEquals(refs.get(0), instance.getTrackedReferences().get(0));
        assertEquals(refs.get(1), instance.getTrackedReferences().get(1));
        
        instance.releaseReference(refs.get(1));
        assertEquals(1, instance.getTrackedReferences().size());
        assertEquals(refs.get(0), instance.getTrackedReferences().get(0));
        
        instance.releaseReference(refs.get(0));
    }

    /**
     * Test of getTrackedService method, of class ReferenceTracker.
     */
    @Test
    public void testGetTrackedService() {
        Registry reg = mock(Registry.class);
        final List<Reference> refs = Arrays.asList(mock(Reference.class),mock(Reference.class),mock(Reference.class));
        final List svcs = Arrays.asList(new Object(), new Object(), new Object());
        when(reg.findAll(any(Descriptor.class))).thenReturn(refs);
        when(reg.retrieve(any(Reference.class))).thenAnswer(new Answer<Object>() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                int i = refs.lastIndexOf(invocation.getArguments()[0]);
                return svcs.get(i);
            }
        });
        Descriptor desc = mock(Descriptor.class);
        
        ReferenceTracker instance = new ReferenceTracker();
        instance.start(reg, desc);
        assertEquals(refs.size(), instance.getAvailableReferences().size());
        assertNull(instance.getTrackedService(refs.get(0)));
        assertNull(instance.getTrackedService(refs.get(1)));
        assertNull(instance.getTrackedService(refs.get(2)));
        
        instance.getService(refs.get(0));
        assertEquals(svcs.get(0), instance.getTrackedService(refs.get(0)));
        assertNull(instance.getTrackedService(refs.get(1)));
        assertNull(instance.getTrackedService(refs.get(2)));
        
        instance.getService(refs.get(2));
        assertEquals(svcs.get(0), instance.getTrackedService(refs.get(0)));
        assertNull(instance.getTrackedService(refs.get(1)));
        assertEquals(svcs.get(2), instance.getTrackedService(refs.get(2)));
        
        instance.releaseReference(refs.get(0));
        assertNull(instance.getTrackedService(refs.get(0)));
        assertNull(instance.getTrackedService(refs.get(1)));
        assertEquals(svcs.get(2), instance.getTrackedService(refs.get(2)));
        
        instance.getService(refs.get(1));
        assertNull(instance.getTrackedService(refs.get(0)));
        assertEquals(svcs.get(1), instance.getTrackedService(refs.get(1)));
        assertEquals(svcs.get(2), instance.getTrackedService(refs.get(2)));
        
        instance.releaseReference(refs.get(2));
        assertNull(instance.getTrackedService(refs.get(0)));
        assertEquals(svcs.get(1), instance.getTrackedService(refs.get(1)));
        assertNull(instance.getTrackedService(refs.get(2)));
        
        instance.releaseReference(refs.get(1));
        assertNull(instance.getTrackedService(refs.get(0)));
        assertNull(instance.getTrackedService(refs.get(1)));
        assertNull(instance.getTrackedService(refs.get(2)));
    }

    /**
     * Test of getService method, of class ReferenceTracker.
     */
    @Test
    public void testGetService_Reference() {
        Registry reg = mock(Registry.class);
        final List<Reference> refs = Arrays.asList(mock(Reference.class),mock(Reference.class),mock(Reference.class));
        final List svcs = Arrays.asList(new Object(), new Object(), new Object());
        when(reg.findAll(any(Descriptor.class))).thenReturn(refs);
        when(reg.retrieve(any(Reference.class))).thenAnswer(new Answer<Object>() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                int i = refs.lastIndexOf(invocation.getArguments()[0]);
                return svcs.get(i);
            }
        });
        Descriptor desc = mock(Descriptor.class);
        
        ReferenceTracker instance = new ReferenceTracker();
        instance.start(reg, desc);
        
        assertEquals(svcs.get(0), instance.getService(refs.get(0)));
        assertEquals(svcs.get(1), instance.getService(refs.get(1)));
        assertEquals(svcs.get(1), instance.getService(refs.get(1)));
        assertEquals(svcs.get(2), instance.getService(refs.get(2)));
        assertNull(instance.getTrackedService(mock(Reference.class)));
        
        instance.removeReference(refs.get(1));
        assertEquals(svcs.get(0), instance.getService(refs.get(0)));
        assertNull(instance.getService(refs.get(1)));
        assertEquals(svcs.get(2), instance.getService(refs.get(2)));
    }

    /**
     * Test of getService method, of class ReferenceTracker.
     */
    @Test
    public void testGetService_0args() {
        Registry reg = mock(Registry.class);
        final Reference[] refs = new Reference[]{mock(Reference.class),mock(Reference.class),mock(Reference.class)};
        final List<Reference> refList = Arrays.asList(refs);
        final Object[] objs = new Object[]{new Object(), new Object(), new Object()};
        when(reg.findAll(any(Descriptor.class))).thenReturn(Arrays.asList(refs));
        when(reg.retrieve(any(Reference.class))).thenAnswer(new Answer<Object>() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                int i = refList.lastIndexOf(invocation.getArguments()[0]);
                return objs[i];
            }
        });
        Descriptor desc = mock(Descriptor.class);
        
        ReferenceTracker instance = new ReferenceTracker();
        instance.start(reg, desc);
        
        assertEquals(objs[0], instance.getService());
        assertEquals(objs[0], instance.getService());
        
        instance.getService(refs[2]);
        assertEquals(objs[0], instance.getService());
        
        instance.releaseReference(refs[0]);
        assertEquals(objs[2], instance.getService());
        
        instance.getService(refs[0]);
        instance.getService(refs[1]);
        assertEquals(objs[2], instance.getService());
        
        instance.releaseReference(refs[0]);
        assertEquals(objs[2], instance.getService());
        
        instance.releaseReference(refs[2]);
        assertEquals(objs[1], instance.getService());
        
        instance.getService(refs[2]);
        assertEquals(objs[1], instance.getService());
        
        instance.releaseReference(refs[1]);
        instance.releaseReference(refs[2]);
        assertEquals(objs[0], instance.getService());
    }

    /**
     * Test of retrieveAllServices method, of class ReferenceTracker.
     */
    @Test
    public void testRetrieveAllServices() {
        Registry reg = mock(Registry.class);
        final Reference[] refs = new Reference[]{mock(Reference.class),mock(Reference.class),mock(Reference.class)};
        final List<Reference> refList = Arrays.asList(refs);
        final Object[] objs = new Object[]{new Object(), new Object(), new Object()};
        when(reg.findAll(any(Descriptor.class))).thenReturn(Arrays.asList(refs));
        when(reg.retrieve(any(Reference.class))).thenAnswer(new Answer<Object>() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                int i = refList.lastIndexOf(invocation.getArguments()[0]);
                return objs[i];
            }
        });
        Descriptor desc = mock(Descriptor.class);
        
        ReferenceTracker instance = new ReferenceTracker();
        instance.start(reg, desc);
        
        List retrieve = instance.retrieveAllServices();
        assertEquals(3, retrieve.size());
        assertEquals(objs[0], retrieve.get(0));
        assertEquals(objs[1], retrieve.get(1));
        assertEquals(objs[2], retrieve.get(2));
        
        instance.releaseReference(refs[0]);
        retrieve = instance.retrieveAllServices();
        assertEquals(3, retrieve.size());
        assertEquals(objs[0], retrieve.get(0));
        assertEquals(objs[1], retrieve.get(1));
        assertEquals(objs[2], retrieve.get(2));
        
        instance.removeReference(refs[1]);
        retrieve = instance.retrieveAllServices();
        assertEquals(2, retrieve.size());
        assertEquals(objs[0], retrieve.get(0));
        assertEquals(objs[2], retrieve.get(1));
    }

    /**
     * Test of getTrackedServices method, of class ReferenceTracker.
     */
    @Test
    public void testGetTrackedServices() {
        Registry reg = mock(Registry.class);
        final Reference[] refs = new Reference[]{mock(Reference.class),mock(Reference.class),mock(Reference.class)};
        final List<Reference> refList = Arrays.asList(refs);
        final Object[] objs = new Object[]{new Object(), new Object(), new Object()};
        when(reg.findAll(any(Descriptor.class))).thenReturn(Arrays.asList(refs));
        when(reg.retrieve(any(Reference.class))).thenAnswer(new Answer<Object>() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                int i = refList.lastIndexOf(invocation.getArguments()[0]);
                return objs[i];
            }
        });
        Descriptor desc = mock(Descriptor.class);
        
        ReferenceTracker instance = new ReferenceTracker();
        instance.start(reg, desc);
        
        assertEquals(0, instance.getTrackedServices().size());
        
        instance.retrieveAllServices();
        List svcs = instance.getTrackedServices();
        assertEquals(3, svcs.size());
        assertEquals(objs[0], svcs.get(0));
        assertEquals(objs[1], svcs.get(1));
        assertEquals(objs[2], svcs.get(2));
        
        instance.releaseReference(refs[1]);
        svcs = instance.getTrackedServices();
        assertEquals(2, svcs.size());
        assertEquals(objs[0], svcs.get(0));
        assertEquals(objs[2], svcs.get(1));
        
        instance.getService(refs[1]);
        svcs = instance.getTrackedServices();
        assertEquals(3, svcs.size());
        assertEquals(objs[0], svcs.get(0));
        assertEquals(objs[2], svcs.get(1));
        assertEquals(objs[1], svcs.get(2));
        
        instance.releaseReference(refs[0]);
        instance.releaseReference(refs[1]);
        svcs = instance.getTrackedServices();
        assertEquals(1, svcs.size());
        assertEquals(objs[2], svcs.get(0));
        
        instance.releaseReference(refs[2]);
        svcs = instance.getTrackedServices();
        assertEquals(0, svcs.size());
        
        Object obj = instance.getService();
        svcs = instance.getTrackedServices();
        assertEquals(1, svcs.size());
        assertEquals(obj, svcs.get(0));
    }

    /**
     * Test of addReference method, of class ReferenceTracker.
     */
    @Test
    public void testAddReference() {
        Registry reg = mock(Registry.class);
        final Reference[] refs = new Reference[]{mock(Reference.class),mock(Reference.class),mock(Reference.class)};
        when(reg.findAll(any(Descriptor.class))).thenReturn(Collections.EMPTY_LIST);
        Descriptor desc = mock(Descriptor.class);
        
        ReferenceTracker instance = new ReferenceTracker();
        instance.start(reg, desc);
        
        assertEquals(0, instance.getReferenceCount());
        
        instance.addReference(refs[0]);
        assertEquals(1, instance.getReferenceCount());
        assertEquals(refs[0], instance.getAvailableReferences().get(0));
        
        instance.addReference(refs[0]);
        assertEquals(1, instance.getReferenceCount());
        assertEquals(refs[0], instance.getAvailableReferences().get(0));
        
        instance.addReference(refs[1]);
        assertEquals(2, instance.getReferenceCount());
        assertEquals(refs[0], instance.getAvailableReferences().get(0));
        assertEquals(refs[1], instance.getAvailableReferences().get(1));
        
        instance.addReference(refs[2]);
        assertEquals(3, instance.getReferenceCount());
        assertEquals(refs[0], instance.getAvailableReferences().get(0));
        assertEquals(refs[1], instance.getAvailableReferences().get(1));
        assertEquals(refs[2], instance.getAvailableReferences().get(2));
        
        instance.addReference(refs[2]);
        assertEquals(3, instance.getReferenceCount());
        assertEquals(refs[0], instance.getAvailableReferences().get(0));
        assertEquals(refs[1], instance.getAvailableReferences().get(1));
        assertEquals(refs[2], instance.getAvailableReferences().get(2));
        
        assertEquals(0, instance.getTrackedReferences().size());
    }

    /**
     * Test of removeReference method, of class ReferenceTracker.
     */
    @Test
    public void testRemoveReference() {
        Registry reg = mock(Registry.class);
        final Reference[] refs = new Reference[]{mock(Reference.class),mock(Reference.class),mock(Reference.class)};
        final List<Reference> refList = Arrays.asList(refs);
        final Object[] objs = new Object[]{new Object(), new Object(), new Object()};
        when(reg.findAll(any(Descriptor.class))).thenReturn(Arrays.asList(refs));
        when(reg.retrieve(any(Reference.class))).thenAnswer(new Answer<Object>() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                int i = refList.lastIndexOf(invocation.getArguments()[0]);
                return objs[i];
            }
        });
        Descriptor desc = mock(Descriptor.class);
        
        ReferenceTracker instance = new ReferenceTracker();
        instance.start(reg, desc);
        assertEquals(3, instance.getReferenceCount());
        assertEquals(refs[0], instance.getAvailableReferences().get(0));
        assertEquals(refs[1], instance.getAvailableReferences().get(1));
        assertEquals(refs[2], instance.getAvailableReferences().get(2));
        
        instance.removeReference(refs[1]);
        assertEquals(2, instance.getReferenceCount());
        assertEquals(refs[0], instance.getAvailableReferences().get(0));
        assertEquals(refs[2], instance.getAvailableReferences().get(1));
        
        instance.removeReference(refs[1]);
        assertEquals(2, instance.getReferenceCount());
        assertEquals(refs[0], instance.getAvailableReferences().get(0));
        assertEquals(refs[2], instance.getAvailableReferences().get(1));
        
        instance.removeReference(mock(Reference.class));
        assertEquals(2, instance.getReferenceCount());
        assertEquals(refs[0], instance.getAvailableReferences().get(0));
        assertEquals(refs[2], instance.getAvailableReferences().get(1));
        
        instance.removeReference(refs[0]);
        assertEquals(1, instance.getReferenceCount());
        assertEquals(refs[2], instance.getAvailableReferences().get(0));
        
        instance.removeReference(refs[2]);
        assertEquals(0, instance.getReferenceCount());
        
        instance.removeReference(refs[0]);
        instance.removeReference(refs[1]);
        instance.removeReference(refs[2]);
        assertEquals(0, instance.getReferenceCount());
    }

    /**
     * Test of releaseReference method, of class ReferenceTracker.
     */
    @Test
    public void testReleaseReference() {
        Registry reg = mock(Registry.class);
        final Reference[] refs = new Reference[]{mock(Reference.class),mock(Reference.class),mock(Reference.class)};
        final List<Reference> refList = Arrays.asList(refs);
        final Object[] objs = new Object[]{new Object(), new Object(), new Object()};
        when(reg.findAll(any(Descriptor.class))).thenReturn(Arrays.asList(refs));
        when(reg.retrieve(any(Reference.class))).thenAnswer(new Answer<Object>() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                int i = refList.lastIndexOf(invocation.getArguments()[0]);
                return objs[i];
            }
        });
        Descriptor desc = mock(Descriptor.class);
        
        ReferenceTracker instance = new ReferenceTracker();
        instance.start(reg, desc);
        List<Reference> rs = instance.getAvailableReferences();
        List<Reference> trs = instance.getTrackedReferences();
        List svcs = instance.getTrackedServices();
        assertEquals(3, rs.size());
        assertEquals(0, trs.size());
        assertEquals(0, svcs.size());
        
        instance.retrieveAllServices();
        svcs = instance.getTrackedServices();
        rs = instance.getAvailableReferences();
        trs = instance.getTrackedReferences();
        assertEquals(3, rs.size());
        assertEquals(3, trs.size());
        assertEquals(3, instance.getTrackedCount());
        assertEquals(3, svcs.size());
        assertEquals(refs[0], trs.get(0));
        assertEquals(refs[1], trs.get(1));
        assertEquals(refs[2], trs.get(2));
        
        instance.releaseReference(refs[1]);
        svcs = instance.getTrackedServices();
        rs = instance.getAvailableReferences();
        trs = instance.getTrackedReferences();
        assertEquals(3, rs.size());
        assertEquals(2, trs.size());
        assertEquals(2, instance.getTrackedCount());
        assertEquals(2, svcs.size());
        assertEquals(refs[0], trs.get(0));
        assertEquals(refs[2], trs.get(1));
        
        instance.releaseReference(refs[1]);
        svcs = instance.getTrackedServices();
        rs = instance.getAvailableReferences();
        trs = instance.getTrackedReferences();
        assertEquals(3, rs.size());
        assertEquals(2, trs.size());
        assertEquals(2, instance.getTrackedCount());
        assertEquals(2, svcs.size());
        assertEquals(refs[0], trs.get(0));
        assertEquals(refs[2], trs.get(1));
        
        instance.releaseReference(refs[2]);
        svcs = instance.getTrackedServices();
        rs = instance.getAvailableReferences();
        trs = instance.getTrackedReferences();
        assertEquals(3, rs.size());
        assertEquals(1, trs.size());
        assertEquals(1, instance.getTrackedCount());
        assertEquals(1, svcs.size());
        assertEquals(refs[0], trs.get(0));
        
        instance.releaseReference(mock(Reference.class));
        svcs = instance.getTrackedServices();
        rs = instance.getAvailableReferences();
        trs = instance.getTrackedReferences();
        assertEquals(3, rs.size());
        assertEquals(1, trs.size());
        assertEquals(1, instance.getTrackedCount());
        assertEquals(1, svcs.size());
        assertEquals(refs[0], trs.get(0));
        
        instance.releaseReference(refs[0]);
        svcs = instance.getTrackedServices();
        rs = instance.getAvailableReferences();
        trs = instance.getTrackedReferences();
        assertEquals(3, rs.size());
        assertEquals(0, trs.size());
        assertEquals(0, instance.getTrackedCount());
        assertEquals(0, svcs.size());
    }

    /**
     * Test of getTrackedCount method, of class ReferenceTracker.
     */
    @Test
    public void testGetTrackedCount() {
        Registry reg = mock(Registry.class);
        final Reference[] refs = new Reference[]{mock(Reference.class),mock(Reference.class),mock(Reference.class)};
        final List<Reference> refList = Arrays.asList(refs);
        final Object[] objs = new Object[]{new Object(), new Object(), new Object()};
        when(reg.findAll(any(Descriptor.class))).thenReturn(Arrays.asList(refs));
        when(reg.retrieve(any(Reference.class))).thenAnswer(new Answer<Object>() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                int i = refList.lastIndexOf(invocation.getArguments()[0]);
                return objs[i];
            }
        });
        Descriptor desc = mock(Descriptor.class);
        
        ReferenceTracker instance = new ReferenceTracker();
        instance.start(reg, desc);
        
        assertEquals(0, instance.getTrackedCount());
        
        instance.retrieveAllServices();
        assertEquals(3, instance.getTrackedCount());
        
        instance.releaseReference(refs[1]);
        assertEquals(2, instance.getTrackedCount());
        
        instance.getService(refs[1]);
        assertEquals(3, instance.getTrackedCount());
        
        instance.releaseReference(refs[0]);
        instance.releaseReference(refs[1]);
        assertEquals(1, instance.getTrackedCount());
        
        instance.removeReference(refs[2]);
        assertEquals(0, instance.getTrackedCount());
        
        instance.getService(refs[2]);
        assertEquals(0, instance.getTrackedCount());
        
        instance.getService();
        assertEquals(1, instance.getTrackedCount());
    }

    /**
     * Test of getReferenceCount method, of class ReferenceTracker.
     */
    @Test
    public void testGetReferenceCount() {
        Registry reg = mock(Registry.class);
        final Reference[] refs = new Reference[]{mock(Reference.class),mock(Reference.class),mock(Reference.class)};
        final List<Reference> refList = Arrays.asList(refs);
        final Object[] objs = new Object[]{new Object(), new Object(), new Object()};
        when(reg.findAll(any(Descriptor.class))).thenReturn(Arrays.asList(refs));
        when(reg.retrieve(any(Reference.class))).thenAnswer(new Answer<Object>() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                int i = refList.lastIndexOf(invocation.getArguments()[0]);
                return objs[i];
            }
        });
        Descriptor desc = mock(Descriptor.class);
        
        ReferenceTracker instance = new ReferenceTracker();
        instance.start(reg, desc);
        assertEquals(3, instance.getReferenceCount());
        
        instance.removeReference(refs[1]);
        assertEquals(2, instance.getReferenceCount());
        
        instance.removeReference(refs[1]);
        assertEquals(2, instance.getReferenceCount());
        
        instance.removeReference(mock(Reference.class));
        assertEquals(2, instance.getReferenceCount());
        
        instance.removeReference(refs[0]);
        assertEquals(1, instance.getReferenceCount());
        
        instance.removeReference(refs[2]);
        assertEquals(0, instance.getReferenceCount());
        
        instance.removeReference(refs[0]);
        instance.removeReference(refs[1]);
        instance.removeReference(refs[2]);
        assertEquals(0, instance.getReferenceCount());
        
        instance.addReference(refs[1]);
        assertEquals(1, instance.getReferenceCount());
        
        instance.addReference(refs[2]);
        assertEquals(2, instance.getReferenceCount());
        
        instance.addReference(refs[2]);
        assertEquals(2, instance.getReferenceCount());
        
        instance.removeReference(refs[1]);
        assertEquals(1, instance.getReferenceCount());
        
        instance.addReference(refs[1]);
        assertEquals(2, instance.getReferenceCount());
        
        instance.addReference(refs[0]);
        assertEquals(3, instance.getReferenceCount());
        
        instance.addReference(mock(Reference.class));
        instance.addReference(mock(Reference.class));
        instance.addReference(mock(Reference.class));
        assertEquals(6, instance.getReferenceCount());
    }

    /**
     * Test of isTracked method, of class ReferenceTracker.
     */
    @Test
    public void testIsTracked() {
        Registry reg = mock(Registry.class);
        final Reference[] refs = new Reference[]{mock(Reference.class),mock(Reference.class),mock(Reference.class)};
        final List<Reference> refList = Arrays.asList(refs);
        final Object[] objs = new Object[]{new Object(), new Object(), new Object()};
        when(reg.findAll(any(Descriptor.class))).thenReturn(Arrays.asList(refs));
        when(reg.retrieve(any(Reference.class))).thenAnswer(new Answer<Object>() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                int i = refList.lastIndexOf(invocation.getArguments()[0]);
                return objs[i];
            }
        });
        Descriptor desc = mock(Descriptor.class);
        
        ReferenceTracker instance = new ReferenceTracker();
        instance.start(reg, desc);
        
        assertFalse(instance.isTracked(refs[0]));
        assertFalse(instance.isTracked(refs[1]));
        assertFalse(instance.isTracked(refs[2]));
        
        instance.retrieveAllServices();
        assertTrue(instance.isTracked(refs[0]));
        assertTrue(instance.isTracked(refs[1]));
        assertTrue(instance.isTracked(refs[2]));
        
        instance.releaseReference(refs[1]);
        assertTrue(instance.isTracked(refs[0]));
        assertFalse(instance.isTracked(refs[1]));
        assertTrue(instance.isTracked(refs[2]));
        
        instance.getService(refs[1]);
        assertTrue(instance.isTracked(refs[0]));
        assertTrue(instance.isTracked(refs[1]));
        assertTrue(instance.isTracked(refs[2]));
        
        instance.releaseReference(refs[0]);
        instance.releaseReference(refs[1]);
        assertFalse(instance.isTracked(refs[0]));
        assertFalse(instance.isTracked(refs[1]));
        assertTrue(instance.isTracked(refs[2]));
        
        instance.removeReference(refs[2]);
        assertFalse(instance.isTracked(refs[0]));
        assertFalse(instance.isTracked(refs[1]));
        assertFalse(instance.isTracked(refs[2]));
        
        instance.getService(refs[2]);
        assertFalse(instance.isTracked(refs[0]));
        assertFalse(instance.isTracked(refs[1]));
        assertFalse(instance.isTracked(refs[2]));
        
        instance.getService();
        assertTrue(instance.isTracked(refs[0]));
        assertFalse(instance.isTracked(refs[1]));
        assertFalse(instance.isTracked(refs[2]));
    }
}