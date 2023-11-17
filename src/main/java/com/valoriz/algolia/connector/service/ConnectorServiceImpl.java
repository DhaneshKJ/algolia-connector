package com.valoriz.algolia.connector.service;

import com.valoriz.algolia.connector.destination.IndexService;
import com.valoriz.algolia.connector.source.shopsphere.ShopsPhereMongoConnector;
import com.valoriz.algolia.connector.fetcher.ConfigFetcher;
import com.valoriz.algolia.connector.model.ConfigFileModel;
import com.valoriz.algolia.connector.model.ConfigurationModel;
import com.valoriz.algolia.connector.model.TransformModel;
//import com.valoriz.algolia.connector.source.shopsphere.ShopsphereTransformer;
import com.valoriz.algolia.connector.transformer.Transformer;
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
    ConfigFetcher configFetcher;

    @Autowired
    ShopsPhereMongoConnector shopsPhereMongoConnector;

    @Autowired
    Transformer transformer;

    @Autowired
    IndexService indexService;

    /**
     * This method is used to trigger the connector engine and bulk index
     *
     * @return List<Document>
     */
    @Override
    public List<Document> connectorEngine() {

        //config fetcher
        ConfigFileModel configFiles = configFetcher.fetchConfigFile();

        String type = configFiles.getConfigurationModel().getSourceConfiguration().getType();
        //        ConfigurationModel configurationModel = ShopsphereTransformer.convertToShopPhereMongoRequest(
        //                configFiles.getFileConfiguration());

        //        TransformModel transformModel = ShopsphereTransformer.transformModelTransformer(
        //                configFiles.getTransformModel());
        //
        //        // raw data aggregation
        List<Document> aggregatedObjects = new ArrayList<>();
        //
        //        //shopsphere mongo connector
        if ("ShopsphereMongoConnector".equals(type)) {
            aggregatedObjects = shopsPhereMongoConnector.shopsphereMongoDataAggregator(
                    configFiles.getConfigurationModel().getSourceConfiguration());
        }

        //        //value transformer
        List<Document> transformedValues = transformer.valueTransformer(aggregatedObjects,
                configFiles.getTransformModel());
        //
        //        //attribute transformer
        List<Document> transformedAttribute = transformer.attributeTransformer(transformedValues,
                configFiles.getTransformModel());
        //
        //        //calling algolia for indexing

        String algoliaApplicationId = configFiles.getConfigurationModel().getAlgoliaApplicationId();
        String algoliaIndexName = configFiles.getConfigurationModel().getAlgoliaIndexName();
        String algoliaAdminApiKey = configFiles.getConfigurationModel().getAlgoliaAdminApiKey();
        List<Document> indexObjects = indexService.bulkIndex(algoliaApplicationId, algoliaIndexName, algoliaAdminApiKey,
                transformedAttribute);
        return indexObjects;

    }
}
