TARGETS: FromStringDeserializer.Std._deserialize for STD_LOCALE and _deserializeFromEmptyString behavior  
ORACLES: TestJdkTypes::testLocale expects empty-string Locale result to be Locale.ROOT (not null)  
CASES: Deserialize JSON string "" as Locale; assert same Locale.ROOT  
CASES: Deserialize normal locale strings supported by existing Locale parsing  
RISKS: Empty-string handling may be shared by other FromStringDeserializer scalar types  
RISKS: Context lacks implementation details and full existing TestJdkTypes locale cases