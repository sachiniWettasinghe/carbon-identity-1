/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.notification.mgt.json;

import org.wso2.carbon.identity.notification.mgt.NotificationMgtConstants;

public class JsonModuleConstants {

    public static final String MODULE_NAME = "json";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String IS_AUTHENTICATED_PROPERTY = "isAuthenticated";
    public static final String TEMPLATE_PROPERTY = "template";
    public static final String USERNAME_PROPERTY = "username";
    public static final String PASSWORD_PROPERTY = "password";
    public static final String CONTENT_TYPE_LABEL = "Content-Type";
    public static final String CONTENT_TYPE_JSON_LABEL = "application/json";

    private JsonModuleConstants() {
    }


    public static class Config {
        public static final String JSON_CONTENT_QNAME = "jsonContentTemplate";
        public static final String ENDPOINT_QNAME = "endpoint";
        public static final String ADDRESS_QNAME = "address";
        public static final String USERNAME_QNAME = "username";
        public static final String PASSWORD_QNAME = "password";
        public static final String AUTH_REQUIRED_QNAME = "AuthenticationRequired";
        public static final String SUBSCRIPTION_NS = MODULE_NAME + "." + NotificationMgtConstants.Configs.SUBSCRIPTION;

        private Config() {
        }
    }
}

