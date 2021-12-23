package me.snwy;

import java.util.List;

abstract class ASTNode {}

class Number extends ASTNode {
    int val;

    Number(int val){
        this.val = val;
    }

    @Override
    public String toString(){
        return Integer.toString(val);
    }
}

class Word extends ASTNode {
    String word;

    Word(String word){
        this.word = word;
    }

    @Override
    public String toString(){
        return word;
    }
}

class GroupNode extends ASTNode {
    List<ASTNode> Items;
    
    GroupNode(List<ASTNode> Items){
        this.Items = Items;
    }

    @Override
    public String toString(){
        return Items.toString();
    }
}

class IfStatement extends ASTNode {
    GroupNode condition;
    GroupNode body;

    IfStatement(GroupNode cNode, GroupNode bNode){
        this.condition = cNode;
        this.body = bNode;
    }

    @Override
    public String toString(){
        return "if " + condition.toString() + " : " + body.toString();
    }
}

class WhileLoop extends ASTNode {
    GroupNode body;

    WhileLoop(GroupNode bNode){
        this.body = bNode;
    }

    @Override
    public String toString(){
        return "while : " + body.toString();
    }
}

class Macro extends ASTNode {
    GroupNode Contents;
    String Name;

    Macro(GroupNode Contents, String Name){
        this.Contents = Contents;
        this.Name = Name;
    }

    @Override
    public String toString(){
        return "word " + Name + " : " + Contents.toString();
    }
}

class Root extends ASTNode {
    List<ASTNode> Program;

    Root(List<ASTNode> Program) {
        this.Program = Program;
    }

    @Override
    public String toString(){
        return Program.toString();
    }
}