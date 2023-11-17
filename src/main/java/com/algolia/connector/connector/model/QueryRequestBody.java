package com.algolia.connector.connector.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class QueryRequestBody {

    private String collectionName;

    private Map<String, Object> queryConditions;

    private List<String> fields;

}
