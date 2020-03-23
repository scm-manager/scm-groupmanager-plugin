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
import React from "react";
import { connect } from "react-redux";
import { Group } from "@scm-manager/ui-types";
import { withRouter } from "react-router-dom";
import { WithTranslation, withTranslation } from "react-i18next";
import {
  apiClient,
  AutocompleteAddEntryToTableField,
  Loading,
  Notification,
  ErrorNotification,
  MemberNameTagGroup,
  SubmitButton
} from "@scm-manager/ui-components";

type Props = WithTranslation & {
  group: Group;
  autocompleteLink: string;
};

type State = {
  groupManagers: string[];
  loading: boolean;
  success?: boolean;
  error?: string;
};

class GroupManager extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      groupManagers: [],
      loading: true
    };
  }

  componentDidMount(): void {
    this.getGroupManagers().then(groupManagers => {
      if (groupManagers) {
        this.setState({
          groupManagers: !groupManagers.managers ? [] : groupManagers.managers,
          loading: false
        });
      }
    });
  }

  getGroupManagers() {
    const url = this.props.group._links.managers.href;
    return apiClient
      .get(url)
      .then(response => response.json())
      .then(groupManagers => {
        return groupManagers;
      })
      .catch(err => {
        this.setState({
          loading: false
        });
        return {
          error: err
        };
      });
  }

  submit = () => {
    const url = this.props.group._links.managers.href;
    const managers = this.state.groupManagers;
    if (url) {
      apiClient
        .put(url, {
          managers
        })
        .then(response => {
          this.setState({
            success: true
          });
          return response;
        })
        .catch(err => {
          this.setState({
            success: false,
            error: err
          });
          return {
            error: err
          };
        });
    }
  };

  handleChange = (groupManagers: string[]) => {
    this.setState({
      groupManagers
    });
  };

  loadUserAutocompletion = (inputValue: string) => {
    return this.loadAutocompletion(this.props.autocompleteLink, inputValue);
  };

  loadAutocompletion(url: string, inputValue: string) {
    const link = url + "?q=";
    return fetch(link + inputValue)
      .then(response => response.json())
      .then(json => {
        return json.map(element => {
          const label = element.displayName ? `${element.displayName} (${element.id})` : element.id;
          return {
            value: element,
            label
          };
        });
      });
  }

  addMember = (value: SelectValue) => {
    const { groupManagers } = this.state;
    groupManagers.push(value.value.id);
    this.setState({
      groupManagers
    });
  };

  render() {
    const { t } = this.props;
    const { groupManagers, loading, success, error } = this.state;

    if (loading) {
      return <Loading />;
    }
    let message = null;

    if (success) {
      message = (
        <Notification
          type={"success"}
          children={t("scm-groupmanager-plugin.add-manager-form.success-message")}
          onClose={() =>
            this.setState({
              success: false
            })
          }
        />
      );
    } else if (error) {
      message = <ErrorNotification error={error.message} />;
    }

    return (
      <>
        {message}
        <MemberNameTagGroup
          members={groupManagers}
          memberListChanged={this.handleChange}
          label={t("scm-groupmanager-plugin.add-manager-form.header")}
          helpText={t("scm-groupmanager-plugin.add-manager-form.help-text")}
        />

        <AutocompleteAddEntryToTableField
          addEntry={this.addMember}
          disabled={false}
          buttonLabel={t("scm-groupmanager-plugin.add-member-button.label")}
          errorMessage={t("scm-groupmanager-plugin.add-member-textfield.error")}
          loadSuggestions={this.loadUserAutocompletion}
          placeholder={t("scm-groupmanager-plugin.add-member-autocomplete.placeholder")}
          loadingMessage={t("scm-groupmanager-plugin.add-member-autocomplete.loading")}
          noOptionsMessage={t("scm-groupmanager-plugin.add-member-autocomplete.no-options")}
        />

        <SubmitButton
          label={t("scm-groupmanager-plugin.add-manager-form.submit")}
          action={this.submit}
          loading={loading}
          disabled={false}
        />
      </>
    );
  }
}

function getUserAutoCompleteLink(state: object): string {
  const link = getLinkCollection(state, "autocomplete").find(i => i.name === "users");
  if (link) {
    return link.href;
  }
  return "";
}

function getLinkCollection(state: object, name: string): Link[] {
  if (state.indexResources.links && state.indexResources.links[name]) {
    return state.indexResources.links[name];
  }
  return [];
}

const mapStateToProps = state => {
  const autocompleteLink = getUserAutoCompleteLink(state);
  return {
    autocompleteLink
  };
};

export default connect(mapStateToProps)(withRouter(withTranslation("plugins")(GroupManager)));
