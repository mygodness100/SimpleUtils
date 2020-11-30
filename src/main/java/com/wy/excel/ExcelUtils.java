package com.wy.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.common.io.Files;
import com.wy.common.Constant;
import com.wy.common.PropConverter;
import com.wy.enums.TipsEnum;
import com.wy.excel.annotation.Excel;
import com.wy.excel.annotation.ExcelColumn;
import com.wy.excel.enums.ExcelAction;
import com.wy.result.ResultException;
import com.wy.utils.DateUtils;
import com.wy.utils.ListUtils;
import com.wy.utils.StrUtils;

import io.swagger.annotations.ApiModelProperty;

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
public interface ExcelUtils {

	ExcelUtils newExcelUtils();

	/**
	 * 根据文件后缀名生成相应的Workbook实例,将数据写入到excel中使用
	 * 
	 * @param path 文件路径
	 * @return Workbook实例
	 */
	static Workbook generateWorkbook(String path) {
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
	static Workbook createIsWorkbook(String path) {
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
	default <T> void writeExcel(List<T> list, String path) {
		writeExcel(list, path, true);
	}

	/**
	 * 将数据写入excel文件,根据excel文件的结尾判断生成那种版本的excel,若未指定类型,自动归结为低版本excel
	 * 
	 * @param path 以.xls或xlsx结尾的文件路径
	 * @param list 实体类数据源集合
	 * @param subject 是否添加字段名称,true添加false不添加,默认添加
	 */
	default <T> void writeExcel(List<T> list, String path, boolean subject) {
		checkParams(list, path, subject);
	}

	/**
	 * 参数检查,sheet最大数量检查
	 * 
	 * @param <T> 泛型数据类型
	 * @param list 数据
	 * @param path 文件路径
	 * @param subject 是否写标题
	 */
	default <T> void checkParams(List<T> list, String path, boolean subject) {
		if (StrUtils.isBlank(path) || ListUtils.isBlank(list)) {
			throw new ResultException(TipsEnum.TIP_LOG_INFO.getMsg("excel写入文件数据源为空"));
		}
		if (StrUtils.isBlank(path)) {
			throw new ResultException(TipsEnum.TIP_LOG_ERROR.getMsg("excel写入文件路径不存在"));
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
	<T> void handleSheet(int index, List<T> list, String path, boolean subject);

	/**
	 * 处理每一个单元格
	 * 
	 * @param <T> 泛型
	 * @param cell 单元格
	 * @param t 需要写入到单元格的数据
	 * @param field 当前字段
	 */
	default <T> void handleCell(Cell cell, T t, Field field) {}

	/**
	 * 给关联类中的字段进行赋值,若有值则赋值,若无值则填""
	 * 
	 * @param <T> 泛型
	 * @param cell excel的单元格
	 * @param t 数据
	 * @param field 字段
	 */
	default <T> void handleRelatedCell(Cell cell, T t, Field field) {}

	/**
	 * 处理第一行的标题
	 * 
	 * @param sheet sheet页
	 * @param fields 第一行的字段
	 */
	default void createFirst(Sheet sheet, List<Field> fields) {}

	/**
	 * 处理listmap中的第一行
	 * 
	 * @param titles 所有的字段,此处因为是map,不可标注注解,只能是字段名
	 * @param sheet sheet页
	 */
	default void createFirst(List<String> titles, Sheet sheet) {}

	/**
	 * 根据excel文件的结尾判断生成那种版本的excel,若未指定类型,自动归结为低版本excel
	 * 
	 * @param path 以.xls或xlsx结尾的文件路径
	 * @param list map类数据源集合
	 * @param subject 是否添加字段名称,默认true添加false不添加
	 */
	default boolean writeExcel(String path, List<Map<String, Object>> list, boolean subject) {
		return false;
	}

	/**
	 * 根据excel文件的结尾来自动判断生成那种版本的excel,若传的文件没有指定类型,自动归结为低版本excel
	 * 
	 * @param path 以.xls或xlsx结尾的文件路径
	 * @param list 数据源
	 */
	default boolean writeExcelAuto(String path, List<List<Object>> datas) {
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
	default boolean writeExcelAuto(List<List<List<Object>>> excel, String path) {
		Workbook workbook = generateWorkbook(path);
		return writeExcel(workbook, excel, path);
	}

	/**
	 * 写入一个xls结尾的excel文件,低版本的excel,Excel2003以前(包括2003)的版本
	 * 
	 * @param excel 数据源
	 * @param path 写入文件路径
	 */
	default boolean writeXLS(String path, List<List<Object>> datas) {
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
	default boolean writeXLS(List<List<List<Object>>> excel, String path) {
		return writeExcel(new HSSFWorkbook(), excel, path);
	}

	/**
	 * 写入一个xlsx结尾的excel文件,高版本的excel,Excel2007的版本
	 * 
	 * @param excel 数据源
	 * @param path 写入文件路径
	 */
	default boolean writeXLSX(String path, List<List<Object>> datas) {
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
	default boolean writeXLSX(List<List<List<Object>>> excel, String path) {
		return writeExcel(new XSSFWorkbook(), excel, path);
	}

	/**
	 * 将数据写入一个excel表中,表以低版本为主,即以xls结尾,默认无字段栏
	 * 
	 * @param excel 数据源
	 * @param path 写入文件路径
	 */
	default boolean writeExcel(Workbook wb, List<List<List<Object>>> datas, String path) {
		return writeExcel(wb, datas, path, null);
	}

	default boolean writeExcel(Workbook wb, List<List<List<Object>>> excel, String path, CellStyle cellStyle) {
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
		return null;
	}

	/**
	 * 读取excel表格数据,默认表格中第一行是key值,且可以使用,不计入数据,从第2行第1列读取数据
	 * 
	 * @param is 输入流
	 * @return 结果集
	 */
	default List<Map<String, Object>> readExcel(InputStream is) {
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
	default List<Map<String, Object>> readExcel(InputStream is, boolean firstUse, List<String> titles) {
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
	default List<Map<String, Object>> readExcel(InputStream is, boolean firstUse, List<String> titles, int beginRow) {
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
	default List<Map<String, Object>> readExcel(InputStream is, boolean firstUse, List<String> titles, int beginRow,
			int beginCol) {
		return null;
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

	static void setCellValue(Row row, int i, Object value) {
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