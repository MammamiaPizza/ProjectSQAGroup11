TARGETS:getCategoryIndex,getSeriesIndex,getRowIndex,getColumnIndex,null-handling in index lookups
ORACLES:index methods should return -1 for null key (not NPE);setCategoryKeys must rebuild internal
maps correctly
ORACLES:clone must deep-copy numerical arrays and keys;equals must reflect full data equality
CASES:null key to every index method;zero-length keys;matching series/category key
arrays;setCategoryKeys then retrieve
CASES:clone after null interval data;getValue with null series/category;equals on cloned vs original
RISKS:no internal source;contracts inferred from failures;buggy version may have additional hidden
null state issues