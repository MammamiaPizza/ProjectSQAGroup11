TARGETS: format(Calendar), applyRules(Calendar), TimeZoneNameRule.appendTo, TimeZoneDisplayKey
ORACLES: match SimpleDateFormat with same pattern&Calendar; verify timezone display matches
Calendar.getTimeZone()
CASES: Calendar TZ != printer TZ; DST on/off; different locales; 12/24h boundaries; null Calendar
RISKS: TimeZoneNameRule may cache using printer's TZ instead of calendar's; format(long) may expose
no-calendar path