package com.algolia.connector.connector.model;

import lombok.Data;
import org.bson.Document;

import java.util.Map;

@Data
public class ConfigFileModel {

    private Document fileConfiguration;

    private Map<String, String> transformerJson;

}
