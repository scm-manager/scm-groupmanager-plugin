package sonia.scm.groupmanager.api;

import de.otto.edison.hal.Links;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import sonia.scm.api.v2.resources.LinkBuilder;
import sonia.scm.groupmanager.service.GroupManagers;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

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
