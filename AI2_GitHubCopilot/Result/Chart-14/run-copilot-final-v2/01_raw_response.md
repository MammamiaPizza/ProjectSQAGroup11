TARGETS: CategoryPlot.removeDomainMarker, CategoryPlot.removeRangeMarker, XYPlot.removeDomainMarker,
XYPlot.removeRangeMarker
ORACLES: No NullPointerException when removing marker from plot with no markers; should handle
uninitialized internal list
CASES: Remove domain/range marker from fresh plot (no markers added); add marker then remove; remove
twice; remove nonexistent
CASES: Remove marker from CategoryPlot with only range markers; remove from XYPlot with only domain
markers; boundary: one marker
RISKS: Cannot inspect internal marker storage; must rely on public API, only exception-free behavior
verifiable