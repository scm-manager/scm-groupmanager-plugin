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
import { useTranslation } from "react-i18next";
import { NavLink } from "@scm-manager/ui-components";
import { binder } from "@scm-manager/ui-extensions";
import { Group, Link } from "@scm-manager/ui-types";
import GroupManager from "./GroupManager";
import { GROUP_MANAGER_BASE, resolveGroupManager } from "./routes";
import { useGroup } from "@scm-manager/ui-api";
import { useParams } from "react-router";

const GroupManagerNavLink = () => {
  const [t] = useTranslation("plugins");
  const { name } = useParams<{ name: string }>();
  const { data: group } = useGroup(name);
  return <NavLink to={resolveGroupManager(group!)} label={t("scm-groupmanager-plugin.navLink")} />;
};

const groupManagerPredicate = ({ group }: { group: Group }) => (group?._links.managers as Link)?.href;

binder.bind("group.setting", GroupManagerNavLink, groupManagerPredicate);

const groupManagerRoute = ({ group }: { group: Group }) => ({
  path: GROUP_MANAGER_BASE,
  element: <GroupManager group={group} />,
});

binder.bind("group.route", groupManagerRoute);
