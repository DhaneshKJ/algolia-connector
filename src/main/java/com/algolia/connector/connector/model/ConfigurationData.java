package com.algolia.connector.connector.model;

import lombok.Data;

import java.util.List;

@Data
public class ConfigurationData {

    private String type;

    private String collectionName;

    private List<String> fieldsToFetch;

    private String connectFrom;

    private List<String> mappingProperties;

    private String priceType;

    private String connectTo;

}
