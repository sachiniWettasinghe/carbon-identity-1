/*
 * Copyright (c) 2015 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.workflow.mgt.impl.userstore;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.workflow.mgt.AbstractWorkflowRequestHandler;
import org.wso2.carbon.identity.workflow.mgt.WorkflowDataType;
import org.wso2.carbon.identity.workflow.mgt.WorkflowException;
import org.wso2.carbon.identity.workflow.mgt.WorkflowRequestStatus;
import org.wso2.carbon.identity.workflow.mgt.impl.internal.IdentityWorkflowServiceComponent;
import org.wso2.carbon.user.api.UserRealm;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.service.RealmService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateRoleUsersWFRequestHandler extends AbstractWorkflowRequestHandler {
    private static final String ROLENAME = "roleName";
    private static final String USER_STORE_DOMAIN = "userStoreDomain";
    private static final String DELETED_USER_LIST = "deletedUserList";
    private static final String NEW_USER_LIST = "newUserList";

    private static final Map<String, String> PARAM_DEFINITION;
    private static Log log = LogFactory.getLog(AddUserWFRequestHandler.class);

    static {
        PARAM_DEFINITION = new HashMap<>();
        PARAM_DEFINITION.put(ROLENAME, WorkflowDataType.STRING_TYPE);
        PARAM_DEFINITION.put(USER_STORE_DOMAIN, WorkflowDataType.STRING_TYPE);
        PARAM_DEFINITION.put(DELETED_USER_LIST, WorkflowDataType.STRING_LIST_TYPE);
        PARAM_DEFINITION.put(NEW_USER_LIST, WorkflowDataType.STRING_LIST_TYPE);
    }

    public boolean startUpdateRoleUsersFlow(String userStoreDomain, String roleName, String[] deletedUsers, String[]
            newUsers) throws WorkflowException {
        Map<String, Object> wfParams = new HashMap<>();
        Map<String, Object> nonWfParams = new HashMap<>();
        wfParams.put(ROLENAME, roleName);
        wfParams.put(USER_STORE_DOMAIN, userStoreDomain);
        wfParams.put(DELETED_USER_LIST, Arrays.asList(deletedUsers));
        wfParams.put(NEW_USER_LIST, Arrays.asList(newUsers));
        return startWorkFlow(wfParams, nonWfParams);
    }

    @Override
    public String getEventId() {
        return UserStoreWFConstants.UPDATE_ROLE_USERS_EVENT;
    }

    @Override
    public Map<String, String> getParamDefinitions() {
        return PARAM_DEFINITION;
    }

    @Override
    public boolean retryNeedAtCallback() {
        return true;
    }

    @Override
    public void onWorkflowCompletion(String status, Map<String, Object> requestParams, Map<String, Object>
            responseAdditionalParams, int tenantId) throws WorkflowException {

        String roleName = (String) requestParams.get(ROLENAME);
        if (roleName == null) {
            throw new WorkflowException("Callback request for Add User received without the mandatory " +
                    "parameter 'username'");
        }
        String userStoreDomain = (String) requestParams.get(USER_STORE_DOMAIN);
        if (StringUtils.isNotBlank(userStoreDomain)) {
            roleName = userStoreDomain + "/" + roleName;
        }

        List<String> deletedUserList = ((List<String>) requestParams.get(DELETED_USER_LIST));
        String[] deletedUsers;
        if (deletedUserList != null) {
            deletedUsers = new String[deletedUserList.size()];
            deletedUsers = deletedUserList.toArray(deletedUsers);
        } else {
            deletedUsers = new String[0];
        }

        List<String> newUserList = ((List<String>) requestParams.get(NEW_USER_LIST));
        String[] newUsers;
        if (newUserList != null) {
            newUsers = new String[newUserList.size()];
            newUsers = newUserList.toArray(newUsers);
        } else {
            newUsers = new String[0];
        }

        if (WorkflowRequestStatus.APPROVED.toString().equals(status) ||
                WorkflowRequestStatus.SKIPPED.toString().equals(status)) {
            try {
                RealmService realmService = IdentityWorkflowServiceComponent.getRealmService();
                UserRealm userRealm = realmService.getTenantUserRealm(tenantId);
                userRealm.getUserStoreManager().updateUserListOfRole(roleName, deletedUsers, newUsers);
            } catch (UserStoreException e) {
                throw new WorkflowException("Error when re-requesting updateUserListOfRole operation for " + roleName,
                        e);
            }
        } else {
            if (retryNeedAtCallback()) {
                //unset threadlocal variable
                unsetWorkFlowCompleted();
            }
            if (log.isDebugEnabled()) {
                log.debug(
                        "Updating role users is aborted for role '" + roleName + "', Reason: Workflow response was " +
                                status);
            }
        }
    }
}
