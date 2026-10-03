TARGETS: Cleaner.clean(Document), isValid(Document); frameset/frame cleaning path in private node copying.
ORACLES: Trigger CleanerTest::handlesFramesets; clean result and validity contract documentation.
CASES: Parse HTML with frameset/frame under an appropriate Whitelist; clean must not throw NPE.
CASES: Verify retained versus removed frame-related nodes/attributes according to supplied Whitelist behavior.
CASES: isValid for clean frameset input and input requiring frame-related removal.
RISKS: Expected allowed tags/attributes depend on Whitelist configuration; no external version behavior available.
RISKS: Private copySafeNodes/createSafeElement are exercised only through public clean/isValid.