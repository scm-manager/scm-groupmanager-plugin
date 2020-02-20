package sonia.scm.groupmanager.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import sonia.scm.api.v2.resources.ErrorDto;
import sonia.scm.groupmanager.service.GroupManagerService;
import sonia.scm.util.AssertUtil;
import sonia.scm.web.VndMediaType;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

@OpenAPIDefinition(tags = {
  @Tag(name = "GroupManager Plugin", description = "GroupManager plugin provided endpoints")
})
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
   *
   * @param uriInfo
   * @param groupName
   * @return the stored GroupManager for the given group
   */
  @GET
  @Path("/{group_name}")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Get group manager", description = "Returns the group manager for the given group.", tags = "GroupManager Plugin")
  @ApiResponse(
    responseCode = "200",
    description = "success",
    content = @Content(
      mediaType = MediaType.APPLICATION_JSON,
      schema = @Schema(implementation = GroupManagersDto.class)
    )
  )
  @ApiResponse(responseCode = "401", description = "not authenticated / invalid credentials")
  @ApiResponse(responseCode = "403", description = "not authorized /  the current user does not have the \"group:manage:id\" privilege")
  @ApiResponse(
    responseCode = "500",
    description = "internal server error",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  public GroupManagersDto get(@Context UriInfo uriInfo, @PathParam("group_name") String groupName) {
    service.checkPermission(groupName);
    return mapper.using(uriInfo).map(service.get(groupName), groupName);
  }


  /**
   * Set the given group managers for the given group
   *
   * @param uriInfo
   * @param groupName
   */
  @PUT
  @Path("/{group_name}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Operation(summary = "Set group managers", description = "Updates the group managers for the given group.", tags = "GroupManager Plugin")
  @ApiResponse(responseCode = "204", description = "no content")
  @ApiResponse(responseCode = "401", description = "not authenticated / invalid credentials")
  @ApiResponse(responseCode = "403", description = "not authorized /  the current user does not have the \"group:manage:id\" privilege")
  @ApiResponse(
    responseCode = "500",
    description = "internal server error",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  public void set(@Context UriInfo uriInfo, @PathParam("group_name") String groupName, GroupManagersDto groupManagersDto) {
    AssertUtil.assertIsNotNull(groupManagersDto);
    service.checkPermission(groupName);
    service.set(mapper.using(uriInfo).map(groupManagersDto), groupName);
  }
}
