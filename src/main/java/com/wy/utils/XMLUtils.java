package com.wy.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.ElementHandler;
import org.dom4j.ElementPath;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.DocumentResult;
import org.dom4j.io.DocumentSource;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.wy.common.Encoding;

/**
 * xml解析
 * 
 * @author 万杨
 */
@SuppressWarnings("unchecked")
public class XMLUtils {
	// 各种结点分类处理
	private static List<Node> nodes = new ArrayList<>();
	private static List<Element> elements = new ArrayList<>();
	private static Set<Namespace> namespaces = new HashSet<>();
	private static List<Attribute> attributes = new ArrayList<>();

	private XMLUtils() {

	}

	/**
	 * 解析url的xml文档
	 * 
	 * @return Document文档模型
	 */
	public static final Document parse(URL url) {
		SAXReader reader = new SAXReader();
		// SAXReader reader = new SAXReader(false);//不进行dtd验证,用于没网的时候本地验证
		try {
			return reader.read(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 解析本地的xml,默认不解析注释
	 * 
	 * @return Document文档模型
	 */
	public static final Document parse(String filePath) {
		return parse(new File(filePath), true);
	}

	/**
	 * 解析本地的xml
	 * 
	 * @param flag为true不解析注释,false解析注释
	 * @return Document文档模型
	 */
	public static final Document parse(String filePath, boolean flag) {
		return parse(new File(filePath), flag);
	}

	public static final Document parse(File filePath) {
		return parse(filePath, true);
	}

	/**
	 * 解析本地的xml
	 * 
	 * @param flag为true不解析注释,false解析注释
	 * @return Document文档模型
	 */
	public static final Document parse(File filePath, boolean flag) {
		try {
			if (!filePath.exists()) {
				throw new Exception("文件不存在");
			}
			return parse(new FileInputStream(filePath), flag);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 对传入的输入流进行解析,默认不解析注释
	 * 
	 * @param is
	 *            输入流
	 */
	public static final Document parse(InputStream is) {
		return parse(is, true);
	}

	/**
	 * 解析本地的xml
	 * 
	 * @param flag为true不解析注释,false解析注释
	 * @return Document文档模型
	 */
	public static final Document parse(InputStream is, boolean flag) {
		try {
			if (is == null) {
				return null;
			}
			SAXReader reader = new SAXReader();
			reader.setIgnoreComments(flag);
			return reader.read(is);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 解析大文件的xml,未实现
	 * 
	 * @Fixed
	 * @param path
	 */
	public static void parseLarge(String path) {
		SAXReader reader = new SAXReader();
		reader.addHandler(path, new ElementHandler() {
			@Override
			public void onStart(ElementPath elementPath) {
				Element element = elementPath.getCurrent();
				System.out.println(element);
				System.out.println(element.getName());
				System.out.println(element.getNodeTypeName());
				if (!"bean".equals(element.getName())) {
					element.detach();
				}
			}

			@Override
			public void onEnd(ElementPath elementPath) {
				Element current = elementPath.getCurrent();
				System.out.println(current);
			}
		});
		try {
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * 遍历解析文档,不需要为每一个结点创建迭代对象
	 */
	public static final void treeWalk(Document document) {
		// 清空各个list结点里的元素
		clearAllNodes();
		treeWalk(document.getRootElement());
	}

	/**
	 * 清空所有node类型中的值
	 */
	private static final void clearAllNodes() {
		nodes = nodes.size() == 0 ? nodes : new ArrayList<Node>();
		elements = elements.size() == 0 ? elements : new ArrayList<Element>();
		namespaces = namespaces.size() == 0 ? namespaces : new HashSet<Namespace>();
		attributes = attributes.size() == 0 ? attributes : new ArrayList<Attribute>();
	}

	/**
	 * 遍历解析元素,快速循环
	 */
	private static final void treeWalk(Element element) {
		elements.add(element);
		attributes.addAll(element.attributes());
		if (ListUtils.isNotBlank(element.declaredNamespaces())) {
			namespaces.addAll(element.declaredNamespaces());
		}
		for (int i = 0, size = element.nodeCount(); i < size; i++) {
			Node node = element.node(i);
			nodes.add(node);
			if (node instanceof Element) {
				treeWalk((Element) node);
			}
		}
	}

	/**
	 * 获得单个标签的命名空间,若是没有指定命名空间前缀,以当前标签的名称为命名空间的前缀
	 */
	private static Map<String, Namespace> getNameSpace(Document doc) {
		Map<String, Namespace> namespace_prefix_ns = new HashMap<>();
		for (Namespace ns : namespaces) {
			String prefix = ns.getPrefix();
			if (StrUtils.isBlank(prefix)) {
				namespace_prefix_ns.put("default", ns);
			} else {
				namespace_prefix_ns.put(ns.getPrefix(), ns);
			}
		}
		return namespace_prefix_ns;
	}

	/**
	 * 对xpath进行分段处理,去掉空值,将xpath分段所对应的属性名值加上
	 * 
	 * @param attrs:xpath分段之后所对应的名值对,名不可为空,值可为空;名为空时,对应的xpath分段将不做任务处理
	 *            若分段表达式对应的一些名值对没有,则可传一个EMPTY@+一个随机数的值作为key,值可不传,如EMPTY@1234
	 * @Fixed 此处没有解决重复标签的属性名相同的问题
	 */
	private static final String createXPath(String xpath, LinkedHashMap<String, String> map) {
		// 处理attrs,LinkedHashMap是一个有序的集合
		List<String> attrs = new ArrayList<>();
		Iterator<Entry<String, String>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			String key = entry.getKey();
			// 对空属性做处理
			if (key.contains("EMPTY@")) {
				attrs.add("");
			} else {
				attrs.add(createAttrVal(key, entry.getValue()));
			}
		}
		return createXPath(xpath, attrs);
	}

	/**
	 * 对xpath进行分段处理,去掉空值,将xpath分段所对应的属性名加上
	 */
	private static final String createXPath(String xpath, List<String> attrs) {
		String[] tags = xpath.split("/+|//+");
		// 利用Arrays.aslist转换来的数组无法进行增删操作,不构造函数重新赋值
		List<String> list = new ArrayList<>(Arrays.asList(tags));
		// 为了避免因为第一次切割产生空值,需要对空值进行剔除
		for (int i = list.size() - 1; i >= 0; i--) {
			if (StrUtils.isBlank(list.get(i))) {
				list.remove(i);
			}
		}
		// 将属性全部拼接到对应的位置,因为不能改变路径,暂时只能用笨办法,每次都截取字符串,改变匹配的字符串,依次截取下去
		List<String> desList = new ArrayList<>();
		int index = 0;
		for (int i = 0; i < list.size(); i++) {
			String tag = list.get(i);
			// 对应的属性名,防止数组越界
			String attr = attrs.size() > i ? attrs.get(i) : "";
			// 从上次的位置开始查找符合的标签
			int first = xpath.indexOf(tag, index);
			// 截取符合的标签并拼上对应的属性名
			String des = String.format("%s%s", xpath.substring(index, first + tag.length()), attr);
			// 改变下次开始寻找的下标
			index = first + tag.length();
			desList.add(des);
		}
		return String.join("", desList);
	}

	/**
	 * 格式化属性名
	 */
	private static final String createAttrVal(String attr) {
		return createAttrVal(attr, null);
	}

	/**
	 * 格式化属性名值
	 */
	private static final String createAttrVal(String attr, String value) {
		return StrUtils.isBlank(attr) ? ""
				: StrUtils.isBlank(value) ? String.format("[@%s]", attr)
						: String.format("[@%s='%s']", attr, value);
	}

	/**
	 * XPath导航,找出Document或任意结点的值
	 */
	public static List<Node> getByXPath(String filePath, String xpath) {
		return getByXPath(new File(filePath), xpath);
	}

	/**
	 * XPath导航,找出Document或任意结点的值
	 */
	public static List<Node> getByXPath(File filePath, String xpath) {
		return getByXPath(parse(filePath), xpath);
	}

	/**
	 * XPath导航,找出Document或者任意的节点Node,如:Attribute,Element或者ProcessingInstruction等
	 * xpath表达式:http://www.w3school.com.cn/xpath/xpath_syntax.asp
	 * 
	 * @param xpath:表达式的值,只能带有//,/,:或|,默认的命名空间可以不写,但是非默认的命名空间需带写完成
	 * @example /beans/bean/mvc:annotaion-driven
	 */
	public static List<Node> getByXPath(Document document, String xpath) {
		return getByXPath(document, xpath, new String[] {});
	}

	/**
	 * 根据属性名查找结点
	 * 
	 * @param xpath
	 *            表达式的值,只能带有//,/或:,默认的命名空间可以不写,但是非默认的命名空间需写完整
	 * @example /beans/bean/mvc:annotaion-driven,完整的格式应该是/beans:beans/beans:bean/mvc:annoation-driven,
	 *          但默认的命名空间最好由程序确定,否则无法正确是否为默认的命名空间;
	 * @warn 每个结点必须唯一,暂时还没解决重复结点问题
	 * @param attr
	 *            多属性名,和xpath中每一个表达式对应
	 * @Fixed
	 */
	public static List<Node> getByXPath(Document document, String xpath, String... attrs) {
		if (StrUtils.isBlank(xpath) || document == null) {
			return null;
		}
		treeWalk(document);
		// 若没有命名空间,则直接可使用xpath进行寻找结点
		if (namespaces.size() == 0) {
			return getByXPathNoNS(document, xpath);
		}
		// 防止有默认命名空间,getnamespceforprefix将无法获取前缀,需要在调用了treewalk之后
		Map<String, Namespace> nameSpace = getNameSpace(document);
		// 如果有,则需要检测每个标签的命名空间
		Element root = document.getRootElement();
		// xpath所需命名空间
		Map<String, String> map = new HashMap<>();
		// 获得xpath标签的数组
		String[] tags = xpath.split("/+|//+");
		// 利用Arrays.aslist转换来的数组无法进行增删操作,不构造函数重新赋值
		List<String> listTags = new ArrayList<>(Arrays.asList(tags));
		// 为了避免因为第一次切割产生空值,需要对空值进行剔除
		for (int j = listTags.size() - 1; j >= 0; j--) {
			if (StrUtils.isBlank(listTags.get(j))) {
				listTags.remove(j);
			}
		}
		// 循环获得标签的命名空间uri以及前缀
		for (int i = 0; i < listTags.size(); i++) {
			String tag = listTags.get(i);
			// 命名空间前缀
			String tagPrefix = "";
			// 若带冒号需要先拿前面的命名空间进行处理
			if (tag.contains(":")) {
				tagPrefix = tag.split(":")[0];
			} else {
				tagPrefix = tag;
			}
			// 在每一个表达式后面加上对应的属性
			String attr = attrs.length > i ? attrs[i] : "";
			// 根据命名空间前缀取得命名空间
			Namespace ns = root.getNamespaceForPrefix(tagPrefix);
			// 默认命名空间前缀
			if (ns == null) {
				map.put("default", nameSpace.get("default").getURI());
				xpath = xpath.replaceAll(tag, "default:" + tag + createAttrVal(attr));
			} else {
				map.put(ns.getPrefix(), ns.getURI());
				xpath = xpath.replaceAll(tag, tag + createAttrVal(attr));
			}
		}
		XPath xPath = document.createXPath(xpath);
		xPath.setNamespaceURIs(map);
		List<Node> selectNodes = xPath.selectNodes(document);
		return selectNodes;
	}

	/**
	 * 精准获得当前文档中所有带指定属性名的结点
	 */
	public static List<Node> getByAttrs(Document document, String... attrs) {
		return getNodesByAttr(document, true, true, attrs);
	}

	/**
	 * 获得当前文档中所有带指定属性名的结点,此方法比通过xpath寻找要快,内存消耗暂时不清楚
	 * 
	 * @param flag
	 *            true精准读取,false模糊读取
	 */
	public static List<Node> getByAttrs(Document document, boolean flag, String... attrs) {
		return getNodesByAttr(document, flag, true, attrs);
	}

	/**
	 * 精准获得当前文档中所有带指定属性值的节点
	 */
	public static List<Node> getByVals(Document document, String... vals) {
		return getNodesByAttr(document, true, false, vals);
	}

	/**
	 * 获得当前文档中所有带指定属性值的节点,此方法比通过xpath寻找要快,内存消耗暂时不清楚
	 * 
	 * @param flag
	 *            true精准读取,false模糊读取
	 */
	public static List<Node> getByVals(Document document, boolean flag, String... vals) {
		return getNodesByAttr(document, flag, false, vals);
	}

	/**
	 * 从文档中通过属性的名称或值快速查找节点,单一的,只能都是属性名或都是属性值
	 * 
	 * @param flag
	 *            是否精确查找,true是
	 * @param key
	 *            是否为属性名查找,true是
	 * @param params
	 *            属性名或属性值
	 */
	private static List<Node> getNodesByAttr(Document document, boolean flag, boolean key, String... params) {
		treeWalk(document);
		ArrayList<Node> nodes = new ArrayList<>();
		for (String attr : params) {
			for (Node node : attributes) {
				Attribute attribute = (Attribute) node;
				String desVal = key ? attribute.getName() : attribute.getValue();
				if (!flag && desVal.contains(attr)) {
					nodes.add(node);
				} else if (flag && desVal.equalsIgnoreCase(attr)) {
					nodes.add(node);
				}
			}
		}
		return nodes;
	}

	/**
	 * 没有命名空间直接利用xpath寻找指定属性名元素节点
	 * 
	 * @param attrs
	 *            属性名
	 */
	public static List<Node> getByXPathAttrsNoNS(Document document, String... attrs) {
		return StrUtils.isAnyBlank(attrs) ? null : getByXPathNoNS(document, null, attrs);
	}

	/**
	 * 没有命名空间直接利用xpath寻找指定元素节点
	 * 
	 * @param xpath的完整路径
	 */
	public static List<Node> getByXPathNoNS(Document document, String xpath) {
		return document.selectNodes(xpath);
	}

	/**
	 * 没有命名空间直接利用xpath寻找指定元素节点
	 * 
	 * @param xpath
	 *            完整路径时,attrs必须为null,否则报错,不完整路径时,属性名可由attrs填写,但必须和xpath的分段表达式
	 * @param attr
	 *            结点所带的属性名,若是有多个属性,必须对应xpath的分段表达式,否则查不出来
	 */
	public static List<Node> getByXPathNoNS(Document document, String xpath, String... attrs) {
		// 没有xpath,只有属性值的时候,取得所有对应的属性值
		if (StrUtils.isBlank(xpath)) {
			List<Node> list = new ArrayList<>();
			for (String attr : attrs) {
				if (StrUtils.isNotBlank(attr)) {
					list.addAll(document.selectNodes(String.format("//@%s", attr)));
				}
			}
			return list;
		}
		// 完整的xpath路径
		if (StrUtils.isAnyBlank(attrs)) {
			return document.selectNodes(xpath);
		}
		// 获得处理后的xpath
		List<String> list = new ArrayList<>();
		for (String attr : attrs) {
			list.add(createAttrVal(attr));
		}
		String desPath = createXPath(xpath, list);
		return document.selectNodes(desPath);
	}

	/**
	 * 没有命名空间利用属性的键值来查询节点
	 * 
	 * @param xpath
	 *            xpath表达式
	 * @param map
	 *            属性键值对,必须与xpath的分段表达式相对应
	 */
	public static List<Node> getByXPathNoNS(Document document, String xpath,
			LinkedHashMap<String, String> map) {
		if (document == null || StrUtils.isBlank(xpath)) {
			return null;
		}
		// 完整的xpath路径
		if (MapUtils.isBlank(map)) {
			return document.selectNodes(xpath);
		}
		// 获得处理后的xpath
		String desPath = createXPath(xpath, map);
		return document.selectNodes(desPath);
	}

	/**
	 * 对文档中的跟元素以及对应的元素进行迭代
	 * 
	 * @example
	 */
	public static final void iteratorDom(Document doc) {
		Element root = doc.getRootElement();
		for (Iterator<?> it = root.elementIterator(); it.hasNext();) {
			Element element = (Element) it.next();
			System.out.println(element);
		}
		for (Iterator<?> it = root.elementIterator("bean"); it.hasNext();) {
			Element element = (Element) it.next();
			System.out.println(element);
		}
		for (Iterator<?> i = root.attributeIterator(); i.hasNext();) {
			Attribute attribute = (Attribute) i.next();
			System.out.println(attribute);
		}
	}

	/**
	 * 修改xml某节点的值
	 * 
	 * @param inputXml
	 *            原xml文件
	 * @param nodes
	 *            要修改的节点
	 * @param attributename
	 *            属性名称
	 * @param value
	 *            新值
	 * @param outXml
	 *            输出文件路径及文件名 如果输出文件为null，则默认为原xml文件
	 * @Fixed
	 */
	public static void modifyDocument(File inputXml, String nodes, String attributename, String value,
			String outXml) {
		try {
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(inputXml);
			List<?> list = document.selectNodes(nodes);
			Iterator<?> iter = list.iterator();
			while (iter.hasNext()) {
				Attribute attribute = (Attribute) iter.next();
				if (attribute.getName().equals(attributename)) {
					attribute.setValue(value);
				}
			}
			XMLWriter output;
			if (outXml != null) { // 指定输出文件
				output = new XMLWriter(new FileWriter(new File(outXml)));
			} else { // 输出文件为原文件
				output = new XMLWriter(new FileWriter(inputXml));
			}
			output.write(document);
			output.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * 字符串转换为Document
	 * 
	 * @example
	 * @Fixed
	 */
	public static Document str2Document(String text) {
		try {
			// 将xml转换成字符串
			// String xml = document.asXML();
			// System.out.println(xml);
			return DocumentHelper.parseText(text);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 保存xml文档
	 */
	public static final void createXML(Document doc, String xmlPath) {
		createXML(doc, xmlPath, Encoding.UTF8);
	}

	/**
	 * 保存xml文档
	 */
	public static final void createXML(Document doc, String xmlPath, String encoding) {
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding(encoding);
		try {
			XMLWriter writer = new XMLWriter(new OutputStreamWriter(new FileOutputStream(xmlPath), encoding),
					format);
			writer.write(doc);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 写入一个xml文件
	 * 
	 * @param rootMap
	 *            根元素 TAGNAME:String,元素名称;
	 *            NAMESPACES:Map<String,String>,多命名空间名值对,key为命名空间前缀,value为uri值;
	 *            ATTRIBUTES:Map<String,String>,多属性名值对; TEXT:String,节点内容;
	 * @param elements
	 *            ID:Long,元素唯一标识; PARENT:Long,父节点标识; CHILDRENS:List<Long>,子节点标识集合;
	 *            TAGNAME:String,元素名称;
	 *            NAMESPACES:Map<String,String>,多命名空间名值对,key为命名空间前缀,value为uri值;
	 *            ATTRIBUTES:Map<String,String>,多属性名值对 TEXT:String,节点内容;
	 */
	public static final void createXML(String filePath, Map<String, Object> rootMap,
			List<Map<String, Object>> elements) {
		try {
			Document document = DocumentHelper.createDocument();
			document.setXMLEncoding(Encoding.UTF8);
			Element root = document.addElement(String.valueOf(rootMap.get("TAGNAME")));
			Object namespaces = rootMap.get("NAMESPACES");
			addAll(root, namespaces, "namespace");
			Object attributes = rootMap.get("ATTRIBUTES");
			addAll(root, attributes, "attribute");
			root.addText(String.valueOf(rootMap.get("TEXT")));
			if (!ListUtils.isBlank(elements)) {
				for (Map<String, Object> map : elements) {
					// 生成本节点
					Element element = createElement(map);
					// 添加子节点
					addChilds(map, element, elements);
					// 添加到根节点
					root.add(element);
				}
			}
			// 以一种优雅的格式写入文件对象
			OutputFormat format = OutputFormat.createPrettyPrint();
			// 紧凑的格式,format也可以不要
			// format = OutputFormat.createCompactFormat();
			// 设置编码
			format.setEncoding(Encoding.UTF8);
			// 设置换行
			format.setNewlines(true);
			// 生成缩进
			format.setIndent(true);
			// 使用4个空格进行缩进, 可以兼容文本编辑器
			format.setIndent(" ");
			// XMLWriter writer = new XMLWriter(new FileOutputStream(new File("name")));
			XMLWriter writer = new XMLWriter(new FileWriter(filePath), format);
			writer.write(document);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 创建一个元素节点
	 */
	public static final Element createElement(Map<String, Object> map) {
		Document doc = DocumentHelper.createDocument();
		String tagName = String.valueOf(map.get("TAGNAME"));
		if (StrUtils.isBlank(tagName)) {
			return null;
		}
		// 新增一个元素节点
		Element element = doc.addElement(tagName);
		// 增加命名空间
		Object namespaces = map.get("NAMESPACES");
		addAll(element, namespaces, "namespace");
		// 增加属性
		Object attributes = map.get("ATTRIBUTES");
		addAll(element, attributes, "attribute");
		// 增加文本
		element.addText(String.valueOf(map.get("TEXT")));
		return element;
	}

	/**
	 * 增加命名空间,属性等多值内容
	 * 
	 * @param type:namespace,attribute
	 */
	private static final void addAll(Element element, Object maps, String type) {
		if (maps != null && maps instanceof Map) {
			Map<String, String> map = (Map<String, String>) maps;
			Iterator<Entry<String, String>> it = map.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, String> entry = it.next();
				if ("namespace".equals(type)) {
					element.addNamespace(entry.getKey(), entry.getValue());
				} else if ("attribute".equals(type)) {
					element.addAttribute(entry.getKey(), entry.getValue());
				}
			}
		}
	}

	/**
	 * 添加子节点,若是没有子节点,则从元素集合中删掉该元素
	 * 
	 * @param self
	 *            本节点
	 * @param parent
	 *            父节点
	 */
	public static void addChilds(Map<String, Object> self, Element parent,
			List<Map<String, Object>> elements) {
		Object childrens = self.get("CHILDRENS");
		if (childrens != null && childrens instanceof List) {
			List<Long> ids = (List<Long>) childrens;
			for (Long id : ids) {
				for (Map<String, Object> maps : elements) {
					if (id == Long.valueOf(String.valueOf(maps.get("ID")))) {
						Element ele = createElement(maps);
						parent.add(ele);
						addChilds(maps, ele, elements);
					}
				}
			}
		} else {
			elements.remove(self);
		}
	}

	/**
	 * 使用Sun公司提供的JAXP API将XSLT应用到文档（Document）上是很简单的
	 * 它允许你使用任何的XSLT引擎（例如：Xalan或SAXON等）来开发。
	 * 下面是一个使用JAXP创建一个转化器（transformer），然后将它应用到文档（Document）
	 * 
	 * @param stylesheet
	 * @Fixed
	 */
	public Document styleDocument(Document document, String stylesheet) {
		try {
			// 使用 JAXP 加载转化器
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer(new StreamSource(stylesheet));
			// 现在来样式化一个文档（Document）
			DocumentSource source = new DocumentSource(document);
			DocumentResult result = new DocumentResult();
			transformer.transform(source, result);
			// 返回经过样式化的文档（Document）
			Document transformedDoc = result.getDocument();
			return transformedDoc;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}