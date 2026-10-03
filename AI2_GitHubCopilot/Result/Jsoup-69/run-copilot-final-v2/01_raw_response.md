TARGETS: FormElement.elements().size() after child removal; formData() after removal
ORACLES: size decrements by 1 after removing a child element via Node.removeChild or Element.remove
CASES: remove first/last/middle child; remove all; remove element not in elements list; remove via
different parent methods
RIKSS: exact trigger method not specified; test uses Element.remove on a child; internal list may
not sync on Node.removeChild or remove via DOM traversal