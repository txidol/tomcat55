/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.catalina.storeconfig;

import java.io.PrintWriter;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.connector.Connector;

/**
 * Store Connector and Listeners
 * 
 * @author Peter Rossbach
 */
public class ConnectorSF extends StoreFactoryBase {

    /**
     * Store Connector description
     * 
     * @param aWriter
     * @param indent
     * @param aConnector
     * @throws Exception
     */
    public void storeChilds(PrintWriter aWriter, int indent, Object aConnector,
            StoreDescription parentDesc) throws Exception {

        if (aConnector instanceof Connector) {
            Connector connector = (Connector) aConnector;
            // Store nested <Listener> elements
            if (connector instanceof Lifecycle) {
                LifecycleListener listeners[] = ((Lifecycle) connector)
                        .findLifecycleListeners();
                storeElementArray(aWriter, indent, listeners);
            }
        }
    }

    protected void printOpenTag(PrintWriter aWriter, int indent, Object bean,
            StoreDescription aDesc) throws Exception {
        aWriter.print("<");
        aWriter.print(aDesc.getTag());
        storeConnectorAttribtues(aWriter, indent, bean, aDesc);
        aWriter.println(">");
    }

    protected void storeConnectorAttribtues(PrintWriter aWriter, int indent,
            Object bean, StoreDescription aDesc) throws Exception {
        if (aDesc.isAttributes()) {
            getStoreAppender().printAttributes(aWriter, indent, false, bean,
                    aDesc);
            /*
             * if (bean instanceof Connector) { StoreDescription elementDesc =
             * getRegistry().findDescription( bean.getClass().getName() +
             * ".[ProtocolHandler]"); if (elementDesc != null) { ProtocolHandler
             * protocolHandler = ((Connector) bean) .getProtocolHandler(); if
             * (protocolHandler != null)
             * getStoreAppender().printAttributes(aWriter, indent, false,
             * protocolHandler, elementDesc); } }
             */
        }
    }

    protected void printTag(PrintWriter aWriter, int indent, Object bean,
            StoreDescription aDesc) throws Exception {
        aWriter.print("<");
        aWriter.print(aDesc.getTag());
        storeConnectorAttribtues(aWriter, indent, bean, aDesc);
        aWriter.println("/>");
    }

}