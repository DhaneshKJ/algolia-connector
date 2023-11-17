package com.algolia.connector.connector.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TransformModel {

    private Map<String, String> valueTransformMap;

    private Map<String, String> attributeTransformMap;


}
