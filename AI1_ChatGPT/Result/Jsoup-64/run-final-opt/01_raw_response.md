TARGETS: HtmlTreeBuilderState handling after empty <style> and <noframes> elements  
ORACLES: Trigger assertions’ serialized DOM: subsequent meta remains in head; “One” remains body text  
CASES: Parse HTML with <head><style></style><meta name=foo> followed by <body>One  
CASES: Parse HTML with <head><noframes></noframes><meta name=foo> followed by <body>One  
CASES: Verify closing head/body tags are parsed as markup, not escaped body text  
RISKS: HtmlTreeBuilderState is package-private; test through public HTML parser behavior  
RISKS: Context gives only failing empty-element sequences; avoid assuming other state transitions