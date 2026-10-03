TARGETS: CodeGenerator.addExpr treats numeric obj-lit keys vs string keys; IRFactory transforms
obj-lit nodes; RenamePrototypes.canRenameObjLitProperty.
ORACLES: Expected output from CodePrinterTest (e.g., "[1]:1", "get "a""), IRFactoryTest counts,
RenamePropertiesTest key rename decisions.
CASES: Integer keys, large double (3E9) keys, getter/setter bracket keys with string quotes,
doc-associated object literals, prototype-property rename via obj-lit.
RISKS: Negative/Infinity/NaN keys, Unicode escapes in keys, reserved words as keys, nested obj lits
may expose missing quote/rename logic.