package sonia.scm.groupmanager.api;

import com.webcohesion.enunciate.metadata.rs.ResponseCode;
import com.webcohesion.enunciate.metadata.rs.StatusCodes;
import sonia.scm.groupmanager.dto.GroupManagerMapper;
import sonia.scm.groupmanager.dto.GroupManagersDto;
import sonia.scm.groupmanager.service.GroupManagerService;
import sonia.scm.util.AssertUtil;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

@Path(GroupManagerResource.PATH)
public class GroupManagerResource {


  public static final String PATH = "v2/plugins/groupmanager";

  private final GroupManagerService service;
  private final GroupManagerMapper mapper;

  @Inject
  public GroupManagerResource(GroupManagerService service, GroupManagerMapper mapper) {
    this.service = service;
    this.mapper = mapper;
  }

  /**
   * Get the stored GroupManager for the given group
   * @param uriInfo
   * @param groupName
   * @return the stored GroupManager for the given group
   */
  @GET
  @Path("/{group_name}")
  @Produces(MediaType.APPLICATION_JSON)
  @StatusCodes({
    @ResponseCode(code = 200, condition = "success"),
    @ResponseCode(code = 401, condition = "not authenticated / invalid credentials"),
    @ResponseCode(code = 403, condition = "not authorized, the current user does not have the \"group:manage:id\" privilege"),
    @ResponseCode(code = 500, condition = "internal server error")
  })
  public GroupManagersDto get(@Context UriInfo uriInfo, @PathParam("group_name") String groupName) {
    service.checkPermission(groupName);
    return mapper.using(uriInfo).map(service.get(groupName));
  }


  /**
   * Set the given group managers for the given group
   * @param uriInfo
   * @param groupName
   */
  @PUT
  @Path("/{group_name}")
  @Produces(MediaType.APPLICATION_JSON)
  @StatusCodes({
    @ResponseCode(code = 200, condition = "success"),
    @ResponseCode(code = 401, condition = "not authenticated / invalid credentials"),
    @ResponseCode(code = 403, condition = "not authorized, the current user does not have the \"group:manage:id\" privilege"),
    @ResponseCode(code = 500, condition = "internal server error")
  })
  public void set(@Context UriInfo uriInfo, @PathParam("group_name")  String groupName, GroupManagersDto groupManagersDto) {
    AssertUtil.assertIsNotNull(groupManagersDto);
    service.checkPermission(groupManagersDto.getGroupName());
    service.set(mapper.using(uriInfo).map(groupManagersDto));
  }
}
