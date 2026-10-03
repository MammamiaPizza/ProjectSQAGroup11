TARGETS: equals(Object), hashCode() of MinMaxCategoryRenderer
ORACLES: Two renderers with identical property states must be equal; distinct states must be unequal
CASES: default instances; after setDrawLines; setGroupStroke; setObjectIcon; setMaxIcon; setMinIcon;
mixed combos for inequality
RISKS: Transient Stroke icon fields may use inaccurate equality; serialization may interfere with
equals contract