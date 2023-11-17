package com.valoriz.algolia.connector.transformer;

import com.valoriz.algolia.connector.model.TransformModel;
import org.bson.Document;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Component
public class TransformerImpl implements Transformer {

    public List<Document> valueTransformer(List<Document> aggregatedObjects, TransformModel transformModel) {
        List<Document> originalValue = aggregatedObjects;
        Map<String, String> transformationType = new HashMap<>();
        transformModel.getTransformer().stream()
                .forEach(obj -> transformationType.put(obj.getInputFieldName(), obj.getConvertTo()));
        List<Document> transformedValue = new ArrayList<>();

        for (Document document : originalValue) {
            if (document instanceof Map) {
                Map<?, ?> mapValue = (Map<?, ?>) document;
                Document transformedDocument = new Document(); // Assuming Document is a class or you may need to use another class

                for (Map.Entry<?, ?> entry : mapValue.entrySet()) {
                    String fieldName = (String) entry.getKey();
                    Object fieldValue = entry.getValue();

                    if (transformationType.containsKey(fieldName) && fieldValue != null && transformationType.get(
                            fieldName) != null) {
                        String type = transformationType.get(fieldName);
                        Object transformedFieldValue = applyTransformation(type, fieldValue);
                        transformedDocument.put(fieldName, transformedFieldValue);
                    } else {
                        transformedDocument.put(fieldName, fieldValue);
                    }
                }
                transformedValue.add(transformedDocument);
            }
        }
        return transformedValue;
    }

    private Object applyTransformation(String transformationType, Object fieldValue) {
        switch (transformationType) {
        case "booleanToString":
            if (fieldValue instanceof Boolean) {
                return fieldValue.toString();
            }
            break;
        case "stringToInteger":
            if (fieldValue instanceof String) {
                try {
                    return Integer.parseInt((String) fieldValue);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            break;
        case "integerToBoolean":
            if (fieldValue instanceof Integer) {
                return (int) fieldValue != 0;
            }
            break;
        case "integerToString":
            if (fieldValue instanceof Integer) {
                return fieldValue.toString();
            }
            break;
        // Add more cases for other transformation types as needed

        }
        // If no transformation is applied or the transformation type is not recognized
        return fieldValue;
    }

    public List<Document> attributeTransformer(List<Document> documents, TransformModel transformModel) {
        Map<String, String> fieldMappings = new HashMap<>();
        transformModel.getTransformer().stream()
                .forEach(obj -> fieldMappings.put(obj.getInputFieldName(), obj.getOutputFieldName()));
        List<Document> updatedIndexObjects = new ArrayList<>();
        for (Document document : documents) {
            Document updatedDocument = new Document(document);

            for (Map.Entry<String, String> entry : fieldMappings.entrySet()) {
                String oldFieldName = entry.getKey();
                String newFieldName = entry.getValue();
                if (updatedDocument.containsKey(oldFieldName) && newFieldName != null) {
                    updatedDocument.put(newFieldName, updatedDocument.get(oldFieldName));
                    updatedDocument.remove(oldFieldName);
                }
            }
            updatedIndexObjects.add(updatedDocument);
        }
        return updatedIndexObjects;
    }

}
