@org.junit.Test
public void parsesScriptDataUntilAnAppropriateEndTag() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse("<script>var x = 1;</script><p>after</p>");

    org.junit.Assert.assertEquals("var x = 1;", document.getElementsByTag("script").first().data());
    org.junit.Assert.assertEquals("after", document.getElementsByTag("p").first().text());
}

@org.junit.Test
public void retainsNonMatchingScriptEndTagAsScriptData() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse("<script>a</scriptx>b</script><p>after</p>");

    org.junit.Assert.assertEquals("a</scriptx>b", document.getElementsByTag("script").first().data());
    org.junit.Assert.assertEquals("after", document.getElementsByTag("p").first().text());
}

@org.junit.Test
public void retainsEscapedNestedScriptContentUntilOuterEndTag() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse("<script><!--<script>foo</script>bar--></script><p>after</p>");

    org.junit.Assert.assertEquals("<!--<script>foo</script>bar-->", document.getElementsByTag("script").first().data());
    org.junit.Assert.assertEquals("after", document.getElementsByTag("p").first().text());
}

@org.junit.Test
public void closesEscapedScriptAfterNonScriptDoubleEscapeCandidate() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse("<script><!--<style>foo</script><p>after</p>");

    org.junit.Assert.assertEquals("<!--<style>foo", document.getElementsByTag("script").first().data());
    org.junit.Assert.assertEquals("after", document.getElementsByTag("p").first().text());
}