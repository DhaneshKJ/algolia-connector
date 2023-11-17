package com.valoriz.algolia.connector.model;

import lombok.Data;

@Data
public class TransformationRule {

    private String inputFieldName;

    private String outputFieldName;

    private String convertTo;

}
