TARGETS: OptionGroup.getOptions() insertion-order preservation; OptionGroup.addOption() ordering.
ORACLES: Bug CLI-266: 'p' added before 'x' must appear first in getOptions(). Options uses
LinkedHashMap for order.
CASES: add options (p,x), verify getOptions() returns [p,x]; single option; empty group; duplicate
add; setSelected with valid/invalid.
RISKS: OptionComparator class not in provided API; internal comparator may compare using order from
getOptions().
CASES: Options.addOptionGroup() should integrate with Options.getOptions() preserving group's
insertion order.