package com.algolia.connector.connector.model;

import lombok.Data;

import java.util.List;

@Data
public class ShopPhereMongoRequest {

    private String inputType;

    private String clientId;

    private boolean active;

    private String applicationId;

    private String adminApiKey;

    private List<ConfigurationData> configurationData;

}
