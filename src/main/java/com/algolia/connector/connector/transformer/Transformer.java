package com.algolia.connector.connector.transformer;

import com.algolia.connector.connector.model.ValueTransformRequest;
import org.bson.Document;

import java.util.List;
import java.util.Map;

public interface Transformer {

    public List<Document> valueTransformer(List<Document> aggregatedObjects, Map<String, String> fieldsToUpdate);

    public List<Document> attributeTransformer(List<Document> documents, Map<String, String> fieldsToUpdate);

}
