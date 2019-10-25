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
