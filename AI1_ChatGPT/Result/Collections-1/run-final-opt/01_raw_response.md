TARGETS: FlatMapIterator.setValue and EntrySetIterator.setValue behavior in Flat3Map.  
ORACLES: Trigger assertions: setValue("NewValue") must return prior value 10, not "NewValue".  
ORACLES: Verify map/entry value becomes "NewValue" after setValue.  
CASES: mapIterator on populated map; advance to entry mapped to 10; call setValue.  
CASES: entrySet().iterator next Map.Entry; call entry.setValue on value 10.  
CASES: Exercise trigger variants 2 and 3, including differing entry positions.  
RISKS: Iterator state/order is unspecified here; select discovered entries rather than assume order.  
RISKS: No source/context states behavior before next(), after remove(), or delegate-map conversion.