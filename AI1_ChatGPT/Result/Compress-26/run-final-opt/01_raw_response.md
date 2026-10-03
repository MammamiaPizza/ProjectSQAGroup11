TARGETS: IOUtils.skip(InputStream,long), especially fallback from skip() to read() when skip returns zero.  
ORACLES: Existing trigger assertions: skip result is 10 for skipUsingRead and skipUsingSkipAndRead.  
CASES: Stream whose skip() returns 0; mixed partial skip/read fallback; request exceeding remaining input.  
RISKS: Context lacks IOUtils implementation and full existing test bodies; avoid assuming unstated read/EOF behavior.