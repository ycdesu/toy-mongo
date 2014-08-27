package org.ycavatars.toymongo.storage;

import org.springframework.stereotype.Repository;

/**
 * @author ycavatars
 */
@Repository("dataFileRepository")
public class DataFileRepositoryImpl implements DataFileRepository {


  @Override public void saveData(String database, String collection, Object data) {

  }

  @Override public void ensureIndex(String database, String collection,
      Object indexField) {

  }

  @Override public Object getData(String database, String collection, Object query) {
    return null;
  }

  @Override public Object listData(String database, String collection, Object query,
      Object sortBy) {
    return null;
  }


}
