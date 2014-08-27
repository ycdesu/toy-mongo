package org.ycavatars.toymongo.core.index;

import com.google.common.base.Preconditions;
import org.springframework.stereotype.Service;
import org.ycavatars.toymongo.storage.DataFileRepository;

import javax.annotation.Resource;

/**
 * Provides index operations. It uses the field on which creates index as a key of
 * the {@link org.ycavatars.toymongo.core.collection.BTreeMap}, and the value is
 * the position of a document in a file.
 *
 * @author ycavatars
 */
@Service("indexService")
public class IndexService {

  @Resource(name = "dataFileRepository")
  private DataFileRepository dataFileRepository;

  /**
   * Read data from files and build indexes
   *
   * @param database
   * @param collection
   * @param field
   */
  public void ensureIndex(String database, String collection, String field) {
    Preconditions.checkNotNull(field);

    dataFileRepository.ensureIndex(database, collection, field);
  }


}
