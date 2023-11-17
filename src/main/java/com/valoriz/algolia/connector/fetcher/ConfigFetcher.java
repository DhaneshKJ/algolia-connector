package com.valoriz.algolia.connector.fetcher;

import com.valoriz.algolia.connector.model.ConfigFileModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.valoriz.algolia.connector.model.ConfigurationModel;
import com.valoriz.algolia.connector.model.TransformModel;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
@Component
public class ConfigFetcher {

    /**
     * This method is used to fetch configuration file
     *
     * @return ConfigFileModel
     */
    public ConfigFileModel fetchConfigFile() {

        // Read JSON data from a file and map it to the Document object
        String jsonFilePath = "C:\\Users\\Dhanesh K J\\Desktop\\Algolia\\FileConfiguration.json";
        String tranformerJsonFilePath = "C:\\Users\\Dhanesh K J\\Desktop\\Algolia\\TransformerJson.json";

        ConfigurationModel configurationModel = null;
        TransformModel transformerJson = null;
        try {
            configurationModel = jsonToShopsPhereModel(jsonFilePath);
            transformerJson = jsonToTransformerModel(tranformerJsonFilePath);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the error appropriately
        }
        if (configurationModel == null) {
            // Handle missing configuration appropriately
            return null;
        }
        if (transformerJson == null) {
            // Handle missing configuration appropriately
            return null;
        }
        ConfigFileModel configFileModel = new ConfigFileModel();
        // configFileModel.setTransformModel(transformerJson);
        configFileModel.setConfigurationModel(configurationModel);
        configFileModel.setTransformModel(transformerJson);
        return configFileModel;
    }

    /**
     * This method is used to read json data from file path and convert to ShopPhereMongoRequest
     *
     * @param filePath
     * @return ShopPhereMongoRequest
     */
    public ConfigurationModel jsonToShopsPhereModel(String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File jsonFile = new File(filePath);
        // Use the ObjectMapper to read the JSON file and map it to the Document object.
        ConfigurationModel configurationModel = objectMapper.readValue(jsonFile, ConfigurationModel.class);
        return configurationModel;
    }

    /**
     * This method is used to read json data from file path and convert to TransformModel
     *
     * @param filePath
     * @return TransformModel
     */
    public TransformModel jsonToTransformerModel(String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File jsonFile = new File(filePath);
        // Use the ObjectMapper to read the JSON file and map it to the Document object.
        TransformModel transformModel = objectMapper.readValue(jsonFile, TransformModel.class);
        return transformModel;
    }
}
