TARGETS: HttpConnection.header(String,String), headers(Map), and request execution/header serialization  
ORACLES: Trigger handlesHeaderEncodingOnRequest and bug report 1172  
CASES: Request with header values requiring encoding; execution must not throw ArrayIndexOutOfBoundsException  
CASES: Ordinary ASCII header value; encoded/non-ASCII header value; boundary short encoded value  
RISKS: Header encoding rules and server-observed expected bytes are not provided  
RISKS: Available context truncates implementation and trigger test body; do not infer unsupported encoding behavior