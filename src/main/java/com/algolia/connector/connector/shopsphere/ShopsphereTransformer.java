package com.algolia.connector.connector.shopsphere;

import com.algolia.connector.connector.model.ConfigurationData;
import com.algolia.connector.connector.model.ShopPhereMongoRequest;
import org.bson.Document;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Component
@Service
public class ShopsphereTransformer {

    public static ShopPhereMongoRequest convertToShopPhereMongoRequest(Document document) {
        ShopPhereMongoRequest request = new ShopPhereMongoRequest();

        // Extracting values from the Document and setting them in the ShopPhereMongoRequest
        request.setInputType(document.getString("inputType"));
        request.setClientId(document.getString("clientId"));
        request.setActive(document.getBoolean("active"));
        request.setApplicationId(document.getString("applicationId"));
        request.setAdminApiKey(document.getString("adminApiKey"));

        // Extracting the configurationData array
        Object configDataObject = document.get("configurationData");

        if (configDataObject instanceof List) {
            List<?> configDataList = (List<?>) configDataObject;

            List<ConfigurationData> configurationDataList = new ArrayList<>();

            for (Object configDataEntry : configDataList) {
                ConfigurationData configData;

                if (configDataEntry instanceof Document) {
                    configData = convertToConfigurationData((Document) configDataEntry);
                } else if (configDataEntry instanceof LinkedHashMap) {
                    // Handle LinkedHashMap, extract values manually
                    LinkedHashMap<?, ?> linkedHashMap = (LinkedHashMap<?, ?>) configDataEntry;

                    // Assuming keys are strings, adjust the types if needed
                    configData = new ConfigurationData();
                    configData.setType((String) linkedHashMap.get("type"));
                    configData.setCollectionName((String) linkedHashMap.get("collectionName"));
                    configData.setFieldsToFetch((List<String>) linkedHashMap.get("fieldsToFetch"));
                    configData.setConnectFrom((String) linkedHashMap.get("connectFrom"));
                    configData.setMappingProperties((List<String>) linkedHashMap.get("mappingProperties"));
                    configData.setPriceType((String) linkedHashMap.get("priceType"));
                    configData.setConnectTo((String) linkedHashMap.get("connectTo"));
                } else {
                    // Log the unexpected type
                    System.err.println(
                            "Unexpected type within 'configurationData' list: " + configDataEntry.getClass());

                    // Handle other cases or throw an exception if necessary
                    throw new RuntimeException("Unexpected type within 'configurationData' list");
                }

                configurationDataList.add(configData);
            }

            request.setConfigurationData(configurationDataList);
        } else if (configDataObject instanceof Document) {
            ConfigurationData configData = convertToConfigurationData((Document) configDataObject);
            List<ConfigurationData> configurationDataList = new ArrayList<>();
            configurationDataList.add(configData);

            request.setConfigurationData(configurationDataList);
        } else {
            // Handle other cases or throw an exception if necessary
            throw new RuntimeException("Unexpected type for 'configurationData'");
        }

        return request;
    }

    private static ConfigurationData convertToConfigurationData(Document document) {
        ConfigurationData configData = new ConfigurationData();

        // Extracting values from the Document and setting them in the ConfigurationData
        configData.setType(document.getString("type"));
        configData.setCollectionName(document.getString("collectionName"));
        configData.setFieldsToFetch((List<String>) document.get("fieldsToFetch"));
        configData.setConnectFrom(document.getString("connectFrom"));
        configData.setMappingProperties((List<String>) document.get("mappingProperties"));
        configData.setPriceType(document.getString("priceType"));
        configData.setConnectTo(document.getString("connectTo"));

        return configData;
    }

}
