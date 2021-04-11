package com.fenix.spirometer.util;

import android.os.Environment;
import android.util.Log;

import com.fenix.spirometer.R;
import com.fenix.spirometer.app.MyApplication;
import com.fenix.spirometer.model.TestReport;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class ExcelUtils {
    private final static String UTF8_ENCODING = "UTF-8";

    private static WritableCellFormat arial14format = null;
    private static WritableCellFormat arial10format = null;
    private static WritableCellFormat arial12format = null;

    private final static String[] TEST_REPORTS = MyApplication.getInstance().getResources().getStringArray(R.array.excel_file_column);
    private final static String FILE_NAME = "TestReports_";
    private final static String FILE_SUFFIX = ".xls";
    private final static String STORAGE_PATH = Environment.getExternalStorageDirectory().getPath() + "/Spirometer/excels/";


    public static void writeOperateTagToExcel(List<TestReport> reports) {
        String fileName = STORAGE_PATH + FILE_NAME + Utils.getDateTimeByMills(System.currentTimeMillis(), "yyMMdd_HHmm") + FILE_SUFFIX;
        initExcel(fileName, TEST_REPORTS);
        writeOperateTagListToExcel(reports, fileName);
    }

    private static void initExcel(String fileName, String[] colName) {
        format();
        WritableWorkbook workbook = null;
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
                Log.d("hff", "fileName = " + file.getPath() + ", exist = " + file.exists());
            }
            workbook = Workbook.createWorkbook(file);
            WritableSheet sheet = workbook.createSheet("Report", 0);
            sheet.addCell(new Label(0, 0, fileName, arial14format));

            for (int col = 0; col < colName.length; col++) {
                sheet.addCell(new Label(col, 0, colName[col], arial10format));
            }
            workbook.write();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    private static void format() {
        try {
            if (arial14format == null) {
                WritableFont arial14font = new WritableFont(WritableFont.ARIAL, 14,
                        WritableFont.BOLD);
                arial14font.setColour(jxl.format.Colour.LIGHT_BLUE);
                arial14format = new WritableCellFormat(arial14font);
                arial14format.setAlignment(jxl.format.Alignment.CENTRE);
                arial14format.setBorder(jxl.format.Border.ALL,
                        jxl.format.BorderLineStyle.THIN);
                arial14format.setBackground(jxl.format.Colour.VERY_LIGHT_YELLOW);
            }

            if (arial10format == null) {
                arial10format = new WritableCellFormat(new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD));
                arial10format.setAlignment(jxl.format.Alignment.CENTRE);
                arial10format.setBorder(jxl.format.Border.ALL,
                        jxl.format.BorderLineStyle.THIN);
                arial10format.setBackground(jxl.format.Colour.LIGHT_BLUE);
            }

            if (arial12format == null) {
                arial12format = new WritableCellFormat(new WritableFont(WritableFont.ARIAL, 12));
                arial12format.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
            }
        } catch (WriteException e) {
            e.printStackTrace();
        }

    }

    private static void writeOperateTagListToExcel(List<TestReport> objList, String fileName) {
        if (objList != null && objList.size() > 0) {
            WritableWorkbook writebook = null;
            InputStream in = null;
            try {
                WorkbookSettings setEncode = new WorkbookSettings();
                setEncode.setEncoding(UTF8_ENCODING);
                in = new FileInputStream(new File(fileName));
                Workbook workbook = Workbook.getWorkbook(in);
                writebook = Workbook.createWorkbook(new File(fileName), workbook);
                WritableSheet sheet = writebook.getSheet(0);
                for (int i = 0; i < objList.size(); i++) {
                    ArrayList<String> list = new ArrayList<String>();
                    TestReport report = objList.get(i);
                    list.add(i + 1 + "");
                    list.add(Utils.getDateTimeByMills(report.getTimeMills(), null));
                    list.add(JSONUtils.model2Json(report.getMember()));
                    list.add(JSONUtils.model2Json(report.getOperator()));
                    list.add(report.getData());
                    for (int j = 0; j < list.size(); j++) {
                        sheet.addCell(new Label(j, i + 1, list.get(j), arial12format));
                    }
                }
                writebook.write();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (writebook != null) {
                    try {
                        writebook.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void readExcel(String filepath) {
        try {
            /**
             * 后续考虑问题,比如Excel里面的图片以及其他数据类型的读取
             **/
            InputStream is = new FileInputStream(filepath);
            Workbook book = Workbook.getWorkbook(new File(filepath));
            book.getNumberOfSheets();
            // 获得第一个工作表对象
            Sheet sheet = book.getSheet(0);
            int Rows = sheet.getRows();
            int Cols = sheet.getColumns();
            System.out.println("当前工作表的名字:" + sheet.getName());
            System.out.println("总行数:" + Rows);
            System.out.println("总列数:" + Cols);
            for (int i = 0; i < Cols; ++i) {
                for (int j = 0; j < Rows; ++j) {
                    // getCell(Col,Row)获得单元格的值
                    System.out.print((sheet.getCell(i, j)).getContents() + "\t");
                }
                System.out.print("\n");
            }
            // 得到第一列第一行的单元格
            Cell cell1 = sheet.getCell(0, 0);
            String result = cell1.getContents();
            System.out.println(result);
            book.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
