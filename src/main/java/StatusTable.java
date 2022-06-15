import com.mongodb.client.MongoCollection;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.Document;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

public class StatusTable {
    private String excelPath;
    private MongoCollection statusCollection, dataCollection;
    public StatusTable(String excelPath, MongoCollection statusCollection, MongoCollection dataCollection) {
        this.excelPath = excelPath;
        this.statusCollection = statusCollection;
        this.dataCollection = dataCollection;
    }

    public void sync() {
        // 文件状态Doc
        Document statusDoc = (Document) statusCollection.find().first();

        // 读取文件
        try {
            File f = new File(excelPath);

            long currentMtime = f.lastModified();
            // 没有更改
            if(statusDoc != null) {
                long savedMTime = statusDoc.getLong("mtime");
                if(savedMTime == currentMtime) {
                    return;
                }
            }

            Workbook workbook = new XSSFWorkbook(f);
            Sheet sheet = workbook.getSheetAt(0);

            Set workIds = new HashSet();
            ArrayList actionModels = new ArrayList();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                // 行校验
                Row row = sheet.getRow(i);
                if(row == null) {
                    System.out.printf("忽略空行: %d\n", i);
                    continue;
                }
                // created_date
                Cell createdDateCell = row.getCell(0);
                long createdDate = 0;
                if(createdDateCell.getCellType() == CellType.NUMERIC) {
                    createdDate = createdDateCell.getDateCellValue().getTime();
                }

                // pn
                Cell pnCell = row.getCell(1);
                if(pnCell == null || pnCell.getCellType() != CellType.STRING) {
                    System.out.printf("%d: Cell.pn 为空,或者类型不正确\n", i);
                    continue;
                }
                String pn = pnCell == null ? "" : pnCell.getStringCellValue();

                // workId
                Cell workIdCell = row.getCell(2);
                if(workIdCell == null || workIdCell.getCellType() != CellType.STRING) {
                    System.out.printf("%d: Cell.workId为空, 或者类型不正确\n", i);
                    continue;
                }
                String workId = workIdCell.getStringCellValue();

                // quantity
                Cell quantityCell  = row.getCell(3);
                if(quantityCell == null || quantityCell.getCellType() != CellType.NUMERIC) {
                    System.out.printf("%d: Cell.quantity为空, 或者类型不正确\n", i);
                    continue;
                }
                int quantity = (int) quantityCell.getNumericCellValue();

                // orderId
                Cell orderIdCell = row.getCell(4);
                if(orderIdCell == null || orderIdCell.getCellType() != CellType.STRING) {
                    System.out.printf("%d: Cell.orderId为空, 或者类型不正确\n", i);
                    continue;
                }
                String orderId = orderIdCell.getStringCellValue();

                // customer
                Cell customerCell = row.getCell(6);
                String customer = customerCell == null ? "" : customerCell.toString();

                // due_date
                Cell dueDateCell = row.getCell(11);
                long dueDate = 0;
                if(dueDateCell != null && dueDateCell.getCellType() == CellType.NUMERIC) {
                    dueDate = dueDateCell.getDateCellValue().getTime();
                }

                if(workIds.contains(workId)) {
                    System.out.printf("第%d行重复workId: %s\r\n", i + 1, workId);
                }
                workIds.add(workId);

                actionModels.add(new UpdateOneModel<>(new Document("name", "A Sample Movie"),
                        new Document("$set", new Document("name", "An Old Sample Movie")),
                        new UpdateOptions().upsert(true)))

            }

            // 更新数据集合
            dataCollection.bulkWrite(actionModels);
            // 更新状态集合

        } catch (IOException | InvalidFormatException e) {
            throw new RuntimeException(e);
        }
    }


}
