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

import React, { FC, useEffect, useState } from "react";
import { AutocompleteObject, Group, Link } from "@scm-manager/ui-types";
import { useTranslation } from "react-i18next";
import {
  apiClient,
  AutocompleteAddEntryToTableField,
  ErrorNotification,
  Loading,
  MemberNameTagGroup,
  Notification,
  SubmitButton
} from "@scm-manager/ui-components";
import { useIndex } from "@scm-manager/ui-api";

type Props = {
  group: Group;
};

const GroupManager: FC<Props> = ({ group }) => {
  const [t] = useTranslation("plugins");
  const [groupManagers, setGroupManagers] = useState<string[]>([]);
  const [loading, setLoading] = useState(true);
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState<Error | null>(null);
  const { data: index } = useIndex();

  useEffect(() => {
    getGroupManagers().then(r => r);
  }, [group]);

  const getGroupManagers = () => {
    return apiClient
      .get((group._links.managers as Link).href)
      .then(response => response.json())
      .then(gm => {
        if (gm) {
          setGroupManagers(!gm.managers ? [] : gm.managers);
          setLoading(false);
        }
      })
      .catch(err => {
        setLoading(false);
        setError(err);
      });
  };

  const submit = () => {
    apiClient
      .put((group._links.managers as Link).href, {
        managers: groupManagers
      })
      .then(() => {
        setSuccess(true);
      })
      .catch(err => {
        setSuccess(false);
        setError(err);
      });
  };

  const getUserAutoCompleteLink = (): string => {
    if (index) {
      const link = (index._links.autocomplete as Link[]).find(i => i.name === "users");
      if (link) {
        return link.href;
      }
    }
    return "";
  };

  const loadAutocompletion = (inputValue: string) => {
    let userAutoCompleteLink = getUserAutoCompleteLink();
    const link = userAutoCompleteLink + "?q=";
    return fetch(link + inputValue)
      .then(response => response.json())
      .then(json => {
        return json.map((element: AutocompleteObject) => {
          const label = element.displayName ? `${element.displayName} (${element.id})` : element.id;
          return {
            value: element,
            label
          };
        });
      });
  };

  if (loading) {
    return <Loading />;
  }
  let message = null;

  if (success) {
    message = (
      <Notification
        type="success"
        children={t("scm-groupmanager-plugin.add-manager-form.success-message")}
        onClose={() => setSuccess(false)}
      />
    );
  } else if (error) {
    message = <ErrorNotification error={error} />;
  }

  return (
    <>
      {message}
      <MemberNameTagGroup
        members={groupManagers}
        memberListChanged={members => setGroupManagers(members)}
        label={t("scm-groupmanager-plugin.add-manager-form.header")}
        helpText={t("scm-groupmanager-plugin.add-manager-form.help-text")}
      />

      <AutocompleteAddEntryToTableField
        addEntry={member => setGroupManagers([...groupManagers, member.value.id])}
        disabled={false}
        buttonLabel={t("scm-groupmanager-plugin.add-member-button.label")}
        loadSuggestions={loadAutocompletion}
        placeholder={t("scm-groupmanager-plugin.add-member-autocomplete.placeholder")}
        loadingMessage={t("scm-groupmanager-plugin.add-member-autocomplete.loading")}
        noOptionsMessage={t("scm-groupmanager-plugin.add-member-autocomplete.no-options")}
      />

      <SubmitButton
        label={t("scm-groupmanager-plugin.add-manager-form.submit")}
        action={submit}
        loading={loading}
        disabled={false}
      />
    </>
  );
};

export default GroupManager;
