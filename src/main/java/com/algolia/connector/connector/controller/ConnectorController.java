package com.algolia.connector.connector.controller;

import com.algolia.connector.connector.service.ConnectorService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/algolia")
public class ConnectorController {

    @Autowired
    ConnectorService connectorService;

    /**
     * This method is used to trigger the connector engine and bulk index
     *
     * @return List<Document>
     */
    @PostMapping("/bulk-index")
    public ResponseEntity<List<Document>> connectorEngine() {
        return ResponseEntity.ok(connectorService.connectorEngine());
    }

}
