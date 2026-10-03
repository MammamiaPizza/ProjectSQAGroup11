TARGETS: ZipArchiveEntry.getName() normalization of backslashes in names<br>
TARGETS: ZipArchiveEntry(String), setName(String), getRawName(), isDirectory()<br>
ORACLES: Known-answer WinZip archives: expected getName() with '/' not '\'<br>
ORACLES: Expected null/slash variants from ZipFile.getEntry() when looking up '/'<br>
CASES: name="ä\ü.txt" → getName() == "ä/ü.txt"<br>
CASES: name="dir\" (trailing backslash) → isDirectory() true, getName() "dir/"<br>
CASES: name="a/b\c" → "a/b/c"; name="\" → "/"; empty, null, only-slashes<br>
CASES: setRawName with backslash via protected setName(name, rawName)<br>
RISKS: Normalization may depend on getPlatform()=0 (FAT); need to reproduce WinZip entry
metadata<br>
RISKS: Without actual ZIP bytes, rely on exposing/populating platform & raw names via
constructor/setters