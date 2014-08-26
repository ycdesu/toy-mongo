package org.ycavatars.toymongo.rest.action.insert;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * InsertCollection controller.
 *
 * @author ycavatars
 */
@RestController
@EnableAutoConfiguration
@RequestMapping(value = "/databases/{databaseName}/collections")
public class InsertCollectionAction {
}
