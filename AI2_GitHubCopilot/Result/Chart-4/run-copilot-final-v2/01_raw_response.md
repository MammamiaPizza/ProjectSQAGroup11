TARGETS: XYPlot.getDataset(), getDataset(int), getDomainAxis(), getRangeAxis()
ORACLES: getDataset() returns null if unset per Javadoc; no NPE in auto-range or draw
ORACLES: auto-range from dataset bounds; plot render must tolerate null PlotRenderingInfo
CASES: new XYPlot with no dataset/axes; draw with null info; replace dataset with null
CASES: clearDomainAxes() then getDomainAxis(); auto-range on empty plot; serialization
RISKS: exact NPE location unknown; only buggy-version signatures available