package com.algolia.connector.connector.fetcher;

import com.algolia.connector.connector.model.ConfigFileModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;
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

        Document fileConfiguration = null;
        Document transformerJson = null;
        try {
            fileConfiguration = readJSONFile(jsonFilePath);
            transformerJson = readJSONFile(tranformerJsonFilePath);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the error appropriately
        }
        if (fileConfiguration == null) {
            // Handle missing configuration appropriately
            return null;
        }
        if (transformerJson == null) {
            // Handle missing configuration appropriately
            return null;
        }
        ConfigFileModel configFileModel = new ConfigFileModel();
        // configFileModel.setTransformModel(transformerJson);
        configFileModel.setFileConfiguration(fileConfiguration);
        configFileModel.setTransformModel(transformerJson);
        return configFileModel;
    }

    /**
     * This method is used to read json data from file path and convert to document
     *
     * @param filePath
     * @return Document
     */
    public Document readJSONFile(String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File jsonFile = new File(filePath);
        // Use the ObjectMapper to read the JSON file and map it to the Document object.
        Document fileConfiguration = objectMapper.readValue(jsonFile, Document.class);
        return fileConfiguration;
    }
}
