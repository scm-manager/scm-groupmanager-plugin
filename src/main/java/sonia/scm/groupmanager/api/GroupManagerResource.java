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

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;

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
  @Operation(
    summary = "Get group manager",
    description = "Returns the group manager for the given group.",
    tags = "GroupManager Plugin",
    operationId = "groupmanager_get_groupmanager")
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
  @Operation(
    summary = "Set group managers",
    description = "Updates the group managers for the given group.",
    tags = "GroupManager Plugin",
    operationId = "groupmanager_set_groupmanager"
  )
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
