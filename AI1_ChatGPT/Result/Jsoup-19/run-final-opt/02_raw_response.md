package org.jsoup.safety;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class WhitelistCustomProtocolsTest {

    private String clean(String html, Whitelist whitelist) {
        Document dirty = Jsoup.parseBodyFragment(html);
        Document clean = new Cleaner(whitelist).clean(dirty);
        return clean.body().html();
    }

    @Test
    public void retainsCustomCidProtocolForImageSource() {
        Whitelist whitelist = Whitelist.basicWithImages()
                .addProtocols("img", "src", "cid");

        assertEquals("<img src=\"cid:12345\" />",
                clean("<img src=\"cid:12345\">", whitelist));
    }

    @Test
    public void supportsChainedProtocolAdditionsForSameAttribute() {
        Whitelist whitelist = new Whitelist()
                .addTags("img")
                .addAttributes("img", "src")
                .addProtocols("img", "src", "cid")
                .addProtocols("img", "src", "http", "https");

        assertEquals("<img src=\"cid:12345\" /><img src=\"http://example.com/image.png\" /><img src=\"https://example.com/image.png\" />",
                clean("<img src=\"cid:12345\"><img src=\"http://example.com/image.png\"><img src=\"https://example.com/image.png\">", whitelist));
    }

    @Test
    public void removesImageSourceWithUnconfiguredProtocol() {
        Whitelist whitelist = Whitelist.basicWithImages()
                .addProtocols("img", "src", "cid");

        assertEquals("<img />",
                clean("<img src=\"javascript:alert(1)\">", whitelist));
    }

    @Test
    public void preservesExistingAllowedProtocolsWhenAddingCustomProtocol() {
        Whitelist whitelist = Whitelist.basicWithImages()
                .addProtocols("img", "src", "cid");

        assertEquals("<img src=\"https://example.com/image.png\" />",
                clean("<img src=\"https://example.com/image.png\">", whitelist));
    }
}