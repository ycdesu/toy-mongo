package org.ycavatars.toymongo.storage;

/**
 * Stores documents and indexes in data files.
 *
 * @author ycavatars
 */
public interface DataFileRepository {

  //TODO data and index have to be BSON or JSON?

  void saveData(String database, String collection, Object data);

  void ensureIndex(String database, String collection, Object indexField);

  Object getData(String database, String collection, Object query);

  Object listData(String database, String collection, Object query, Object sortBy);
}
