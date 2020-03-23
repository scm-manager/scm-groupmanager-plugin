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

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
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
