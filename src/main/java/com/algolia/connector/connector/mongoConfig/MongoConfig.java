package com.algolia.connector.connector.mongoConfig;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Component
@ConfigurationProperties(prefix = "mongo")
@Data
public class MongoConfig {

    private Connection connection = new Connection();

    private Database database = new Database();

    public Connection getConnection() {
        return connection;
    }

    public Database getDatabase() {
        return database;
    }

    public static class Connection {

        private String url;

        // Getter and setter for url

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class Database {

        private String name;

        // Getter and setter for name

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}
