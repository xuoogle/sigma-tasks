import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.Document;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

public class ProductionStatusTable {
    private static final String url = "C:\\eta\\p\\sigma\\fair\\auto\\excels\\产品状态表.xlsx";
    private Config config;
    public ProductionStatusTable(Config config) {
        this.config = config;
    }

    public void parse() {
        // 连接mongodb
        String  uri = config.getMongodbUri();

        MongoClient mongoClient = MongoClients.create(uri);
        MongoDatabase database = mongoClient.getDatabase(config.getProductionStatusTableDatabaseName());
        MongoCollection collection = database.getCollection(config.getProductionStatusTableDataCollectionName());
        Document doc = (Document) collection.find().first();
        System.out.println(doc.toJson());

        System.out.println(Arrays.asList(4,3,2));
        // 读取文件
        try {
            FileInputStream file = new FileInputStream(
                    "C:\\eta\\p\\sigma\\fair\\auto\\excels\\产品状态表.xlsx");
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i < sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                System.out.println(row.toString());
                long dt = row.getCell(0).getDateCellValue().getTime();
                String partId = row.getCell(1).toString();
                String workId = row.getCell(2).toString();
                int quantity = (int) row.getCell(3).getNumericCellValue();

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
