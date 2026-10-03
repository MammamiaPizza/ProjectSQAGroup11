TARGETS: getLegendItems(), getLegendItem(int,int), plot/dataset and renderer series visibility handling.  
ORACLES: Trigger expects legend item collection count 1; existing renderer/plot legend-item behavior is source.  
CASES: One visible series in plot dataset yields one legend item through getLegendItems().  
CASES: Multiple series; verify each eligible series contributes its getLegendItem result.  
CASES: Hidden/ineligible series or null legend item is excluded from collection.  
RISKS: Abstract class requires a concrete renderer and CategoryPlot/dataset setup.  
RISKS: Context omits exact visibility APIs and expected behavior for null plot/dataset/indexes.