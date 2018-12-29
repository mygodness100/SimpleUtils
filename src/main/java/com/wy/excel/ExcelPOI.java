package com.wy.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

/**
 * 虽然不喜欢韩国棒子写是jxl,但是apache导入的包太多,若是简单的只是导入导出excel,用jxl即可
 * apache操作excel的包,HSSFWorkbook是操作Excel2003以前(包括2003)的版本,扩展名是.xls
 * 需要导入包:poi-3.17,commons-codec-1.10,commons-collections4-4.1,commons-logging-1.2,log4j-1.2.17
 * XSSFWorkbook是操作Excel2007的版本,扩展名是.xlsx
 * xmlbeans-2.6.0,curvesapi-1.04,poi-ooxml-schemas-3.17,poi-ooxml-3.17
 * 
 * @author wanyang
 */
public class ExcelPOI {
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
		try {
			if (StrUtils.isBlank(path) || ListUtils.isBlank(list)) {
				return false;
			}
			List<String> row = new ArrayList<>();
			List<List<String>> col = new ArrayList<>();
			List<List<List<String>>> excel = new ArrayList<>();
			List<String> subjects = new ArrayList<String>();
			for (int i = 0; i < list.size(); i++) {
				T t = list.get(i);
				Class<?> clazz = t.getClass();
				// 判断是否为复合类,此处判断是否为pojo
				if (clazz.isSynthetic()) {
					Field[] fields = clazz.getDeclaredFields();
					for (Field field : fields) {
						// 先添加第一行的字段
						if (subject) {
							subjects.add(field.getName());
						}
						field.isAccessible();
						row.add(field.get(t).toString());
					}
					// 判断是否是一个map映射
				} else if (clazz == Map.class) {
					// 先将map里的字段全部取出来放进一个list中,保证数据的顺序不会出错
					// 同上的方法将数据加入到excel表中
				}
				subject = false;
				col.add(row);
			}
			col.add(0, subjects);
			excel.add(col);
			return createExcelAuto(excel, path);
		} catch (Exception e) {
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
	public boolean writeExcelAuto(String path, List<List<String>> list) {
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
		if (ClassUtils.isEmpty(path)) {
			return false;
		}
		Workbook wb = null;
		if (path.endsWith(".xlsx")) {
			wb = new XSSFWorkbook();
		} else if (path.endsWith(".xls")) {
			wb = new HSSFWorkbook();
		} else if (!path.contains(".")) {
			path += ".xls";
			wb = new HSSFWorkbook();
		} else {
			return false;
		}
		return writeExcel(wb, excel, path);
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
		try (FileOutputStream fos = new FileOutputStream(path);) {
			// 需判断文件是否存在,判断文件结尾
			// TODO
			for (int page = 0; page < excel.size(); page++) {
				// 创建表单
				Sheet sheet = wb.createSheet();
				// 行循环
				for (int row = 0; row < excel.get(page).size(); row++) {
					Row r = sheet.createRow(row);
					// if (row % 2 == 0) {
					// r.setHeight((short) 0x249);
					// }
					// 列循环
					for (int col = 0; col < excel.get(page).get(row).size(); col++) {
						Cell c = r.createCell(col);
						c.setCellValue(excel.get(page).get(row).get(col));
						if (cellStyle != null) {
							c.setCellStyle(cellStyle);
						}
					}
				}
				wb.setSheetName(page, "第" + page + "页");
			}
			wb.write(fos);
			wb.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (wb != null) {
				try {
					wb.close();
				} catch (Exception e2) {
					e2.printStackTrace();
					return false;
				}
			}
		}
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
		// TODO
		File file = new File(path);
		if (!file.exists()) {
			throw new ResultException("文件不存在");
		}
		InputStream is = null;
		Workbook wb = null;
		try {
			is = new FileInputStream(file);
			if (file.getName().endsWith(".xls")) {
				wb = new HSSFWorkbook(is);
			} else {
				wb = new XSSFWorkbook(is);
			}
			int sheets = wb.getNumberOfSheets();
			List<Map<String, Object>> res = new ArrayList<>();
			for (int i = 0; i < sheets; i++) {
				List<Map<String, Object>> handlerRow = handlerRow(wb.getSheetAt(i));
				if (handlerRow != null) {
					res.addAll(handlerRow);
				}
			}
			return res;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (wb != null) {
				try {
					wb.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
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