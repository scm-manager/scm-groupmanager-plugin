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
