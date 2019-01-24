package sonia.scm.groupmanager;

import com.google.inject.AbstractModule;
import org.mapstruct.factory.Mappers;
import sonia.scm.groupmanager.api.GroupManagerMapper;
import sonia.scm.plugin.Extension;

@Extension
public class MapperModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(GroupManagerMapper.class).to(Mappers.getMapper(GroupManagerMapper.class).getClass());
  }
}
