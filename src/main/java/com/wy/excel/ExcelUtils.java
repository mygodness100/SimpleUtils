package com.wy.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
	 * @param path 文件路径
	 * @return Workbook实例
	 */
	public static Workbook createIsWorkbook(String path) {
		File file = new File(path);
		if (!file.exists()) {
			throw new ResultException("文件不存在");
		}
		try {
			if (path.endsWith(".xlsx")) {
				return new XSSFWorkbook(new FileInputStream(file));
			} else if (path.endsWith(".xls")) {
				return new HSSFWorkbook(new FileInputStream(file));
			} else {
				throw new ResultException("excel文件格式不正确!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据excel文件的结尾来自动判断生成那种版本的excel,若传的文件没有指定类型,自动归结为低版本excel
	 * 
	 * @param path 以.xls或xlsx结尾的文件路径
	 * @param list 数据源
	 * @param subject 是否添加字段名称,true添加false不添加,默认添加
	 */
	public static <T> boolean writeExcel(List<T> list, String path) {
		return writeExcel(list, path, true);
	}

	/**
	 * 根据excel文件的结尾来自动判断生成那种版本的excel,若传的文件没有指定类型,自动归结为低版本excel
	 * 
	 * @param path 以.xls或xlsx结尾的文件路径
	 * @param list 数据源
	 * @param subject 是否添加字段名称,true添加false不添加,默认添加
	 */
	public static <T> boolean writeExcel(List<T> list, String path, boolean subject) {
		if (StrUtils.isBlank(path) || ListUtils.isBlank(list)) {
			log.info("路径不存在或数据源为空!");
			return false;
		}
		try (FileOutputStream fos = new FileOutputStream(path);
				Workbook workbook = createWorkbook(path);) {
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
					if (Date.class == field.getType() && !Objects.isNull(field.get(t))) {
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
		} catch (IOException | NoSuchFieldException | SecurityException
				| IllegalAccessException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 处理第一行的标题
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
	 * 根据excel文件的结尾来自动判断生成那种版本的excel,若传的文件没有指定类型,自动归结为低版本excel
	 * 
	 * @param path 以.xls或xlsx结尾的文件路径
	 * @param list 数据源
	 */
	public static boolean writeExcelAuto(String path, List<List<String>> list) {
		List<List<List<String>>> excel = new ArrayList<>();
		excel.add(list);
		return writeExcel(excel, path);
	}

	/**
	 * 根据excel文件的结尾来自动判断生成那种版本的excel,若传的文件没有指定类型,自动归结为低版本excel
	 * 
	 * @param path 以.xls或xlsx结尾的文件路径
	 * @param list 数据源
	 */
	public static boolean createExcelAuto(List<List<List<String>>> excel, String path) {
		Workbook workbook = createWorkbook(path);
		return writeExcel(workbook, excel, path);
	}

	/**
	 * 写入一个xls结尾的excel文件,低版本的excel,Excel2003以前(包括2003)的版本
	 * 
	 * @param excel 数据源
	 * @param path 写入文件路径
	 */
	public static boolean writeXLS(String path, List<List<String>> excel) {
		List<List<List<String>>> list = new ArrayList<>();
		list.add(excel);
		Workbook wb = new HSSFWorkbook();
		return writeExcel(wb, list, path);
	}

	/**
	 * 写入一个xls结尾的excel文件,低版本的excel,Excel2003以前(包括2003)的版本
	 * 
	 * @param excel 数据源
	 * @param path 写入文件路径
	 */
	public static boolean writeXLS(List<List<List<String>>> excel, String path) {
		Workbook wb = new HSSFWorkbook();
		return writeExcel(wb, excel, path);
	}

	/**
	 * 写入一个xlsx结尾的excel文件,高版本的excel,Excel2007的版本
	 * 
	 * @param excel 数据源
	 * @param path 写入文件路径
	 */
	public static boolean writeXLSX(String path, List<List<String>> excel) {
		List<List<List<String>>> list = new ArrayList<>();
		list.add(excel);
		Workbook wb = new XSSFWorkbook();
		return writeExcel(wb, list, path);
	}

	/**
	 * 写入一个xlsx结尾的excel文件,高版本的excel,Excel2007的版本
	 * 
	 * @param excel 数据源
	 * @param path 写入文件路径
	 */
	public static boolean writeXLSX(List<List<List<String>>> excel, String path) {
		Workbook wb = new XSSFWorkbook();
		return writeExcel(wb, excel, path);
	}

	/**
	 * 将数据写入一个excel表中,表以低版本为主,即以xls结尾,默认无字段栏
	 * 
	 * @param excel 数据源
	 * @param path 写入文件路径
	 */
	public static boolean writeExcel(Object wb, List<List<List<String>>> datas, String path) {
		return writeExcel((Workbook) wb, datas, path, null);
	}

	public static boolean writeExcel(Workbook wb, List<List<List<String>>> excel, String path,
			CellStyle cellStyle) {
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
						c.setCellValue(excel.get(page).get(row).get(col));
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
	 * 根据excel模版来写入数据,需要先从一个excel中读取格式等信息 不同的版本需要不同的模版文件
	 */
	public static boolean writeTemp() {
		return false;
	}

	/**
	 * 将excel表中的数据读取到流或数据库或内存中
	 * 
	 * @param excel 数据源
	 * @param path 写入文件路径
	 */
	public List<Map<String, Object>> readExcel(String path) {
		List<Map<String, Object>> res = new ArrayList<>();
		try (Workbook wb = createIsWorkbook(path);) {
			int sheets = wb.getNumberOfSheets();
			for (int i = 0; i < sheets; i++) {
				List<Map<String, Object>> handlerRow = handlerRow(wb.getSheetAt(i));
				if (handlerRow != null) {
					res.addAll(handlerRow);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * cxcel必须有标题,若没有则将不会报错
	 */
	private List<Map<String, Object>> handlerRow(Sheet sheet) {
		int rows = sheet.getLastRowNum();
		if (rows < 2) {
			return null;
		}
		// 获取第一行的数据库字段对应的中文名,只拿税号和验证码
		Row first = sheet.getRow(0);
		short cellNum = first.getLastCellNum();
		if (cellNum < 1) {
			return null;
		}
		List<Map<String, Object>> res = new ArrayList<>();
		for (int j = 1; j < rows + 1; j++) {
			Map<String, Object> rowData = new HashMap<>();
			for (int k = 0; k < cellNum; k++) {
				String cellVal = handlerCell(sheet.getRow(j).getCell(k).getCellTypeEnum(),
						sheet.getRow(j).getCell(k));
				rowData.put(first.getCell(k).getStringCellValue(), cellVal);
			}
			res.add(rowData);
		}
		return res;
	}

	private String handlerCell(CellType cellType, Cell cell) {
		switch (cellType) {
			case BLANK:
			case _NONE:
				return null;
			case BOOLEAN:
				return String.valueOf(cell.getBooleanCellValue());
			case NUMERIC:
				return String.valueOf((long) cell.getNumericCellValue());
			case STRING:
				return cell.getStringCellValue();
			default:
				return null;
		}
	}
}