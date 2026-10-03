TARGETS: XYSeries.add(Number,Number) and add(double,double) with duplicate x; addOrUpdate;
indexOf(Number); update; delete; getY
ORACLES: Bug 862: adding duplicate x when allowDuplicateXValues=false should replace/update, no
exception; item count stays 1; getY returns new value
CASES: Normal: add unique x with autoSort; Boundary: duplicate x after first item (size=1) with
allowDuplicateXValues=false—expect replace; duplicate with allowDuplicate=true—expect two items
CASES: indexOf for existing duplicate x; addOrUpdate for new vs existing x; update on non-existent
x; delete start/end; toArray consistency
RISKS: Bug triggers on second add with duplicate x; must test both autoSort values; indexOf may
return -1 incorrectly; expected behavior only from bug summary