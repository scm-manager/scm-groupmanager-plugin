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
    assertThat(info.getStringPermissions()).containsExactlyInAnyOrder("group:read,modify,manage,delete:group_1", "group:list");
  }

  @Test
  void shouldNotAddPermissionToNotStoredGroupManager() {
    PrincipalCollection principals = new SimplePrincipalCollection("user_1", "DefaultRealm");
    when(service.getManagedGroups("user_1")).thenReturn(Lists.newArrayList());
    AuthorizationInfo info = groupManagerRealm.doGetAuthorizationInfo(principals);
    assertThat(info.getStringPermissions()).isNull();
  }

}
