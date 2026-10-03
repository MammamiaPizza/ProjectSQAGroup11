@Test
public void parsesCommentAndContinuesWithFollowingContent() {
    assertEquals("After", Parser.parseBodyFragment("<!-- ignored --><p>After</p>", "").select("p").get(0).text());
}

@Test
public void parsesCdataAsText() {
    assertEquals("<em>raw</em>", Parser.parseBodyFragment("<![CDATA[<em>raw</em>]]>", "").body().text());
}

@Test
public void parsesXmlDeclarationBeforeHtmlContent() {
    assertEquals("After", Parser.parse("<?xml version='1.0'?><p>After</p>", "").select("p").get(0).text());
}

@Test
public void preservesMarkupInsideTextareaAsText() {
    assertEquals("<b>One</b>", Parser.parseBodyFragment("<textarea><b>One</b></textarea>", "").select("textarea").get(0).text());
}