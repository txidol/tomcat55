/*
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


package org.apache.catalina.startup;


import java.lang.reflect.Method;
import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.apache.catalina.deploy.SecurityConstraint;
import org.apache.catalina.deploy.SecurityRoleRef;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.digester.Rule;
import org.apache.tomcat.util.digester.RuleSetBase;
import org.xml.sax.Attributes;


/**
 * <p><strong>RuleSet</strong> for processing the contents of a web application
 * deployment descriptor (<code>/WEB-INF/web.xml</code>) resource.</p>
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class WebRuleSet extends RuleSetBase {


    // ----------------------------------------------------- Instance Variables


    /**
     * The matching pattern prefix to use for recognizing our elements.
     */
    protected String prefix = null;
    
    
    /**
     * The <code>SetSessionConfig</code> rule used to parse the web.xml
     */
    protected SetSessionConfig sessionConfig;
    
    
    /**
     * The <code>SetLoginConfig</code> rule used to parse the web.xml
     */
    protected SetLoginConfig loginConfig;

    
    /**
     * The <code>SetJspConfig</code> rule used to parse the web.xml
     */    
    protected SetJspConfig jspConfig;


    // ------------------------------------------------------------ Constructor


    /**
     * Construct an instance of this <code>RuleSet</code> with the default
     * matching pattern prefix.
     */
    public WebRuleSet() {

        this("");

    }


    /**
     * Construct an instance of this <code>RuleSet</code> with the specified
     * matching pattern prefix.
     *
     * @param prefix Prefix for matching pattern rules (including the
     *  trailing slash character)
     */
    public WebRuleSet(String prefix) {

        super();
        this.namespaceURI = null;
        this.prefix = prefix;

    }


    // --------------------------------------------------------- Public Methods


    /**
     * <p>Add the set of Rule instances defined in this RuleSet to the
     * specified <code>Digester</code> instance, associating them with
     * our namespace URI (if any).  This method should only be called
     * by a Digester instance.</p>
     *
     * @param digester Digester instance to which the new Rule instances
     *  should be added.
     */
    public void addRuleInstances(Digester digester) {
        sessionConfig = new SetSessionConfig();
        jspConfig = new SetJspConfig();
        loginConfig = new SetLoginConfig();
        
        digester.addRule(prefix + "web-app",
                         new SetPublicIdRule("setPublicId"));

        digester.addCallMethod(prefix + "web-app/context-param",
                               "addParameter", 2);
        digester.addCallParam(prefix + "web-app/context-param/param-name", 0);
        digester.addCallParam(prefix + "web-app/context-param/param-value", 1);

        digester.addCallMethod(prefix + "web-app/display-name",
                               "setDisplayName", 0);

        digester.addRule(prefix + "web-app/distributable",
                         new SetDistributableRule());

        digester.addObjectCreate(prefix + "web-app/ejb-local-ref",
                                 "org.apache.catalina.deploy.ContextLocalEjb");
        digester.addRule(prefix + "web-app/ejb-local-ref",
                new SetNextNamingRule("addLocalEjb",
                            "org.apache.catalina.deploy.ContextLocalEjb"));

        digester.addCallMethod(prefix + "web-app/ejb-local-ref/description",
                               "setDescription", 0);
        digester.addCallMethod(prefix + "web-app/ejb-local-ref/ejb-link",
                               "setLink", 0);
        digester.addCallMethod(prefix + "web-app/ejb-local-ref/ejb-ref-name",
                               "setName", 0);
        digester.addCallMethod(prefix + "web-app/ejb-local-ref/ejb-ref-type",
                               "setType", 0);
        digester.addCallMethod(prefix + "web-app/ejb-local-ref/local",
                               "setLocal", 0);
        digester.addCallMethod(prefix + "web-app/ejb-local-ref/local-home",
                               "setHome", 0);

        digester.addObjectCreate(prefix + "web-app/ejb-ref",
                                 "org.apache.catalina.deploy.ContextEjb");
        digester.addRule(prefix + "web-app/ejb-ref",
                new SetNextNamingRule("addEjb",
                            "org.apache.catalina.deploy.ContextEjb"));

        digester.addCallMethod(prefix + "web-app/ejb-ref/description",
                               "setDescription", 0);
        digester.addCallMethod(prefix + "web-app/ejb-ref/ejb-link",
                               "setLink", 0);
        digester.addCallMethod(prefix + "web-app/ejb-ref/ejb-ref-name",
                               "setName", 0);
        digester.addCallMethod(prefix + "web-app/ejb-ref/ejb-ref-type",
                               "setType", 0);
        digester.addCallMethod(prefix + "web-app/ejb-ref/home",
                               "setHome", 0);
        digester.addCallMethod(prefix + "web-app/ejb-ref/remote",
                               "setRemote", 0);

        digester.addObjectCreate(prefix + "web-app/env-entry",
                                 "org.apache.catalina.deploy.ContextEnvironment");
        digester.addRule(prefix + "web-app/env-entry",
                new SetNextNamingRule("addEnvironment",
                            "org.apache.catalina.deploy.ContextEnvironment"));

        digester.addCallMethod(prefix + "web-app/env-entry/description",
                               "setDescription", 0);
        digester.addCallMethod(prefix + "web-app/env-entry/env-entry-name",
                               "setName", 0);
        digester.addCallMethod(prefix + "web-app/env-entry/env-entry-type",
                               "setType", 0);
        digester.addCallMethod(prefix + "web-app/env-entry/env-entry-value",
                               "setValue", 0);

        digester.addObjectCreate(prefix + "web-app/error-page",
                                 "org.apache.catalina.deploy.ErrorPage");
        digester.addSetNext(prefix + "web-app/error-page",
                            "addErrorPage",
                            "org.apache.catalina.deploy.ErrorPage");

        digester.addCallMethod(prefix + "web-app/error-page/error-code",
                               "setErrorCode", 0);
        digester.addCallMethod(prefix + "web-app/error-page/exception-type",
                               "setExceptionType", 0);
        digester.addCallMethod(prefix + "web-app/error-page/location",
                               "setLocation", 0);

        digester.addObjectCreate(prefix + "web-app/filter",
                                 "org.apache.catalina.deploy.FilterDef");
        digester.addSetNext(prefix + "web-app/filter",
                            "addFilterDef",
                            "org.apache.catalina.deploy.FilterDef");

        digester.addCallMethod(prefix + "web-app/filter/description",
                               "setDescription", 0);
        digester.addCallMethod(prefix + "web-app/filter/display-name",
                               "setDisplayName", 0);
        digester.addCallMethod(prefix + "web-app/filter/filter-class",
                               "setFilterClass", 0);
        digester.addCallMethod(prefix + "web-app/filter/filter-name",
                               "setFilterName", 0);
        digester.addCallMethod(prefix + "web-app/filter/large-icon",
                               "setLargeIcon", 0);
        digester.addCallMethod(prefix + "web-app/filter/small-icon",
                               "setSmallIcon", 0);

        digester.addCallMethod(prefix + "web-app/filter/init-param",
                               "addInitParameter", 2);
        digester.addCallParam(prefix + "web-app/filter/init-param/param-name",
                              0);
        digester.addCallParam(prefix + "web-app/filter/init-param/param-value",
                              1);

        digester.addObjectCreate(prefix + "web-app/filter-mapping",
                                 "org.apache.catalina.deploy.FilterMap");
        digester.addSetNext(prefix + "web-app/filter-mapping",
                            "addFilterMap",
                            "org.apache.catalina.deploy.FilterMap");

        digester.addCallMethod(prefix + "web-app/filter-mapping/filter-name",
                               "setFilterName", 0);
        digester.addCallMethod(prefix + "web-app/filter-mapping/servlet-name",
                               "setServletName", 0);
        digester.addCallMethod(prefix + "web-app/filter-mapping/url-pattern",
                               "setURLPattern", 0);

        digester.addCallMethod(prefix + "web-app/filter-mapping/dispatcher",
                               "setDispatcher", 0);

         digester.addCallMethod(prefix + "web-app/listener/listener-class",
                                "addApplicationListener", 0);
         
        digester.addRule(prefix + "web-app/jsp-config",
                         jspConfig);
        
        digester.addCallMethod(prefix + "web-app/jsp-config/jsp-property-group/url-pattern",
                               "addJspMapping", 0);

        digester.addRule(prefix + "web-app/login-config",
                         loginConfig);

        digester.addObjectCreate(prefix + "web-app/login-config",
                                 "org.apache.catalina.deploy.LoginConfig");
        digester.addSetNext(prefix + "web-app/login-config",
                            "setLoginConfig",
                            "org.apache.catalina.deploy.LoginConfig");

        digester.addCallMethod(prefix + "web-app/login-config/auth-method",
                               "setAuthMethod", 0);
        digester.addCallMethod(prefix + "web-app/login-config/realm-name",
                               "setRealmName", 0);
        digester.addCallMethod(prefix + "web-app/login-config/form-login-config/form-error-page",
                               "setErrorPage", 0);
        digester.addCallMethod(prefix + "web-app/login-config/form-login-config/form-login-page",
                               "setLoginPage", 0);

        digester.addCallMethod(prefix + "web-app/mime-mapping",
                               "addMimeMapping", 2);
        digester.addCallParam(prefix + "web-app/mime-mapping/extension", 0);
        digester.addCallParam(prefix + "web-app/mime-mapping/mime-type", 1);

        digester.addObjectCreate(prefix + "web-app/resource-env-ref",
            "org.apache.catalina.deploy.ContextResourceEnvRef");
        digester.addRule(prefix + "web-app/resource-env-ref",
                    new SetNextNamingRule("addResourceEnvRef",
                        "org.apache.catalina.deploy.ContextResourceEnvRef"));

        digester.addCallMethod(prefix + "web-app/resource-env-ref/resource-env-ref-name",
                "setName", 0);
        digester.addCallMethod(prefix + "web-app/resource-env-ref/resource-env-ref-type",
                "setType", 0);

        digester.addObjectCreate(prefix + "web-app/message-destination",
                                 "org.apache.catalina.deploy.MessageDestination");
        digester.addSetNext(prefix + "web-app/message-destination",
                            "addMessageDestination",
                            "org.apache.catalina.deploy.MessageDestination");

        digester.addCallMethod(prefix + "web-app/message-destination/description",
                               "setDescription", 0);
        digester.addCallMethod(prefix + "web-app/message-destination/display-name",
                               "setDisplayName", 0);
        digester.addCallMethod(prefix + "web-app/message-destination/icon/large-icon",
                               "setLargeIcon", 0);
        digester.addCallMethod(prefix + "web-app/message-destination/icon/small-icon",
                               "setSmallIcon", 0);
        digester.addCallMethod(prefix + "web-app/message-destination/message-destination-name",
                               "setName", 0);

        digester.addObjectCreate(prefix + "web-app/message-destination-ref",
                                 "org.apache.catalina.deploy.MessageDestinationRef");
        digester.addSetNext(prefix + "web-app/message-destination-ref",
                            "addMessageDestinationRef",
                            "org.apache.catalina.deploy.MessageDestinationRef");

        digester.addCallMethod(prefix + "web-app/message-destination-ref/description",
                               "setDescription", 0);
        digester.addCallMethod(prefix + "web-app/message-destination-ref/message-destination-link",
                               "setLink", 0);
        digester.addCallMethod(prefix + "web-app/message-destination-ref/message-destination-ref-name",
                               "setName", 0);
        digester.addCallMethod(prefix + "web-app/message-destination-ref/message-destination-type",
                               "setType", 0);
        digester.addCallMethod(prefix + "web-app/message-destination-ref/message-destination-usage",
                               "setUsage", 0);

        digester.addObjectCreate(prefix + "web-app/resource-ref",
                                 "org.apache.catalina.deploy.ContextResource");
        digester.addRule(prefix + "web-app/resource-ref",
                new SetNextNamingRule("addResource",
                            "org.apache.catalina.deploy.ContextResource"));

        digester.addCallMethod(prefix + "web-app/resource-ref/description",
                               "setDescription", 0);
        digester.addCallMethod(prefix + "web-app/resource-ref/res-auth",
                               "setAuth", 0);
        digester.addCallMethod(prefix + "web-app/resource-ref/res-ref-name",
                               "setName", 0);
        digester.addCallMethod(prefix + "web-app/resource-ref/res-sharing-scope",
                               "setScope", 0);
        digester.addCallMethod(prefix + "web-app/resource-ref/res-type",
                               "setType", 0);

        digester.addObjectCreate(prefix + "web-app/security-constraint",
                                 "org.apache.catalina.deploy.SecurityConstraint");
        digester.addSetNext(prefix + "web-app/security-constraint",
                            "addConstraint",
                            "org.apache.catalina.deploy.SecurityConstraint");

        digester.addRule(prefix + "web-app/security-constraint/auth-constraint",
                         new SetAuthConstraintRule());
        digester.addCallMethod(prefix + "web-app/security-constraint/auth-constraint/role-name",
                               "addAuthRole", 0);
        digester.addCallMethod(prefix + "web-app/security-constraint/display-name",
                               "setDisplayName", 0);
        digester.addCallMethod(prefix + "web-app/security-constraint/user-data-constraint/transport-guarantee",
                               "setUserConstraint", 0);

        digester.addObjectCreate(prefix + "web-app/security-constraint/web-resource-collection",
                                 "org.apache.catalina.deploy.SecurityCollection");
        digester.addSetNext(prefix + "web-app/security-constraint/web-resource-collection",
                            "addCollection",
                            "org.apache.catalina.deploy.SecurityCollection");
        digester.addCallMethod(prefix + "web-app/security-constraint/web-resource-collection/http-method",
                               "addMethod", 0);
        digester.addCallMethod(prefix + "web-app/security-constraint/web-resource-collection/url-pattern",
                               "addPattern", 0);
        digester.addCallMethod(prefix + "web-app/security-constraint/web-resource-collection/web-resource-name",
                               "setName", 0);

        digester.addCallMethod(prefix + "web-app/security-role/role-name",
                               "addSecurityRole", 0);

        digester.addRule(prefix + "web-app/servlet",
                         new WrapperCreateRule());
        digester.addSetNext(prefix + "web-app/servlet",
                            "addChild",
                            "org.apache.catalina.Container");

        digester.addCallMethod(prefix + "web-app/servlet/init-param",
                               "addInitParameter", 2);
        digester.addCallParam(prefix + "web-app/servlet/init-param/param-name",
                              0);
        digester.addCallParam(prefix + "web-app/servlet/init-param/param-value",
                              1);

        digester.addCallMethod(prefix + "web-app/servlet/jsp-file",
                               "setJspFile", 0);
        digester.addCallMethod(prefix + "web-app/servlet/load-on-startup",
                               "setLoadOnStartupString", 0);
        digester.addCallMethod(prefix + "web-app/servlet/run-as/role-name",
                               "setRunAs", 0);

        digester.addRule(prefix + "web-app/servlet/security-role-ref",
                new SecurityRoleRefCreateRule());

        digester.addCallMethod(
                prefix + "web-app/servlet/security-role-ref/role-link",
                "setLink", 0);
        digester.addCallMethod(
                prefix + "web-app/servlet/security-role-ref/role-name",
                "setName", 0);

        digester.addCallMethod(prefix + "web-app/servlet/servlet-class",
                              "setServletClass", 0);
        digester.addCallMethod(prefix + "web-app/servlet/servlet-name",
                              "setName", 0);

        digester.addCallMethod(prefix + "web-app/servlet-mapping",
                               "addServletMapping", 2);
        digester.addCallParam(prefix + "web-app/servlet-mapping/servlet-name", 1);
        digester.addCallParam(prefix + "web-app/servlet-mapping/url-pattern", 0);

        digester.addRule(prefix + "web-app/session-config",
                         sessionConfig);
        
        digester.addCallMethod(prefix + "web-app/session-config/session-timeout",
                               "setSessionTimeout", 1,
                               new Class[] { Integer.TYPE });
        digester.addCallParam(prefix + "web-app/session-config/session-timeout", 0);

        digester.addCallMethod(prefix + "web-app/taglib",
                               "addTaglib", 2);
        digester.addCallParam(prefix + "web-app/taglib/taglib-location", 1);
        digester.addCallParam(prefix + "web-app/taglib/taglib-uri", 0);

        digester.addCallMethod(prefix + "web-app/welcome-file-list/welcome-file",
                               "addWelcomeFile", 0);

        digester.addCallMethod(prefix + "web-app/locale-encoding-mapping-list/locale-encoding-mapping",
                              "addLocaleEncodingMappingParameter", 2);
        digester.addCallParam(prefix + "web-app/locale-encoding-mapping-list/locale-encoding-mapping/locale", 0);
        digester.addCallParam(prefix + "web-app/locale-encoding-mapping-list/locale-encoding-mapping/encoding", 1);

    }

    /**
     * Reset counter used for validating the web.xml file.
     */
    public void recycle(){
        jspConfig.isJspConfigSet = false;
        sessionConfig.isSessionConfigSet = false;
        loginConfig.isLoginConfigSet = false;
    }
}


// ----------------------------------------------------------- Private Classes


/**
 * Rule to check that the <code>login-config</code> is occuring 
 * only 1 time within the web.xml
 */
final class SetLoginConfig extends Rule {
    protected boolean isLoginConfigSet = false;
    public SetLoginConfig() {
    }

    public void begin(String namespace, String name, Attributes attributes)
        throws Exception {
        if (isLoginConfigSet){
            throw new IllegalArgumentException(
            "<login-config> element is limited to 1 occurance");
        }
        isLoginConfigSet = true;
    }

}


/**
 * Rule to check that the <code>jsp-config</code> is occuring 
 * only 1 time within the web.xml
 */
final class SetJspConfig extends Rule {
    protected boolean isJspConfigSet = false;
    public SetJspConfig() {
    }

    public void begin(String namespace, String name, Attributes attributes)
        throws Exception {
        if (isJspConfigSet){
            throw new IllegalArgumentException(
            "<jsp-config> element is limited to 1 occurance");
        }
        isJspConfigSet = true;
    }

}


/**
 * Rule to check that the <code>session-config</code> is occuring 
 * only 1 time within the web.xml
 */
final class SetSessionConfig extends Rule {
    protected boolean isSessionConfigSet = false;
    public SetSessionConfig() {
    }

    public void begin(String namespace, String name, Attributes attributes)
        throws Exception {
        if (isSessionConfigSet){
            throw new IllegalArgumentException(
            "<session-config> element is limited to 1 occurance");
        }
        isSessionConfigSet = true;
    }

}

/**
 * A Rule that calls the <code>setAuthConstraint(true)</code> method of
 * the top item on the stack, which must be of type
 * <code>org.apache.catalina.deploy.SecurityConstraint</code>.
 */

final class SetAuthConstraintRule extends Rule {

    public SetAuthConstraintRule() {
    }

    public void begin(String namespace, String name, Attributes attributes)
        throws Exception {
        SecurityConstraint securityConstraint =
            (SecurityConstraint) digester.peek();
        securityConstraint.setAuthConstraint(true);
        if (digester.getLogger().isDebugEnabled()) {
            digester.getLogger()
               .debug("Calling SecurityConstraint.setAuthConstraint(true)");
        }
    }

}


/**
 * Class that calls <code>setDistributable(true)</code> for the top object
 * on the stack, which must be a <code>org.apache.catalina.Context</code>.
 */

final class SetDistributableRule extends Rule {

    public SetDistributableRule() {
    }

    public void begin(String namespace, String name, Attributes attributes)
        throws Exception {
        Context context = (Context) digester.peek();
        context.setDistributable(true);
        if (digester.getLogger().isDebugEnabled()) {
            digester.getLogger().debug
               (context.getClass().getName() + ".setDistributable( true)");
        }
    }

}


/**
 * Class that calls a property setter for the top object on the stack,
 * passing the public ID of the entity we are currently processing.
 */

final class SetPublicIdRule extends Rule {

    public SetPublicIdRule(String method) {
        this.method = method;
    }

    private String method = null;

    public void begin(String namespace, String name, Attributes attributes)
        throws Exception {

        Object top = digester.peek();
        Class paramClasses[] = new Class[1];
        paramClasses[0] = "String".getClass();
        String paramValues[] = new String[1];
        paramValues[0] = digester.getPublicId();

        Method m = null;
        try {
            m = top.getClass().getMethod(method, paramClasses);
        } catch (NoSuchMethodException e) {
            digester.getLogger().error("Can't find method " + method + " in "
                                       + top + " CLASS " + top.getClass());
            return;
        }

        m.invoke(top, (Object [])paramValues);
        if (digester.getLogger().isDebugEnabled())
            digester.getLogger().debug("" + top.getClass().getName() + "." 
                                       + method + "(" + paramValues[0] + ")");

    }

}


/**
 * A Rule that calls the factory method on the specified Context to
 * create the object that is to be added to the stack.
 */

final class WrapperCreateRule extends Rule {

    public WrapperCreateRule() {
    }

    public void begin(String namespace, String name, Attributes attributes)
        throws Exception {
        Context context =
            (Context) digester.peek(digester.getCount() - 1);
        Wrapper wrapper = context.createWrapper();
        digester.push(wrapper);
        if (digester.getLogger().isDebugEnabled())
            digester.getLogger().debug("new " + wrapper.getClass().getName());
    }

    public void end(String namespace, String name)
        throws Exception {
        Wrapper wrapper = (Wrapper) digester.pop();
        if (digester.getLogger().isDebugEnabled())
            digester.getLogger().debug("pop " + wrapper.getClass().getName());
    }

}

/**
 * A Rule that adds a security-role-ref to a servlet, allowing for the fact that
 * role-link is an optional element.
 */
final class SecurityRoleRefCreateRule extends Rule {
    
    public SecurityRoleRefCreateRule() {
    }
    
    public void begin(String namespace, String name, Attributes attributes)
            throws Exception {
        SecurityRoleRef securityRoleRef = new SecurityRoleRef();
        digester.push(securityRoleRef);
        if (digester.getLogger().isDebugEnabled())
            digester.getLogger().debug("new SecurityRoleRef");
    }

    public void end(String namespace, String name)
            throws Exception {
        SecurityRoleRef securityRoleRef = (SecurityRoleRef) digester.pop();
        Wrapper wrapper = (Wrapper) digester.peek();
        
        wrapper.addSecurityReference(securityRoleRef.getName(),
                securityRoleRef.getLink());

        if (digester.getLogger().isDebugEnabled())
            digester.getLogger().debug("pop SecurityRoleRef");
    }
}