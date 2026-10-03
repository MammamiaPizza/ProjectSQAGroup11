TARGETS: process(Token, HtmlTreeBuilder) in states that handle <image> (likely InBody). The
anythingElse or start-tag logic
ORACLES: HTML spec: <image> is parsed as <img> void elem. Expected output from parsing input string,
verified via Document.outerHtml()
CASES: Normal <image>→<img />, <image alt="x">→<img alt="x" />, nested <image><p>, <image/>
self-closing, in <table>, multiple images
RISKS: Only one trigger test; scope of conversion may be incomplete. Other void-element rules might
interact. Relies on lower-level parsing