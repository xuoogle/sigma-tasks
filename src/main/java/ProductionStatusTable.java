
import static com.mongodb.client.model.Filters.eq;
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
    public static void main(String[] args) {
        System.out.println("测试数据库");
        String  uri = "mongodb://r:r@localhost:27017/";

        MongoClient mongoClient = MongoClients.create(uri);
        MongoDatabase database = mongoClient.getDatabase("marking");
        MongoCollection collection = database.getCollection("product_status_table_status");
        Document doc = (Document) collection.find().first();
        System.out.println(doc.toJson());

        System.out.println(Arrays.asList(4,3,2));


//        try {
//            FileInputStream file = new FileInputStream(
//                    "C:\\eta\\p\\sigma\\fair\\auto\\excels\\产品状态表.xlsx");
//            Workbook workbook = new XSSFWorkbook(file);
//            Sheet sheet = workbook.getSheetAt(0);
//
//            for(int i = 1; i < sheet.getLastRowNum(); i++ ) {
//                Row row = sheet.getRow(i);
//                System.out.println(row.toString());
//                long dt = row.getCell(0).getDateCellValue().getTime();
//                String partId = row.getCell(1).toString();
//                String workId = row.getCell(2).toString();
//                int quantity = (int) row.getCell(3).getNumericCellValue();
//                System.out.printf("%d: %d %s %s %d%n", i, dt, partId, workId, quantity);
//
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }
}
