package me.snwy;

import java.util.ArrayList;
import java.util.List;
import org.javatuples.Pair;
import me.snwy.Lexer.TokenType;

public class Parser {
    public List<Pair<TokenType, String>> TokenStream;
    Parser(List<Pair<TokenType, String>> tokens){
        TokenStream = tokens;
    }

    //#region Parsing
    
    boolean peek(TokenType type) {
        if(TokenStream.size() > 0 && TokenStream.get(0).getValue0() == type){
            return true;
        }
        return false;
    }

    String eat(TokenType type) throws Exception {
        if(!peek(type))
            throw new Exception("Unexpected token " + type.name());
        return TokenStream.remove(0).getValue1();
    }

    ASTNode number() throws NumberFormatException, Exception {
        return new Number(Integer.parseInt(eat(TokenType.Int)));
    }

    ASTNode word() throws Exception {
        return new Word(eat(TokenType.Word));
    }

    ASTNode atom() throws NumberFormatException, Exception {
        if(peek(TokenType.Int))
            return number();
        return word();
    }

    ASTNode group() throws NumberFormatException, Exception {
        GroupNode gnode = new GroupNode(new ArrayList<ASTNode>());
        while(peek(TokenType.If) || peek(TokenType.Int) || peek(TokenType.Word) || peek(TokenType.While)){
            if(peek(TokenType.If))
                gnode.Items.add(ifs());
            if(peek(TokenType.Int))
                gnode.Items.add(number());
            if(peek(TokenType.Word))
                gnode.Items.add(word());
            if(peek(TokenType.While))
                gnode.Items.add(whiles());
        }
        return gnode;
    }

    ASTNode ifs() throws Exception {
        eat(TokenType.If);
        ASTNode c = group();
        eat(TokenType.Colon);
        ASTNode b = group();
        eat(TokenType.Semicolon);
        return new IfStatement((GroupNode)c, (GroupNode)b);
    }

    ASTNode whiles() throws Exception {
        eat(TokenType.While);
        eat(TokenType.Colon);
        ASTNode b = group();
        eat(TokenType.Semicolon);
        return new WhileLoop((GroupNode)b);
    }

    ASTNode worddef() throws Exception {
        eat(TokenType.Colon);
        String name = ((Word)word()).word;
        ASTNode body = group();
        eat(TokenType.Semicolon);
        return new Macro((GroupNode)body, name);
    }

    ASTNode root() throws Exception {
        Root root = new Root(new ArrayList<ASTNode>());
        while(peek(TokenType.If) || peek(TokenType.Int) || peek(TokenType.Word) || peek(TokenType.Colon) || peek(TokenType.While)) {
            if(peek(TokenType.If)) {
                root.Program.add(ifs());
            } else if(peek(TokenType.Int)) {
                root.Program.add(number());
            } else if(peek(TokenType.Word)){
                root.Program.add(word());
            } else if(peek(TokenType.Colon)){
                root.Program.add(worddef());
            } else if(peek(TokenType.While)){
                root.Program.add(whiles());
            } else {
                continue;
            }
        }
        return root;
    }

    //#endregion Parsing
}
