package com.wy.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.wy.enums.TipsEnum;
import com.wy.result.ResultException;
import com.wy.utils.ClassUtils;
import com.wy.utils.ListUtils;
import com.wy.utils.StrUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * apache操作excel的包,HSSFWorkbook是操作Excel2003以前(包括2003)的版本,扩展名是.xls
 * 需要导入包:poi-3.17,commons-codec-1.10,commons-collections4-4.1,commons-logging-1.2,log4j-1.2.17
 * XSSFWorkbook是操作Excel2007的版本,扩展名是.xlsx
 * xmlbeans-2.6.0,curvesapi-1.04,poi-ooxml-schemas-3.17,poi-ooxml-3.17
 * 
 * @author paradiseWy
 */
@Slf4j
public class ExcelUtils {

	/**
	 * 根据文件后缀名生成相应的Workbook实例
	 * 
	 * @param path 文件路径
	 * @return Workbook实例
	 */
	public static Workbook createWorkbook(String path) {
		if (StrUtils.isBlank(path)) {
			throw new ResultException("文件路径错误");
		}
		if (path.endsWith(".xlsx")) {
			return new XSSFWorkbook();
		} else if (!path.endsWith(".xls") && !path.endsWith(".xlsx")) {
			path += ".xls";
		}
		return new HSSFWorkbook();
	}

	/**
	 * 根据文件后缀名生成相应的Workbook实例,直接读取文件,设置输入流
	 * 
	 * @param path 文件路径
	 * @return Workbook实例
	 */
	public static Workbook createIsWorkbook(String path) {
		File file = new File(path);
		if (!file.exists()) {
			throw new ResultException("文件不存在");
		}
		Workbook workbook = null;
		try {
			if (path.endsWith(".xlsx")) {
				workbook = new XSSFWorkbook(new FileInputStream(file));
			} else if (path.endsWith(".xls")) {
				workbook = new HSSFWorkbook(new FileInputStream(file));
			} else {
				throw new ResultException("excel文件格式不正确!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return workbook;
	}

	/**
	 * 根据excel文件的结尾判断生成那种版本的excel,若未指定类型,自动归结为低版本excel
	 * 
	 * @param path 以.xls或xlsx结尾的文件路径
	 * @param list 实体类数据源集合
	 */
	public static <T> boolean writeExcel(List<T> list, String path) {
		return writeExcel(list, path, true);
	}

	/**
	 * 根据excel文件的结尾判断生成那种版本的excel,若未指定类型,自动归结为低版本excel
	 * 
	 * @param path    以.xls或xlsx结尾的文件路径
	 * @param list    实体类数据源集合
	 * @param subject 是否添加字段名称,true添加false不添加,默认添加
	 */
	public static <T> boolean writeExcel(List<T> list, String path, boolean subject) {
		if (StrUtils.isBlank(path) || ListUtils.isBlank(list)) {
			log.info("路径不存在或数据源为空!");
			return false;
		}
		try (FileOutputStream fos = new FileOutputStream(path); Workbook workbook = createWorkbook(path);) {
			Sheet sheet = workbook.createSheet();
			int beginRow = subject ? 1 : 0;
			Class<?> clazz = list.get(0).getClass();
			List<String> allField = ClassUtils.getEntityField(clazz);
			for (int i = 0; i < list.size(); i++) {
				Row row = sheet.createRow(beginRow);
				T t = list.get(i);
				for (int j = 0; j < allField.size(); j++) {
					Field field = clazz.getDeclaredField(allField.get(j));
					field.setAccessible(true);
					Cell cell = row.createCell(j);
					if (!Objects.isNull(field.get(t)) && Date.class == field.getType()) {
						cell.setCellValue((Date) field.get(t));
					} else {
						cell.setCellValue(Objects.toString(field.get(t), ""));
					}
				}
				beginRow++;
			}
			if (subject) {
				createFirst(sheet, allField);
			}
			workbook.write(fos);
			return true;
		} catch (IOException | NoSuchFieldException | SecurityException | IllegalAccessException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 处理第一行的标题
	 * 
	 * @param sheet sheet页
	 * @param datas 第一行的数据
	 */
	public static void createFirst(Sheet sheet, Collection<String> datas) {
		Row first = sheet.createRow(0);
		int j = 0;
		for (String data : datas) {
			Cell cell = first.createCell(j);
			cell.setCellValue(data);
			j++;
		}
	}

	/**
	 * 根据excel文件的结尾判断生成那种版本的excel,若未指定类型,自动归结为低版本excel
	 * 
	 * @param path 以.xls或xlsx结尾的文件路径
	 * @param list map类数据源集合
	 */
	public static boolean writeExcel(String path, List<Map<String, Object>> list) {
		return writeExcel(path, list, true);
	}

	/**
	 * 根据excel文件的结尾判断生成那种版本的excel,若未指定类型,自动归结为低版本excel
	 * 
	 * @param path    以.xls或xlsx结尾的文件路径
	 * @param list    map类数据源集合
	 * @param subject 是否添加字段名称,true添加false不添加,默认添加
	 */
	public static boolean writeExcel(String path, List<Map<String, Object>> list, boolean subject) {
		if (StrUtils.isBlank(path) || ListUtils.isBlank(list)) {
			log.info("路径不存在或数据源为空!");
			return false;
		}
		try (FileOutputStream fos = new FileOutputStream(path); Workbook workbook = createWorkbook(path);) {
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
				createFirst(sheet, allField);
			}
			workbook.write(fos);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 根据excel文件的结尾来自动判断生成那种版本的excel,若传的文件没有指定类型,自动归结为低版本excel
	 * 
	 * @param path 以.xls或xlsx结尾的文件路径
	 * @param list 数据源
	 */
	public static boolean writeExcelAuto(String path, List<List<Object>> datas) {
		List<List<List<Object>>> excel = new ArrayList<>();
		excel.add(datas);
		return writeExcelAuto(excel, path);
	}

	/**
	 * 根据excel文件的结尾来自动判断生成那种版本的excel,若传的文件没有指定类型,自动归结为低版本excel
	 * 
	 * @param path 以.xls或xlsx结尾的文件路径
	 * @param list 数据源
	 */
	public static boolean writeExcelAuto(List<List<List<Object>>> excel, String path) {
		Workbook workbook = createWorkbook(path);
		return writeExcel(workbook, excel, path);
	}

	/**
	 * 写入一个xls结尾的excel文件,低版本的excel,Excel2003以前(包括2003)的版本
	 * 
	 * @param excel 数据源
	 * @param path  写入文件路径
	 */
	public static boolean writeXLS(String path, List<List<Object>> datas) {
		List<List<List<Object>>> excel = new ArrayList<>();
		excel.add(datas);
		return writeXLS(excel, path);
	}

	/**
	 * 写入一个xls结尾的excel文件,低版本的excel,Excel2003以前(包括2003)的版本
	 * 
	 * @param excel 数据源
	 * @param path  写入文件路径
	 */
	public static boolean writeXLS(List<List<List<Object>>> excel, String path) {
		return writeExcel(new HSSFWorkbook(), excel, path);
	}

	/**
	 * 写入一个xlsx结尾的excel文件,高版本的excel,Excel2007的版本
	 * 
	 * @param excel 数据源
	 * @param path  写入文件路径
	 */
	public static boolean writeXLSX(String path, List<List<Object>> datas) {
		List<List<List<Object>>> excel = new ArrayList<>();
		excel.add(datas);
		return writeXLSX(excel, path);
	}

	/**
	 * 写入一个xlsx结尾的excel文件,高版本的excel,Excel2007的版本
	 * 
	 * @param excel 数据源
	 * @param path  写入文件路径
	 */
	public static boolean writeXLSX(List<List<List<Object>>> excel, String path) {
		return writeExcel(new XSSFWorkbook(), excel, path);
	}

	/**
	 * 将数据写入一个excel表中,表以低版本为主,即以xls结尾,默认无字段栏
	 * 
	 * @param excel 数据源
	 * @param path  写入文件路径
	 */
	public static boolean writeExcel(Workbook wb, List<List<List<Object>>> datas, String path) {
		return writeExcel(wb, datas, path, null);
	}

	public static boolean writeExcel(Workbook wb, List<List<List<Object>>> excel, String path, CellStyle cellStyle) {
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
	 * 根据excel模版来写入数据,需要先从一个excel中读取格式等信息 不同的版本需要不同的模版文件 FIXME
	 */
	public static boolean writeTemp() {
		return false;
	}

	/**
	 * 读取excel表格数据,默认表格中第一行是key值,且可以使用,不计入数据,从第2行第1列读取数据
	 * 
	 * @param path 需要读取的excel文件路径
	 * @return 结果集
	 */
	public static List<Map<String, Object>> readExcel(String path) {
		return readExcel(path, true, null);
	}

	/**
	 * 读取excel中的数据.第一行不计入数据,从2行第1列开始读取数据
	 * 
	 * @param path     文件地址
	 * @param firstUse 每个sheet中第一行数据是否可作为字段使用,true可,false不可
	 * @param titles   当firstUse为true时,该值不使用.若是false,则该值为字段名或key值,但是第一行数据仍不使用
	 * @return list集合
	 */
	public static List<Map<String, Object>> readExcel(String path, boolean firstUse, List<String> titles) {
		return readExcel(path, firstUse, titles, 0);
	}

	/**
	 * 读取excel中的数据.excel的第一行作为key值,不计入数据,从第beginRow+2行第1列开始读取数据
	 * 
	 * @param path     文件地址
	 * @param firstUse 每个sheet中第一行数据是否可作为字段使用,true可,false不可
	 * @param titles   当firstUse为true时,该值不使用.若是false,则该值为字段名或key值,但是第一行数据仍不使用
	 * @param beginRow 从第beginRow+2行开始读取数据
	 * @return list集合
	 */
	public static List<Map<String, Object>> readExcel(String path, boolean firstUse, List<String> titles,
			int beginRow) {
		return readExcel(path, firstUse, titles, beginRow, 0);
	}

	/**
	 * 读取excel中的数据.excel的第一行作为key值,不计入数据,从第beginRow+2行第beginCol+1列开始读取数据
	 * 
	 * @param path     需要读取的excel路径
	 * @param firstUse 每个sheet中第一行数据是否可作为字段使用,true可,false不可
	 * @param titles   当firstUse为true时,该值不使用.若是false,则该值为字段名或key值,但是第一行数据仍不使用
	 * @param beginRow 从第beginRow+2行开始读取数据
	 * @param beginCol 从第beginCol+1列开始读取excel
	 * @return 结果集
	 */
	public static List<Map<String, Object>> readExcel(String path, boolean firstUse, List<String> titles, int beginRow,
			int beginCol) {
		try (Workbook wb = createIsWorkbook(path);) {
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
	 * 读取excel表格数据,默认表格中第一行是key值,且可以使用,不计入数据,从第2行第1列读取数据
	 * 
	 * @param is 输入流
	 * @return 结果集
	 */
	public static List<Map<String, Object>> readExcel(InputStream is) {
		return readExcel(is, true, null);
	}

	/**
	 * 读取excel中的数据.第一行不计入数据,从2行第1列开始读取数据
	 * 
	 * @param is       输入流
	 * @param firstUse 每个sheet中第一行数据是否可作为字段使用,true可,false不可
	 * @param titles   当firstUse为true时,该值不使用.若是false,则该值为字段名或key值的集合,但是第一行数据仍不使用
	 * @return 结果集
	 */
	public static List<Map<String, Object>> readExcel(InputStream is, boolean firstUse, List<String> titles) {
		return readExcel(is, firstUse, titles, 0);
	}

	/**
	 * 读取excel中的数据.excel的第一行作为key值,不计入数据,从第beginRow+2行第1列开始读取数据
	 * 
	 * @param is       输入流
	 * @param firstUse 每个sheet中第一行数据是否可作为字段使用,true可,false不可
	 * @param titles   当firstUse为true时,该值不使用.若是false,则该值为字段名或key值,但是第一行数据仍不使用
	 * @param beginRow 从第beginRow+2行开始读取excel
	 * @return 结果集
	 */
	public static List<Map<String, Object>> readExcel(InputStream is, boolean firstUse, List<String> titles,
			int beginRow) {
		return readExcel(is, firstUse, titles, beginRow, 0);
	}

	/**
	 * 读取excel中的数据.excel的第一行作为key值,不计入数据,从第beginRow+2行第beginCol+1列开始读取数据
	 * 
	 * @param is       输入流
	 * @param firstUse 每个sheet中第一行数据是否可作为字段使用,true可,false不可
	 * @param titles   当firstUse为true时,该值不使用.若是false,则该值为字段名或key值,但是第一行数据仍不使用
	 * @param beginRow 从第beginRow+2行开始读取excel数据
	 * @param beginCol 从第beginCol+1列开始读取excel
	 * @return 结果集
	 */
	public static List<Map<String, Object>> readExcel(InputStream is, boolean firstUse, List<String> titles,
			int beginRow, int beginCol) {
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
	 * @param sheet    每一个sheet页中的数据
	 * @param firstUse 每个sheet中第一行数据是否可作为字段使用,true可,false不可
	 * @param titles   当firstUse为true时,该值不使用.若是false,则该值为字段名或key值,但是第一行数据仍不使用
	 * @param beginRow 从第beginRow+2行开始读取excel数据
	 * @param beginCol 从第beginCol+1列开始读取excel
	 * @return 结果集
	 */
	private static List<Map<String, Object>> handlerRow(Sheet sheet, boolean firstUse, List<String> titles,
			int beginRow, int beginCol) {
		int rows = sheet.getLastRowNum();
		if (rows < 2) {
			return null;
		}
		Row first = sheet.getRow(beginRow);
		short cellNum = first.getLastCellNum();
		if (cellNum < 1) {
			return null;
		}
		List<Map<String, Object>> res = new ArrayList<>();
		for (int j = beginRow + 1; j < rows + 1; j++) {
			Map<String, Object> rowData = new HashMap<>();
			for (int k = beginCol; k < cellNum; k++) {
				Object cellVal = handlerCell(sheet.getRow(j).getCell(k));
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

	private static Object handlerCell(Cell cell) {
		if (Objects.isNull(cell)) {
			return null;
		}
		switch (cell.getCellTypeEnum()) {
		case BLANK:
		case _NONE:
			return null;
		case BOOLEAN:
			return cell.getBooleanCellValue();
		case NUMERIC:
			return cell.getNumericCellValue();
		case STRING:
			return cell.getStringCellValue();
		case FORMULA:
			return cell.getRichStringCellValue().getString();
		default:
			return null;
		}
	}
}