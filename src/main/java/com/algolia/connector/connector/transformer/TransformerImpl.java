package com.algolia.connector.connector.transformer;

import com.algolia.connector.connector.model.ValueTransformRequest;
import org.bson.Document;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Component
public class TransformerImpl implements Transformer {

    public List<Document> valueTransformer(ValueTransformRequest valueTransformRequest) {
        List<Document> originalValue = valueTransformRequest.getInputType();

        Map<String, String> transformationType = valueTransformRequest.getOutputType();
        List<Document> transformedValue = new ArrayList<>();

        for (Document document : originalValue) {
            if (document instanceof Map) {
                Map<?, ?> mapValue = (Map<?, ?>) document;
                Document transformedDocument = new Document(); // Assuming Document is a class or you may need to use another class

                for (Map.Entry<?, ?> entry : mapValue.entrySet()) {
                    String fieldName = (String) entry.getKey();
                    Object fieldValue = entry.getValue();

                    if (transformationType.containsKey(fieldName)) {
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

    public List<Document> attributeTransformer(List<Document> documents, Map<String, String> fieldsToUpdate) {
        List<Document> updatedIndexObjects = new ArrayList<>();
        for (Document document : documents) {
            Document updatedDocument = new Document(document);
            Map<String, String> fieldMappings = fieldsToUpdate;
            for (Map.Entry<String, String> entry : fieldMappings.entrySet()) {
                String oldFieldName = entry.getKey();
                String newFieldName = entry.getValue();
                if (updatedDocument.containsKey(oldFieldName)) {
                    updatedDocument.put(newFieldName, updatedDocument.get(oldFieldName));
                    updatedDocument.remove(oldFieldName);
                }
            }
            updatedIndexObjects.add(updatedDocument);
        }
        return updatedIndexObjects;
    }

}
