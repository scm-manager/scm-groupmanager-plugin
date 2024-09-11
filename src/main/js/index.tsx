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

import React from "react";
import { binder } from "@scm-manager/ui-extensions";
import { NavLink } from "@scm-manager/ui-components";
import GroupManager from "./GroupManager";
import { Route } from "react-router-dom";

const groupmanagerPredicate = (props: object) => {
  return props.group && props.group._links && props.group._links.managers;
};

const GroupManagerRoute = ({ url, group }) => {
  return <Route path={`${url}/settings/groupmanager`} render={() => <GroupManager group={group} />} />;
};

binder.bind("group.route", GroupManagerRoute, groupmanagerPredicate);

const GroupManagerNavLink = ({ url, group }) => {
  return <NavLink to={`${url}/settings/groupmanager`} label="Managers" />;
};

binder.bind("group.setting", GroupManagerNavLink, groupmanagerPredicate);
