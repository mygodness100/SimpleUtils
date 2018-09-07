package com.wy.common;

import java.nio.charset.Charset;

public interface Encoding {
    String UTF8 = "UTF-8";
    String GBK = "GBK";
    String GB2312 = "GB2312";
    String ASCII = "US-ASCII";
    String ISO_8859_1 = "ISO-8859-1";
    String UTF16BE = "UTF-16BE";
    String UTF16LE = "UTF-16LE";
    String UTF16 = "UTF-16";

    Charset CHARSET_UTF8 = Charset.forName(UTF8);
    Charset CHARSET_GBK = Charset.forName(GBK);
    Charset CHARSET_GB2312 = Charset.forName(GB2312);
    Charset CHARSET_ASCII = Charset.forName(ASCII);
    Charset CHARSET_ISO_8859_1 = Charset.forName(ISO_8859_1);
    Charset CHARSET_UTF16 = Charset.forName(UTF16);
    Charset CHARSET_UTF16BE = Charset.forName(UTF16BE);
    Charset CHARSET_UTF16LE = Charset.forName(UTF16LE);
}