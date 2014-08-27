package org.ycavatars.toymongo.storage;

import com.google.common.base.Preconditions;

import java.io.RandomAccessFile;

/**
 * This file is composed of extents which holds index and documents.
 *
 * @author ycavatars
 */
public class DataFile {

  private RandomAccessFile file;

  private String database;

  public DataFile(String database) {
    this.database = Preconditions.checkNotNull(database);
  }

  /**
   * Returns an existing extent which has enough space for a new document or creates
   * a new one.
   *
   * @return
   */
  public Extent getAvailableExtent() {
    return null;
  }

}
