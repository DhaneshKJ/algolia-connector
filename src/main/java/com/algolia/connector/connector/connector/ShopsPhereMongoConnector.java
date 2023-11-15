    package com.algolia.connector.connector.connector;

import com.algolia.connector.connector.model.ShopPhereMongoRequest;
import com.algolia.connector.connector.mongoConfig.MongoConfig;
import org.bson.Document;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;

@Component
@Service
public class ShopsPhereMongoConnector extends MongoConnector {

    public ShopsPhereMongoConnector(MongoConfig mongoConfig) {
        super(mongoConfig);
    }

    public List<Document> shopsphereMongoDataAggregator(ShopPhereMongoRequest shopPhereMongoRequest) {
        List<Document> updatedProducts = new ArrayList<>();
        if ("ShopsPhereMongo".equals(shopPhereMongoRequest.getInputType())) {
            String clientId = shopPhereMongoRequest.getClientId();
            Boolean active = shopPhereMongoRequest.isActive();
            List<String> mappingProperties = new ArrayList<>();
            List<String> fields = new ArrayList<>();

            shopPhereMongoRequest.getConfigurationData().forEach(configurationData -> {
                String collectionName = configurationData.getCollectionName();
                fields.addAll(configurationData.getFieldsToFetch());
                List<String> currentFields = configurationData.getFieldsToFetch();

                if (configurationData.getMappingProperties() != null) {
                    mappingProperties.addAll(configurationData.getMappingProperties());
                }
                if ("default".equals(configurationData.getType())) {
                    updatedProducts.addAll(fetchDataByDefaultCriteria(clientId, active, collectionName, currentFields,
                            configurationData.getMappingProperties()));
                }
                if ("direct".equals(configurationData.getType())) {
                    updatedProducts.addAll(mapDirectFields(updatedProducts, shopPhereMongoRequest));
                }
                if ("inDirect".equals(configurationData.getType())) {
                    updatedProducts.addAll(mapInDirectFields(updatedProducts, shopPhereMongoRequest));
                }
            });
        }

        List<Document> aggregatedObjects = removeDuplicates(updatedProducts);

        return aggregatedObjects;
    }

    public static List<Document> removeDuplicates(List<Document> updatedProducts) {
        HashSet<Document> uniqueSet = new HashSet<>();
        List<Document> uniqueDocuments = new ArrayList<>();

        for (Document doc : updatedProducts) {
            // If the document is successfully added to the set, it's unique
            if (uniqueSet.add(doc)) {
                uniqueDocuments.add(doc);
            }
            // If the document is not added, it's a duplicate, and we skip it
        }

        return uniqueDocuments;
    }

    public static List<Document> updateValues(List<Document> inputObjects, List<Object> transformedResult) {
        List<Document> updatedObjects = new ArrayList<>();

        int minSize = Math.min(inputObjects.size(), transformedResult.size());

        for (int i = 0; i < minSize; i++) {
            Document inputObject = inputObjects.get(i);
            Object transformedEntry = transformedResult.get(i);

            if (transformedEntry instanceof Map) {
                Map<?, ?> transformedMap = (Map<?, ?>) transformedEntry;

                Set<String> inputKeys = inputObject.keySet();

                for (Object key : transformedMap.keySet()) {
                    if (inputKeys.contains(key)) {
                        // Key is present in both inputObject and transformedResult
                        // Overwrite the value in inputObject
                        inputObject.put((String) key, transformedMap.get(key));
                    }
                }

                updatedObjects.add(inputObject);
            }
        }

        return updatedObjects;
    }

    public List<Document> mapDirectFields(List<Document> updatedProducts, ShopPhereMongoRequest shopPhereMongoRequest) {
        String clientId = shopPhereMongoRequest.getClientId();
        Boolean active = shopPhereMongoRequest.isActive();
        List<Document> updatedProductsList = new ArrayList<>();
        shopPhereMongoRequest.getConfigurationData().forEach(configurationData -> {
            if ("direct".equals(configurationData.getType())) {
                String collectionName = configurationData.getCollectionName();
                List<String> fields = configurationData.getFieldsToFetch();
                // Clear the list before processing a new configurationData
                updatedProductsList.clear();
                for (Document pro : updatedProducts) {
                    String productId = (String) pro.get("productBrand");
                    List<String> categoryIds = (List<String>) pro.get("parentCategories");
                    // Create a new list for each product's brand IDs
                    List<String> productBrandIds = Collections.singletonList(productId);
                    if (categoryIds != null || productId != null) {
                        List<Document> fetchedCategories = fetchDataByDirectCriteria(clientId, active, collectionName,
                                categoryIds, fields);

                        List<Document> fetchedBrands = fetchDataByDirectCriteria(clientId, active, collectionName,
                                productBrandIds, fields);

                        // Remove "_id" field from fetched categories
                        fetchedCategories.forEach(category -> category.remove("_id"));
                        // Remove "_id" field from fetched brands
                        fetchedBrands.forEach(brand -> brand.remove("_id"));
                        // Add fetched categories to the product
                        if (!fetchedCategories.isEmpty()) {
                            pro.append("categories", fetchedCategories);
                        }

                        // Add fetched brands to the product
                        if (!fetchedBrands.isEmpty()) {
                            pro.append("brand", fetchedBrands);
                        }

                        // removeUnwantedFields(pro, fields);

                        updatedProductsList.add(pro);
                    }
                }
            }

        });

        return updatedProductsList;
    }

    private List<Document> mapInDirectFields(List<Document> updatedProducts,
            ShopPhereMongoRequest shopPhereMongoRequest) {
        String clientId = shopPhereMongoRequest.getClientId();
        Boolean active = shopPhereMongoRequest.isActive();
        List<Document> updatedProductsList = new ArrayList<>();
        shopPhereMongoRequest.getConfigurationData().forEach(configurationData -> {
            List<String> mappingProperties = configurationData.getMappingProperties();
            if ("inDirect".equals(configurationData.getType())) {
                String collectionName = configurationData.getCollectionName();
                List<String> fields = configurationData.getFieldsToFetch();
                fields.add("priceType");
                // Clear the list before processing a new configurationData
                updatedProductsList.clear();
                for (Document pro : updatedProducts) {
                    String productId = (String) pro.get("_id");
                    if (productId != null) {
                        List<Document> fetchedPrice = fetchDataByInDirectCriteria(configurationData.getPriceType(),
                                active, collectionName, productId, fields, mappingProperties);

                        // Add fetched categories to the product
                        if (!fetchedPrice.isEmpty()) {
                            pro.append("price", fetchedPrice);
                        }

                        // Remove "_id" field from fetched price
                        fetchedPrice.forEach(price -> price.remove("_id"));

                        // removeUnwantedFields(pro, fields);

                        updatedProductsList.add(pro);
                    }
                }
            }

        });
        return updatedProductsList;

    }

}
