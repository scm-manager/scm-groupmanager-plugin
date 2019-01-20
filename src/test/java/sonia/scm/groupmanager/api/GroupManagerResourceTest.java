package sonia.scm.groupmanager.api;

import com.github.sdorra.shiro.ShiroRule;
import com.github.sdorra.shiro.SubjectAware;
import com.google.common.collect.Lists;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import sonia.scm.groupmanager.service.GroupManagerService;
import sonia.scm.groupmanager.service.GroupManagers;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@SubjectAware(configuration = "classpath:sonia/scm/groupmanager/shiro.ini")
@RunWith(MockitoJUnitRunner.class)
public class GroupManagerResourceTest {

  @Rule
  public ShiroRule shiro = new ShiroRule();

  private GroupManagerResource resource;

  @Mock
  GroupManagerService service;

  private GroupManagerMapper mapper = new GroupManagerMapperImpl();

  private Dispatcher dispatcher;
  private final MockHttpResponse response = new MockHttpResponse();


  @Before
  public void init() {
    resource = new GroupManagerResource(service, mapper);
    dispatcher = MockDispatcherFactory.createDispatcher();
    dispatcher.getRegistry().addSingletonResource(resource);
  }

  @Test
  @SubjectAware(username = "trillian", password = "secret")
  public void shouldGetGroupManagers() throws URISyntaxException {
    GroupManagers groupManagers = new GroupManagers();
    String groupName = "group_1";
    groupManagers.setManagers(Lists.newArrayList("user_1", "user_2"));
    when(service.get(groupName)).thenReturn(groupManagers);
    doCallRealMethod().when(service).checkPermission(groupName);

    MockHttpRequest request = MockHttpRequest
      .get("/" + GroupManagerResource.PATH + "/" + groupName)
      .accept(MediaType.APPLICATION_JSON);

    dispatcher.invoke(request, response);
    assertThat(response.getStatus())
      .isEqualTo(HttpServletResponse.SC_OK);
    assertThat(response.getContentAsString())
      .contains(groupName, "user_1", "user_2");
    System.out.println(response.getContentAsString());
  }


  @Test
  @SubjectAware(username = "trillian", password = "secret")
  public void shouldPUTGroupManagers() throws URISyntaxException {
    String groupName = "group_1";
    doCallRealMethod().when(service).checkPermission(groupName);

    MockHttpRequest request = MockHttpRequest
      .put("/" + GroupManagerResource.PATH + "/" + groupName)
      .content("{\"managers\":[\"user_1\",\"user_2\",\"user_3\"]}".getBytes())
      .contentType(MediaType.APPLICATION_JSON);

    dispatcher.invoke(request, response);
    assertThat(response.getStatus())
      .isEqualTo(HttpServletResponse.SC_NO_CONTENT);
    verify(service).set(argThat(gm -> {
      assertThat(gm.getManagers()).containsExactlyInAnyOrder("user_1", "user_2", "user_3");
      return true;
    }), eq(groupName));

  }

}
