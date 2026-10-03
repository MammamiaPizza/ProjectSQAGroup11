TARGETS: Whitelist.addProtocols and protocol validation used when cleaning URL-valued attributes.  
ORACLES: CleanerTest::handlesCustomProtocols expects `<img src="cid:12345" />` to be retained.  
CASES: Allow custom `cid` protocol for `img`/`src`; verify `cid:12345` survives cleaning.  
CASES: Verify chaining of addProtocols and multiple configured protocols.  
RISKS: Protocol parsing may reject colon-based custom schemes or normalize URLs unexpectedly.  
RISKS: Context lacks full Cleaner API/setup and expected behavior for malformed or relative URLs.