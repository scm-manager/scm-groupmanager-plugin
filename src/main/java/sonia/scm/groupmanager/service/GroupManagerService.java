package sonia.scm.groupmanager.service;


import org.apache.shiro.SecurityUtils;
import sonia.scm.store.ConfigurationStoreFactory;

import javax.inject.Inject;

public class GroupManagerService {

  public static final String PERMISSION = "group:manage";
  public static final String STORE_NAME = "groupManager";
  private final AllGroupManagers allGroupManagers;

  @Inject
  public GroupManagerService(ConfigurationStoreFactory storeFactory) {
    allGroupManagers = storeFactory.withType(AllGroupManagers.class).withName(STORE_NAME).build().get();
  }

  public void set(GroupManagers groupManagers){
    allGroupManagers.getGroupManagers().put(groupManagers.getGroupName(), groupManagers);
  }

  public GroupManagers get(String groupName){
    return allGroupManagers.getGroupManagers().get(groupName);
  }

  public boolean isPermitted(String groupName) {
    return SecurityUtils.getSubject().isPermitted(PERMISSION+":"+groupName);
  }

  public void checkPermission(String groupName) {
    SecurityUtils.getSubject().checkPermission(PERMISSION+":"+groupName);
  }
}
