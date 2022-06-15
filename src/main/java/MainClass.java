
import static com.mongodb.client.model.Filters.eq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.poi.openxml4j.util.ZipSecureFile;

import java.io.File;
import java.io.IOException;

public class MainClass {
    public static void main(String[] args) {
        System.out.println("MainClass Startup.");
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        try {
            Config config = mapper.readValue(new File("./config.yaml"), Config.class);

            MongoClient mongoClient = MongoClients.create(config.getMongodbUri());
            MongoDatabase database = mongoClient.getDatabase(config.getProductionStatusTableDatabaseName());

            ZipSecureFile.setMinInflateRatio(0);
            // 生产状态表
            new StatusTable(
                    config.getProductionStatusTablePath(), config.getProductionStatusTableSheetName(),
                    database.getCollection(config.getProductionStatusTableStatusCollectionName()),
                    database.getCollection(config.getProductionStatusTableDataCollectionName())
            ).sync();

            // 装配状态表
            new StatusTable(
                    config.getAssemblyStatusTablePath(), config.getAssemblyStatusTableSheetName(),
                    database.getCollection(config.getAssemblyStatusTableStatusCollectionName()),
                    database.getCollection(config.getAssemblyStatusTableDataCollectionName())
            ).sync();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
