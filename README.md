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
string ::= // string
number ::= // decimal number
null ::= "null"
symbol ::= [a-zA-Z][a-zA-Z0-9_]*
accessor ::= "$"[0-9]
raw = "\$`.*`"
macroSymbol ::= "\$[a-zA-Z][a-zA-Z0-9_]*" // TODO

symbolWithAccessor ::= [accessor"."]symbol
term ::= (null | symbolWithAccessor | string | number | raw)

binaryOp ::= (">" | "<" | ">=" | "<=" | "==" | "!=")
binaryCond ::= symbolWithAccessor binaryOp term

logicalOp ::= "&&" | "||"

expr ::= binalyCond {logicalOp binaryCond}

joinType ::= "<<" | "><"
join ::= joinType symbol "?" expr

orderType ::= "/>" | "\>"

column = (symbolWithAccessor | raw)

from ::= symbol {join}
select ::= ":" column {column}
where ::= "?" expr
limitOffset ::= "@" number [- number] // TODO ignore float number
order ::= orderType symbolWithAccessor {symbolWithAccessor}

simql ::= from [select] [where] [limitOffset] [order]

order ::= // TODO
groupBy ::= // TODO maybe omit

// define
table = "deft" symbol "=" from // TODO
```
