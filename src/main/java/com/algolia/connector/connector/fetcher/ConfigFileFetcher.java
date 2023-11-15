package com.algolia.connector.connector.fetcher;

import com.algolia.connector.connector.model.ConfigFileModel;
import com.algolia.connector.connector.model.ConfigurationData;
import com.algolia.connector.connector.model.ShopPhereMongoRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Component
public class ConfigFileFetcher {

    public ConfigFileModel fetchConfigFile() {

        // Read JSON data from a file and map it to the Document object
        String jsonFilePath = "C:\\Users\\Dhanesh K J\\Desktop\\Algolia\\FileConfiguration.json";
        String tranformerJsonFilePath = "C:\\Users\\Dhanesh K J\\Desktop\\Algolia\\TransformerJson.json";

        Document fileConfiguration = null;
        Map<String, String> transformerJson = null;
        try {
            fileConfiguration = readJSONFile(jsonFilePath);
            transformerJson = readJSONFileAndConvertToMap(tranformerJsonFilePath);

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
        configFileModel.setTransformerJson(transformerJson);
        configFileModel.setFileConfiguration(fileConfiguration);
        return configFileModel;
    }

    public Document readJSONFile(String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File jsonFile = new File(filePath);
        // Use the ObjectMapper to read the JSON file and map it to the Document object.
        Document fileConfiguration = objectMapper.readValue(jsonFile, Document.class);
        return fileConfiguration;
    }

    public Map<String, String> readJSONFileAndConvertToMap(String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File jsonFile = new File(filePath);
        // Use the ObjectMapper to read the JSON file and map it to the Document object.
        Map<String, String> fileConfiguration = objectMapper.readValue(jsonFile, Map.class);
        return fileConfiguration;
    }


}
