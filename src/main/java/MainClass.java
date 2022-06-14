
import static com.mongodb.client.model.Filters.eq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.io.File;
import java.io.IOException;

public class MainClass {
    public static void main(String[] args) {
        System.out.println("MainClass Startup.");
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        try {
            Config config = mapper.readValue(new File("./config.yaml"), Config.class);
            // 生产状态表
            MongoClient mongoClient = MongoClients.create(config.getMongodbUri());
            MongoDatabase database = mongoClient.getDatabase(config.getProductionStatusTableDatabaseName());
            MongoCollection statusCollection = database.getCollection(config.getProductionStatusTableStatusCollectionName());
            MongoCollection dataCollection = database.getCollection(config.getProductionStatusTableDataCollectionName());

            StatusTable productionStatusTable = new StatusTable(config.getProductionStatusTablePath(),
                    statusCollection, dataCollection);
            productionStatusTable.sync();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
