package com.valoriz.algolia.connector.model;

import lombok.Data;

import java.util.List;

@Data
public class CollectionConfiguration {

    private String type;

    private String collectionName;

    private List<String> fieldsToFetch;

    private List<String> mappingProperties;

    private String priceType;

}
