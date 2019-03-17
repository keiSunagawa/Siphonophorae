## RUN Application
```
# compile scala code
$ cd siphonophorae/scripts
$ ./scalajs-compile.sh

# build electron view
$ cd siphonophorae/electron-view
$ yarn
$ yarn webpack
$ yarn start
```

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

orderType ::= "/>" | "\>"

from ::= symbol {join}
select ::= ":" symbolWithAccessor {symbolWithAccessor}
where ::= "?" expr
limitOffset ::= "@" number [- number] // TODO ignore float number
order ::= orderType symbolWithAccessor {symbolWithAccessor}

simql ::= from [select] [where] [limitOffset] [order]

order ::= // TODO
groupBy ::= // TODO maybe omit
```
