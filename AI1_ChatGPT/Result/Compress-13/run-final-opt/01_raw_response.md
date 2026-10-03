TARGETS: ZipArchiveEntry name normalization/getName, raw-name retention, and platform-dependent separator handling.
ORACLES: Trigger assertions: decoded "ä\\/" expected from "ä\\"; ZipFile lookup must not retain "ä\\ü.txt".
CASES: Archive entries with backslash names, including non-ASCII "ä\\ü.txt", read via ZipArchiveInputStream and ZipFile.
CASES: Verify normalized slash name and successful lookup using slash-separated equivalent.
RISKS: setName(String, byte[]) is protected; public constructor behavior alone may not exercise archive decoding.
RISKS: Context lacks archive fixture construction details and explicit expected behavior for Unix/FAT or ordinary backslashes.