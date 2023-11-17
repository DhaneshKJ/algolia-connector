package com.algolia.connector.connector.algolia;

import com.algolia.search.DefaultSearchClient;
import com.algolia.search.SearchClient;
import com.algolia.search.SearchIndex;
import org.bson.Document;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Component
@Service
public class Index {

    /**
     * This method is used to bulk index by fileConfiguration and indexObjects
     *
     * @param fileConfiguration
     * @param indexObjects
     * @return indexObjects
     */
    public List<Document> bulkIndex(Document fileConfiguration, List<Document> indexObjects) {
        SearchIndex<Document> index;
        try (SearchClient client = DefaultSearchClient.create((String) fileConfiguration.get("applicationId"),
                (String) fileConfiguration.get("adminApiKey"))) {
            index = client.initIndex((String) fileConfiguration.get("indexName"), Document.class);
            index.saveObjects(indexObjects, true).waitTask();
            return indexObjects;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
