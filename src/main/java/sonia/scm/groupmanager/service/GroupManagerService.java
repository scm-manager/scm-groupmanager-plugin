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

package sonia.scm.groupmanager.service;


import org.apache.shiro.SecurityUtils;
import sonia.scm.store.ConfigurationStore;
import sonia.scm.store.ConfigurationStoreFactory;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.ArrayList;
import java.util.List;


@Singleton
public class GroupManagerService {

  public static final String PERMISSION = "group:manage";
  public static final String STORE_NAME = "groupManager";
  private final ConfigurationStore<AllGroupManagers> store;

  @Inject
  public GroupManagerService(ConfigurationStoreFactory storeFactory) {
    store = storeFactory.withType(AllGroupManagers.class).withName(STORE_NAME).build();
  }

  private AllGroupManagers getAllGroupManagers() {
    AllGroupManagers allGroupManagers = store.get();
    if (allGroupManagers == null) {
      allGroupManagers = new AllGroupManagers();
      store.set(allGroupManagers);
    }
    return allGroupManagers;
  }

  public void set(GroupManagers groupManagers, String groupName) {
    AllGroupManagers allGroupManagers = getAllGroupManagers();
    allGroupManagers.getGroupManagers().put(groupName, groupManagers);
    store.set(allGroupManagers);
  }

  public GroupManagers get(String groupName) {
    return getAllGroupManagers().getGroupManagers().get(groupName);
  }

  public boolean isPermitted(String groupName) {
    return SecurityUtils.getSubject().isPermitted(PERMISSION + ":" + groupName);
  }

  public void checkPermission(String groupName) {
    SecurityUtils.getSubject().checkPermission(PERMISSION + ":" + groupName);
  }

  /**
   * Return the Groups managed from the given user
   *
   * @param username
   * @return the Groups managed from the given user
   */
  public List<String> getManagedGroups(String username) {
    List<String> result = new ArrayList<>();
    getAllGroupManagers().getGroupManagers().forEach((groupName, groupManagers) -> {
      if (groupManagers.getManagers().stream().anyMatch(s -> s.equals(username))) {
        result.add(groupName);
      }
    });
    return result;
  }
}
