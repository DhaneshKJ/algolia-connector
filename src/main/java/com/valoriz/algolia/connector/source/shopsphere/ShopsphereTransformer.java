//package com.valoriz.algolia.connector.source.shopsphere;
//
//import com.valoriz.algolia.connector.model.SourceConfiguration;
//import com.valoriz.algolia.connector.model.ConfigurationModel;
//import com.valoriz.algolia.connector.model.TransformModel;
//import org.bson.Document;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//
//@Component
//@Service
//public class ShopsphereTransformer {
//
//    /**
//     * This method is used to convert configuration file to shopsphere config model
//     *
//     * @param document
//     * @return ShopPhereMongoRequest
//     */
//    public static ConfigurationModel convertToShopPhereMongoRequest(Document document) {
//        ConfigurationModel request = new ConfigurationModel();
//
//        // Extracting values from the Document and setting them in the ShopPhereMongoRequest
//        request.setAlgoliaApplicationId(document.getString("algoliaApplicationId"));
//        request.setAlgoliaIndexName(document.getString("algoliaIndexName"));
//        request.setAlgoliaAdminApiKey(document.getString("algoliaAdminApiKey"));
//
//        //        request.setApplicationId(document.getString("applicationId"));
//        //        request.setAdminApiKey(document.getString("adminApiKey"));
//
//        // Extracting the configurationData array
//
//        SourceConfiguration sourceConfiguration = new SourceConfiguration();
//        Object configDataObject = document.get("sourceConfiguration");
//
//        if (configDataObject != null) {
//
//        }
//
//        //Object configDataObject = document.get("configurationData");
//
//        if (configDataObject instanceof List) {
//            List<?> configDataList = (List<?>) configDataObject;
//            List<SourceConfiguration> sourceConfigurationList = new ArrayList<>();
//            for (Object configDataEntry : configDataList) {
//                SourceConfiguration configData;
//                if (configDataEntry instanceof Document) {
//                    configData = convertToConfigurationData((Document) configDataEntry);
//                } else if (configDataEntry instanceof LinkedHashMap) {
//                    // Handle LinkedHashMap, extract values manually
//                    LinkedHashMap<?, ?> linkedHashMap = (LinkedHashMap<?, ?>) configDataEntry;
//
//                    // Assuming keys are strings, adjust the types if needed
//                    configData = new SourceConfiguration();
//                    configData.setType((String) linkedHashMap.get("type"));
//                    configData.setCollectionName((String) linkedHashMap.get("collectionName"));
//                    configData.setFieldsToFetch((List<String>) linkedHashMap.get("fieldsToFetch"));
//                    configData.setConnectFrom((String) linkedHashMap.get("connectFrom"));
//                    configData.setMappingProperties((List<String>) linkedHashMap.get("mappingProperties"));
//                    configData.setPriceType((String) linkedHashMap.get("priceType"));
//                    configData.setConnectTo((String) linkedHashMap.get("connectTo"));
//                } else {
//                    // Log the unexpected type
//                    System.err.println(
//                            "Unexpected type within 'configurationData' list: " + configDataEntry.getClass());
//                    // Handle other cases or throw an exception if necessary
//                    throw new RuntimeException("Unexpected type within 'configurationData' list");
//                }
//                sourceConfigurationList.add(configData);
//            }
//            request.setConfigurationData(sourceConfigurationList);
//        } else if (configDataObject instanceof Document) {
//            SourceConfiguration configData = convertToConfigurationData((Document) configDataObject);
//            List<SourceConfiguration> sourceConfigurationList = new ArrayList<>();
//            sourceConfigurationList.add(configData);
//            request.setConfigurationData(sourceConfigurationList);
//        } else {
//            // Handle other cases or throw an exception if necessary
//            throw new RuntimeException("Unexpected type for 'configurationData'");
//        }
//        return request;
//    }
//
//    /**
//     * This method is used to convert configurationData object to ConfigurationData model
//     *
//     * @param document
//     * @return ConfigurationData
//     */
//    private static SourceConfiguration convertToConfigurationData(Document document) {
//        SourceConfiguration configData = new SourceConfiguration();
//
//        // Extracting values from the Document and setting them in the ConfigurationData
//        configData.setType(document.getString("type"));
//        configData.setCollectionName(document.getString("collectionName"));
//        configData.setFieldsToFetch((List<String>) document.get("fieldsToFetch"));
//        configData.setConnectFrom(document.getString("connectFrom"));
//        configData.setMappingProperties((List<String>) document.get("mappingProperties"));
//        configData.setPriceType(document.getString("priceType"));
//        configData.setConnectTo(document.getString("connectTo"));
//
//        return configData;
//    }
//
//    /**
//     * This method is used to convert transformDocument to TransformModel
//     *
//     * @param transformDocument
//     * @return TransformModel
//     */
//    public static TransformModel transformModelTransformer(Document transformDocument) {
//        TransformModel transformModel = new TransformModel();
//
//        @SuppressWarnings("unchecked") Map<String, String> valueTransformMap = (Map<String, String>) transformDocument.get(
//                "valueTransform");
//        transformModel.setValueTransformMap(valueTransformMap);
//
//        @SuppressWarnings("unchecked") Map<String, String> attributeTransformMap = (Map<String, String>) transformDocument.get(
//                "attributeTransform");
//        transformModel.setAttributeTransformMap(attributeTransformMap);
//        return transformModel;
//    }
//
//}
