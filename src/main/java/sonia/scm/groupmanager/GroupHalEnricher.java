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

import sonia.scm.api.v2.resources.Enrich;
import sonia.scm.api.v2.resources.HalAppender;
import sonia.scm.api.v2.resources.LinkBuilder;
import sonia.scm.api.v2.resources.HalEnricher;
import sonia.scm.api.v2.resources.HalEnricherContext;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.group.Group;
import sonia.scm.groupmanager.api.GroupManagerResource;
import sonia.scm.groupmanager.service.GroupManagerService;
import sonia.scm.plugin.Extension;

import jakarta.inject.Inject;
import jakarta.inject.Provider;

@Extension
@Enrich(Group.class)
public class GroupHalEnricher implements HalEnricher {

  private Provider<ScmPathInfoStore> scmPathInfoStoreProvider;
  private GroupManagerService service;

  @Inject
  public GroupHalEnricher(Provider<ScmPathInfoStore> scmPathInfoStoreProvider, GroupManagerService service) {
    this.scmPathInfoStoreProvider = scmPathInfoStoreProvider;
    this.service = service;
  }

  @Override
  public void enrich(HalEnricherContext context, HalAppender appender) {
    Group group = context.oneByType(Group.class).orElse(null);
    if (group != null && group.getName() != null) {
      String groupName = group.getName();
      if (service.isPermitted(groupName)) {
        LinkBuilder linkBuilder = new LinkBuilder(scmPathInfoStoreProvider.get().get(), GroupManagerResource.class);
        appender.appendLink("managers", linkBuilder.method("get").parameters(groupName).href());
      }
    }
  }
}
