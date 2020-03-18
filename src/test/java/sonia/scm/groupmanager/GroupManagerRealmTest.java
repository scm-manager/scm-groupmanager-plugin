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

import com.google.common.collect.Lists;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.groupmanager.service.GroupManagerService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupManagerRealmTest {

  @Mock
  GroupManagerService service;

  @InjectMocks
  GroupManagerRealm groupManagerRealm;

  @Test
  void shouldAddPermissionToStoredGroupManager() {
    PrincipalCollection principals = new SimplePrincipalCollection("user_1", "DefaultRealm");
    when(service.getManagedGroups("user_1")).thenReturn(Lists.newArrayList("group_1"));
    AuthorizationInfo info = groupManagerRealm.doGetAuthorizationInfo(principals);
    assertThat(info.getStringPermissions()).containsExactlyInAnyOrder("group:read,modify,manage:group_1", "group:list");
  }

  @Test
  void shouldNotAddPermissionToNotStoredGroupManager() {
    PrincipalCollection principals = new SimplePrincipalCollection("user_1", "DefaultRealm");
    when(service.getManagedGroups("user_1")).thenReturn(Lists.newArrayList());
    AuthorizationInfo info = groupManagerRealm.doGetAuthorizationInfo(principals);
    assertThat(info.getStringPermissions()).isNull();
  }

}
