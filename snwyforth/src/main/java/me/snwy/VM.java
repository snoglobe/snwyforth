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
        System.out.println(stack);
        switch(instr){
            case 0x00:
                stack.push(Byte.valueOf(oparg));
                break;
            case 0x01:
                // TODO: remove this
                break;
            case 0x02:
                byte trap = stack.pop();
                switch(trap) {
                    case 0x0:
                        System.out.print(stack.pop().toString());
                    case 0x1:
                        stack.push((byte)System.in.read());
                } 
                break;
            case 0x03:
                stack.push((byte)(stack.pop() + stack.pop()));
                break;
            case 0x04:
                stack.push((byte)(stack.pop() - stack.pop()));
                break;
            case 0x05:
                stack.push((byte)(stack.pop() * stack.pop()));
                break;
            case 0x06:
                stack.push((byte)(stack.pop() / stack.pop()));
                break;
            case 0x07:
                stack.push((byte)(stack.pop() % stack.pop()));
                break;
            case 0x08:
                stack.push((byte)(stack.pop() == stack.pop() ? 1 : 0));
                break;
            case 0x09:
                stack.push((byte)(stack.pop() != stack.pop() ? 1 : 0));
                break;
            case 0x0A:
                stack.push((byte)(stack.pop() <= stack.pop() ? 1 : 0));
                break;
            case 0x0B:
                stack.push((byte)(stack.pop() >= stack.pop() ? 1 : 0));
                break;
            case 0x0C:
                stack.push((byte)(stack.pop() < stack.pop() ? 1 : 0));
                break;
            case 0x0D:
                stack.push((byte)(stack.pop() > stack.pop() ? 1 : 0));
                break;
            case 0x0E:
                if(stack.pop() != 0) {
                    ExecuteStream(GetStream(oparg));
                }
                break;
            case 0x0F:
                stack.push(stack.peek());
                break;
            case 0x10: {
                byte a = stack.pop();
                byte b = stack.pop();
                stack.push(a);
                stack.push(b);
                break;
            }
            case 0x11: {
                byte c = stack.pop();
                byte b = stack.pop();
                byte a = stack.pop();
                stack.push(b);
                stack.push(c);
                stack.push(a);
                break;
            }
            case 0x12: {
                byte c = stack.pop();
                byte b = stack.pop();
                byte a = stack.pop();
                stack.push(c);
                stack.push(a);
                stack.push(b);
                break;
            }
            case 0x13: {
                stack.pop();
                break;
            }
            case 0x14: {
                byte a = stack.pop();
                byte b = stack.pop();
                stack.push(a);
                stack.push(b);
                stack.push(a);
                break;
            }
            case 0x15: {
                byte a = stack.pop();
                stack.pop();
                stack.push(a);
                break;
            }
            case 0x16: {
                byte b = stack.pop();
                byte a = stack.pop();
                stack.push(b);
                stack.push(a);
                stack.push(b);
            }
            case 0x17: {
                ExecuteStream(GetStream(oparg));
                break;
            }
        }
    }

    void dump(){
        System.out.println("[i] Stack : " + stack.toString());
    }
}