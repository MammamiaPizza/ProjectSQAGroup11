TARGETS: append(String,int,int), append(StringBuffer,int,int), append(StrBuilder,int,int),
insert(int,char[],int,int)
ORACLES: StrBuilder Javadoc: throw StringIndexOutOfBoundsException for invalid start/length, expand
buffer, append correctly
CASES: startIndex=0 length=0; startIndex<0; length<0; startIndex+length>sourceLength;
startIndex>sourceLength; append after ensureCapacity
CASES: boundary where total size=capacity, then append(n) forces resize; negative length in
appendPadding; append with full source range
RISKS: AIOOBE during System.arraycopy in append/insert if buffer resized incorrectly; only public
API visible, missing internal implementation