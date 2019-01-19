package sonia.scm.groupmanager.service;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class GroupManagers {

  /**
   * the id of the managed group
   */
  private String groupName;

  /**
   * the user ids of the group manager
   */
  private List<String> managers;

}
