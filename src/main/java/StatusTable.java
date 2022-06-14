import com.mongodb.client.MongoCollection;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.Document;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if(row == null) {
                    System.out.printf("忽略空行: %d\n", i);
                    continue;
                }
                Cell createdDateCell = row.getCell(0);
                long createdDate = 0;
                if(createdDateCell.getCellType() == CellType.NUMERIC) {
                    createdDate = createdDateCell.getDateCellValue().getTime();
                }
//                long created_date = createdDate != null ? createdDate.getTime() : 0;
                Cell pnCell = row.getCell(1);
                String pn = pnCell == null ? "" : pnCell.getStringCellValue();

                Cell workIdCell = row.getCell(2);

                String workId = workIdCell !=null ? workIdCell.getStringCellValue() : "";
                if(workId == "") {
                    System.out.printf("忽略workId为空的行: %d\n", i);
                    continue;
                }

                int quantity = (int) row.getCell(3).getNumericCellValue();
                String orderId = row.getCell(4).toString();
                String customer = row.getCell(6).toString();
                Date dueDate = row.getCell(11).getDateCellValue();
                long due_date = dueDate != null ? dueDate.getTime() : 0;
                // System.out.printf("%d: %s %s, %s, %d\r\n", i + 1, workId, orderId, customer, due_date);
                if(workIds.contains(workId)) {
                    System.out.printf("第%d行重复workId: %s\r\n", i + 1, workId);
                }
//                System.out.printf("repeat: %d: %s %s, %s, %d\r\n", i + 1, workId, orderId, customer, due_date);
                workIds.add(workId);

            }
            System.out.println(workIds.size());
        } catch (IOException | InvalidFormatException e) {
            throw new RuntimeException(e);
        }
    }


}
