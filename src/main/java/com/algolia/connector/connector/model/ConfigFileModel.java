package com.algolia.connector.connector.model;

import lombok.Data;
import org.bson.Document;

import java.util.List;
import java.util.Map;

@Data
public class ConfigFileModel {

    private Document fileConfiguration;

    private Document transformModel;

}
