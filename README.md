# snwyforth

a forth i wrote in java
compiled to bytecode

# usage
`snwyforth [program] [-v : verbose]`
snwyforth has/will have a REPL which is loaded if you don't provide a program.

# how to use
an if statement goes like   
```
if <condition to eval> :
    <stuff to do>
;
```  
a while statement goes like  
```
while :
    <stuff to do, every loop it will test the top of the stack>
;
```  
list of traps:  
```
0x0 : print as decimal number to stdout
0x1 : get character and push to stack as ASCII value
0x2 : pop stack and exit with code
```
builtin words:  
```
Dup ( a -- a a )
Swap ( a b -- b a )
Rotl ( a b c -- b c a )
Rotr ( a b c -- c a b )
Drop ( a -- )
Over ( a -- a b a )
Nip ( a b -- b )
Tuck ( a b -- b a b )
```  
other than that it's a standard forth lol  
example programs in `test/`
