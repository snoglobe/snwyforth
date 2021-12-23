package me.snwy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Compiler {
    Root ASTree;
    List<ILChunk> Compiled;
    List<ILChunk> DataSection;
    HashMap<String, Byte> FunctionPointers = new HashMap<>();
    int NextAvailableFpointer = 0;

    private static final HashMap<String, Byte> BuiltinWords = new HashMap<>();
    static {
        BuiltinWords.put("trap", Byte.valueOf((byte)0x02));
        BuiltinWords.put("+", Byte.valueOf((byte)0x03));
        BuiltinWords.put("-", Byte.valueOf((byte)0x04));
        BuiltinWords.put("*", Byte.valueOf((byte)0x05));
        BuiltinWords.put("/", Byte.valueOf((byte)0x06));
        BuiltinWords.put("%", Byte.valueOf((byte)0x07));
        BuiltinWords.put("=", Byte.valueOf((byte)0x08));
        BuiltinWords.put("!=", Byte.valueOf((byte)0x09));
        BuiltinWords.put("<=", Byte.valueOf((byte)0x0A));
        BuiltinWords.put(">=", Byte.valueOf((byte)0x0B));
        BuiltinWords.put("<", Byte.valueOf((byte)0x0C));
        BuiltinWords.put(">", Byte.valueOf((byte)0x0D));
        BuiltinWords.put("dup", Byte.valueOf((byte)0x0F));
        BuiltinWords.put("swap", Byte.valueOf((byte)0x10));
        BuiltinWords.put("rotl", Byte.valueOf((byte)0x11));
        BuiltinWords.put("rotr", Byte.valueOf((byte)0x12));
        BuiltinWords.put("drop", Byte.valueOf((byte)0x13));
        BuiltinWords.put("over", Byte.valueOf((byte)0x14));
        BuiltinWords.put("nip", Byte.valueOf((byte)0x15));
        BuiltinWords.put("tuck", Byte.valueOf((byte)0x16));
    }

    Compiler(Root AST){
        this.ASTree = AST;
        this.Compiled = new ArrayList<>();
        this.DataSection = new ArrayList<>();
    }

    // i'd rather deepthroat a cactus than maintain this ever
    void Compile() {
        for(ASTNode i : ASTree.Program) {
            if(i instanceof Macro) {
                ILChunk[] compiled = Compile(i);
                int BlobIndex = NextAvailableFpointer;
                NextAvailableFpointer += (compiled.length + 1) * 2;
                DataSection.addAll(Arrays.asList(compiled));
                DataSection.add(new ILChunk(OpCode.Ret, (byte)0x00));
                FunctionPointers.put(((Macro)i).Name, ((byte)BlobIndex));
            } else {
                Compiled.addAll(Arrays.asList(Compile(i)));
            }
        }
    }

    // this function is pure cancer and it's presence disproves the existence of a higher being in any capacity
    ILChunk[] Compile(ASTNode i) {
        if(i instanceof Word) {
            if(!BuiltinWords.containsKey(((Word)i).word)){
                if(!FunctionPointers.containsKey(((Word)i).word)) {
                    System.out.println("[x] Undefined word " + ((Word)i).word);
                } else {
                    return new ILChunk[]{ new ILChunk(OpCode.FCall, FunctionPointers.get(((Word)i).word).byteValue())};
                }
            } else 
                return new ILChunk[] { new ILChunk(OpCode.fromId(BuiltinWords.get(((Word)i).word)), (byte)0x0)};
        } else if(i instanceof Number) {
            return new ILChunk[] { new ILChunk(OpCode.Push, (byte)((Number)i).val)};
        } else if(i instanceof Macro) {
            List<ILChunk> compiledWord = new ArrayList<>();
            for(ASTNode inode : ((Macro)i).Contents.Items){
                compiledWord.add(Compile(inode)[0]);
            }
            ILChunk[] ret = new ILChunk[compiledWord.size()];
            ret = compiledWord.toArray(new ILChunk[ret.length]);
            return ret;
        } else if(i instanceof GroupNode) {
            List<ILChunk> out = new ArrayList<>();
            for(ASTNode j : ((GroupNode)i).Items) {
                out.add(Compile(j)[0]);
            }
            ILChunk[] ret = new ILChunk[out.size()];
            ret = out.toArray(new ILChunk[ret.length]);
            return ret;
        } else if(i instanceof IfStatement) {
            ILChunk[] cond = Compile(((IfStatement)i).condition);
            ILChunk[] body = Compile(((IfStatement)i).body);
            int BlobIndex = NextAvailableFpointer;
            NextAvailableFpointer += (body.length + 1) * 2;
            DataSection.addAll(Arrays.asList(body));
            DataSection.add(new ILChunk(OpCode.Ret, (byte)0x00));
            ArrayList<ILChunk> out = new ArrayList<>(Arrays.asList(cond)); 
            out.add(new ILChunk(OpCode.If, (byte)BlobIndex));
            ILChunk[] ret = new ILChunk[out.size()];
            ret = out.toArray(new ILChunk[ret.length]);
            return ret;
        } else if(i instanceof WhileLoop) {
            ILChunk[] body = Compile(((WhileLoop)i).body);
            int BlobIndex = NextAvailableFpointer;
            NextAvailableFpointer += (body.length + 1) * 2;
            DataSection.addAll(Arrays.asList(body));
            DataSection.add(new ILChunk(OpCode.Ret, (byte)0x00));
            ArrayList<ILChunk> out = new ArrayList<>(); 
            out.add(new ILChunk(OpCode.While, (byte)BlobIndex));
            ILChunk[] ret = new ILChunk[out.size()];
            ret = out.toArray(new ILChunk[ret.length]);
            return ret;
        }
        System.out.println("[x] Cannot compile object " + i);
        dump();
        return null;
    }

    void dump() {
        System.out.println("[i] Dump info: ");
        System.out.println("[i] Compiled " + Compiled.toString());
        System.out.println("[i] FunctionPointers " + FunctionPointers.toString());
        System.out.println("[i] DataSection " + DataSection.toString());
    }
}
