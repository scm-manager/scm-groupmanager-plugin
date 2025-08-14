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

package sonia.scm.groupmanager.api;

import com.github.sdorra.shiro.ShiroRule;
import com.github.sdorra.shiro.SubjectAware;
import com.google.common.collect.Lists;
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
import sonia.scm.web.RestDispatcher;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.MediaType;
import java.io.UnsupportedEncodingException;
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

  private RestDispatcher dispatcher;
  private final MockHttpResponse response = new MockHttpResponse();


  @Before
  public void init() {
    resource = new GroupManagerResource(service, mapper);
    dispatcher = new RestDispatcher();
    dispatcher.addSingletonResource(resource);
  }

  @Test
  @SubjectAware(username = "trillian", password = "secret")
  public void shouldGetGroupManagers() throws URISyntaxException, UnsupportedEncodingException {
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
