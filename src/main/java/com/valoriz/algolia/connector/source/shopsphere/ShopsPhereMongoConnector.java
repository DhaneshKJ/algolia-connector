package com.valoriz.algolia.connector.source.shopsphere;

import com.valoriz.algolia.connector.model.SourceConfiguration;
import com.valoriz.algolia.connector.source.MongoConnector;
import com.valoriz.algolia.connector.model.ConfigurationModel;
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

    /**
     * This method is used to aggregate documents from different collections
     *
     * @param configurationModel
     * @return List<Document>
     */
    public List<Document> shopsphereMongoDataAggregator(SourceConfiguration sourceConfiguration) {
        List<Document> updatedProducts = new ArrayList<>();
        String clientId = sourceConfiguration.getClientId();
        Boolean active = sourceConfiguration.getActive();
        List<String> mappingProperties = new ArrayList<>();
        List<String> fields = new ArrayList<>();
        sourceConfiguration.getCollectionConfiguration().forEach(configurationData -> {
            String collectionName = configurationData.getCollectionName();
            fields.addAll(configurationData.getFieldsToFetch());
            List<String> currentFields = configurationData.getFieldsToFetch();
            if (configurationData.getMappingProperties() != null) {
                mappingProperties.addAll(configurationData.getMappingProperties());
            }
            if ("default".equals(configurationData.getType())) {
                updatedProducts.addAll(mapDefault(clientId, active, collectionName, currentFields,
                        configurationData.getMappingProperties(), sourceConfiguration));
            }
            if ("direct".equals(configurationData.getType())) {
                updatedProducts.addAll(mapDirectFields(updatedProducts, sourceConfiguration));
            }
            if ("inDirect".equals(configurationData.getType())) {
                updatedProducts.addAll(mapInDirectFields(updatedProducts, sourceConfiguration));
            }
        });
        List<Document> aggregatedObjects = removeDuplicates(updatedProducts);
        return aggregatedObjects;
    }

    /**
     * This method is used to remove duplicate objects in document list
     *
     * @param updatedProducts
     * @return List<Document>
     */
    public static List<Document> removeDuplicates(List<Document> updatedProducts) {
        HashSet<Document> uniqueSet = new HashSet<>();
        List<Document> uniqueDocuments = new ArrayList<>();
        for (Document doc : updatedProducts) {
            // If the document is successfully added to the set, it's unique
            if (uniqueSet.add(doc)) {
                uniqueDocuments.add(doc);
            }
        }
        return uniqueDocuments;
    }

    /**
     * This method is utilized for linking current collection to other collections using fields and values within the
     * current collection.
     *
     * @param updatedProducts
     * @param configurationModel
     * @return List<Document>
     */
    public List<Document> mapDirectFields(List<Document> updatedProducts, SourceConfiguration sourceConfiguration) {
        String clientId = sourceConfiguration.getClientId();
        Boolean active = sourceConfiguration.getActive();
        List<Document> updatedProductsList = new ArrayList<>();
        sourceConfiguration.getCollectionConfiguration().forEach(configurationData -> {
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

    /**
     * This method is utilized for linking current collection to other collections using fields and values within the
     * current collection.
     *
     * @return List<Document>
     */
    public List<Document> mapDefault(String clientId, Boolean active, String collectionName, List<String> currentFields,
            List<String> mappingProperties, SourceConfiguration sourceConfiguration) {
        List<Document> docs = new ArrayList<>();
        sourceConfiguration.getCollectionConfiguration().forEach(configurationData -> {
            if ("default".equals(configurationData.getType())) {
                docs.addAll(
                        fetchDataByDefaultCriteria(clientId, active, collectionName, currentFields, mappingProperties));
            }
        });
        return docs;
    }

    /**
     * This method is utilized for linking from current collection to other collections using fields and values from
     * other collection.
     *
     * @param updatedProducts
     * @param configurationModel
     * @return List<Document>
     */
    private List<Document> mapInDirectFields(List<Document> updatedProducts, SourceConfiguration sourceConfiguration) {
        Boolean active = sourceConfiguration.getActive();
        List<Document> updatedProductsList = new ArrayList<>();
        sourceConfiguration.getCollectionConfiguration().forEach(configurationData -> {
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