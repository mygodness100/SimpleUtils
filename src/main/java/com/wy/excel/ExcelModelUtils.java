package com.wy.excel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.wy.common.Constant;
import com.wy.enums.TipsEnum;
import com.wy.excel.annotation.Excel;
import com.wy.excel.annotation.ExcelRelated;
import com.wy.excel.enums.ExcelAction;
import com.wy.result.ResultException;
import com.wy.utils.StrUtils;

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

	@Override
	public ExcelUtils newExcelUtils() {
		return getInstance();
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
	@Override
	public <T> void handleSheet(int index, List<T> list, String path, boolean subject) {
		try (FileOutputStream fos = new FileOutputStream(path);
				Workbook workbook = ExcelUtils.generateWorkbook(path);) {
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
	private <T> List<Field> handleClass(Class<T> clazz) {
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
	@Override
	public <T> void handleCell(Cell cell, T t, Field field) {
		field.setAccessible(true);
		try {
			if (field.isAnnotationPresent(ExcelRelated.class)) {
				handleRelatedCell(cell, t, field);
			} else {
				ExcelUtils.setCellValue(cell, field, field.get(t));
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
	@Override
	public <T> void handleRelatedCell(Cell cell, T t, Field field) {
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
					ExcelUtils.setCellValue(cell, declaredField, declaredField.get(related));
				}
			} else {
				for (int i = 0; i < columns.length; i++) {
					ExcelUtils.setCellValue(cell, null, null);
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
	@Override
	public void createFirst(Sheet sheet, List<Field> fields) {
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
}