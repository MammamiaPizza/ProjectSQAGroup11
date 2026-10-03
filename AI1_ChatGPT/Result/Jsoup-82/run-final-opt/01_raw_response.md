TARGETS: DataUtil.parseInputStream charset detection and fallback when a declared charset cannot encode input  
TARGETS: DataUtil.load(InputStream,...,Parser) integration path used by HtmlParser parsing  
ORACLES: Trigger expects document output charset "UTF-8", not declared "ISO-2022-CN"  
ORACLES: Charset declarations are extracted by getCharsetFromContentType/charsetPattern and validated by validateCharset  
CASES: HTML declaring ISO-2022-CN with content that cannot be encoded by that charset; expect UTF-8 fallback  
CASES: Valid declared charset and compatible content; preserve the valid charset behavior  
CASES: Absent, malformed, or unsupported charset declarations; verify existing fallback/error behavior  
RISKS: Exact document serialization/output-charset API and input fixture details are not provided  
