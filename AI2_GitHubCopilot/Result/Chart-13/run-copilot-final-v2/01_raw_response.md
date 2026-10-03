TARGETS:
org.jfree.chart.block.BorderArrangement.arrangeNN(BlockContainer,Graphics2D)—width-constraint sizing
path
ORACLES: assert no IllegalArgumentException; resultant sizes non-negative; widths within constraint
CASES: widthConstraint=0, widthConstraint<border width, widthConstraint just enough for one border,
no border blocks, mixed content
CASES: container with null/null-size blocks; empty container; block widths that reduce available
space to negative
RISKS: protected arrangeNN called indirectly; must test through a container arrangement trigger
RISKS: only failure symptom known—negative upper bound for Range; exact expected outcome for tight
constraints not specified