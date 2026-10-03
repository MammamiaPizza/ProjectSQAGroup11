TARGETS: Whitelist.addProtocols() and testValidProtocol() for custom URL protocol handling in src
attributes
ORACLES: CleanerTest::handlesCustomProtocols expects cleaned HTML to preserve "cid:12345" in img src
CASES: Normal: add single protocol (cid) for img src; Boundary: multiple protocols, :all tag
wildcard, preserveRelativeLinks interaction; Error: protocol value missing colon, no protocols added
RISKS: Only testValidProtocol internal logic is testable via Cleaner; typed-value matching may
silently drop custom protocols