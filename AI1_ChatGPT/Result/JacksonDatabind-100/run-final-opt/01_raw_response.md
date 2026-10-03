TARGETS: TreeTraversingParser.getBinaryValue(Base64Variant), readBinaryValue(Base64Variant) on TextNode
ORACLES: TestConversions::testBase64Text and bug 2096 trigger assertion outcomes
CASES: Base64 URL variant with decoded data length 1; verify binary access succeeds
CASES: Compare getBinaryValue result and readBinaryValue output for the same TextNode
RISKS: Broken handling of unpadded/short Base64 text causes unexpected end-of-String
RISKS: Context provides no implementation diff or broader Base64 variant expectations