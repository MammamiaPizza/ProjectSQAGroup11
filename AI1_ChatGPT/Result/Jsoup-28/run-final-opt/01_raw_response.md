TARGETS: Entities.unescape(String, boolean): relaxed vs strict named/numeric reference decoding.  
TARGETS: Parser/Tokeniser entity handling in text and attribute values through Parser.parse(...).  
ORACLES: Trigger expectations distinguish exact named entities from prefixes of longer names.  
ORACLES: Existing failures show semicolonless base entities may decode, extended entities require strict matching.  
CASES: Relaxed: &amp;, &quot;, &reg; decode; &amp;icy and &amp;hopf remain literal.  
CASES: Reject shortest-prefix decoding: &amp;clubsuite; remains while &clubsuit; decodes.  
CASES: Attribute URLs preserve &num_rooms, &children, &int; preserve &wr and &mid without semicolons.  
CASES: Verify named entities such as &angst are not decoded as shorter &ang.  
RISKS: Entities.unescape is package-private; direct tests require org.jsoup.nodes package context.