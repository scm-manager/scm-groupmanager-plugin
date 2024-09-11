/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

package sonia.scm.groupmanager;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.AllowAllCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import sonia.scm.groupmanager.service.GroupManagerService;
import sonia.scm.plugin.Extension;

import jakarta.inject.Inject;
import java.util.List;

@Extension
@Slf4j
public class GroupManagerRealm extends AuthorizingRealm {

  private GroupManagerService service;

  @Inject
  public GroupManagerRealm(GroupManagerService service) {
    this.service = service;
    setCredentialsMatcher(new AllowAllCredentialsMatcher());
    setName("GroupManager");
  }

  @Override
  protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) {
    return null;
  }

  @Override
  protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
    String username = principals.getPrimaryPrincipal().toString();
   log.trace("search groupmanager privileges for user {}", username);
    List<String> groups = service.getManagedGroups(username);
    SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
    if (!groups.isEmpty()) {
      authorizationInfo.addStringPermission("group:list");
      groups.forEach(groupName -> {
        authorizationInfo.addStringPermission("group:read,modify,manage:" + groupName);
        log.trace("apply groupmanager privileges for user {} and group {}", username, groupName);
      });
    } else {
      log.trace("the user {} has no groupmanager privileges ", username);
    }
    return authorizationInfo;
  }
}
