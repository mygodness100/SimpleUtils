package com.wy.common;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public interface Encoding {
	String UTF8 = "UTF-8";
	String GBK = "GBK";
	String GB2312 = "GB2312";
	String ASCII = "US-ASCII";
	String ISO_8859_1 = "ISO-8859-1";
	String UTF16BE = "UTF-16BE";
	String UTF16LE = "UTF-16LE";
	String UTF16 = "UTF-16";

	Charset CHARSET_UTF8 = StandardCharsets.UTF_8;
	Charset CHARSET_UTF16 = StandardCharsets.UTF_16;
	Charset CHARSET_UTF16BE = StandardCharsets.UTF_16BE;
	Charset CHARSET_UTF16LE = StandardCharsets.UTF_16LE;
	Charset CHARSET_ISO_8859_1 = StandardCharsets.ISO_8859_1;
	Charset CHARSET_US_ASCII = StandardCharsets.US_ASCII;
	Charset CHARSET_GBK = Charset.forName(GBK);
	Charset CHARSET_GB2312 = Charset.forName(GB2312);
}