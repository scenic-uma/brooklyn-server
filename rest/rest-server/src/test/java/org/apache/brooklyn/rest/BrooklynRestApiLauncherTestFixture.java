/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.brooklyn.rest;

import org.apache.brooklyn.api.mgmt.ManagementContext;
import org.apache.brooklyn.core.entity.Entities;
import org.apache.brooklyn.core.internal.BrooklynProperties;
import org.apache.brooklyn.core.mgmt.internal.LocalManagementContext;
import org.apache.brooklyn.core.server.BrooklynServerConfig;
import org.apache.brooklyn.core.server.BrooklynServiceAttributes;
import org.apache.brooklyn.rest.security.provider.AnyoneSecurityProvider;
import org.apache.brooklyn.util.core.osgi.Compat;
import org.apache.brooklyn.util.exceptions.Exceptions;
import org.eclipse.jetty.server.NetworkConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.reflections.util.ClasspathHelper;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;

public abstract class BrooklynRestApiLauncherTestFixture {

    Server server = null;
    
    @AfterMethod(alwaysRun=true)
    public void stopServer() throws Exception {
        if (server!=null) {
            ManagementContext mgmt = getManagementContextFromJettyServerAttributes(server);
            server.stop();
            if (mgmt!=null) Entities.destroyAll(mgmt);
            server = null;
        }
    }
    
    protected Server newServer() {
        try {
            Server server = BrooklynRestApiLauncher.launcher()
                    .forceUseOfDefaultCatalogWithJavaClassPath(true)
                    .securityProvider(AnyoneSecurityProvider.class)
                    .start();
            return server;
        } catch (Exception e) {
            throw Exceptions.propagate(e);
        }
    }
    
    protected Server useServerForTest(Server server) {
        if (this.server!=null) {
            Assert.fail("Test only meant for single server; already have "+this.server+" when checking "+server);
        } else {
            this.server = server;
        }
        return server;
    }
    
    /** @deprecated since 0.9.0 use {@link #getBaseUriHostAndPost(Server)} or {@link #getBaseUriRest(Server)} */
    public static String getBaseUri(Server server) {
        return getBaseUriHostAndPost(server);
    }
    
    protected String getBaseUriHostAndPost() {
        return getBaseUriHostAndPost(server);
    }
    /** returns base of server, without trailing slash - e.g. <code>http://localhost:8081</code> */
    public static String getBaseUriHostAndPost(Server server) {
        return "http://localhost:"+((NetworkConnector)server.getConnectors()[0]).getLocalPort();
    }
    protected String getBaseUriRest() {
        return getBaseUriRest(server);
    }
    /** returns REST endpoint, with trailing slash */
    public static String getBaseUriRest(Server server) {
        return getBaseUriHostAndPost(server)+"/v1/";
    }
    
    public static void forceUseOfDefaultCatalogWithJavaClassPath(Server server) {
        ManagementContext mgmt = getManagementContextFromJettyServerAttributes(server);
        forceUseOfDefaultCatalogWithJavaClassPath(mgmt);
    }

    public static void forceUseOfDefaultCatalogWithJavaClassPath(ManagementContext manager) {
        // TODO duplication with BrooklynRestApiLauncher ?
        
        // don't use any catalog.xml which is set
        ((BrooklynProperties)manager.getConfig()).put(BrooklynServerConfig.BROOKLYN_CATALOG_URL, BrooklynRestApiLauncher.SCANNING_CATALOG_BOM_URL);
        // sets URLs for a surefire
        ((LocalManagementContext)manager).setBaseClassPathForScanning(ClasspathHelper.forJavaClassPath());
        // this also works
//        ((LocalManagementContext)manager).setBaseClassPathForScanning(ClasspathHelper.forPackage("brooklyn"));
        // but this (near-default behaviour) does not
//        ((LocalManagementContext)manager).setBaseClassLoader(getClass().getClassLoader());
    }

    public static void enableAnyoneLogin(Server server) {
        ManagementContext mgmt = getManagementContextFromJettyServerAttributes(server);
        enableAnyoneLogin(mgmt);
    }

    public static void enableAnyoneLogin(ManagementContext mgmt) {
        ((BrooklynProperties)mgmt.getConfig()).put(BrooklynWebConfig.SECURITY_PROVIDER_CLASSNAME, 
                AnyoneSecurityProvider.class.getName());
    }

    public static ManagementContext getManagementContextFromJettyServerAttributes(Server server) {
        return getManagementContext((ContextHandler) server.getHandler());
    }
    
    public static ManagementContext getManagementContext(ContextHandler jettyServerHandler) {
        ManagementContext managementContext = Compat.getInstance().getManagementContext();
        if (managementContext == null && jettyServerHandler != null) {
            managementContext = (ManagementContext) jettyServerHandler.getAttribute(BrooklynServiceAttributes.BROOKLYN_MANAGEMENT_CONTEXT);
        }
        return managementContext;
    }
}
