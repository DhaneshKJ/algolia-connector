package com.algolia.connector.connector.transformer;

import com.algolia.connector.connector.model.ValueTransformRequest;

import java.util.List;

public interface ValueTransformer {

    public List<Object> valueTransformer(ValueTransformRequest valueTransformRequest);

}
