package com.wy.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.wy.excel.ExcelUtils;

import lombok.Getter;
import lombok.Setter;

/**
 * @apiNote 注册节点
 * @author ParadiseWY
 * @date 2019年11月1日
 */
@Getter
@Setter
public class NodeInfo {

	private String provinceZh;

	private String province;

	private String sectionOwnerZh;

	private String sectionOwner;

	private String sectionBranchOwnerZh;

	private String sectionBranchOwner;

	private String sectionZh;

	private String section;

	private String tollStationZh;

	private String tollStationPy;

	private String tollStation;
	
	public static void main(String[] args) {

		List<Map<String, Object>> list = ExcelUtils.readExcel("f://nodeinfos/tags.xlsx");
		for (Map<String, Object> data : list) {
			System.out.println(data);
			String filename = data.get("pinying").toString() + "_" + data.get("收费站") + "_"
					+ data.get("tollStation");
			File file = new File("f://nodeinfos//" + filename);
			if (file.exists()) {
				file.delete();
			}
			try (OutputStream os = new FileOutputStream(file);
					OutputStreamWriter osWriter = new OutputStreamWriter(os);
					BufferedWriter bw = new BufferedWriter(osWriter);) {
				writeNewLine(bw, "{");
				bw.write("	");
				writeNewLine(bw, "\"description\": \"\",");
				bw.write("	");
				writeNewLine(bw, "\"enable_gpu\": false,");
				bw.write("	");
				writeNewLine(bw, "\"edge_config\": {");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "\"DISABLE_MODULES\": \"edgefunction,twin,edgemesh\",");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "\"MONITOR_INTERVAL\": \"60\"");
				bw.write("	");
				writeNewLine(bw, "},");
				bw.write("	");
				writeNewLine(bw, "\"log_configs\": [");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "{");
				bw.write("	");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "\"component\": \"system\",");
				bw.write("	");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "\"type\": \"LTS\",");
				bw.write("	");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "\"level\": \"error\",");
				bw.write("	");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "\"size\": 100,");
				bw.write("	");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "\"rotate_num\": 5,");
				bw.write("	");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "\"rotate_period\": \"daily\"");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "},");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "{");
				bw.write("	");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "\"component\": \"app\",");
				bw.write("	");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "\"type\": \"LTS\",");
				bw.write("	");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "\"level\": \"off\",");
				bw.write("	");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "\"size\": 100,");
				bw.write("	");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "\"rotate_num\": 5,");
				bw.write("	");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "\"rotate_period\": \"daily\"");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "}");
				bw.write("	");
				writeNewLine(bw, "],");
				bw.write("	");
				writeNewLine(bw, "\"tags\": [");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "{");
				bw.write("	");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "\"key\": \"province\",");
				bw.write("	");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "\"value\": \"" + data.get("province") + "\"");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "},");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "{");
				bw.write("	");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "\"key\": \"sectionOwner\",");
				bw.write("	");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "\"value\": \"" + data.get("sectionOwner") + "\"");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "},");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "{");
				bw.write("	");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "\"key\": \"sectionBranchOwner\",");
				bw.write("	");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "\"value\": \"" + data.get("sectionBranchOwner") + "\"");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "},");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "{");
				bw.write("	");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "\"key\": \"section\",");
				bw.write("	");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "\"value\": \"" + data.get("section") + "\"");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "},");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "{");
				bw.write("	");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "\"key\": \"tollStation\",");
				bw.write("	");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "\"value\": \"" + data.get("tollStation") + "\"");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "},");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "{");
				bw.write("	");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "\"key\": \"masterSlave\",");
				bw.write("	");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "\"value\": \""
						+ (Objects.isNull(data.get("masterSlave")) ? "" : data.get("masterSlave"))
						+ "\"");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "},");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "{");
				bw.write("	");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "\"key\": \"frontBack\",");
				bw.write("	");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "\"value\": \""
						+ (Objects.isNull(data.get("frontBack")) ? "" : data.get("frontBack"))
						+ "\"");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "},");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "{");
				bw.write("	");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "\"key\": \"arch\",");
				bw.write("	");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "\"value\": \""
						+ (Objects.isNull(data.get("arch")) ? "" : data.get("arch")) + "\"");
				bw.write("	");
				bw.write("	");
				writeNewLine(bw, "}");
				bw.write("	");
				writeNewLine(bw, "]");
				writeNewLine(bw, "}");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println(list);
	}
	
	public static void writeNewLine(BufferedWriter bw, String str) {
		try {
			bw.write(str);
			bw.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}