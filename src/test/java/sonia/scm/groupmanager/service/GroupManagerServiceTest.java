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
