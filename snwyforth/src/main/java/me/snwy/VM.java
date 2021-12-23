package me.snwy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

class VM {
    byte[] program;
    byte[] data;
    HashMap<String, Byte> symbols;
    Stack<Byte> stack;
    int pc = 0;

    VM(byte[] program, byte[] data, HashMap<String, Byte> symbols){
        this.program = program;
        this.data = data;
        this.symbols = symbols;
        this.stack = new Stack<>();
    }

    static byte[] toPrimitives(Byte[] oBytes)
    {
        byte[] bytes = new byte[oBytes.length];
        for(int i = 0; i < oBytes.length; i++){
            bytes[i] = oBytes[i];
        }
        return bytes;
    }

    static Byte[] toObjects(byte[] bytesPrim) {

        Byte[] bytes = new Byte[bytesPrim.length];
        int i = 0;
        for (byte b : bytesPrim) bytes[i++] = b; //Autoboxing
        return bytes;
    
    }

    void Step() throws IOException {
        byte instr = program[pc];
        byte oparg = program[++pc];
        Execute(instr, oparg);
        pc++;
    }

    void ExecuteStream(byte[] code) throws IOException{
        for(int lpc = 0; lpc < code.length; lpc += 1){
            byte instr = code[lpc];
            byte oparg = code[++lpc];
            if(instr == 0x18)
                return;
            Execute(instr, oparg);
        }
    }

    byte[] GetStream(byte base){
        ArrayList<Byte> outchunk = new ArrayList<>();
        int i = base;
        while(data[i] != 0x18) {
            outchunk.add(data[i]);
            i++;
        }
        Byte[] temp = outchunk.toArray(new Byte[outchunk.size()]);
        return toPrimitives(temp);
    }

    void Execute(byte instr, byte oparg) throws IOException {
        switch(instr){
            case 0x00: // push
                stack.push(Byte.valueOf(oparg));
                break;
            case 0x02: // trap
                byte trap = stack.pop();
                switch(trap) {
                    case 0x0:
                        System.out.print(stack.pop().toString());
                        break;
                    case 0x1:
                        System.out.print(new String(new byte[]{stack.pop()}, "US-ASCII"));
                        break;
                    case 0x2:
                        stack.push((byte)System.in.read());
                        break;
                    case 0x3:
                        System.exit(stack.pop());
                        break;
                    case 0x4:
                        System.out.print((char)7);
                        break;
                    case 0x5:
                        stack.push((byte)Integer.parseInt(System.console().readLine()));
                        break;
                } 
                break;
            case 0x03: // add
                stack.push((byte)(stack.pop() + stack.pop()));
                break;
            case 0x04: // sub
                stack.push((byte)(stack.pop() - stack.pop()));
                break;
            case 0x05: // mul
                stack.push((byte)(stack.pop() * stack.pop()));
                break;
            case 0x06: // div
                stack.push((byte)(stack.pop() / stack.pop()));
                break;
            case 0x07: // mod
                stack.push((byte)(stack.pop() % stack.pop()));
                break;
            case 0x08: // equals
                stack.push((byte)(stack.pop() == stack.pop() ? 1 : 0));
                break;
            case 0x09: // doesn't equal
                stack.push((byte)(stack.pop() != stack.pop() ? 1 : 0));
                break;
            case 0x0A: // greater or equal
                stack.push((byte)(stack.pop() <= stack.pop() ? 1 : 0));
                break;
            case 0x0B: // less or equal
                stack.push((byte)(stack.pop() >= stack.pop() ? 1 : 0));
                break;
            case 0x0C: // greater than
                stack.push((byte)(stack.pop() > stack.pop() ? 1 : 0));
                break;
            case 0x0D: // less than
                stack.push((byte)(stack.pop() < stack.pop() ? 1 : 0));
                break;
            case 0x0E: // if
                if(stack.pop() != 0) {
                    ExecuteStream(GetStream(oparg));
                }
                break;
            case 0x0F: // dup
                stack.push(stack.peek());
                break;
            case 0x10: { // swap
                byte a = stack.pop();
                byte b = stack.pop();
                stack.push(a);
                stack.push(b);
                break;
            }
            case 0x11: { // rotl
                byte c = stack.pop();
                byte b = stack.pop();
                byte a = stack.pop();
                stack.push(b);
                stack.push(c);
                stack.push(a);
                break;
            }
            case 0x12: { // rotr
                byte c = stack.pop();
                byte b = stack.pop();
                byte a = stack.pop();
                stack.push(c);
                stack.push(a);
                stack.push(b);
                break;
            }
            case 0x13: { // drop
                stack.pop();
                break;
            }
            case 0x14: { // over
                byte a = stack.pop();
                byte b = stack.pop();
                stack.push(a);
                stack.push(b);
                stack.push(a);
                break;
            }
            case 0x15: { // nip
                byte a = stack.pop();
                stack.pop();
                stack.push(a);
                break;
            }
            case 0x16: { // tuck
                byte b = stack.pop();
                byte a = stack.pop();
                stack.push(b);
                stack.push(a);
                stack.push(b);
                break;
            }
            case 0x17: { // word execute
                ExecuteStream(GetStream(oparg));
                break;
            }
            case 0x19: { // while
                while(stack.pop() != 0){
                    ExecuteStream(GetStream(oparg));
                }
                break;
            }
        }
        if(App.verbose)
            dump();
    }

    void dump(){
        System.out.println("[i] Stack : " + stack.toString());
    }
}