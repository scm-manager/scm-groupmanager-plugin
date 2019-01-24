package sonia.scm.groupmanager.service;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.store.ConfigurationStore;
import sonia.scm.store.ConfigurationStoreFactory;
import sonia.scm.store.InMemoryConfigurationStoreFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupManagerServiceTest {


  @Mock
  ConfigurationStore<AllGroupManagers> store;

  ConfigurationStoreFactory configurationStoreFactory ;

  private AllGroupManagers allGroupManagers;

  GroupManagerService service;

  @BeforeEach
  void init() {
    configurationStoreFactory = new InMemoryConfigurationStoreFactory(store);
    service = new GroupManagerService(configurationStoreFactory);
  }


  @Test
  void shouldGetManagedGroups() {
    allGroupManagers = new AllGroupManagers();
    GroupManagers groupManagers = new GroupManagers();
    groupManagers.setManagers(Lists.newArrayList("user_1", "user_2"));
    allGroupManagers.getGroupManagers().put("group_1", groupManagers);
    allGroupManagers.getGroupManagers().put("group_2", groupManagers);
    when(store.get()).thenReturn(allGroupManagers);
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
    when(store.get()).thenReturn(allGroupManagers);
    service.set(groupManagers, "group_3");
    verify(store).set(argThat(allGroupManagers1 -> {
      assertThat(allGroupManagers1.getGroupManagers().keySet()).containsExactlyInAnyOrder("group_1", "group_2", "group_3");
      return true;
    }));
  }

}
