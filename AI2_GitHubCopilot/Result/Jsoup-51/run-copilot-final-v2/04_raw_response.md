Looking at the coverage gaps, I need to target:

 1. consumeAsString() (lines 50-51, 62)
 2. consumeData() (lines 157-168) — stops at &, <, null
 3. consumeDigitSequence() (lines 239-247) — consumes digits, stops at non-digit
 4. cacheString() with hashcode conflict (lines 349-374) — needs two different strings with the same
hashCode to hit the else branch at lines 372-374

The classic string pair " (one double-quote) and "!" (one exclamation-mark) are not guaranteed
same-hash; the famous Java collision pair is "Aa" and "BB" — their String.hashCode() values are both
2112. That's the reliable way to provoke the cache-conflict path: cache "Aa", then consume "BB"
through the same slot.

These methods (consumeAsString, consumeData, consumeDigitSequence, advance, cacheString,
rangeEquals) are all package-private inside org.jsoup.parser, so the test (which lives in that
package) is fine. new String(...) calls cacheString internally for non-consumeToAny variants, and
the string pool ensures "Aa" and "BB" collide.