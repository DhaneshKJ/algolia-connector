package com.valoriz.algolia.connector.transformer;

import com.valoriz.algolia.connector.model.TransformModel;
import org.bson.Document;

import java.util.List;
import java.util.Map;

public interface Transformer {

    public List<Document> valueTransformer(List<Document> aggregatedObjects, TransformModel transformModel);

    public List<Document> attributeTransformer(List<Document> documents, TransformModel transformModel);

}
