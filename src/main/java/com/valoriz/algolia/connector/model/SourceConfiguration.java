package com.valoriz.algolia.connector.model;

import lombok.Data;

import java.util.List;

@Data
public class SourceConfiguration {

    private String type;

    private String clientId;

    private Boolean active;

    List<CollectionConfiguration> collectionConfiguration;

}
