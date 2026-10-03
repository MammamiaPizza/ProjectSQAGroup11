TARGETS: CharacterReader: consumeTo('<'), advance(), current() edge handling. TokeniserState:
BeforeAttributeName/AttributeName/AttributeValue read() when encountering '<'.
ORACLES: Expected output from failing tests: <p
=a>One<a[></a></p><p><a>Something</a></p><a]>Else</a> and <p[></p><p></p><div
id="one"><span>Two</span></div]>.
CASES: 1) Rough attrs: "<p =a>One<a[ <p>Something</a></p><a <p]>Else</a>" → verify token emission
matches expected. 2) Less-than inside tag name: "<p a<" → should close and start new tag. 3)
Boundary: "<div id='one'<span>Two</span>" → '<' triggers tag start inside attr. 4) Error: dangling
'<' in values like "<a href='<'>".
RISKS: Only two triggers; may miss states like AfterAttributeName; no access to fixed code—expected
output is implicit.