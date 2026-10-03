package org.jsoup.nodes;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class EntitiesFrac34Test {
    @Test
    public void unescapeDecodesFrac34NamedEntity() {
        assertEquals("¾", Entities.unescape("&frac34;"));
    }

    @Test
    public void unescapeDecodesFrac34WithinMixedText() {
        assertEquals("<> Å π π 新 there &! ¾",
                Entities.unescape("&lt;&gt; &Aring; &pi; &#960; &#x65b0; there &amp;! &frac34;"));
    }

    @Test
    public void unescapeDecodesFrac34AdjacentToTextAndOtherEntities() {
        assertEquals("before¾after & done",
                Entities.unescape("before&frac34;after &amp; done"));
    }

    @Test
    public void unescapeKeepsExistingBasicNamedEntityDecoding() {
        assertEquals("<>&\"", Entities.unescape("&lt;&gt;&amp;&quot;"));
    }
}