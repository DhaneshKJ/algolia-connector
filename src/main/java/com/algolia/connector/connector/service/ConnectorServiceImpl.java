package com.algolia.connector.connector.service;

import com.algolia.connector.connector.algolia.Index;
import com.algolia.connector.connector.shopsphere.ShopsPhereMongoConnector;
import com.algolia.connector.connector.fetcher.ConfigFetcher;
import com.algolia.connector.connector.model.ConfigFileModel;
import com.algolia.connector.connector.model.ShopPhereMongoRequest;
import com.algolia.connector.connector.model.TransformModel;
import com.algolia.connector.connector.model.ValueTransformRequest;
import com.algolia.connector.connector.shopsphere.ShopsphereTransformer;
import com.algolia.connector.connector.transformer.Transformer;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConnectorServiceImpl implements ConnectorService {

    @Autowired
    ConfigFetcher configFetcher;

    @Autowired
    ShopsPhereMongoConnector shopsPhereMongoConnector;

    @Autowired
    Transformer transformer;

    @Autowired
    Index index;

    /**
     * This method is used to trigger the connector engine and bulk index
     *
     * @return List<Document>
     */
    @Override
    public List<Document> connectorEngine() {

        //config fetcher
        ConfigFileModel configFiles = configFetcher.fetchConfigFile();
        ShopPhereMongoRequest shopPhereMongoRequest = ShopsphereTransformer.convertToShopPhereMongoRequest(
                configFiles.getFileConfiguration());
        TransformModel transformModel = ShopsphereTransformer.transformModelTransformer(
                configFiles.getTransformModel());

        // raw data aggregation
        List<Document> aggregatedObjects = new ArrayList<>();

        //shopsphere mongo connector
        if ("ShopsphereMongoConnector".equals(shopPhereMongoRequest.getInputType())) {
            aggregatedObjects.addAll(shopsPhereMongoConnector.shopsphereMongoDataAggregator(shopPhereMongoRequest));
        }

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

        //calling algolia for indexing
        List<Document> indexObjects = index.bulkIndex(configFiles.getFileConfiguration(), transformedAttribute);
        return indexObjects;
    }
}
