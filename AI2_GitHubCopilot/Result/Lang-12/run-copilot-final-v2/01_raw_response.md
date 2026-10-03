TARGETS: random(int,char...), random(int,String), random(int,int,int,boolean,boolean),
random(int,int,int,boolean,boolean,char...)
ORACLES: No ArrayIndexOutOfBoundsException; returned string length == count
ORACLES: Characters belong to supplied char array/string or start-end numeric range
CASES: empty char[] (length 0) – triggers AIOOBE; null chars/String – likely
IllegalArgumentException
CASES: start > end; start=end; negative start/end; start > Character.MAX_CODE_POINT; count=0 with
valid chars
CASES: large count with tiny char set; chars array of length 1; supplementary characters in range
RISKS: Without source, cannot confirm integer overflow in range-length calculation or exact bounds
checks
RISKS: Tests should verify correctness of length and allowed chars only; randomness distribution is
not the target