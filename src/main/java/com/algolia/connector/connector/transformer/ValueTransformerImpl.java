package com.algolia.connector.connector.transformer;

import com.algolia.connector.connector.model.ValueTransformRequest;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Component
public class ValueTransformerImpl implements ValueTransformer {

    public List<Object> valueTransformer(ValueTransformRequest valueTransformRequest) {
        List<Object> originalValue = valueTransformRequest.getInputType();

        Map<String, String> transformationType = valueTransformRequest.getOutputType();
        List<Object> transformedValue = new ArrayList<>();

        for (Object value : originalValue) {
            if (value instanceof Map) {
                Map<?, ?> mapValue = (Map<?, ?>) value;
                Map<String, Object> transformedEntry = new LinkedHashMap<>(); // Use LinkedHashMap to maintain order

                for (Map.Entry<?, ?> entry : mapValue.entrySet()) {
                    String fieldName = (String) entry.getKey();
                    Object fieldValue = entry.getValue();

                    if (transformationType.containsKey(fieldName)) {
                        String type = transformationType.get(fieldName);
                        Object transformedFieldValue = applyTransformation(type, fieldValue);
                        transformedEntry.put(fieldName, transformedFieldValue);
                    } else {
                        transformedEntry.put(fieldName, fieldValue);
                    }
                }
                transformedValue.add(transformedEntry);
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

}
