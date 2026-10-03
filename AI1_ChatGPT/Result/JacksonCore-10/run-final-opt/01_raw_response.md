TARGETS: addName/findName overloads; collision placement, rehash sizing, spillover bounds, and count accessors.  
ORACLES: existing trigger assertions: bucket counts 16384/1024 and synthetic total 8534; no AIOOBE for issue 207.  
CASES: colliding 1-, 2-, 3-quad and long-quad names; verify returned/stored names and lookup after growth.  
CASES: fill collision tiers through spillover and rehash thresholds; assert size/primary/secondary/tertiary/spillover/total counts.  
CASES: root→child additions and release; validate child lookup and published table behavior where observable.  
RISKS: collision inputs and exact expected counts depend on existing trigger fixtures; no alternate-version behavior available.