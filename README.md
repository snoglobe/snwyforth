# snwyforth

a forth i wrote in java
compiled to bytecode

# usage
`snwyforth [program] [-v : verbose]`
snwyforth has a REPL which is loaded if you don't provide a program.

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
0x1 : print as ascii
0x2 : get character and push to stack as ASCII value
0x3 : pop stack and exit with code
0x4 : terminal bell
0x5 : input as decimal
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
you can import another program with
```
import <path to program>
```
it will be looked for in `<jar-location>/dict/<file>` first, and if not found there it will be looked for in your working path.
to install a new vocabulary, just plop the `.sf` file(s) into `<jar-location>/dict/`.  
you can store data into a name with
```
<value> store <name>
```
be warned that only the first char of the name identifies it.  
there are pointers too. think of them as an array kind-of.
to create a pointer
```
<len> ptr <name>
```
be warned that only the first char of the name identifies it.  
to increment the pointer
```
iptr <name>
```
to decrement the pointer
```
dptr <name>
```
to push the value of the pointer
```
gptr <name>
```
to set the value of the pointer
```
<value> sptr <name>
```

other than that it's a standard forth lol  
example programs in `test/`

# building
you'll need `mvn`  
just run `mvn package` in the `snwyforth/` folder
