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
package org.jflux.api.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jflux.api.registry.Descriptor;
import org.jflux.api.registry.Reference;
import org.jflux.api.registry.Registry;
import org.jflux.api.registry.basic.BasicDescriptor;
import org.jflux.api.service.DependencySpec.Cardinality;
import org.jflux.api.service.DependencySpec.UpdateStrategy;
import org.jflux.api.service.binding.BindingSpec;
import org.jflux.api.service.binding.BindingSpec.BindingStrategy;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 *
 * @author matt
 */
public class ServiceManagerTest {
    
    public ServiceManagerTest() {
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
     * Test of start method, of class ServiceManager.
     */
    @Test
    public void testStart() {
        System.out.println("start");
        Registry registry = mock(Registry.class);
        final Reference[] refs = new Reference[]{mock(Reference.class),mock(Reference.class),mock(Reference.class)};
        final List<Reference> refList = Arrays.asList(refs);
        when(registry.findAll(any(Descriptor.class))).thenReturn(refList);
        final Object[] objs = new Object[]{new Object(), new Object(), new Object()};
        when(registry.retrieve(any(Reference.class))).thenAnswer(new Answer<Object>() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                int i = refList.lastIndexOf(invocation.getArguments()[0]);
                return objs[i];
            }
        });
        ServiceLifecycle l = new LifecycleImpl();
        List<DependencySpec> specs = l.getDependencySpecs();
        ServiceLifecycle lifecycle = spy(l);
        RegistrationStrategy strat = mock(RegistrationStrategy.class);
        Map<String, BindingSpec> bind = new HashMap<String, BindingSpec>();
        Descriptor desc0 = new BasicDescriptor(Object.class.getName(), null);
        BindingSpec bind0 = new BindingSpec(specs.get(0), desc0, BindingStrategy.EAGER);
        Descriptor desc1 = new BasicDescriptor(Object.class.getName(), null);
        BindingSpec bind1 = new BindingSpec(specs.get(1), desc1, BindingStrategy.EAGER);
        Descriptor desc2 = new BasicDescriptor(Object.class.getName(), null);
        BindingSpec bind2 = new BindingSpec(specs.get(2), desc2, BindingStrategy.EAGER);
        bind.put(specs.get(0).getDependencyName(), bind0);
        bind.put(specs.get(1).getDependencyName(), bind1);
        bind.put(specs.get(2).getDependencyName(), bind2);
        ServiceManager instance = new ServiceManager(
                lifecycle, bind, strat, null);
        instance.start(registry);
        verify(strat).register(any(Map.class));
    }
    

    /**
     * Test of stop method, of class ServiceManager.
     */
    @Test
    public void testStop() {
        System.out.println("stop");
        ServiceManager instance = null;
        instance.stop();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isSatisfied method, of class ServiceManager.
     */
    @Test
    public void testIsSatisfied() {
        System.out.println("isSatisfied");
        ServiceManager instance = null;
        boolean expResult = false;
        boolean result = instance.isSatisfied();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isAvailable method, of class ServiceManager.
     */
    @Test
    public void testIsAvailable() {
        System.out.println("isAvailable");
        ServiceManager instance = null;
        boolean expResult = false;
        boolean result = instance.isAvailable();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
    class LifecycleImpl implements ServiceLifecycle<Map> {

        @Override
        public List<DependencySpec> getDependencySpecs() {
            return Arrays.asList(
                    new DependencySpec("dependencyA", Object.class.getName(), 
                            Cardinality.MANDATORY_UNARY, UpdateStrategy.DYNAMIC, null),
                    new DependencySpec("dependencyB", Object.class.getName(), 
                            Cardinality.MANDATORY_UNARY, UpdateStrategy.DYNAMIC, null),
                    new DependencySpec("dependencyC", Object.class.getName(), 
                            Cardinality.MANDATORY_UNARY, UpdateStrategy.DYNAMIC, null));
            
        }

        @Override
        public Map createService(Map dependencyMap) {
            return dependencyMap;
        }

        @Override
        public Map handleDependencyChange(Map service, String changeType, String dependencyName, Object dependency, Map<String,Object> availableDependencies) {
            return availableDependencies;
        }

        @Override
        public void disposeService(Map service, Map availableDependencies) {
        }

        @Override
        public String[] getServiceClassNames() {
            return new String[]{Map.class.getName()};
        }
    }
}