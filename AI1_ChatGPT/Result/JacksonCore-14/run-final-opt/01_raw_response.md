TARGETS: IOContext allocation/release pairs, especially release*Buffer validation and wrongBuf() message.  
ORACLES: Trigger requires invalid release exception message to contain "smaller than original".  
CASES: Allocate each buffer, release same buffer; verify no exception and allocation slot can be reused.  
CASES: Release a smaller byte[] than allocated for read/write/base64; expect IllegalArgumentException with required substring.  
CASES: Release a smaller char[] than allocated for token/concat/name-copy; expect same message requirement.  
CASES: Release unowned/equal-or-larger replacement buffers; validate rejection behavior/message where applicable.  
CASES: Call allocReadIOBuffer/allocWriteEncodingBuffer/allocTokenBuffer with minSize boundary values.  
RISKS: Exact recycler buffer sizes and post-release reuse behavior are not specified by provided context.