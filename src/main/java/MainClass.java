
import static com.mongodb.client.model.Filters.eq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class MainClass {
    public static void main(String[] args) {
        System.out.println("MainClass Startup.");
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        try {
            Config config = mapper.readValue(new File("./config.yaml"), Config.class);

            System.out.println(config.getProductionStatusTablePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
