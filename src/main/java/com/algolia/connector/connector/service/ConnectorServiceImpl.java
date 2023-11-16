package com.algolia.connector.connector.service;

import com.algolia.connector.connector.algolia.Index;
import com.algolia.connector.connector.connector.ShopsPhereMongoConnector;
import com.algolia.connector.connector.fetcher.ConfigFileFetcher;
import com.algolia.connector.connector.model.ConfigFileModel;
import com.algolia.connector.connector.model.ShopPhereMongoRequest;
import com.algolia.connector.connector.model.TransformModel;
import com.algolia.connector.connector.model.ValueTransformRequest;
import com.algolia.connector.connector.shopsphere.ShopsphereTransformer;
import com.algolia.connector.connector.transformer.Transformer;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConnectorServiceImpl implements ConnectorService {

    @Autowired
    ConfigFileFetcher configFileFetcher;

    @Autowired
    ShopsPhereMongoConnector shopsPhereMongoConnector;

    @Autowired
    Transformer transformer;

    @Autowired
    Index index;

    @Override
    public void connectorEngine() {
        ConfigFileModel configFiles = configFileFetcher.fetchConfigFile();
        ShopPhereMongoRequest shopPhereMongoRequest = ShopsphereTransformer.convertToShopPhereMongoRequest(
                configFiles.getFileConfiguration());

        TransformModel transformModel = ShopsphereTransformer.transformModelTransformer(
                configFiles.getTransformModel());

        //raw data aggregation
        List<Document> aggregatedObjects = shopsPhereMongoConnector.shopsphereMongoDataAggregator(
                shopPhereMongoRequest);

        ValueTransformRequest valueTransformRequest = new ValueTransformRequest();
        valueTransformRequest.setInputType(aggregatedObjects);
        valueTransformRequest.setOutputType(transformModel.getValueTransformMap());

        //value transformer
        List<Document> transformedValues = transformer.valueTransformer(valueTransformRequest);

        //attribute transformer
        List<Document> transformedAttribute = transformer.attributeTransformer(transformedValues,
                transformModel.getAttributeTransformMap());

        //List<Document> updatedVal = ShopsPhereMongoConnector.updateValues(aggregatedObjects, transformedValues);
        // List<Document> removeDuplicates = ShopsPhereMongoConnector.removeDuplicates(updatedVal);
        List<Document> indexObjects = index.bulkIndex(configFiles.getFileConfiguration(), transformedAttribute);
    }
}
