// @flow

import React from "react";
import {binder} from "@scm-manager/ui-extensions";
import {NavLink} from "@scm-manager/ui-components";
import GroupManager from "./GroupManager";
import {Route} from "react-router-dom";

const groupmanagerPredicate = (props: Object) => {
  return props.group && props.group._links && props.group._links.managers;
};

const GroupManagerRoute = ({url, group}) => {
  return (
    <Route
      path={`${url}/groupmanager/edit`}
      render={() => <GroupManager group={group}  />}
    />
  );
};

binder.bind("group.route", GroupManagerRoute, groupmanagerPredicate);

function matches(route: any) {
  const regex = new RegExp(".*(/groupmanager)/.*");
  return route.location.pathname.match(regex) || route.location.pathname.match(".*(groupmanager)/.*");
}

const GroupManagerNavLink = ({url, group}) => {
  return (
    <NavLink
      to={`${url}/groupmanager/edit`}
      label="Managers"
      activeWhenMatch={matches}
    />
  );
};

binder.bind(
  "group.setting",
  GroupManagerNavLink,
  groupmanagerPredicate
);
