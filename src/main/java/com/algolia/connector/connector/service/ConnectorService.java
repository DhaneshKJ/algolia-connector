package com.algolia.connector.connector.service;

import org.bson.Document;

import java.util.List;

public interface ConnectorService {

    /**
     * This method is used to trigger the connector engine and bulk index
     *
     * @return List<Document>
     */
    List<Document> connectorEngine();

}
