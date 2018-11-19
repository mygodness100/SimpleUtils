package com.wy.db;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import com.wy.enums.Java2SqlEnum;

/**
 * 数据库配置文件
 * @author paradiseWy
 */
public class DBConfig {
	public static final String COLUMN_SQL = "select * from {0}";
	// 需要生成的文件地址0资源文件路径1分隔符2包路径3分隔符4文件名
	public static final String FILE_DES = "{0}{1}{2}{3}{4}";

	public static final String FILE_ = null;

	/**
	 * 默认java类型和sql类型对照关系
	 */
	public static final HashMap<String, List<String>> JAVA_TO_SQL = new HashMap<String, List<String>>() {
		private static final long serialVersionUID = 1L;
		{
			put("Integer", Java2SqlEnum.INTEGER.getSqlType());
			put("Long", Java2SqlEnum.LONG.getSqlType());
			put("Double", Java2SqlEnum.DOUBLE.getSqlType());
			put("Boolean", Java2SqlEnum.BOOLEAN.getSqlType());
			put("String", Java2SqlEnum.STRING.getSqlType());
			put("Date", Java2SqlEnum.DATE.getSqlType());
			put("byte[]", Java2SqlEnum.BYTES.getSqlType());
			put("BigDecimal", Java2SqlEnum.BIGDECIMAL.getSqlType());
		}
	};

	/**
	 * 默认生成mvc相关文件的配置
	 */
	public static final HashMap<String, Object> MVC_CONFIG = new HashMap<String, Object>() {
		private static final long serialVersionUID = 1L;
		{
			put("project_entity", "src/main/java");
			put("project_dao", "src/main/java");
			put("project_mapper", "src/main/java");
			put("project_xml", "src/main/java");
			put("project_service", "src/main/java");
			put("project_controller", "src/main/java");
			put("package_entity", "");
			put("package_dao", "");
			put("package_mapper", "");
			put("package_xml", "");
			put("package_service", "");
			put("package_controller", "");
			put("base_entity", "");
			put("base_dao", "");
			put("base_mapper", "");
			put("base_xml", "");
			put("base_service", "");
			put("base_controller", "");
		}
	};

	/**
	 * 若存在配置文件,则使用配置文件的对应关系,若不存在,则直接使用默认对应关系
	 */
	static {
		Properties props = new Properties();
		try (InputStream is = DBUtils.class.getClassLoader()
				.getResourceAsStream("generator/java2sql.properties");
				InputStream db = DBUtils.class.getClassLoader()
						.getResourceAsStream("generator/generator.properties");) {
			// 加载java和sql类型的对照文件
			if (!Objects.isNull(is)) {
				props.clear();
				props.load(is);
				for (Map.Entry<Object, Object> entry : props.entrySet()) {
					if (!Objects.isNull(entry.getValue())) {
						if ("bytes".equals((String) entry.getKey())) {
							JAVA_TO_SQL.put("byte[]",
									Arrays.asList(((String) entry.getValue()).split(",")));
						} else {
							JAVA_TO_SQL.put((String) entry.getKey(),
									Arrays.asList(((String) entry.getValue()).split(",")));
						}
					}
				}
			}
			// 加载生成mvc文件的配置文件
			if (!Objects.isNull(db)) {
				props.clear();
				props.load(db);

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}