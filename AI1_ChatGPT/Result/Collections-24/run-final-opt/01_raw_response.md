TARGETS: unmodifiableBoundedCollection(BoundedCollection) and unmodifiableBoundedCollection(Collection) factories  
TARGETS: iterator; add/addAll/clear/remove/removeAll/retainAll; isFull/maxSize delegation  
ORACLES: Trigger tests specify factory identity expectations and unmodifiable behavior assertions  
ORACLES: BoundedCollection state supplies expected isFull(), maxSize(), and iteration contents  
CASES: Decorate a bounded collection; verify factory result/identity behavior without content changes  
CASES: Normal iteration and delegated isFull/maxSize on empty, partial, and full bounded collections  
CASES: Each collection mutator and iterator.remove() must be rejected on the wrapper  
CASES: Re-wrap an already unmodifiable bounded collection to test idempotence/identity  
RISKS: Collection overload may need bounded-collection discovery; exact non-bounded behavior is not provided  
RISKS: No source or full test assertions supplied; avoid assumptions beyond listed signatures and triggers