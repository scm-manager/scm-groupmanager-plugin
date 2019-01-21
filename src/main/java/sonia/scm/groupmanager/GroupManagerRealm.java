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
    log.info("search groupmanager privileges for user {}", username);
    List<String> groups = service.getManagedGroups(username);
    SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
    if (!groups.isEmpty()) {
      authorizationInfo.addStringPermission("group:list");
      groups.forEach(groupName -> {
        authorizationInfo.addStringPermission("group:read,modify,manage:" + groupName);
        log.info("apply groupmanager privileges for user {} and group {}", username, groupName);
      });
    } else {
      log.info("the user {} has no groupmanager privileges ", username);
    }
    return authorizationInfo;
  }
}
