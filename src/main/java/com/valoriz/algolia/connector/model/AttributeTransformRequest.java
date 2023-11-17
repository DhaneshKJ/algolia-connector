package com.valoriz.algolia.connector.model;

import lombok.Data;
import org.bson.Document;

import java.util.List;
import java.util.Map;

@Data
public class AttributeTransformRequest {

    private List<Document> documents;

    private Map<String, String> fieldsToUpdate;

}
