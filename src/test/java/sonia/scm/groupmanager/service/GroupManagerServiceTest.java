/*
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

import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.store.ConfigurationStore;
import sonia.scm.store.ConfigurationStoreFactory;
import sonia.scm.store.InMemoryConfigurationStoreFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class GroupManagerServiceTest {


  ConfigurationStore<AllGroupManagers> store;

  ConfigurationStoreFactory configurationStoreFactory ;

  private AllGroupManagers allGroupManagers;

  GroupManagerService service;

  @BeforeEach
  void init() {
    configurationStoreFactory = new InMemoryConfigurationStoreFactory();
    store = configurationStoreFactory.withType(AllGroupManagers.class).withName("groupManager").build();
    service = new GroupManagerService(configurationStoreFactory);
  }


  @Test
  void shouldGetManagedGroups() {
    allGroupManagers = new AllGroupManagers();
    GroupManagers groupManagers = new GroupManagers();
    groupManagers.setManagers(Lists.newArrayList("user_1", "user_2"));
    allGroupManagers.getGroupManagers().put("group_1", groupManagers);
    allGroupManagers.getGroupManagers().put("group_2", groupManagers);
    store.set(allGroupManagers);
    List<String> groups = service.getManagedGroups("user_1");
    assertThat(groups).containsExactlyInAnyOrder("group_1", "group_2");
  }

  @Test
  void shouldSetGroupManager() {
    allGroupManagers = new AllGroupManagers();
    GroupManagers groupManagers = new GroupManagers();
    groupManagers.setManagers(Lists.newArrayList("user_1", "user_2"));
    allGroupManagers.getGroupManagers().put("group_1", groupManagers);
    allGroupManagers.getGroupManagers().put("group_2", groupManagers);
    store.set(allGroupManagers);
    service.set(groupManagers, "group_3");

    assertThat(allGroupManagers.getGroupManagers().keySet()).containsExactlyInAnyOrder("group_1", "group_2", "group_3");

  }

}
