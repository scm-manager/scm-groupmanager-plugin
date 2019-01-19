package sonia.scm.groupmanager.service;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@XmlRootElement(name = "all-group-managers")
@XmlAccessorType(XmlAccessType.FIELD)
public class AllGroupManagers {

  private Map<String , GroupManagers> groupManagers;

}
