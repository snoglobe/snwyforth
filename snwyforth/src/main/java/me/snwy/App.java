package me.snwy;

import java.nio.file.Files;
import java.nio.file.Path;

public class App 
{
    public static void main( String[] args ) throws Exception
    {
        String actual = Files.readString(Path.of(args[0]));
        Lexer l = new Lexer(actual);
        l.Lex();
        System.out.println(l.Tokens);
        Parser p = new Parser(l.Tokens);
        ASTNode AST = p.root();
        System.out.println(AST);
        Compiler c = new Compiler((Root)AST);
        c.Compile();
        System.out.println(c.toString());
    }
}
