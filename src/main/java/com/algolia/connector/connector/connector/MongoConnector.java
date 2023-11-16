package com.algolia.connector.connector.connector;

import com.algolia.connector.connector.model.QueryRequestBody;
import com.algolia.connector.connector.mongoConfig.MongoConfig;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "mongo")
public class MongoConnector implements NoSqlConnector {

    private final MongoConfig mongoConfig;

    public MongoConnector(MongoConfig mongoConfig) {
        this.mongoConfig = mongoConfig;
    }

    //    private final MongoConfig mongoConfig;

    //    @Autowired
    //    public MongoConnector(MongoConfig mongoConfig) {
    //        this.mongoConfig = mongoConfig;
    //    }

    //    private static final String CONNECTION_URL = "mongodb://mozanta_commerce_app_user:30oct2019**@localhost:18401/mozantacommerce";
    //
    //    private static final String DATABASE_NAME = "mozantacommerce";

    public List<Document> fetchDocumentsByCriteria(QueryRequestBody queryRequestBody) {
        String connectionUrl = mongoConfig.getConnection().getUrl();
        String databaseName = mongoConfig.getDatabase().getName();

        MongoClient mongoClient = MongoClients.create(connectionUrl);

        MongoDatabase database = mongoClient.getDatabase(databaseName);
        List<Document> documents = new ArrayList<>();
        try {
            MongoCollection<Document> collection = database.getCollection(queryRequestBody.getCollectionName());
            Map<String, Object> queryConditions = queryRequestBody.getQueryConditions();
            Document query;

            if (queryConditions.containsKey("_id")) {
                String idString = queryConditions.get("_id").toString();
                ObjectId objectId = new ObjectId(idString);
                query = new Document("_id", objectId);
            } else {
                query = new Document(queryConditions);
            }

            // Check if fields are specified in the request
            if (queryRequestBody.getFields() != null && !queryRequestBody.getFields().isEmpty()) {
                Document projection = new Document();
                for (String field : queryRequestBody.getFields()) {
                    projection.append(field, 1);
                }
                FindIterable<Document> result = collection.find(query).projection(projection);

                for (Document document : result) {
                    normalizeDocumentId(document);
                    documents.add(document);
                }
            } else {
                // If no fields are specified, fetch all fields
                FindIterable<Document> result = collection.find(query);

                for (Document document : result) {
                    normalizeDocumentId(document);
                    documents.add(document);
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Handle exceptions properly (log, rethrow, etc.)
        }
        return documents;
    }

    private void normalizeDocumentId(Document document) {
        Object idField = document.get("_id");
        if (idField instanceof ObjectId) {
            document.put("_id", ((ObjectId) idField).toString());
        }
    }

    public List<Document> fetchDataByDefaultCriteria(String clientId, Boolean active, String collectionName,
            List<String> fields, List<String> mappingProperties) {
        QueryRequestBody queryRequestBody = new QueryRequestBody();
        List<String> newFields = new ArrayList<>(fields);
        Map<String, Object> queryConditions = new HashMap<>();

        //criteria
        queryConditions.put("clientId", clientId);
        queryConditions.put("active", active);

        queryRequestBody.setCollectionName(collectionName);
        queryRequestBody.setQueryConditions(queryConditions);
        queryRequestBody.setFields(newFields);
        if (mappingProperties != null) {
            newFields.addAll(mappingProperties);
        }

        List<Document> documents = fetchDocumentsByCriteria(queryRequestBody);
        return documents;
    }

    public List<Document> fetchDataByDirectCriteria(String clientId, Boolean active, String collectionName,
            List<String> ids, List<String> fields) {
        List<String> newFields = new ArrayList<>();
        newFields.addAll(fields);
        List<Document> documents = new ArrayList<>();

        for (String id : ids) {
            QueryRequestBody queryRequestBody = new QueryRequestBody();
            Map<String, Object> queryConditions = new HashMap<>();

            //criteria
            queryConditions.put("_id", id);
            queryConditions.put("active", active);
            queryConditions.put("clientId", clientId);

            queryRequestBody.setCollectionName(collectionName);
            queryRequestBody.setQueryConditions(queryConditions);
            queryRequestBody.setFields(newFields);

            // Assuming fetchDocumentsByCriteria returns a List<Document>
            List<Document> documentsResult = fetchDocumentsByCriteria(queryRequestBody);

            // Add the result to the categories list
            documents.addAll(documentsResult);
        }

        return documents;
    }

    public List<Document> fetchDataByInDirectCriteria(String priceType, Boolean active, String collectionName,
            String id, List<String> fields, List<String> mappingProperties) {
        List<Document> documents = new ArrayList<>();

        QueryRequestBody queryRequestBody = new QueryRequestBody();
        Map<String, Object> queryConditions = new HashMap<>();

        //criteria
        mappingProperties.stream().forEach(mappingProperty -> queryConditions.put(mappingProperty, id));

        queryConditions.put("enabled", active);
        if (priceType != null) {
            queryConditions.put("priceType", priceType);
        }

        // queryConditions.put("clientId", clientId);

        queryRequestBody.setCollectionName(collectionName);
        queryRequestBody.setQueryConditions(queryConditions);
        queryRequestBody.setFields(fields);

        // Assuming fetchDocumentsByCriteria returns a List<Document>
        List<Document> documentsResult = fetchDocumentsByCriteria(queryRequestBody);

        // Add the result to the categories list
        documents.addAll(documentsResult);

        return documents;
    }

}
