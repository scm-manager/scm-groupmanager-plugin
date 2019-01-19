package sonia.scm.groupmanager.dto;

import de.otto.edison.hal.Links;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import sonia.scm.api.v2.resources.LinkBuilder;
import sonia.scm.groupmanager.api.GroupManagerResource;
import sonia.scm.groupmanager.service.GroupManagerService;
import sonia.scm.groupmanager.service.GroupManagers;

import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;

import static de.otto.edison.hal.Link.link;

@Mapper
public abstract class GroupManagerMapper {

  private LinkBuilder linkBuilder;
  @Inject
  private GroupManagerService service;

  public GroupManagerMapper() {
  }

  public abstract GroupManagers map(GroupManagersDto groupManagersDto);

  @Mapping(target = "attributes", ignore = true)
  public abstract GroupManagersDto map(GroupManagers groupManager);


  public GroupManagerMapper using(UriInfo uriInfo) {
    this.linkBuilder = new LinkBuilder(uriInfo::getBaseUri, GroupManagerResource.class);
    return this;
  }

  @AfterMapping
  void addLinks(@MappingTarget GroupManagersDto dto) {
    if (service.isPermitted(dto.getGroupName())){
      Links.Builder links = Links.linkingTo();
      String link = linkBuilder.method("get").parameters(dto.getGroupName()).href();
      links.single(link("manage", link));
      dto.add(links.build());
    }
  }



}
