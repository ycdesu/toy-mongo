package org.ycavatars.toymongo.storage;

import com.google.common.base.Preconditions;

/**
 * Logical container within {@link org.ycavatars.toymongo.storage.DataFile} used to
 * store documents and index.
 *
 * @author ycavatars
 */
public class Extent {

  private final int paddingSize;

  public Extent(int paddingSize) {
    Preconditions.checkArgument(paddingSize > 0);
    this.paddingSize = paddingSize;
  }
}
