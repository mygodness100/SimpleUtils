package com.wy.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.common.io.Files;
import com.wy.common.Constant;
import com.wy.enums.TipsEnum;
import com.wy.io.IOUtils;
import com.wy.result.ResultException;
import com.wy.utils.DateUtils;
import com.wy.utils.ListUtils;
import com.wy.utils.NumUtils;
import com.wy.utils.StrUtils;

/**
 * apache操作excel的包,HSSFWorkbook是操作Excel2003以前(包括2003)的版本,扩展名是.xls
 * 需要导入包:poi-3.17,commons-codec-1.10,commons-collections4-4.1,commons-logging-1.2,log4j-1.2.17
 * XSSFWorkbook是操作Excel2007的版本,扩展名是.xlsx
 * xmlbeans-2.6.0,curvesapi-1.04,poi-ooxml-schemas-3.17,poi-ooxml-3.17
 * 
 * @author ParadiseWY
 * @date 2020-11-23 16:11:10
 * @git {@link https://github.com/mygodness100}
 */
public interface ExcelUtils {

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
	static Workbook generateReadWorkbook(String path) {
		File file = new File(path);
		if (!file.exists()) {
			throw new ResultException("文件不存在");
		}
		Workbook workbook = null;
		try {
			if (path.endsWith(".xlsx")) {
				workbook = new XSSFWorkbook(new FileInputStream(file));
			} else {
				workbook = new HSSFWorkbook(new FileInputStream(file));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return workbook;
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
		switch (cell.getCellType()) {
		case BOOLEAN:
			return cell.getBooleanCellValue();
		case NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				return cell.getDateCellValue();
			}
			return cell.getNumericCellValue();
		case STRING:
			return cell.getStringCellValue();
		case FORMULA:
			if (cell.getCachedFormulaResultType() == CellType.NUMERIC) {
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
	 * @param value 单元格值
	 */
	static void setCellValue(Cell cell, Object value) {
		Class<? extends Object> clazz = value.getClass();
		if (clazz == Boolean.class || clazz == boolean.class) {
			cell.setCellValue(Boolean.valueOf(value.toString()));
		} else if (NumberUtils.isCreatable(value.toString())) {
			cell.setCellValue(Double.valueOf(value.toString()));
		} else if (clazz == Date.class) {
			cell.setCellValue((Date) value);
		} else {
			cell.setCellValue(value.toString());
		}
	}

	/**
	 * 根据值创建不同类型的Cell并设置值
	 * 
	 * @param row 行
	 * @param index 行中的第几个cell
	 * @param value 值
	 */
	static void setCellValue(Row row, int index, Object value) {
		if (Objects.isNull(row)) {
			return;
		}
		if (Objects.isNull(value)) {
			row.createCell(index).setCellValue("");
			return;
		}
		Class<? extends Object> clazz = value.getClass();
		if (clazz == Boolean.class || clazz == boolean.class) {
			row.createCell(index, CellType.BOOLEAN).setCellValue(Boolean.valueOf(value.toString()));
		} else if (value instanceof Number) {
			row.createCell(index, CellType.NUMERIC).setCellValue(Double.valueOf(value.toString()));
		} else if (value instanceof Date) {
			row.createCell(index).setCellValue(DateUtils.formatDateTime((Date) value));
		} else {
			row.createCell(index, CellType.STRING).setCellValue(String.valueOf(value));
		}
	}

	/**
	 * 将数据写入到excel文件中:<br>
	 * 1.若不指定文件后缀,则默认文件后缀为.xls<br>
	 * 2.若文件不存在,则创建<br>
	 * 3.若文件已经存在,则从第一行开始写数据,且第一行为标题,真实数据从第2行开始<br>
	 * 4.每个sheet页写入的最大行数默认为65535
	 * 
	 * @param <T> 泛型数据类型
	 * @param datas 实体类数据源集合或Map数据源集合
	 * @param path 文件路径,若文件路径不带后缀,则默认后缀为.xls
	 */
	default <T> void write(List<T> datas, String path) {
		write(datas, path, Constant.EXCEL_SHEET_MAX);
	}

	default <T> void write(List<T> datas, File file) {
		write(datas, file, Constant.EXCEL_SHEET_MAX);
	}

	/**
	 * 将数据写入到excel文件中:<br>
	 * 1.若不指定文件后缀,则默认文件后缀为.xls<br>
	 * 2.若文件不存在,则创建<br>
	 * 3.若文件已经存在,则从第一行开始写数据,且第一行为标题,真实数据从第2行开始<br>
	 * 
	 * @param <T> 泛型数据类型
	 * @param datas 实体类数据源集合或Map数据源集合
	 * @param path 文件路径,若文件路径不带后缀,则默认后缀为.xls
	 * @param sheetMax 每个sheet页的最大写入行数,默认65535
	 */
	default <T> void write(List<T> datas, String path, int sheetMax) {
		write(datas, path, sheetMax, true);
	}

	default <T> void write(List<T> datas, File file, int sheetMax) {
		write(datas, file, sheetMax, true);
	}

	/**
	 * 将数据写入到excel文件中:<br>
	 * 1.若不指定文件后缀,则默认文件后缀为.xls<br>
	 * 2.若文件不存在,则创建<br>
	 * 
	 * @param <T> 泛型数据类型
	 * @param datas 实体类数据源集合或Map数据源集合
	 * @param path 文件路径,若文件路径不带后缀,则默认后缀为.xls
	 * @param sheetMax 每个sheet页的最大写入行数,默认65535
	 * @param subject 是否添加标题,默认true添加false不添加,真实数据从第2行开始写入
	 */
	default <T> void write(List<T> datas, String path, int sheetMax, boolean subject) {
		writeSheet(datas, path, sheetMax, subject);
	}

	default <T> void write(List<T> datas, File file, int sheetMax, boolean subject) {
		writeSheet(datas, file, sheetMax, subject);
	}

	/**
	 * 参数检查,sheet最大数量检查
	 * 
	 * @param <T> 泛型数据类型
	 * @param datas 实体类数据源集合或Map数据源集合
	 * @param path 文件路径,若文件路径不带后缀,则默认后缀为.xls
	 * @param sheetMax 每个sheet页的最大写入行数,默认65535
	 * @param subject 是否添加标题,默认true添加false不添加,真实数据从第2行开始写入
	 */
	default <T> void writeSheet(List<T> datas, String path, int sheetMax, boolean subject) {
		if (StrUtils.isBlank(path)) {
			throw new ResultException(TipsEnum.TIP_LOG_ERROR.getMsg("excel写入文件路径不存在"));
		}
		writeSheet(datas, new File(path), sheetMax, subject);
	}

	default <T> void writeSheet(List<T> datas, File file, int sheetMax, boolean subject) {
		if (ListUtils.isBlank(datas)) {
			throw new ResultException(TipsEnum.TIP_LOG_INFO.getMsg("excel写入文件数据源为空"));
		}
		if (Objects.isNull(file)) {
			throw new ResultException(TipsEnum.TIP_LOG_ERROR.getMsg("excel写入文件不能为空"));
		}
		IOUtils.fileExists(file);
		sheetMax = sheetMax >= Constant.EXCEL_SHEET_MAX ? Constant.EXCEL_SHEET_MAX : sheetMax;
		long sheetNum = Math.round(NumUtils.div(datas.size(), sheetMax));
		for (int i = 1; i <= sheetNum; i++) {
			writeSheet(i, datas, file, sheetMax, subject);
		}
	}

	/**
	 * 处理每一个sheet页
	 *
	 * @param <T> 泛型
	 * @param index sheet页下标,从1开始
	 * @param datas 实体类数据源集合或Map数据源集合
	 * @param path 文件路径,若文件路径不带后缀,则默认后缀为.xls
	 * @param sheetMax 每个sheet页的最大写入行数,默认65535
	 * @param subject 是否添加标题,默认true添加false不添加,真实数据从第2行开始写入
	 */
	default <T> void writeSheet(int index, List<T> datas, String path, int sheetMax, boolean subject) {
		writeSheet(index, datas, new File(path), sheetMax, subject);
	}

	default <T> void writeSheet(int index, List<T> datas, File file, int sheetMax, boolean subject) {
		try (OutputStream fos = new FileOutputStream(file);) {
			handleSheet(index, datas, fos, sheetMax, subject);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ResultException("文件输出流初始化失败");
		}
	}

	/**
	 * 导出excel数据表格:<br>
	 * 1.默认导出的文件名:数据导出<br>
	 * 2.默认文件后缀为xls<br>
	 * 3.默认文件名字节数组为本地默认编码<br>
	 * 4.默认导出时文件名的编码为本地默认编码<br>
	 * 5.默认每个sheet页最大行数为65535<br>
	 * 6.默认每个sheet页都有标题
	 * 
	 * @param resp 响应
	 * @param datas 需要导出的数据
	 */
	default <T> void exportExcel(List<T> datas, HttpServletResponse resp) {
		exportExcel(datas, resp, Constant.EXCEL_FILE_NAME);
	}

	default <T> void exportExcel(List<T> datas, HttpServletResponse resp, String excelName) {
		exportExcel(datas, resp, excelName, Charset.defaultCharset());
	}

	default <T> void exportExcel(List<T> datas, HttpServletResponse resp, String excelName, Charset encode) {
		exportExcel(datas, resp, excelName, encode, Charset.defaultCharset());
	}

	default <T> void exportExcel(List<T> datas, HttpServletResponse resp, String excelName, Charset encode,
			Charset decode) {
		exportExcel(datas, resp, excelName, encode.displayName(), decode.displayName());
	}

	default <T> void exportExcel(List<T> datas, HttpServletResponse resp, String excelName, String encode,
			String decode) {
		exportExcel(datas, resp, excelName, encode, decode, Constant.EXCEL_SHEET_MAX);
	}

	default <T> void exportExcel(List<T> datas, HttpServletResponse resp, String excelName, String encode,
			String decode, int sheetMax) {
		exportExcel(datas, resp, excelName, encode, decode, sheetMax, true);
	}

	/**
	 * 导出excel到前端页面下载,若是中文文件名,默认编码时要用gbk,解码时要用iso8859-1
	 * 
	 * @param <T> 泛型
	 * @param datas 导出的数据
	 * @param resp 响应
	 * @param excelName 文件名,可不带后缀,默认后缀为xls
	 * @param encode 文件名编码的字符集
	 * @param decode 文件名解压的字符集
	 * @param sheetMax 每个sheet页的最大写入行数,默认65535
	 * @param subject 是否添加标题,默认true添加false不添加,真实数据从第2行开始写入
	 */
	default <T> void exportExcel(List<T> datas, HttpServletResponse resp, String excelName, String encode,
			String decode, int sheetMax, boolean subject) {
		resp.setContentType("application/download");
		try (OutputStream os = resp.getOutputStream();) {
			// 处理文件名后缀
			String fileExtension = Files.getFileExtension(excelName);
			if (StrUtils.isBlank(fileExtension)) {
				excelName += "." + Constant.EXCEL_FILE_NAME_SUFFIX;
			}
			// 处理文件编码
			resp.setHeader("Content-Disposition",
					"attchament;filename=" + new String(excelName.getBytes(encode), Charset.forName(decode)));
			// 处理每个sheet页最大行数据
			sheetMax = sheetMax >= Constant.EXCEL_SHEET_MAX ? Constant.EXCEL_SHEET_MAX : sheetMax;
			long sheetNum = Math.round(NumUtils.div(datas.size(), sheetMax));
			for (int i = 1; i <= sheetNum; i++) {
				handleSheet(i, datas, os, sheetMax, subject);
			}
		} catch (IOException e) {
			throw new ResultException("导出失败", e);
		}
	}

	/**
	 * 文件导出到输入流中生成excel文件
	 * 
	 * @param <T> 泛型
	 * @param index sheet页下标,从1开始
	 * @param datas 实体类数据源集合或Map数据源集合
	 * @param os 输入流
	 * @param sheetMax 每个sheet页的最大写入行数,默认65535
	 * @param subject 是否添加标题,默认true添加false不添加,真实数据从第2行开始写入
	 */
	<T> void handleSheet(int index, List<T> datas, OutputStream os, int sheetMax, boolean subject);

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
}