TARGETS: ObjectReader multi-value binding paths, especially _initForMultiRead and _bindAndReadValues.  
ORACLES: ReadValuesTest::testRootBeans is the only stated expected-success source.  
CASES: Read sequential root bean values from UTF-32 input; verify iterator yields expected values.  
CASES: Cover UTF-32 character decoding near initial bytes, where trigger reports char #1/byte #7.  
RISKS: Current failure is CharConversionException for invalid UTF-32 character 0x2261223a.  
RISKS: Exact input encoding, bean type, and expected values are not provided in this context.