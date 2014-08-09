package org.ycavatars.toymongo.rest.action.list;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ListIndexes controller.
 *
 * @author ycavatars
 */
@RestController
@EnableAutoConfiguration
@RequestMapping(value = "/databases/{databaseName}/collections/{collectionName}/indexes")
public class ListIndexAction {
}
