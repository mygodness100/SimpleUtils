package com.wy.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.common.io.Files;
import com.wy.common.Constant;
import com.wy.enums.TipsEnum;
import com.wy.excel.annotation.Excel;
import com.wy.excel.annotation.ExcelColumn;
import com.wy.excel.annotation.ExcelRelated;
import com.wy.excel.enums.ExcelAction;
import com.wy.excel.enums.PropConverter;
import com.wy.result.ResultException;
import com.wy.utils.DateUtils;
import com.wy.utils.ListUtils;
import com.wy.utils.StrUtils;

import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;

/**
 * apache操作excel的包,HSSFWorkbook是操作Excel2003以前(包括2003)的版本,扩展名是.xls
 * 需要导入包:poi-3.17,commons-codec-1.10,commons-collections4-4.1,commons-logging-1.2,log4j-1.2.17
 * XSSFWorkbook是操作Excel2007的版本,扩展名是.xlsx
 * xmlbeans-2.6.0,curvesapi-1.04,poi-ooxml-schemas-3.17,poi-ooxml-3.17 FIXME
 * 所有的方法都暂时没有考虑类上的注解,单元格可以添加注解,选择列表
 * 
 * @author ParadiseWY
 * @date 2020-11-23 16:11:10
 * @git {@link https://github.com/mygodness100}
 */
@Slf4j
public class ExcelUtils {

	/**
	 * 根据文件后缀名生成相应的Workbook实例,将数据写入到excel中使用
	 * 
	 * @param path 文件路径
	 * @return Workbook实例
	 */
	public static Workbook generateWorkbook(String path) {
		if (StrUtils.isBlank(path)) {
			throw new ResultException(TipsEnum.TIP_LOG_ERROR.getMsg("文件路径错误"));
		}
		if (Objects.equals("xlsx", Files.getFileExtension(path))) {
			return new XSSFWorkbook();
		} else {
			return new HSSFWorkbook();
		}
	}

	/**
	 * 根据文件后缀名生成相应的Workbook实例,读取excel文件中的数据时使用
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
	public static <T> void writeExcel(List<T> list, String path) {
		writeExcel(list, path, true);
	}

	/**
	 * 将数据写入excel文件,根据excel文件的结尾判断生成那种版本的excel,若未指定类型,自动归结为低版本excel
	 * 
	 * @param path 以.xls或xlsx结尾的文件路径
	 * @param list 实体类数据源集合
	 * @param subject 是否添加字段名称,true添加false不添加,默认添加
	 */
	public static <T> void writeExcel(List<T> list, String path, boolean subject) {
		if (ListUtils.isBlank(list)) {
			log.info(TipsEnum.TIP_LOG_INFO.getMsg("excel写入文件数据源为空"));
			return;
		}
		if (StrUtils.isBlank(path)) {
			log.info(TipsEnum.TIP_LOG_ERROR.getMsg("excel写入文件路径不存在"));
			throw new ResultException("路径不存在");
		}
		double sheetNum = Math.ceil(list.size() / Constant.EXCEL_SHEET_MAX);
		for (int i = 0; i < sheetNum; i++) {
			handleSheet(i, list, path, subject);
		}
	}

	/**
	 * 处理每一个sheet页
	 *
	 * @param <T> 泛型
	 * @param index sheet页下标
	 * @param list 数据集
	 * @param path 写文件路径
	 * @param subject 是否需要第一排的标题
	 */
	public static <T> void handleSheet(int index, List<T> list, String path, boolean subject) {
		try (FileOutputStream fos = new FileOutputStream(path); Workbook workbook = generateWorkbook(path);) {
			Sheet sheet = workbook.createSheet();
			int beginRow = subject ? 1 : 0;
			Class<?> clazz = list.get(0).getClass();
			List<Field> fields = handleClass(clazz);
			int startNo = index * Constant.EXCEL_SHEET_MAX;
			int endNo = Math.min(startNo + Constant.EXCEL_SHEET_MAX, list.size());
			for (int i = startNo; i < endNo; i++) {
				Row row = sheet.createRow(beginRow);
				T t = list.get(i);
				for (int j = 0; j < fields.size(); j++) {
					handleCell(row.createCell(j), t, fields.get(j));
				}
				beginRow++;
			}
			if (subject) {
				createFirst(sheet, fields);
			}
			workbook.write(fos);
		} catch (IOException | SecurityException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 从类中筛选出需要导出的字段
	 * 
	 * @param <T> 需要导出的泛型
	 * @param clazz 泛型的字节码
	 * @return 需要导出的字段集合
	 */
	public static <T> List<Field> handleClass(Class<T> clazz) {
		List<Field> result = new ArrayList<>();
		Field[] parentFields = clazz.getSuperclass().getDeclaredFields();
		if (ArrayUtils.isNotEmpty(parentFields)) {
			result.addAll(Arrays.asList(parentFields));
		}
		Field[] childFields = clazz.getDeclaredFields();
		if (ArrayUtils.isEmpty(childFields)) {
			throw new ResultException("该类中没有定义字段,请检查");
		} else {
			result.addAll(Arrays.asList(childFields));
		}
		return result.stream().filter(t -> {
			if (t.isAnnotationPresent(Excel.class)) {
				if (t.getAnnotation(Excel.class).excelAction() == ExcelAction.EXPORT
						|| t.getAnnotation(Excel.class).excelAction() == ExcelAction.ALL) {
					return true;
				}
			}
			if (t.isAnnotationPresent(ExcelRelated.class)) {
				return true;
			}
			return false;
		}).collect(Collectors.toList());
	}

	/**
	 * 处理每一个单元格
	 * 
	 * @param <T> 泛型
	 * @param cell 单元格
	 * @param t 需要写入到单元格的数据
	 * @param field 当前字段
	 */
	public static <T> void handleCell(Cell cell, T t, Field field) {
		field.setAccessible(true);
		try {
			if (field.isAnnotationPresent(ExcelRelated.class)) {
				handleRelatedCell(cell, t, field);
			} else {
				setCellValue(cell, field, field.get(t));
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 给关联类中的字段进行赋值,若有值则赋值,若无值则填""
	 * 
	 * @param <T> 泛型
	 * @param cell excel的单元格
	 * @param t 数据
	 * @param field 字段
	 */
	public static <T> void handleRelatedCell(Cell cell, T t, Field field) {
		try {
			Object related = field.get(t);
			String[] columns = field.getAnnotation(ExcelRelated.class).relatedAttrs();
			if (Objects.nonNull(related)) {
				for (String column : columns) {
					Field declaredField = related.getClass().getDeclaredField(column);
					if (Objects.isNull(declaredField)) {
						throw new ResultException(TipsEnum.TIP_LOG_ERROR.getMsg("excel操作关联类中relatedAttribute属性设置错误"));
					}
					declaredField.setAccessible(true);
					setCellValue(cell, declaredField, declaredField.get(related));
				}
			} else {
				for (int i = 0; i < columns.length; i++) {
					setCellValue(cell, null, null);
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 处理第一行的标题
	 * 
	 * @param sheet sheet页
	 * @param fields 第一行的字段
	 */
	public static void createFirst(Sheet sheet, List<Field> fields) {
		Row first = sheet.createRow(0);
		for (int i = 0; i < fields.size(); i++) {
			Cell cell = first.createCell(i);
			Field field = fields.get(i);
			field.setAccessible(true);
			if (field.isAnnotationPresent(Excel.class)) {
				String title = StrUtils.isBlank(field.getAnnotation(Excel.class).value()) ? field.getName()
						: field.getAnnotation(Excel.class).value();
				cell.setCellValue(title);
			} else if (field.isAnnotationPresent(ExcelRelated.class)) {
				String[] excels = field.getAnnotation(ExcelRelated.class).relatedAttrs();
				for (String excel : excels) {
					if (StrUtils.isBlank(excel)) {
						throw new ResultException(TipsEnum.TIP_LOG_ERROR.getMsg("关联字段缺少value或targetAttr"));
					}
					cell.setCellValue(excel);
				}
			}
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
	 * @param path 以.xls或xlsx结尾的文件路径
	 * @param list map类数据源集合
	 * @param subject 是否添加字段名称,true添加false不添加,默认添加
	 */
	public static boolean writeExcel(String path, List<Map<String, Object>> list, boolean subject) {
		if (StrUtils.isBlank(path) || ListUtils.isBlank(list)) {
			log.info("路径不存在或数据源为空!");
			return false;
		}
		try (FileOutputStream fos = new FileOutputStream(path); Workbook workbook = generateWorkbook(path);) {
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
	public static void createFirst(List<String> titles, Sheet sheet) {
		Row first = sheet.createRow(0);
		int j = 0;
		for (String title : titles) {
			Cell cell = first.createCell(j);
			cell.setCellValue(title);
			j++;
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
		Workbook workbook = generateWorkbook(path);
		return writeExcel(workbook, excel, path);
	}

	/**
	 * 写入一个xls结尾的excel文件,低版本的excel,Excel2003以前(包括2003)的版本
	 * 
	 * @param excel 数据源
	 * @param path 写入文件路径
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
	 * @param path 写入文件路径
	 */
	public static boolean writeXLS(List<List<List<Object>>> excel, String path) {
		return writeExcel(new HSSFWorkbook(), excel, path);
	}

	/**
	 * 写入一个xlsx结尾的excel文件,高版本的excel,Excel2007的版本
	 * 
	 * @param excel 数据源
	 * @param path 写入文件路径
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
	 * @param path 写入文件路径
	 */
	public static boolean writeXLSX(List<List<List<Object>>> excel, String path) {
		return writeExcel(new XSSFWorkbook(), excel, path);
	}

	/**
	 * 将数据写入一个excel表中,表以低版本为主,即以xls结尾,默认无字段栏
	 * 
	 * @param excel 数据源
	 * @param path 写入文件路径
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
	 * @param path 文件地址
	 * @param firstUse 每个sheet中第一行数据是否可作为字段使用,true可,false不可
	 * @param titles 当firstUse为true时,该值不使用.若是false,则该值为字段名或key值,但是第一行数据仍不使用
	 * @return list集合
	 */
	public static List<Map<String, Object>> readExcel(String path, boolean firstUse, List<String> titles) {
		return readExcel(path, firstUse, titles, 0);
	}

	/**
	 * 读取excel中的数据.excel的第一行作为key值,不计入数据,从第beginRow+2行第1列开始读取数据
	 * 
	 * @param path 文件地址
	 * @param firstUse 每个sheet中第一行数据是否可作为字段使用,true可,false不可
	 * @param titles 当firstUse为true时,该值不使用.若是false,则该值为字段名或key值,但是第一行数据仍不使用
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
	 * @param path 需要读取的excel路径
	 * @param firstUse 每个sheet中第一行数据是否可作为字段使用,true可,false不可
	 * @param titles 当firstUse为true时,该值不使用.若是false,则该值为字段名或key值,但是第一行数据仍不使用
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
	 * @param is 输入流
	 * @param firstUse 每个sheet中第一行数据是否可作为字段使用,true可,false不可
	 * @param titles 当firstUse为true时,该值不使用.若是false,则该值为字段名或key值的集合,但是第一行数据仍不使用
	 * @return 结果集
	 */
	public static List<Map<String, Object>> readExcel(InputStream is, boolean firstUse, List<String> titles) {
		return readExcel(is, firstUse, titles, 0);
	}

	/**
	 * 读取excel中的数据.excel的第一行作为key值,不计入数据,从第beginRow+2行第1列开始读取数据
	 * 
	 * @param is 输入流
	 * @param firstUse 每个sheet中第一行数据是否可作为字段使用,true可,false不可
	 * @param titles 当firstUse为true时,该值不使用.若是false,则该值为字段名或key值,但是第一行数据仍不使用
	 * @param beginRow 从第beginRow+1行开始读取excel
	 * @return 结果集
	 */
	public static List<Map<String, Object>> readExcel(InputStream is, boolean firstUse, List<String> titles,
			int beginRow) {
		return readExcel(is, firstUse, titles, beginRow, 0);
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
	 * @param sheet 每一个sheet页中的数据
	 * @param firstUse 每个sheet中第一行数据是否可作为字段使用,true可,false不可
	 * @param titles 当firstUse为true时,该值不使用.若是false,则该值为字段名或key值,但是第一行数据仍不使用
	 * @param beginRow 从第beginRow+1行开始读取excel数据
	 * @param beginCol 从第beginCol+1列开始读取excel
	 * @return 结果集
	 */
	private static List<Map<String, Object>> handlerRow(Sheet sheet, boolean firstUse, List<String> titles,
			int beginRow, int beginCol) {
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
				Object cellVal = getCellValue(sheet.getRow(j).getCell(k));
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

	/**
	 * 获得单元格值
	 * 
	 * @param cell 单元格
	 * @return 单元格值
	 */
	public static Object getCellValue(Cell cell) {
		if (Objects.isNull(cell)) {
			return "";
		}
		switch (cell.getCellTypeEnum()) {
		case BOOLEAN:
			return cell.getBooleanCellValue();
		case NUMERIC:
			if (HSSFDateUtil.isCellDateFormatted(cell)) {
				return cell.getDateCellValue();
			}
			return cell.getNumericCellValue();
		case STRING:
			return cell.getStringCellValue();
		case FORMULA:
			if (cell.getCachedFormulaResultTypeEnum() == CellType.NUMERIC) {
				return cell.getNumericCellValue();
			} else {
				return cell.getRichStringCellValue().getString();
			}
		default:
			return cell.getErrorCellValue();
		}
	}

	/**
	 * 设置单元格格式以及值
	 * 
	 * @param cell 单元格
	 * @param field 字段
	 * @param value 单元格值
	 */
	public static void setCellValue(Cell cell, Field field, Object value) {
		if (Objects.isNull(field) || Objects.isNull(value)) {
			cell.setCellValue("");
			return;
		}
		Class<? extends Object> clazz = value.getClass();
		if (clazz == Date.class) {
			cell.setCellValue((Date) value);
		} else if (clazz == Boolean.class || clazz == boolean.class) {
			cell.setCellValue(Boolean.parseBoolean(clazz.toString()));
		} else if (NumberUtils.isCreatable(value.toString())) {
			cell.setCellValue(Double.valueOf(value.toString()));
		} else {
			cell.setCellValue(value.toString());
		}
	}

	/**
	 * 导出excel数据表格
	 * 
	 * @param resp 响应
	 * @param datas 需要导出的数据
	 * @param excelName excel表格名字
	 */
	public static <T> void exportExcel(List<T> datas, HttpServletResponse resp, String excelName) {
		resp.setContentType("application/download");
		try (OutputStream os = resp.getOutputStream();) {
			resp.setHeader("Content-Disposition", "attchament;filename="
					+ new String((excelName + ".xls").getBytes("GBK"), StandardCharsets.ISO_8859_1));
			exportExcel(datas, os);
		} catch (IOException e) {
			throw new ResultException("导出失败", e);
		}
	}

	/**
	 * 导出excel数据表格
	 * 
	 * @param os 输出流
	 * @param datas 需要到处的数据
	 */
	public static <T> void exportExcel(List<T> datas, OutputStream os) {
		if (ListUtils.isBlank(datas)) {
			return;
		}
		try (Workbook book = new HSSFWorkbook();) {
			Class<? extends Object> clazz = datas.get(0).getClass();
			// 若存在Excel注解,判断是否能导入导出,没有注解默认可以导入导出
			if (clazz.isAnnotationPresent(Excel.class)) {
				Excel excel = clazz.getAnnotation(Excel.class);
				if (excel.excelAction() == ExcelAction.IMPORT) {
					throw new ResultException("该类只允许导入,不允许导出");
				}
				// TODO 取出所有不可导出字段,与后面字段上带ExcelColumn注解的比较
			}
			Field[] fields = clazz.getDeclaredFields();
			Sheet sheet = book.createSheet();
			// 生成第一行的字段
			Row firstRow = sheet.createRow(0);
			int j = 0;
			String titleName = "";
			for (Field field : fields) {
				field.setAccessible(true);
				// 常量和静态变量不导出
				if (Modifier.isFinal(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
					continue;
				}
				// 默认所有字段都可以导出导入,判断有ExcelColumn的个别行为
				if (field.isAnnotationPresent(ExcelColumn.class)) {
					ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
					// 只允许导入,不允许导出或什么都不做
					if (excelColumn.excelAction() == ExcelAction.IMPORT
							|| excelColumn.excelAction() == ExcelAction.NOTHING) {
						continue;
					}
					if (StrUtils.isNotBlank(excelColumn.value())) {
						titleName = excelColumn.value();
					}
					// 第一行的显示字段:ExcelColumn->ApiModelProperty->Java属性
					if (StrUtils.isBlank(excelColumn.value())) {
						if (field.isAnnotationPresent(ApiModelProperty.class)) {
							ApiModelProperty apiModelProperty = field.getAnnotation(ApiModelProperty.class);
							titleName = StrUtils.isBlank(apiModelProperty.value()) ? field.getName()
									: apiModelProperty.value();
						} else {
							titleName = field.getName();
						}
					}
				} else {
					if (field.isAnnotationPresent(ApiModelProperty.class)) {
						ApiModelProperty apiModelProperty = field.getAnnotation(ApiModelProperty.class);
						titleName = StrUtils.isBlank(apiModelProperty.value()) ? field.getName()
								: apiModelProperty.value();
					} else {
						titleName = field.getName();
					}
				}
				Cell c = firstRow.createCell(j);
				c.setCellValue(titleName);
				j++;
			}
			// 写入数据
			for (int row = 0; row < datas.size(); row++) {
				Row r = sheet.createRow(row + 1);
				int i = 0;
				T data = datas.get(row);
				for (Field field : fields) {
					field.setAccessible(true);
					if (Modifier.isFinal(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
						continue;
					}
					if (field.isAnnotationPresent(ExcelColumn.class)) {
						ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
						// 只允许导入,不允许导出
						if (excelColumn.excelAction() == ExcelAction.IMPORT
								|| excelColumn.excelAction() == ExcelAction.NOTHING) {
							continue;
						}
						// 是否有特殊值需要选择
						if (excelColumn.propConverter() != PropConverter.class) {
							Class<? extends PropConverter> select = excelColumn.propConverter();
							setCellValue(r, i, PropConverter.getMember(select.getEnumConstants(), field.get(data)));
							i++;
							continue;
						}
					}
					setCellValue(r, i, field.get(data));
					i++;
				}
			}
			book.write(os);
		} catch (IOException | IllegalArgumentException | IllegalAccessException | SecurityException e) {
			e.printStackTrace();
		}
	}

	private static void setCellValue(Row row, int i, Object value) {
		if (Objects.isNull(row)) {
			return;
		}
		if (Objects.isNull(value)) {
			row.createCell(i).setCellValue("");
			return;
		}
		Class<? extends Object> clazz = value.getClass();
		if (clazz == Boolean.class || clazz == boolean.class) {
			row.createCell(i, CellType.BOOLEAN).setCellValue(Boolean.parseBoolean(value.toString()));
		} else if (value instanceof Number) {
			row.createCell(i, CellType.NUMERIC).setCellValue(Double.parseDouble(value.toString()));
		} else if (value instanceof Date) {
			row.createCell(i, CellType.STRING).setCellValue(DateUtils.formatDateTime((Date) value));
		} else {
			row.createCell(i, CellType.STRING).setCellValue(String.valueOf(value));
		}
	}
}