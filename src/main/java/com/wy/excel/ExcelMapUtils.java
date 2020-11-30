package com.wy.excel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.wy.enums.TipsEnum;
import com.wy.utils.ListUtils;
import com.wy.utils.StrUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Map数据类型Excel工具类
 * 
 * @author ParadiseWY
 * @date 2020-11-30 17:05:43
 * @git {@link https://github.com/mygodness100}
 */
@Slf4j
public class ExcelMapUtils implements ExcelUtils {

	private ExcelMapUtils() {}

	private static class Inner {

		private static final ExcelMapUtils INSTANCE = new ExcelMapUtils();
	}

	public static ExcelMapUtils getInstance() {
		return Inner.INSTANCE;
	}

	/**
	 * 根据excel文件的结尾判断生成那种版本的excel,若未指定类型,自动归结为低版本excel
	 * 
	 * @param path 以.xls或xlsx结尾的文件路径
	 * @param list map类数据源集合
	 * @param subject 是否添加字段名称,true添加false不添加,默认添加
	 */
	@Override
	public boolean writeExcel(String path, List<Map<String, Object>> list, boolean subject) {
		if (StrUtils.isBlank(path) || ListUtils.isBlank(list)) {
			log.info("路径不存在或数据源为空!");
			return false;
		}
		try (FileOutputStream fos = new FileOutputStream(path);
				Workbook workbook = ExcelUtils.generateWorkbook(path);) {
			Sheet sheet = workbook.createSheet();
			int beginRow = subject ? 1 : 0;
			List<String> allField = new ArrayList<>(list.get(0).keySet());
			for (int i = 0; i < list.size(); i++) {
				Row row = sheet.createRow(beginRow);
				for (int j = 0; j < allField.size(); j++) {
					Object object = list.get(i).get(allField.get(j));
					Cell cell = row.createCell(j);
					if (object != null && Date.class == object.getClass()) {
						cell.setCellValue((Date) object);
					} else {
						cell.setCellValue(Objects.toString(object, ""));
					}
				}
				beginRow++;
			}
			if (subject) {
				createFirst(allField, sheet);
			}
			workbook.write(fos);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 处理listmap中的第一行
	 * 
	 * @param titles 所有的字段,此处因为是map,不可标注注解,只能是字段名
	 * @param sheet sheet页
	 */
	@Override
	public void createFirst(List<String> titles, Sheet sheet) {
		Row first = sheet.createRow(0);
		int j = 0;
		for (String title : titles) {
			Cell cell = first.createCell(j);
			cell.setCellValue(title);
			j++;
		}
	}

	@Override
	public boolean writeExcel(Workbook wb, List<List<List<Object>>> excel, String path, CellStyle cellStyle) {
		try (FileOutputStream fos = new FileOutputStream(path); Workbook workbook = wb;) {
			for (int page = 0; page < excel.size(); page++) {
				// 创建表单
				Sheet sheet = workbook.createSheet();
				// 行循环
				for (int row = 0; row < excel.get(page).size(); row++) {
					Row r = sheet.createRow(row);
					// 列循环
					for (int col = 0; col < excel.get(page).get(row).size(); col++) {
						Cell c = r.createCell(col);
						c.setCellValue(Objects.toString(excel.get(page).get(row).get(col), ""));
						if (cellStyle != null) {
							c.setCellStyle(cellStyle);
						}
					}
				}
				workbook.setSheetName(page, "第" + page + "页");
			}
			workbook.write(fos);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 读取excel中的数据.excel的第一行作为key值,不计入数据,从第beginRow+2行第beginCol+1列开始读取数据
	 * 
	 * @param path 需要读取的excel路径
	 * @param firstUse 每个sheet中第一行数据是否可作为字段使用,true可,false不可
	 * @param titles 当firstUse为true时,该值不使用.若是false,则该值为字段名或key值,但是第一行数据仍不使用
	 * @param beginRow 从第beginRow+2行开始读取数据
	 * @param beginCol 从第beginCol+1列开始读取excel
	 * @return 结果集
	 */
	public List<Map<String, Object>> readExcel(String path, boolean firstUse, List<String> titles, int beginRow,
			int beginCol) {
		try (Workbook wb = ExcelUtils.createIsWorkbook(path);) {
			List<Map<String, Object>> res = new ArrayList<>();
			int sheetNum = wb.getNumberOfSheets();
			for (int i = 0; i < sheetNum; i++) {
				List<Map<String, Object>> handlerRow = handlerRow(wb.getSheetAt(i), firstUse, titles, beginRow,
						beginCol);
				if (handlerRow != null) {
					res.addAll(handlerRow);
				}
			}
			return res;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 读取excel中的数据.excel的第一行作为key值,不计入数据,从第beginRow+2行第beginCol+1列开始读取数据
	 * 
	 * @param is 输入流
	 * @param firstUse 每个sheet中第一行数据是否可作为字段使用,true可,false不可
	 * @param titles 当firstUse为true时,该值不使用.若是false,则该值为字段名或key值,但是第一行数据仍不使用
	 * @param beginRow 从第beginRow+1行开始读取excel数据
	 * @param beginCol 从第beginCol+1列开始读取excel
	 * @return 结果集
	 */
	@Override
	public List<Map<String, Object>> readExcel(InputStream is, boolean firstUse, List<String> titles, int beginRow,
			int beginCol) {
		try (Workbook wb = WorkbookFactory.create(is);) {
			int sheets = wb.getNumberOfSheets();
			List<Map<String, Object>> res = new ArrayList<>();
			for (int i = 0; i < sheets; i++) {
				List<Map<String, Object>> row = handlerRow(wb.getSheetAt(i), firstUse, titles, beginRow, beginCol);
				if (ListUtils.isNotBlank(row)) {
					res.addAll(row);
				}
			}
			return res;
		} catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
			e.printStackTrace();
			log.error(TipsEnum.TIP_LOG_ERROR.getMsg("上传excel文件解析失败->" + e.getMessage()));
		}
		return null;
	}

	/**
	 * 读取excel中的数据
	 * 
	 * @param sheet 每一个sheet页中的数据
	 * @param firstUse 每个sheet中第一行数据是否可作为字段使用,true可,false不可
	 * @param titles 当firstUse为true时,该值不使用.若是false,则该值为字段名或key值,但是第一行数据仍不使用
	 * @param beginRow 从第beginRow+1行开始读取excel数据
	 * @param beginCol 从第beginCol+1列开始读取excel
	 * @return 结果集
	 */
	private List<Map<String, Object>> handlerRow(Sheet sheet, boolean firstUse, List<String> titles, int beginRow,
			int beginCol) {
		// int rows = sheet.getLastRowNum();
		int rows = sheet.getPhysicalNumberOfRows();
		if (rows < 2) {
			return null;
		}
		Row first = sheet.getRow(beginRow);
		short cellNum = first.getLastCellNum();
		if (cellNum < 1) {
			return null;
		}
		List<Map<String, Object>> res = new ArrayList<>();
		for (int j = beginRow + 1; j < rows; j++) {
			Map<String, Object> rowData = new HashMap<>();
			for (int k = beginCol; k < cellNum; k++) {
				Object cellVal = ExcelUtils.getCellValue(sheet.getRow(j).getCell(k));
				if (firstUse) {
					rowData.put(String.valueOf(first.getCell(k)), cellVal);
				} else {
					if (ListUtils.isBlank(titles)) {
						rowData.put("column" + k, cellVal);
					} else {
						rowData.put(titles.get(k - beginCol), cellVal);
					}
				}
			}
			res.add(rowData);
		}
		return res;
	}
}