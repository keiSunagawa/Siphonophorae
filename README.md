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
macroArg ::= expr | symbol
macroApply ::= "\$[a-zA-Z][a-zA-Z0-9_]*"(" [macroArg] {"," macroArg} ")"

symbolWithAccessor ::= [accessor"."]symbol
tableSymbol ::= macroApply | raw | symbol
highSymbol ::= macroApply | raw | symbolWithAccessor
term ::= (null | highSymbol | string | number)

binaryOp ::= (">" | "<" | ">=" | "<=" | "==" | "!=")
binaryCond ::= highSymbol binaryOp term

logicalOp ::= "&&" | "||"

expr ::= binalyCond {logicalOp binaryCond}

joinType ::= "<<" | "><"
join ::= joinType TableSymbokl "?" expr

orderType ::= "/>" | "\>"

from ::= TableSymbol {join}
select ::= ":" highSymbol {highSymbol}
where ::= "?" expr
limitOffset ::= "@" number [- number] // TODO ignore float number
order ::= orderType highSymbol {highSymbol}

simql ::= from [select] [where] [limitOffset] [order]

order ::= // TODO
groupBy ::= // TODO maybe omit

// define
macroParam ::= symbol ":" ("Symbol" | "Expr") // 一旦二種類
macroFunc = "defun" symbol "="  // TODO
```
