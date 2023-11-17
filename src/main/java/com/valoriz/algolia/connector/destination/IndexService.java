package com.valoriz.algolia.connector.destination;

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
public class IndexService {

    /**
     * This method is used to bulk index by fileConfiguration and indexObjects
     *
     * @param fileConfiguration
     * @param indexObjects
     * @return indexObjects
     */
    public List<Document> bulkIndex(String algoliaApplicationId, String algoliaIndexName, String algoliaAdminApiKey,
            List<Document> indexObjects) {
        SearchIndex<Document> index;
        try (SearchClient client = DefaultSearchClient.create(algoliaApplicationId, algoliaAdminApiKey)) {
            index = client.initIndex(algoliaIndexName, Document.class);
            index.saveObjects(indexObjects, true).waitTask();
            return indexObjects;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
