package me.snwy;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class App 
{
    static boolean verbose;
    public static void main( String[] args ) throws Exception
    {
        verbose = Arrays.asList(args).contains("-v");
        String actual = Files.readString(Path.of(args[0]));
        Lexer l = new Lexer(actual);
        l.Lex();
        Parser p = new Parser(l.Tokens);
        ASTNode AST = p.root();
        Compiler c = new Compiler((Root)AST);
        c.Compile();
        if(verbose)
            c.dump();
        VM vm = new VM(ILChunkToList(c.Compiled), ILChunkToList(c.DataSection), c.FunctionPointers);
        while(vm.pc < vm.program.length){
            vm.Step();
        }
        if(verbose)
            vm.dump();
    }

    static byte[] ILChunkToList(List<ILChunk> chunks){
        Byte[] program = new Byte[chunks.size() * 2];
        ArrayList<Byte> li = new ArrayList<>();
        for(int i = 0; i < chunks.size(); i++) {
            li.add(chunks.get(i).toBytes()[0]);
            li.add(chunks.get(i).toBytes()[1]);
        }
        program = li.toArray(program);
        byte[] bytes = new byte[program.length];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = program[i];
        }
        return bytes;
    }
}
