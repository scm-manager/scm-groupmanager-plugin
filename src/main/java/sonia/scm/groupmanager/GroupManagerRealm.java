/**
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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

import javax.inject.Inject;
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
