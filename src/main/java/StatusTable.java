import com.mongodb.MongoException;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StatusTable {
    private String excelPath, sheetName;
    private MongoCollection statusCollection, dataCollection;

    public StatusTable(String excelPath, String sheetName, MongoCollection statusCollection, MongoCollection dataCollection) {
        this.excelPath = excelPath;
        this.sheetName = sheetName;
        this.statusCollection = statusCollection;
        this.dataCollection = dataCollection;
    }

    public void sync() {
        // 文件状态Doc
        Document statusQueryDocument = new Document("path", excelPath);
        Document statusDoc = (Document) statusCollection.find(statusQueryDocument).first();
        File f = new File(excelPath);
        if(!f.exists()) {
            System.out.printf("不存在: %s\n", excelPath);
        }
        long currentMtime = f.lastModified();
        // 没有更改
        if (statusDoc != null) {
        long savedMTime = statusDoc.getLong("mtime");
        if (savedMTime == currentMtime) {
            System.out.printf("没有更改 %s %s\n", new Date(currentMtime).toString(), excelPath);
            return;
        }
    }
        System.out.printf("更改时间: %s, %s\n", new Date(currentMtime).toString(), excelPath);

        try (Workbook workbook = new XSSFWorkbook(new FileInputStream(excelPath));) {
            Sheet sheet = workbook.getSheet(sheetName);
            Set workIds = new HashSet();
            ArrayList actionModels = new ArrayList();
            Pattern patternWorkid = Pattern.compile("[Ww]*-*(\\d+)");
            System.out.printf("%d 行\n", sheet.getLastRowNum());
            for (int i = 1; i <= Math.min(sheet.getLastRowNum(), 30000); i++) {
                int rows = i + 1;
                // 行校验
                Row row = sheet.getRow(i);
                if (row == null) {
                    // System.out.printf("忽略空行: %d\n", rows);
                    continue;
                }
                // created_date
                Cell createdDateCell = row.getCell(0);
                long createdDate = 0;
                if (createdDateCell.getCellType() == CellType.NUMERIC) {
                    createdDate = createdDateCell.getDateCellValue().getTime();
                }

                // pn
                Cell pnCell = row.getCell(1);
                String pn = pnCell == null ? "" : pnCell.toString();

                // workId: 不能为空
                Cell workIdCell = row.getCell(2);
                if (workIdCell == null) {
                    System.out.printf("%d: Cell.workId为空\n", i);
                    continue;
                }
                String workId = workIdCell.toString().trim();
                if(workId == "") {
                    continue;
                }

                Matcher matcher = patternWorkid.matcher(workId);
                if(matcher.find()) {
                    workId = matcher.group(1);
                } else {
                    System.out.printf("%d invalid workid: %s\n", rows, workId);
                }


                // quantity: 为空->0, 数字/公式: value
                Cell quantityCell = row.getCell(3);
                double quantity = 0;
                if (quantityCell != null) {
                    CellType cellType = quantityCell.getCellType();
                    if (cellType == CellType.NUMERIC || cellType == CellType.FORMULA) {
                        quantity = quantityCell.getNumericCellValue();
                    } else {
                        System.out.printf("%d quantity: %s, type: %s", rows, quantityCell.toString(), quantityCell.getCellType());
                    }
                }

                // orderId
                Cell orderIdCell = row.getCell(4);
                String orderId = orderIdCell == null ? "" : orderIdCell.toString();

                // customer
                Cell customerCell = row.getCell(6);
                String customer = customerCell == null ? "" : customerCell.toString();

                // due_date
                Cell dueDateCell = row.getCell(11);
                long dueDate = 0;
                if (dueDateCell == null || dueDateCell.getCellType() == CellType.BLANK) {
                    dueDate = 0;
                } else if (dueDateCell.getCellType() == CellType.NUMERIC) {
                    dueDate = dueDateCell.getDateCellValue().getTime();
                } else {
                    System.out.printf("%d due_date=%s type=%s", rows, dueDateCell.toString(), dueDateCell.getCellType());
                }

                // 工单重复检测
                if (workIds.contains(workId)) {
                    System.out.printf("第%d行重复workId: %s\r\n", rows, workId);
                }
                workIds.add(workId);

                Document queryDocument = new Document("workid", workId);
                Document setDocument = new Document("workid", workId)
                        .append("created_date", createdDate)
                        .append("pn", pn)
                        .append("quantity", quantity)
                        .append("orderid", orderId)
                        .append("customer", customer)
                        .append("due_date", dueDate);
                actionModels.add(new UpdateOneModel<>(queryDocument,
                        new Document("$set", setDocument),
                        new UpdateOptions().upsert(true)));

            }
            System.out.printf("actionModels: %d\n",actionModels.size());
            // 更新数据集合
            dataCollection.bulkWrite(actionModels);
            // 最后更新状态集合
            Document newStatusDocument = new Document("path", excelPath)
                    .append("mtime", currentMtime);
            statusCollection.updateOne(
                    statusQueryDocument,
                    new Document("$set", newStatusDocument),
                    new UpdateOptions().upsert(true)
            );


        } catch (IOException  | MongoException e) {
            throw new RuntimeException(e);
        }
    }


}
