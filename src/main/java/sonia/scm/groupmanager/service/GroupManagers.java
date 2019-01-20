package sonia.scm.groupmanager.service;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class GroupManagers {

  /**
   * the user ids of the group manager
   */
  private List<String> managers = new ArrayList<>();

}
