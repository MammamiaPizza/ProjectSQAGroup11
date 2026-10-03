TARGETS: AnnotationMap.merge(primary, secondary); annotation precedence and preservation  
TARGETS: AnnotationMap.get, annotations, size, addIfNotPresent, add behavior  
ORACLES: Trigger expects bundled mixin serialization {"bar":"result"}, not {"stuff":"result"}  
ORACLES: AnnotationMap contents returned by get/annotations/size after merge or add  
CASES: Merge maps with same annotation type; verify primary/secondary precedence via retrieved annotation  
CASES: Merge disjoint maps; verify both annotations and resulting size  
CASES: Merge empty/nonempty maps and both empty maps  
CASES: addIfNotPresent duplicate type versus add replacement behavior  
RISKS: Annotation instances/types require available test annotations; no separate expected spec is provided