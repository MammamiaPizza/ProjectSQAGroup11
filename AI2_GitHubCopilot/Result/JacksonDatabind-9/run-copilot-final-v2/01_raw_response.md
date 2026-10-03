TARGETS: StdKeySerializer.serialize(Object,…) – for Class keys strip "class " prefix so toString
gives full name without "class ".
ORACLES: TestMapSerialization.testClassKey expects key like "[]java.lang.String" (brackets, then
class name) not "[class ]java.lang.String".
CASES: Normal class (String), primitive (int), interface, array class (String[]), null key,
non-Class key (unchanged), inner class, duplicate keys.
RISKS: Overly broad toString replacement may break non-Class keys that contain "class ". Only Class
instances should be treated. Ensure no regression for null key.