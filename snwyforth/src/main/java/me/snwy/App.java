package me.snwy;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class App 
{
    static boolean verbose;
    public static void main( String[] args ) throws Exception
    {
        if(args.length > 0 && !args[0].equals("-v"))
        {
            verbose = Arrays.asList(args).contains("-v");
            String actual = Files.readString(Path.of(args[0]));
            Lexer l = new Lexer(actual);
            l.Lex();
            System.out.println(l.Tokens);
            Parser p = new Parser(l.Tokens);
            ASTNode AST = p.root();
            System.out.println(AST);
            Compiler c = new Compiler((Root)AST);
            c.Compile();
            if(verbose)
                c.dump();
            VM vm = new VM(ILChunkToList(c.Compiled), ILChunkToList(c.DataSection), c.FunctionPointers);
            while(vm.pc < vm.program.length){
                if(verbose)
                    vm.dump();
                vm.Step();
            }
        } else {
            if(args.length > 0)
                verbose = Arrays.asList(args).contains("-v");
            Package mainPackage = App.class.getPackage();
            String version = mainPackage.getImplementationVersion();
            System.out.println("snwyforth v" + version);
            VM vm = new VM(new byte[0], new byte[0], new HashMap<>());
            boolean exit = false;
            Scanner sc = new Scanner(System.in);
            while(!exit) {
                System.out.print("--> ");
                String text = sc.nextLine();
                Lexer l = new Lexer(text);
                l.Lex();
                Parser p = new Parser(l.Tokens);
                Root AST = (Root)p.root();
                Compiler c = new Compiler(AST);
                c.FunctionPointers = vm.symbols;
                c.Compile();
                vm.program = ILChunkToList(c.Compiled);
                vm.data = VM.toPrimitives(concatWithCollection(VM.toObjects(vm.data), VM.toObjects(ILChunkToList(c.DataSection))));
                vm.symbols.putAll(c.FunctionPointers);
                vm.pc = 0;
                while(vm.pc < vm.program.length){
                    vm.Step();
                }
                System.out.println(vm.failed ? " ?" : " ok");
            }
            sc.close();
        }
    }

    public static String thisPath() throws UnsupportedEncodingException{
        String path = Path.of(App.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent().toString();
        return URLDecoder.decode(path, "UTF-8");
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

    static <T> T[] concatWithCollection(T[] array1, T[] array2) {
        List<T> resultList = new ArrayList<>(array1.length + array2.length);
        Collections.addAll(resultList, array1);
        Collections.addAll(resultList, array2);
    
        @SuppressWarnings("unchecked")
        //the type cast is safe as the array1 has the type T[]
        T[] resultArray = (T[]) Array.newInstance(array1.getClass().getComponentType(), 0);
        return resultList.toArray(resultArray);
    }
}
