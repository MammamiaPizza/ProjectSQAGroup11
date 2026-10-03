TARGETS: equals(Object), hashCode() on ZipArchiveEntry (subclass of java.util.zip.ZipEntry)
ORACLES: equals must return false when entries differ in platform, unix mode, or internal/external
attributes (ZipEntry compares only name/comment in parent)
ORACLES: hashCode must be consistent with equals; two equal entries must have same hashCode
CASES: same name but different platform (PLATFORM_UNIX vs PLATFORM_FAT)
CASES: same name but different unix mode or external attributes
CASES: entries differing only in internal attributes or extra fields
CASES: null, self, different class, subclass instance
RISKS: exact failing condition in testNotEquals unknown; need to infer from parent equals ignoring
subclass fields
RISKS: may need to add equals/hashCode checks for platform, external attributes, extra fields that
were missing
RISKS: ensure clone() methods also preserve attributes correctly for equality tests