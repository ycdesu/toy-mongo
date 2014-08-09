package org.yc.toymongo.rest.action.insert;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * InsertMultipleDocuments controller.
 *
 * @author ycavatars
 */
@RestController
@EnableAutoConfiguration
@RequestMapping(value = "/databases/{databaseName}/collections/{collectionName}/documents" +
    "/insertAll")
public class InsertDocumentsAction {
}
