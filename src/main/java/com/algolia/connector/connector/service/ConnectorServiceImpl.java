package com.algolia.connector.connector.service;

import com.algolia.connector.connector.algoliaIndex.Index;
import com.algolia.connector.connector.connector.ShopsPhereMongoConnector;
import com.algolia.connector.connector.fetcher.ConfigFileFetcher;
import com.algolia.connector.connector.model.ConfigFileModel;
import com.algolia.connector.connector.model.ShopPhereMongoRequest;
import com.algolia.connector.connector.model.ValueTransformRequest;
import com.algolia.connector.connector.shopsphere.ShopsphereTransformer;
import com.algolia.connector.connector.transformer.ValueTransformer;
import com.algolia.connector.connector.transformer.ValueTransformerImpl;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ConnectorServiceImpl implements ConnectorService {

    @Autowired
    ConfigFileFetcher configFileFetcher;

    @Autowired
    ShopsPhereMongoConnector shopsPhereMongoConnector;

    @Autowired
    ValueTransformer valueTransformer;

    @Autowired
    Index index;

    @Override
    public void connectorEngine() {
        ConfigFileModel configFiles = configFileFetcher.fetchConfigFile();
        ShopPhereMongoRequest shopPhereMongoRequest = ShopsphereTransformer.convertToShopPhereMongoRequest(
                configFiles.getFileConfiguration());

        List<Document> aggregatedObjects = shopsPhereMongoConnector.shopsphereMongoDataAggregator(
                shopPhereMongoRequest);

        ValueTransformRequest valueTranformRequest = new ValueTransformRequest();
        List<Object> reqsBody = new ArrayList<>();
        for (Document inputObject : aggregatedObjects) {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("weight", inputObject.get("weight"));
            requestBody.put("rank", inputObject.get("rank"));
            reqsBody.add(requestBody);
        }
        valueTranformRequest.setInputType(reqsBody);
        valueTranformRequest.setOutputType(configFiles.getTransformerJson());

        List<Object> transformedresult = valueTransformer.valueTransformer(valueTranformRequest);

        List<Document> indexObjects = ShopsPhereMongoConnector.removeDuplicates(
                ShopsPhereMongoConnector.updateValues(aggregatedObjects, transformedresult));
        index.bulkIndex(configFiles.getFileConfiguration(), indexObjects);
    }
}
