TARGETS: Element.clone(), classNames(), addClass(), removeClass(), toggleClass(), hasClass()
ORACLES: after clone, mutating clone’s class set must leave original unchanged (independent copies)
CASES: clone with classes, then add/remove/toggle on clone, assert original.classNames() unchanged;
empty set, single class
RISKS: internal class name Set likely shared reference in clone; clone may need deep copy of class
attribute/Set