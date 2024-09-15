package Resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.compress.archivers.dump.InvalidFormatException;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Excel {
	private XSSFWorkbook workbook = null;
	private XSSFSheet sheet = null;
	private int row = 0;
	private int column = 0;
	private String FilePath;
	private String FileName;

	public void createNewExcel(String filepath, String fileName) {
		workbook = new XSSFWorkbook();
		FilePath = filepath;
		FileName = fileName;

		new File(FilePath).mkdirs();
		try {
			File file1 = new File(FilePath + File.separator + FileName + ".xlsx");
			if (file1.isFile()) {
				file1.delete();
			}
			file1.createNewFile();
			System.out.println("New File created: " + FilePath + File.separator + FileName + ".xlsx");
		} catch (Exception e) {
			System.out.println("File not created: " + filepath + e.getMessage());
			e.printStackTrace();
		}
	}

	public void setMargin(int x, int y) {
		column += x;
		row += y;
	}

	public void createNewSheet(String sheetName) {
		sheet = workbook.createSheet(sheetName);
		row = 0;
		column = 0;
	}

	public void insertRow(List<String> listRow) {
		Row newRow = sheet.createRow(row++);
		CellStyle style = workbook.createCellStyle();
		style.setFillPattern(FillPatternType.BIG_SPOTS);
		int cellCount = column;
		for (int i = 0; i < listRow.size(); i++) {
			Cell cell = newRow.createCell(cellCount++);
			String data = listRow.get(i);
			if(data.toLowerCase().startsWith("pass")) {
				style.setFillBackgroundColor(IndexedColors.BRIGHT_GREEN.getIndex());
				cell.setCellStyle(style);
			}
			else if(data.toLowerCase().startsWith("fail")) {
				style.setFillBackgroundColor(IndexedColors.RED.getIndex());
				cell.setCellStyle(style);
			}
			cell.setCellValue(data);
		}

	}

	public void setHeader(List<String> listHeader) {
		CellStyle style = workbook.createCellStyle();
		style.setFillBackgroundColor(IndexedColors.BLACK.getIndex());
		style.setFillPattern(FillPatternType.BIG_SPOTS);
		Row newRow = sheet.createRow(row++);
		
		int cellCount = column;
		for (int i = 0; i < listHeader.size(); i++) {
			Cell cell = newRow.createCell(cellCount++);
			cell.setCellValue(listHeader.get(i).toUpperCase());
			cell.setCellStyle(style);
		}
		
	}

	public boolean generateFile() {
		try {
			FileOutputStream outputStream = new FileOutputStream(FilePath + File.separator + FileName + ".xlsx");
			workbook.write(outputStream);
			workbook.close();
			outputStream.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void readExcel(String filepath, String fileName, int sheetInd) {
		try {
			FilePath = filepath;
			FileName = fileName;
			row = 0;
			column = 0;
			// reading data from a file in the form of bytes
			FileInputStream fis = new FileInputStream(FilePath + File.separator + FileName + ".xlsx");
			// constructs an XSSFWorkbook object, by buffering the whole stream into the
			// memory
			workbook = new XSSFWorkbook(fis);
			sheet = workbook.getSheetAt(sheetInd);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public String ReadCellData(int vRow, int vColumn) {
		String value = null; // variable for storing the cell value

		Row row = sheet.getRow(vRow); // returns the logical row
		Cell cell = row.getCell(vColumn); // getting the cell representing the given column
//		value = (String)cell.getStringCellValue(); // getting cell value
		value = getCellValueAsString(cell);
		return value; // returns the cell value
	}
	
	private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

	public void insertFrom(int r, int c) {
		row = r;
		column = c;
	}

	public void updateExcel(String filepath, String fileName, int sheetInd) {
		String excelFilePath = filepath + File.separator + fileName + ".xlsx";

		try {
			FileInputStream inputStream = new FileInputStream(new File(excelFilePath));
			workbook = (XSSFWorkbook) WorkbookFactory.create(inputStream);
			sheet = workbook.getSheetAt(sheetInd);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateRow(List<String> listRow) {
		try {
			Row newRow = sheet.getRow(row++);

			int cellCount = column;
			for (int i = 0; i < listRow.size(); i++) {
				Cell cell = newRow.createCell(cellCount++);
				cell.setCellValue(listRow.get(i));
			}
		} catch (NullPointerException e) {

			row--;
			Row newRow = sheet.createRow(row++);

			int cellCount = column;
			for (int i = 0; i < listRow.size(); i++) {
				Cell cell = newRow.createCell(cellCount++);
				cell.setCellValue(listRow.get(i));
			}
		} catch (Exception f) {
			f.getMessage();
		}

	}

}
