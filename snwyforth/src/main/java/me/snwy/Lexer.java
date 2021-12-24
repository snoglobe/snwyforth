package me.snwy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.javatuples.Pair;

public class Lexer {
    static enum TokenType {
        LBrace, RBrace, If, Word, Int, Colon, Semicolon, While, Import, Pointer, Store, Gptr, Sptr, Iptr, Dptr, Lp, Rp
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
                    break;
                case "import":
                    Tokens.add(new Pair<TokenType, String>(TokenType.Import, "import"));
                    break;
                case "ptr":
                    Tokens.add(new Pair<TokenType, String>(TokenType.Pointer, "ptr"));
                    break;
                case "gptr":
                    Tokens.add(new Pair<TokenType, String>(TokenType.Gptr, "gptr"));
                    break;
                case "sptr":
                    Tokens.add(new Pair<TokenType, String>(TokenType.Sptr, "sptr"));
                    break;
                case "iptr":
                    Tokens.add(new Pair<TokenType, String>(TokenType.Iptr, "iptr"));
                    break;
                case "dptr":
                    Tokens.add(new Pair<TokenType, String>(TokenType.Dptr, "dptr"));
                    break;
                case "store":
                    Tokens.add(new Pair<TokenType, String>(TokenType.Store, "store"));
                    break;
                case "(":
                    Tokens.add(new Pair<TokenType, String>(TokenType.Lp, "("));
                    break;
                case ")":
                    Tokens.add(new Pair<TokenType, String>(TokenType.Lp, ")"));
                    break;
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
        
        Iterator<Pair<TokenType, String>> iter = Tokens.iterator();
        while(iter.hasNext()) {
            String v = iter.next().getValue1();
            if(v == "(") {
                iter.remove();
                while(iter.hasNext() && iter.next().getValue1() != ")"){
                    iter.remove();
                }
            }
        }
        Tokens.removeIf(x -> x.getValue1() == ")");
    }

    Lexer(String Program){
        Text = Program;
        Tokens = new ArrayList<Pair<TokenType, String>>();
    }

}