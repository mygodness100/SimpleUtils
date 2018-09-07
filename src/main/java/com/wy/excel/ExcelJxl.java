package com.wy.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.wy.utils.ListUtils;
import com.wy.utils.StrUtils;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * excel操作,jxl对图片的支持有限,仅支持png格式
 * @author 万杨
 */
public class ExcelJxl {

	/**
	 * 读取指定路径的xsl文件,可以是远程文件
	 */
	public static ExcelModel readExcel(String url) {
		InputStream is = null;
		ExcelModel model = new ExcelModel();
		try {
			if (url.startsWith("http://") || url.startsWith("https://")) {
				URL _url = new URL(url);
				HttpURLConnection conn = (HttpURLConnection) _url.openConnection();
				conn.connect();
				is = conn.getInputStream();
			} else {
				File file = new File(url);
				if (file.exists()) {
					is = new FileInputStream(file);
				} else {
					throw new Exception("文件不存在");
				}
			}
			Workbook wb = Workbook.getWorkbook(is);
			Sheet[] sheet = wb.getSheets(); // 获得excel所有表区间,可通过下标或sheet名字
			List<ExcelPage> pages = new ArrayList<>();
			for (int s = 0; s < sheet.length; s++) { // 所有表区间循环
				ExcelPage page = new ExcelPage();
				List<ExcelRow> rows = new ArrayList<ExcelRow>();
				for (int r = 0; r < sheet[s].getRows(); r++) { // 所有行循环
					ExcelRow row = new ExcelRow();
					List<String> content = new ArrayList<>();
					for (int c = 0; c < sheet[s].getColumns(); c++) { // 所有列循环
						content.add(sheet[s].getCell(c, r).getContents());
					}
					row.setColDatas(content);
					rows.add(row);
				}
				page.setRowDatas(rows);
				pages.add(page);
			}
			model.setPageDatas(pages);
			wb.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return model;
	}
	
	/**
	 * 从输入流中读取数据,写入excel中
	 */
	public static ExcelModel readExcel(InputStream is) {
		ExcelModel model = new ExcelModel();
		Workbook wb = null;
		try {
			wb = Workbook.getWorkbook(is);
		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Sheet[] sheet = wb.getSheets(); // 获得excel所有表区间,可通过下标或sheet名字
		List<ExcelPage> pages = new ArrayList<>();
		for (int s = 0; s < sheet.length; s++) { // 所有表区间循环
			ExcelPage page = new ExcelPage();
			List<ExcelRow> rows = new ArrayList<ExcelRow>();
			for (int r = 0; r < sheet[s].getRows(); r++) { // 所有行循环
				ExcelRow row = new ExcelRow();
				List<String> content = new ArrayList<>();
				for (int c = 0; c < sheet[s].getColumns(); c++) { // 所有列循环
					content.add(sheet[s].getCell(c, r).getContents());
				}
				row.setColDatas(content);
				rows.add(row);
			}
			page.setRowDatas(rows);
			pages.add(page);
		}
		model.setPageDatas(pages);
		wb.close();
		return model;
	}
	
	/**
	 * 将实体类传入,并传入一个地址
	 * @param t			实体类的list集合
	 * @param path		数据存放地址
	 */
	public static <T> boolean writeExcel(List<T> t,String path) {
		if(StrUtils.isBlank(path) || ListUtils.isEmpty(t)) {
			return false;
		}
		try {
			Class<?> clazz = t.getClass();
			if(clazz.isSynthetic()) {			//判断是否为复合类,此处判断是否为pojo
				Field[] fields = clazz.getDeclaredFields();
				List<String> row = new ArrayList<>();
				List<List<String>> col = new ArrayList<>();
				List<List<List<String>>> page = new ArrayList<>();
				for(int i=0;i<t.size();i++) {
					for(Field field : fields) {
						row.add(field.get(t).toString());					
					}
					col.add(row);
				}
				page.add(col);
				writeExcel(path,page);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 将数据写入实体文件中,最好是request.getServletContext().getRealPath(File.separator)产生的路径,可返回文件名让前端调用查看
	 * @param path		实体文件地址
	 * @param excel	List<List<List<String>>>数据
	 */
	public static void writeExcel(String path,List<List<List<String>>> excel){
		try {
			if(!path.endsWith(".xls")) {
				path+=".xls";
			}
			File file = new File(path);
			if(!file.exists()) {
				file.getParentFile().mkdirs();
			}
			WritableWorkbook book = Workbook.createWorkbook(new File(path));
			writeExcel(book, excel);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void writeExcel(OutputStream os,List<List<List<String>>> excel){
		try {
			WritableWorkbook book = Workbook.createWorkbook(os);
			writeExcel(book, excel);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeExcel(WritableWorkbook book,List<List<List<String>>> excel){
		try {
			for (int page = 0; page < excel.size(); page++) {
				//创建表单
				WritableSheet sheet = book.createSheet("第" + page + "页", page);
				//行循环
				for (int row = 0; row < excel.get(page).size(); row++) {
					//列循环
					for (int col = 0; col < excel.get(page).get(row).size(); col++) {
						Label label = new Label(col, row,
								excel.get(page).get(row).get(col));
						//将定义好的单元格添加到工作表中
						sheet.addCell(label);
					}
				}
			}
			book.write();
			book.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 利用文件来写excel,写入后的数据将直接影响文件
	 */
	public static void writeExcel(String path,ExcelModel excel) {
		try {
			WritableWorkbook book = Workbook.createWorkbook(new File(path));
			writeExcel(book, excel);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 利用输出流写excel,写入后的数据将直接影响输出流
	 */
	public static void writeExcel(OutputStream os,ExcelModel excel) {
		try {
			WritableWorkbook book = Workbook.createWorkbook(os);
			writeExcel(book, excel);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 写xsl文件
	 */
	public static void writeExcel(WritableWorkbook book, ExcelModel excel) {
		try {
			for (int page = 0; page < excel.getSize(); page++) {
				//创建表单
				WritableSheet sheet = book.createSheet("第" + page + "页", page);
				//行循环
				for (int row = 0; row < excel.getPageDatas().get(page).getSize(); row++) {
					//列循环
					for (int col = 0; col < excel.getPageDatas().get(page).getRowDatas().get(row).getSize(); col++) {
						//添加单元格样式
//						WritableCellFormat wcf = new WritableCellFormat();
//						wcf.setAlignment(Alignment.CENTRE);
//						wcf.setBorder(Border.BOTTOM, BorderLineStyle.DASH_DOT);//设置边框线
//						Label label = new Label(col,row,"content",wcf);
//						sheet.addCell(label);
						//单独设置单元格字体
//						WritableFont wf = new WritableFont(WritableFont.createFont("草书"),20);
//						WritableCellFormat wcf = new WritableCellFormat(wf);
//						new Label(col,row,"content",wcf);
						//创建一个带字体的单元格,参数字体样式,大小,是否加粗,未知,是否有下划线,颜色,未知
//						WritableFont wf = new WritableFont(WritableFont.TIMES, 18, WritableFont.NO_BOLD,
//								false, UnderlineStyle.NO_UNDERLINE, Colour.YELLOW,null);
//						WritableCellFormat wcf = new WritableCellFormat(wf);
//						Label label = new Label(col,row,excel.getPageDatas().get(page).getRowDatas().get(row)
//								.getColDatas().get(col),wcf);
						//添加一个带时间的单元格
//						DateTime dt = new DateTime(col,row,new Date());
//						sheet.addCell(dt);
						//添加公式单元格
//						Formula fm = new Formula(col, row, "Sum(A1:A9)");
//						sheet.addCell(fm);
						//添加图片
//						WritableImage wi = new WritableImage(col, row, 20, 20, new File(path));
//						sheet.addImage(wi);
						//合并单元格,合并了m-x+1列,n-y+1行
//						sheet.mergeCells(x, y, m, n);
						//创建一个单元格,参数是行,列,值
						Label label = new Label(col, row,
								excel.getPageDatas().get(page).getRowDatas().get(row).getColDatas().get(col));
						//将定义好的单元格添加到工作表中
						sheet.addCell(label);
					}
				}
			}
			// 写入数据并关闭文件
			book.write();
			book.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
