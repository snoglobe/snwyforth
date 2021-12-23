package me.snwy;

import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;

public class Lexer {
    static enum TokenType {
        LBrace, RBrace, If, Word, Int, Colon, Semicolon, While, Import
    }

    List<Pair<TokenType, String>> Tokens; // too lazy to define my own Token type, so this works
    String Text;

    void Lex()
    {
        for(String T : Text.split("\\s")) // for ease of lexing, everything is space-delimited
            switch(T){
                case ":":
                    Tokens.add(new Pair<TokenType, String>(TokenType.Colon, ":"));
                    break;
                case ";":
                    Tokens.add(new Pair<TokenType, String>(TokenType.Semicolon, ";"));
                    break;
                case "if":
                    Tokens.add(new Pair<TokenType, String>(TokenType.If, "if"));
                    break;
                case "while":
                    Tokens.add(new Pair<TokenType, String>(TokenType.While, "while"));
                case "import":
                    Tokens.add(new Pair<TokenType, String>(TokenType.Import, ":import"));
                case " ":
                    break;
                case "":
                    break;
                default:
                    try {
                        Integer.parseInt(T); // is it numeric? this will throw if not
                        Tokens.add(new Pair<TokenType, String>(TokenType.Int, T)); // idk why i called it "int" since doubles also can be parsed
                    } catch (NumberFormatException e) {
                        Tokens.add(new Pair<TokenType, String>(TokenType.Word, T));
                    }
                    break;
            }
    }

    Lexer(String Program){
        Text = Program;
        Tokens = new ArrayList<Pair<TokenType, String>>();
    }

}