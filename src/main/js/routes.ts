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

import { SINGLE_GROUP_BASE_ROUTE, SINGLE_GROUP_SUB_ROUTES } from "@scm-manager/ui-core";
import { Group } from "@scm-manager/ui-types";

export const GROUP_MANAGER_BASE = "groupmanager";

export const resolveGroupManager = (group: Group) => {
  return `/${SINGLE_GROUP_BASE_ROUTE}/${group.name}/${SINGLE_GROUP_SUB_ROUTES.SETTINGS}/${GROUP_MANAGER_BASE}`;
};
