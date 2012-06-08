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
package org.jflux.api.core.node.chain;

import java.util.List;
import org.jflux.api.core.node.ProcessorNode;
import org.jflux.api.core.Listener;
import org.jflux.api.core.Notifier;

/**
 *
 * @author Matthew Stevenson <www.jflux.org>
 */
public class ProcessorChain<In,Out> extends 
        NodeChain implements ProcessorNode<In, Out> {
    private ProcessorNode<In,?> myHeadNode;
    private ProcessorNode<?,Out> myTailNode;
    private List<ProcessorNode> myNodes;

    ProcessorChain(List<ProcessorNode> nodes) {
        super(nodes);
        if(nodes.isEmpty()){
            throw new IllegalArgumentException();
        }
        myHeadNode = nodes.get(0);
        myTailNode = nodes.get(nodes.size()-1);
        myNodes = nodes;
    }
    
    @Override
    public Listener<In> getListener() {
        return myHeadNode.getListener();
    }

    @Override
    public Notifier<Out> getNotifier() {
        return myTailNode.getNotifier();
    }
    
    public List<ProcessorNode> getProcessorNodes(){
        return myNodes;
    }
}
