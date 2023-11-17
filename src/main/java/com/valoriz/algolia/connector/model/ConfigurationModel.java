package com.valoriz.algolia.connector.model;

import lombok.Data;

@Data
public class ConfigurationModel {

    private String algoliaApplicationId;

    private String algoliaIndexName;

    private String algoliaAdminApiKey;

    private SourceConfiguration sourceConfiguration;

    //    private String inputType;
    //
    //    private String clientId;
    //
    //    private boolean active;
    //
    //    private String adminApiKey;

}
