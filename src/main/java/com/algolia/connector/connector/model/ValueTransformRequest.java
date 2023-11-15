package com.algolia.connector.connector.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ValueTransformRequest {

    private List<Object> inputType;

    private Map<String, String> outputType;

}
