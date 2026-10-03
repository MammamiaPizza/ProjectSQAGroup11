TARGETS: LocaleUtils.toLocale(String str) — parsing locale strings like "fr__POSIX"
ORACLES: Expected from testLang328 & java.util.Locale.toString/toLanguageTag conventions
CASES: Normal: "fr_FR","en_US". Boundary: "fr__POSIX"(empty country), "fr","fr_". Error: null, "",
"_","_FR"
RISKS: Limited to toLocale parsing logic; no visibility into other LocaleUtils methods' behavior