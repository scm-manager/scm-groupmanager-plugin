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
package sonia.scm.groupmanager.service;


import org.apache.shiro.SecurityUtils;
import sonia.scm.store.ConfigurationStore;
import sonia.scm.store.ConfigurationStoreFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
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
