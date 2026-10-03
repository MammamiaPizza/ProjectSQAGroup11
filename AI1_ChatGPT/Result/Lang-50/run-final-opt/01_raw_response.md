TARGETS: getDateInstance(style), getDateInstance(style, TimeZone, Locale) default-locale caching  
TARGETS: getDateTimeInstance(style, style), getDateTimeInstance(style, style, TimeZone, Locale)  
ORACLES: returned FastDateFormat.getLocale() must be the current Locale.getDefault() when locale is omitted  
CASES: set default en_US; obtain date instance; set default de_DE; obtain same-style date instance; assert de_DE  
CASES: repeat default-locale transition for date-time instance with fixed date/time styles  
CASES: verify explicit Locale overload remains that explicit locale despite default-locale changes  
RISKS: restore Locale default after each test; static caches make test ordering/state isolation significant  
RISKS: supplied context omits full getDateTimeInstance signatures and implementation details