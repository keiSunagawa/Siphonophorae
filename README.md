## SIMQL syntax
```
string ::= // simple string
number ::= // decimal number
symbol ::= [a-zA-Z][a-zA-Z0-9_]*
accessor ::= "$"[0-9]
symbolWithAccessor ::= [accessor "."]symbol
term ::= (symbolWithAccessor | string | number)

binaryOp ::= (">" | "<" | ">=" | "<=" | "=" | "<>")
binaryCond ::= term binaryOp term

logicalOp ::= "&&" | "||"

expr ::= binalyCond {logicalOp binaryCond}

joinType ::= "<<" | "><"
join ::= joinType symbol "?" expr

from ::= symbol {join}
select ::= ":" symbolWithAccessor {symbolWithAccessor}
where ::= "?" expr

simql ::= from [select] [where]

order ::= // TODO
groupBy ::= // TODO maybe omit
```
