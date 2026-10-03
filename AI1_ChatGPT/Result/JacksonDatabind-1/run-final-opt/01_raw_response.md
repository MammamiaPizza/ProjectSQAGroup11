TARGETS: BeanPropertyWriter.serializeAsColumn for POJO-as-array property serialization.
ORACLES: Trigger expects [null,[],"bar"], not [null,[null,]"bar"].
CASES: Null column value with null suppression enabled must omit its array element.
CASES: Non-null adjacent columns retain order and serialize normally.
CASES: Empty array-valued column remains [] and is not confused with a null column.
RISKS: Distinguish serializeAsColumn from serializeAsField and serializeAsPlaceholder behavior.
RISKS: Context provides only one trigger and no constructor/setup details for direct writer tests.