TARGETS: DatasetUtilities range/minimum/maximum methods exercised by testBug2849731_2 and _3.  
ORACLES: Existing trigger tests and Bug 959 failure evidence are the only expected-result sources provided.  
CASES: Datasets containing null Number values; verify affected calls do not throw NullPointerException.  
CASES: Normal numeric datasets and mixtures of numeric/null values around range extrema.  
CASES: Empty datasets and datasets whose values are all null, where supported by the invoked dataset type.  
RISKS: Exact affected methods and expected Range/Number results are not shown in the supplied context.  
RISKS: Do not infer behavior from another program version or unprovided overloads/APIs.