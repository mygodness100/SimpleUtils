package com.wy.excel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.wy.common.PropConverter;
import com.wy.excel.annotation.Excel;
import com.wy.excel.annotation.ExcelColumn;
import com.wy.excel.enums.ExcelAction;
import com.wy.result.ResultException;
import com.wy.utils.ListUtils;
import com.wy.utils.StrUtils;

import io.swagger.annotations.ApiModelProperty;

/**
 * 实体类Excel工具类
 * 
 * @author ParadiseWY
 * @date 2020-11-30 17:06:09
 * @git {@link https://github.com/mygodness100}
 */
public class ExcelModelUtils implements ExcelUtils {

	private ExcelModelUtils() {}

	private static class Inner {

		private static final ExcelModelUtils INSTANCE = new ExcelModelUtils();
	}

	public static ExcelModelUtils getInstance() {
		return Inner.INSTANCE;
	}

	/**
	 * 处理每一个sheet页
	 *
	 * @param <T> 泛型
	 * @param index sheet页下标,从1开始
	 * @param datas 实体类数据源集合
	 * @param path 文件路径,若文件路径不带后缀,则默认后缀为.xls
	 * @param sheetMax 每个sheet页的最大写入行数,默认65535
	 * @param subject 是否添加标题,默认true添加false不添加,真实数据从第2行开始写入
	 */
	@Override
	public <T> void writeSheet(int index, List<T> datas, String path, int sheetMax, boolean subject) {
		try (FileOutputStream fos = new FileOutputStream(path);
				Workbook workbook = ExcelUtils.generateWorkbook(path);) {
			Sheet sheet = workbook.createSheet();
			// 若有标题,从第2行开始写数据
			int beginRow = subject ? 1 : 0;
			int startNo = (index - 1) * sheetMax;
			int endNo = Math.min(startNo + sheetMax, datas.size());
			Class<?> clazz = datas.get(0).getClass();
			List<Field> fields = handleClass(clazz, ExcelAction.EXPORT);
			for (int i = startNo; i < endNo; i++) {
				Row row = sheet.createRow(beginRow);
				T t = datas.get(i);
				for (int j = 0; j < fields.size(); j++) {
					handleCell(row.createCell(j), t, fields.get(j));
				}
				beginRow++;
			}
			// 若标题存在,生成第1行的标题
			if (subject) {
				genereateTitle(sheet, fields);
			}
			workbook.write(fos);
		} catch (IOException | SecurityException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 处理每一个sheet页,主要是将数据导出到输出流中,提供下载
	 *
	 * @param <T> 泛型
	 * @param index sheet页下标,从1开始
	 * @param datas 实体类数据源集合
	 * @param os 输出流,默认是.xls结尾的文件流
	 * @param sheetMax 每个sheet页的最大写入行数,默认65535
	 * @param subject 是否添加标题,默认true添加false不添加,真实数据从第2行开始写入
	 */
	@Override
	public <T> void handleSheet(int index, List<T> datas, OutputStream os, int sheetMax, boolean subject) {
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
							ExcelUtils.setCellValue(r, i,
									PropConverter.getMember(select.getEnumConstants(), field.get(data)));
							i++;
							continue;
						}
					}
					ExcelUtils.setCellValue(r, i, field.get(data));
					i++;
				}
			}
			book.write(os);
		} catch (IOException | IllegalArgumentException | IllegalAccessException | SecurityException e) {
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
	private <T> List<Field> handleClass(Class<T> clazz, ExcelAction excelAction) {
		if (excelAction != ExcelAction.IMPORT || excelAction != ExcelAction.EXPORT) {
			throw new ResultException("只能是导入或导出类型");
		}
		// 所有待处理字段集合
		List<Field> result = new ArrayList<>();
		// 处理父类@Excel
		handleClass(result, clazz.getSuperclass(), excelAction);
		// 处理本类@Excel
		handleClass(result, clazz, excelAction);
		return result.stream().filter(t -> {
			// 剔除静态变量和常量
			if (Modifier.isStatic(t.getModifiers()) || Modifier.isFinal(t.getModifiers())) {
				return false;
			}
			if (t.isAnnotationPresent(ExcelColumn.class)) {
				if (t.getAnnotation(ExcelColumn.class).excelAction() == excelAction
						|| t.getAnnotation(ExcelColumn.class).excelAction() == ExcelAction.ALL) {
					return true;
				}
			}
			return true;
		}).collect(Collectors.toList());
	}

	private void handleClass(List<Field> result, Class<?> clazz, ExcelAction excelAction) {
		boolean flag = true;
		if (clazz.isAnnotationPresent(Excel.class)) {
			Excel annotation = clazz.getAnnotation(Excel.class);
			if (annotation.excelAction() == ExcelAction.NOTHING
					|| (annotation.excelAction() != ExcelAction.ALL && annotation.excelAction() != excelAction)) {
				flag = false;
			}
		}
		if (flag) {
			Field[] fields = clazz.getDeclaredFields();
			if (ArrayUtils.isNotEmpty(fields)) {
				result.addAll(Arrays.asList(fields));
			}
		}
	}

	/**
	 * 处理每一个单元格
	 * 
	 * @param <T> 泛型
	 * @param cell 单元格
	 * @param t 需要写入到单元格的数据
	 * @param field 当前字段
	 */
	@Override
	public <T> void handleCell(Cell cell, T t, Field field) {
		field.setAccessible(true);
		try {
			ExcelUtils.setCellValue(cell, field.get(t));
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 处理第一行的标题
	 * 
	 * @param sheet sheet页
	 * @param fields 第一行的字段
	 */
	public void genereateTitle(Sheet sheet, List<Field> fields) {
		Row first = sheet.createRow(0);
		for (int i = 0; i < fields.size(); i++) {
			Cell cell = first.createCell(i);
			Field field = fields.get(i);
			field.setAccessible(true);
			String title = field.getName();
			if (field.isAnnotationPresent(ApiModelProperty.class)) {
				title = StrUtils.isBlank(field.getAnnotation(ApiModelProperty.class).value()) ? title
						: field.getAnnotation(ApiModelProperty.class).value();
			}
			if (field.isAnnotationPresent(ExcelColumn.class)) {
				title = StrUtils.isBlank(field.getAnnotation(ExcelColumn.class).value()) ? title
						: field.getAnnotation(ExcelColumn.class).value();
			}
			cell.setCellValue(title);
		}
	}
}