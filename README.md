## SIMQL syntax
```
string ::= // simple string
number ::= // decimal number
symbol ::= [a-zA-Z][a-zA-Z0-9_]*
term ::= (string | number | symbol)

binaryOp ::= (">" | "<" | ">=" | "<=" | "=" | "<>" | "in")
binaryCond ::= term binaryOp term

logicalOp ::= "&&" | "||"

expr ::= binalyCond {logicalOp binaryCond}

joinType ::= "<<" | "><"
join ::= joinType symbol "?" expr

from ::= symbol {join}
select ::= ":" symbol {symbol}
where ::= "?" expr

simql ::= from [select] [where]

order ::= // TODO
groupBy ::= // TODO maybe omit
```
