//package com.algolia.connector.connector.connectorRouter;
//
//import com.algolia.connector.connector.model.ShopPhereMongoRequest;
//import org.bson.Document;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Service;
//
//import java.util.*;
//
//@Service
//@Component
//public class DataAggregator {
//
//    public List<Document> aggregateData(ShopPhereMongoRequest shopPhereMongoRequest,
//            Map<String, String> transformerJson) {
//        List<Document> updatedProducts = new ArrayList<>();
//        if ("ShopsPhereMongo".equals(shopPhereMongoRequest.getInputType())) {
//            String clientId = shopPhereMongoRequest.getClientId();
//            Boolean active = shopPhereMongoRequest.isActive();
//            List<String> mappingProperties = new ArrayList<>();
//            List<String> fields = new ArrayList<>();
//
//            shopPhereMongoRequest.getConfigurationData().forEach(configurationData -> {
//                String collectionName = configurationData.getCollectionName();
//                fields.addAll(configurationData.getFieldsToFetch());
//                List<String> currentFields = configurationData.getFieldsToFetch();
//
//                if (configurationData.getMappingProperties() != null) {
//                    mappingProperties.addAll(configurationData.getMappingProperties());
//                }
//                if ("default".equals(configurationData.getType())) {
//                    updatedProducts.addAll(fetchDataByDefaultCriteria(clientId, active, collectionName, currentFields,
//                            configurationData.getMappingProperties()));
//                }
//                if ("direct".equals(configurationData.getType())) {
//                    updatedProducts.addAll(mapDirectFields(updatedProducts, shopPhereMongoRequest));
//                }
//                if ("inDirect".equals(configurationData.getType())) {
//                    updatedProducts.addAll(mapInDirectFields(updatedProducts, shopPhereMongoRequest));
//                }
//            });
//        }
//        ValueTranformRequest valueTranformRequest = new ValueTranformRequest();
//        List<Object> reqsBody = new ArrayList<>();
//        List<Document> inputObjects = removeDuplicates(updatedProducts);
//        for (Document inputObject : inputObjects) {
//            Map<String, Object> requestBody = new HashMap<>();
//            requestBody.put("weight", inputObject.get("weight"));
//            requestBody.put("rank", inputObject.get("rank"));
//            reqsBody.add(requestBody);
//        }
//        valueTranformRequest.setInputType(reqsBody);
//        valueTranformRequest.setOutputType(transformerJson);
//        List<Object> transformedresult = valueTransformer(valueTranformRequest);
//        return removeDuplicates(updateValues(inputObjects, transformedresult));
//    }
//
//    public List<Document> fetchDataByDefaultCriteria(String clientId, Boolean active, String collectionName,
//            List<String> fields, List<String> mappingProperties) {
//        QueryRequestBody queryRequestBody = new QueryRequestBody();
//        List<String> newFields = new ArrayList<>(fields);
//        Map<String, Object> queryConditions = new HashMap<>();
//
//        //criteria
//        queryConditions.put("clientId", clientId);
//        queryConditions.put("active", active);
//
//        queryRequestBody.setCollectionName(collectionName);
//        queryRequestBody.setQueryConditions(queryConditions);
//        queryRequestBody.setFields(newFields);
//        if (mappingProperties != null) {
//            newFields.addAll(mappingProperties);
//        }
//
//        List<Document> documents = fetchDocumentsByCriteria(queryRequestBody);
//        return documents;
//    }
//
//    public List<Document> fetchDataByDirectCriteria(String clientId, Boolean active, String collectionName,
//            List<String> ids, List<String> fields) {
//        List<String> newFields = new ArrayList<>();
//        newFields.addAll(fields);
//        List<Document> documents = new ArrayList<>();
//
//        for (String id : ids) {
//            QueryRequestBody queryRequestBody = new QueryRequestBody();
//            Map<String, Object> queryConditions = new HashMap<>();
//
//            //criteria
//            queryConditions.put("_id", id);
//            queryConditions.put("active", active);
//            queryConditions.put("clientId", clientId);
//
//            queryRequestBody.setCollectionName(collectionName);
//            queryRequestBody.setQueryConditions(queryConditions);
//            queryRequestBody.setFields(newFields);
//
//            // Assuming fetchDocumentsByCriteria returns a List<Document>
//            List<Document> documentsResult = fetchDocumentsByCriteria(queryRequestBody);
//
//            // Add the result to the categories list
//            documents.addAll(documentsResult);
//        }
//
//        return documents;
//    }
//
//    public List<Document> fetchDataByInDirectCriteria(String priceType, Boolean active, String collectionName,
//            String id, List<String> fields, List<String> mappingProperties) {
//        List<Document> documents = new ArrayList<>();
//
//        QueryRequestBody queryRequestBody = new QueryRequestBody();
//        Map<String, Object> queryConditions = new HashMap<>();
//
//        //criteria
//        mappingProperties.stream().forEach(mappingProperty -> queryConditions.put(mappingProperty, id));
//
//        queryConditions.put("enabled", active);
//        if (priceType != null) {
//            queryConditions.put("priceType", priceType);
//        }
//
//        // queryConditions.put("clientId", clientId);
//
//        queryRequestBody.setCollectionName(collectionName);
//        queryRequestBody.setQueryConditions(queryConditions);
//        queryRequestBody.setFields(fields);
//
//        // Assuming fetchDocumentsByCriteria returns a List<Document>
//        List<Document> documentsResult = fetchDocumentsByCriteria(queryRequestBody);
//
//        // Add the result to the categories list
//        documents.addAll(documentsResult);
//
//        return documents;
//    }
//
//    private static List<Document> removeDuplicates(List<Document> updatedProducts) {
//        HashSet<Document> uniqueSet = new HashSet<>();
//        List<Document> uniqueDocuments = new ArrayList<>();
//
//        for (Document doc : updatedProducts) {
//            // If the document is successfully added to the set, it's unique
//            if (uniqueSet.add(doc)) {
//                uniqueDocuments.add(doc);
//            }
//            // If the document is not added, it's a duplicate, and we skip it
//        }
//
//        return uniqueDocuments;
//    }
//
//}
