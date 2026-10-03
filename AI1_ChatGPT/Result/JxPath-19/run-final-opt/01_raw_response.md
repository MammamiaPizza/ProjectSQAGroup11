TARGETS: DOMNodePointer.asPath and QName-relative element position for aliased namespace siblings.  
TARGETS: JDOMNodePointer.asPath and QName-relative element position for aliased namespace siblings.  
ORACLES: Trigger expects /a:doc[1]/a:elem[1] then /a:doc[1]/a:elem[2].  
CASES: Iterate /a:doc/a:elem over two same-QName sibling elements using alias prefix a.  
CASES: Verify DOM and JDOM produce distinct sequential paths, not duplicate [1] paths.  
CASES: Check first and second sibling boundaries in the selected iterator result.  
RISKS: Prefix aliasing may cause QName comparison/relative-position logic to treat siblings incorrectly.  
RISKS: Available context gives no broader namespace, attribute, text, or PI expected behavior.