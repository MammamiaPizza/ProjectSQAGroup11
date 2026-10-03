TARGETS: Parser.parseTextNode; parseEndTag; parseCdata; popStackToClose on script/data text
ORACLES: parsed Document html() equals expected serialization from trigger test
CASES: text after </script> data; script with inner text then trailing text; nested empty tags
RISKS: only trigger method name/signature available; exact expected html() output not shown in full