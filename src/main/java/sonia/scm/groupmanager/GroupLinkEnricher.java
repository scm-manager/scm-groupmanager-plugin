package sonia.scm.groupmanager;

import sonia.scm.api.v2.resources.Enrich;
import sonia.scm.api.v2.resources.LinkAppender;
import sonia.scm.api.v2.resources.LinkBuilder;
import sonia.scm.api.v2.resources.LinkEnricher;
import sonia.scm.api.v2.resources.LinkEnricherContext;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.group.Group;
import sonia.scm.groupmanager.api.GroupManagerResource;
import sonia.scm.groupmanager.service.GroupManagerService;
import sonia.scm.plugin.Extension;

import javax.inject.Inject;
import javax.inject.Provider;

@Extension
@Enrich(Group.class)
public class GroupLinkEnricher implements LinkEnricher {

  private Provider<ScmPathInfoStore> scmPathInfoStoreProvider;
  private GroupManagerService service;

  @Inject
  public GroupLinkEnricher(Provider<ScmPathInfoStore> scmPathInfoStoreProvider, GroupManagerService service) {
    this.scmPathInfoStoreProvider = scmPathInfoStoreProvider;
    this.service = service;
  }

  @Override
  public void enrich(LinkEnricherContext context, LinkAppender appender) {
    Group group = context.oneByType(Group.class).orElse(null);
    if (group != null && group.getName() != null) {
      String groupName = group.getName();
      if (service.isPermitted(groupName)) {
        LinkBuilder linkBuilder = new LinkBuilder(scmPathInfoStoreProvider.get().get(), GroupManagerResource.class);
        appender.appendOne("managers", linkBuilder.method("get").parameters(groupName).href());
      }
    }
  }
}
