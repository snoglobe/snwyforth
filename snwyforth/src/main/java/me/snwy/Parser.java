package me.snwy;

import java.nio.file.Path;
import java.nio.file.Files;
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

    ASTNode imports() throws Exception {
        eat(TokenType.Import);
        return new ImportStatement(eat(TokenType.Word));
    }

    ASTNode root() throws Exception {
        Root root = new Root(new ArrayList<ASTNode>());
        while(peek(TokenType.If) || peek(TokenType.Int) || peek(TokenType.Word) || peek(TokenType.Colon) || peek(TokenType.While) || peek(TokenType.Import)) {
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
            } else if(peek(TokenType.Import)){
                root.Program.add(imports());
            } else {
                continue;
            }
        }
        ArrayList<ASTNode> ImportAST = new ArrayList<>();
        for(int in = 0; in < root.Program.size(); in++){
            ASTNode i = root.Program.get(in);
            if(i instanceof ImportStatement) {
                String path = ((ImportStatement)i).toImport;
                String Program;
                if(Files.exists(Path.of(App.thisPath() + "/dict/" + path)))
                    Program = Files.readString(Path.of(App.thisPath() + "/dict/" + path));
                else
                    Program = Files.readString(Path.of(path));
                Lexer l = new Lexer(Program);
                l.Lex();
                Parser p = new Parser(l.Tokens);
                ImportAST.addAll(((Root)p.root()).Program);
                root.Program.remove(i);
            }
        }
        ImportAST.addAll(root.Program);
        root.Program = ImportAST;
        return root;
    }

    //#endregion Parsing
}
