TARGETS: keySet() iterator, getKeys(), keysAsListed ordering, addProperty, setProperty,
clearProperty
ORACLES: Expected keys from loaded .properties file; addProperty insertion order; size after
add/remove/clear
CASES: Load multi-key file → check keySet size and contents; add then iterate; remove while
iterating; empty after clear; duplicate key; subset keys
RISKS: Cannot inspect keysAsListed; must derive expected key order from addProperty calls; iterator
NoSuchElement may signal bug