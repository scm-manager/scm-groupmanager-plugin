/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
