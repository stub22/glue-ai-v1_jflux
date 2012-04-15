/*
 *  Copyright 2012 by The JFlux Project (www.jflux.org).
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
import org.jflux.api.core.node.ConsumerNode;
import org.jflux.api.core.node.Node;
import org.jflux.api.core.node.ProcessorNode;
import org.jflux.api.core.node.ProducerNode;
import org.jflux.api.core.util.playable.BasicPlayable;
import org.jflux.api.core.util.Notifier;

/**
 *
 * @author Matthew Stevenson <www.robokind.org>
 */
public class NodeChain extends BasicPlayable implements Node{
    private ProducerNode myProducer;
    private List<ProcessorNode> myProcessorList;
    private ConsumerNode myConsumer;
    
    NodeChain(ProducerNode producer, 
            List<ProcessorNode> procs, ConsumerNode consumer){
       myProducer = producer;
       myProcessorList = procs;
       myConsumer = consumer;
    }
    
    @Override
    public boolean start(){
        if(!super.start()){
            return false;
        }
        wire();
        return true;
    }
    
    private void wire(){
        Notifier n = myProducer == null ? null : myProducer.getNotifier();
        for(ProcessorNode proc : myProcessorList){
            if(proc == null){
                continue;
            }
            if(n == null){
                n = proc.getNotifier();
            }else{
                n = wire(n, proc);
            }
            proc.start();
        }
        wire(n,myConsumer);
        myConsumer.start();
        myProducer.start();
    }
    
    private <N,T> Notifier<N> wire(Notifier<T> n, ProcessorNode<T,N> proc){
        if(proc == null){
            return null;
        }
        if(n != null){
            n.addListener(proc.getListener());
        }
        return proc.getNotifier();
    }
    
    private <T> void wire(Notifier<T> n, ConsumerNode<T> consumer){
        if(n == null || consumer == null){
            return;
        }
        n.addListener(consumer.getListener());
    }
}
