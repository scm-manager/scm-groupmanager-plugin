package sonia.scm.groupmanager.service;


import org.apache.shiro.SecurityUtils;
import sonia.scm.store.ConfigurationStore;
import sonia.scm.store.ConfigurationStoreFactory;

import javax.inject.Inject;

public class GroupManagerService {

  public static final String PERMISSION = "group:manage";
  public static final String STORE_NAME = "groupManager";
  private AllGroupManagers allGroupManagers;
  private final ConfigurationStore<AllGroupManagers> store;

  @Inject
  public GroupManagerService(ConfigurationStoreFactory storeFactory) {
    store = storeFactory.withType(AllGroupManagers.class).withName(STORE_NAME).build();
    allGroupManagers = store.get();
    if (allGroupManagers == null) {
      allGroupManagers = new AllGroupManagers();
      store.set(allGroupManagers);
    }
  }

  public void set(GroupManagers groupManagers, String groupName) {
    allGroupManagers.getGroupManagers().put(groupName, groupManagers);
    store.set(allGroupManagers);
  }

  public GroupManagers get(String groupName) {
    return allGroupManagers.getGroupManagers().get(groupName);
  }

  public boolean isPermitted(String groupName) {
    return SecurityUtils.getSubject().isPermitted(PERMISSION + ":" + groupName);
  }

  public void checkPermission(String groupName) {
    SecurityUtils.getSubject().checkPermission(PERMISSION + ":" + groupName);
  }
}
