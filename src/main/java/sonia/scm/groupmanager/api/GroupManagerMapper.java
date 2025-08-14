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

import de.otto.edison.hal.Links;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import sonia.scm.api.v2.resources.LinkBuilder;
import sonia.scm.groupmanager.service.GroupManagers;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;

@Mapper
public abstract class GroupManagerMapper {

  private LinkBuilder linkBuilder;

  public abstract GroupManagers map(GroupManagersDto groupManagersDto);

  @Mapping(target = "attributes", ignore = true)
  public abstract GroupManagersDto map(GroupManagers groupManager, @Context String groupName);


  public GroupManagerMapper using(UriInfo uriInfo) {
    this.linkBuilder = new LinkBuilder(uriInfo::getBaseUri, GroupManagerResource.class);
    return this;
  }

  @AfterMapping
  void addLinks(@MappingTarget GroupManagersDto dto, @Context String groupName) {
    dto.add(Links.linkingTo().self(linkBuilder.method("get").parameters(groupName).href()).build());
  }
}
