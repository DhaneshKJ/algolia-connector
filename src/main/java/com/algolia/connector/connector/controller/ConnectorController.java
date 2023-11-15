package com.algolia.connector.connector.controller;

import com.algolia.connector.connector.service.ConnectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/algolia")
public class ConnectorController {

    @Autowired
    ConnectorService connectorService;

    @PostMapping("/bulk-index")
    public ResponseEntity<String> connectorEngine() {
        connectorService.connectorEngine();
        return ResponseEntity.ok("Successfully Indexed");
    }



}
