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
import { useTranslation } from "react-i18next";
import { useIndex } from "@scm-manager/ui-api";
import { apiClient, MemberNameTagGroup } from "@scm-manager/ui-components";
import {
  Loading,
  ErrorNotification,
  Notification,
  Button,
  useDocumentTitle,
  ComboboxField,
} from "@scm-manager/ui-core";
import { AutocompleteObject, Group, Link, Option } from "@scm-manager/ui-types";

type Props = {
  group: Group;
};

const GroupManager: FC<Props> = ({ group }) => {
  const [t] = useTranslation("plugins");
  useDocumentTitle(t("scm-groupmanager-plugin.navLink"), group.name);
  const [groupManagers, setGroupManagers] = useState<string[]>([]);
  const [loading, setLoading] = useState(true);
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState<Error | null>(null);
  const index = useIndex();

  useEffect(() => {
    getGroupManagers();
  }, [group]);

  const getGroupManagers = async () => {
    setLoading(true);
    try {
      const response = await apiClient.get((group._links.managers as Link).href);
      const gm = await response.json();
      setGroupManagers(gm?.managers || []);
    } catch (err) {
      setError(err as Error);
    } finally {
      setLoading(false);
    }
  };

  const getUserAutoCompleteLink = (): string => {
    if (index) {
      const link = (index._links.autocomplete as Link[]).find((i) => i.name === "users");
      if (link) {
        return link.href;
      }
    }
    return "";
  };

  const loadAutocompletion = (query: string) => {
    if (query.length < 2) {
      return [];
    }

    const userAutoCompleteLink = getUserAutoCompleteLink();
    const link = userAutoCompleteLink + "?q=";

    return fetch(link + query)
      .then((response) => response.json())
      .then((json) => {
        const apiResults = json.map((element: AutocompleteObject) => {
          const label = element.displayName ? `${element.displayName} (${element.id})` : element.id;
          return {
            value: element,
            label,
          };
        });

        const exactMatch = apiResults.find((o: any) => o.value.id.toLowerCase() === query.toLowerCase());

        if (!exactMatch) {
          const customOption = {
            value: { id: query, displayName: query },
            label: `${query}`,
          };
          return [customOption, ...apiResults];
        }

        return apiResults;
      });
  };

  const onSelectFromAutocomplete = (option?: Option<AutocompleteObject>) => {
    if (option?.value?.id) {
      addManager(option.value.id);
    }
  };

  const addManager = (managerName: string) => {
    if (managerName && !groupManagers.includes(managerName)) {
      setGroupManagers([...groupManagers, managerName]);
    }
  };

  const saveChanges = () => {
    apiClient
      .put((group._links.managers as Link).href, { managers: groupManagers })
      .then(() => setSuccess(true))
      .catch(setError);
  };

  if (loading) return <Loading />;

  return (
    <>
      {success && (
        <Notification
          type="success"
          children={t("scm-groupmanager-plugin.add-manager-form.success-message")}
          onClose={() => setSuccess(false)}
        />
      )}
      {error && <ErrorNotification error={error} />}

      <MemberNameTagGroup
        members={groupManagers}
        memberListChanged={setGroupManagers}
        label={t("scm-groupmanager-plugin.add-manager-form.header")}
        helpText={t("scm-groupmanager-plugin.add-manager-form.help-text")}
      />

      <ComboboxField
        label={t("scm-groupmanager-plugin.add-manager-form.input")}
        options={loadAutocompletion}
        onChange={onSelectFromAutocomplete}
        value={null}
      />

      <hr />

      <div className="level">
        <div className="level-left" />
        <div className="level-right">
          <Button variant="primary" isLoading={loading} onClick={saveChanges}>
            {t("scm-groupmanager-plugin.add-manager-form.submit")}
          </Button>
        </div>
      </div>
    </>
  );
};

export default GroupManager;
