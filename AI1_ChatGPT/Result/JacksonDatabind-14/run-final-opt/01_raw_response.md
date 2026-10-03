TARGETS: ObjectReader.withValueToUpdate and update binding path used by TestUpdateValue.testIssue744.  
ORACLES: Trigger expects update to DataB not to reject field "i" as an unrecognized property.  
CASES: Update a DataB target from JSON containing its known "da"/"k" fields plus inherited/shared "i".  
CASES: Verify update result preserves/applies fields on the supplied target object.  
RISKS: Exact DataB/DataA structure and intended values are absent; derive assertions only from trigger fixture.