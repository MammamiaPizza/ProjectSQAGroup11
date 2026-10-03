TARGETS: reflectionHashCode overloads; registry lifecycle during cyclic object reflection  
ORACLES: Trigger expects getRegistry() to be null after testReflectionObjectCycle  
CASES: Self-cycle and mutual-cycle objects passed to reflectionHashCode  
CASES: Repeated cyclic reflection calls; verify completion and registry cleanup each time  
CASES: Normal acyclic object reflection as a regression baseline  
RISKS: ThreadLocal registry may retain an empty Set ([]) instead of being removed/null  
RISKS: Context provides only the cycle trigger; field-selection/transient expectations are unspecified