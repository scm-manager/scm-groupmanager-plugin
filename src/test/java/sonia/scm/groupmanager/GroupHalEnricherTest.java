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

package sonia.scm.groupmanager;

import com.github.sdorra.shiro.ShiroRule;
import com.github.sdorra.shiro.SubjectAware;
import com.google.inject.Provider;
import com.google.inject.util.Providers;
import org.apache.shiro.util.ThreadContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import sonia.scm.api.v2.resources.HalAppender;
import sonia.scm.api.v2.resources.HalEnricherContext;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.group.Group;
import sonia.scm.groupmanager.service.GroupManagerService;

import java.net.URI;

import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@SubjectAware(configuration = "classpath:sonia/scm/groupmanager/shiro.ini")
@RunWith(MockitoJUnitRunner.class)
public class GroupHalEnricherTest {

  private Provider<ScmPathInfoStore> scmPathInfoStoreProvider;

  @Rule
  public ShiroRule shiro = new ShiroRule();

  @Mock
  private HalAppender appender;
  private GroupHalEnricher enricher;

  @Mock
  private GroupManagerService service;


  public GroupHalEnricherTest() {
    // cleanup state that might have been left by other tests
    ThreadContext.unbindSecurityManager();
    ThreadContext.unbindSubject();
    ThreadContext.remove();
  }

  @Before
  public void setUp() {
    ScmPathInfoStore scmPathInfoStore = new ScmPathInfoStore();
    scmPathInfoStore.set(() -> URI.create("https://scm-manager.org/scm/api/"));
    scmPathInfoStoreProvider = Providers.of(scmPathInfoStore);
  }

  @Test
  @SubjectAware(username = "gm", password = "secret")
  public void shouldEnrichGroup() {
    enricher = new GroupHalEnricher(scmPathInfoStoreProvider, service);
    Group group = new Group("xml", "group_1");
    HalEnricherContext context = HalEnricherContext.of(group);
    doCallRealMethod().when(service).isPermitted("group_1");

    enricher.enrich(context, appender);
    verify(appender).appendLink("managers", "https://scm-manager.org/scm/api/v2/plugins/groupmanager/group_1");
  }

  @Test
  @SubjectAware(username = "unpriv", password = "secret")
  public void shouldNotEnrichGroupBecauseOfMissingPermission() {
    enricher = new GroupHalEnricher(scmPathInfoStoreProvider, service);
    Group group = new Group("xml", "group_1");
    HalEnricherContext context = HalEnricherContext.of(group);
    doCallRealMethod().when(service).isPermitted("group_1");

    enricher.enrich(context, appender);
    verify(appender, never()).appendLink("managers", "https://scm-manager.org/scm/api/v2/plugins/groupmanager/group_1");
  }

}
