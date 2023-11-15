package com.algolia.connector.connector.algoliaIndex;

import com.algolia.search.DefaultSearchClient;
import com.algolia.search.SearchClient;
import com.algolia.search.SearchIndex;
import org.bson.Document;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Component
@Service
public class Index {

    public void bulkIndex(Document fileConfiguration, List<Document> indexObjects) {
        SearchClient client = DefaultSearchClient.create((String) fileConfiguration.get("applicationId"),
                (String) fileConfiguration.get("adminApiKey"));
        SearchIndex<Document> index = client.initIndex((String) fileConfiguration.get("indexName"), Document.class);
        index.saveObjects(indexObjects, true).waitTask();
    }

}
