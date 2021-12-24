package me.snwy;

enum OpCode {
    // VM THINGS
    Push((byte)0x00), // push immediate : 0x00
    PushL((byte)0x01), // push load : 0x01

    Trap((byte)0x02), // the VM will have these built in for things like I/O and shit : 0x02

    // ARITHMETIC OPS

    Add((byte)0x03), // + : 0x03
    Sub((byte)0x04), // - : 0x04
    Mul((byte)0x05), // * : 0x05
    Div((byte)0x06), // / : 0x06
    Mod((byte)0x07), // % : 0x07

    // CONDITIONAL OPS

    Eq((byte)0x08), // == : 0x08
    Neq((byte)0x09), // != : 0x09
    Leq((byte)0x0A), // <= : 0x0A
    Geq((byte)0x0B), // >= : 0x0B
    Lt((byte)0x0C), // < : 0x0C
    Gt((byte)0x0D), // > : 0x0D

    If((byte)0x0E), // the oparg is a pointer to the body in the storage section : 0x0E

    // STACK OPS

    Dup((byte)0x0F), // ( a -- a a ) : 0x0F
    Swap((byte)0x10), // ( a b -- b a ) : 0x10
    Rotl((byte)0x11), // ( a b c -- b c a ) : 0x11
    Rotr((byte)0x12), // ( a b c -- c a b ) : 0x12
    Drop((byte)0x13), // ( a -- ) : 0x13
    Over((byte)0x14), // ( a -- a b a ) : 0x14
    Nip((byte)0x15), // ( a b -- b ) : 0x15
    Tuck((byte)0x16), // ( a b -- b a b ) : 0x16

    FCall((byte)0x17), // Function call - oparg is function pointer
    Ret((byte)0x18), // return thing : 0x18

    While((byte)0x19), // while loop - oparg is body;

    Alloc((byte)0x1A), // ptr alloc - pop() is length, oparg is slot
    Store((byte)0x1B), // store - pop() is value, oparg is slot

    IncPointer((byte)0x1C), // increment pointer - slot is oparg
    DecPointer((byte)0x1D), // decrement pointer - slot is oparg
    SetPointer((byte)0x1E), // set pointer - value is pop()
    GetPointer((byte)0x1F), // get pointer - pushes value under pointer to the stack
    GetStored((byte)0x20); // get stored - oparg is slot
    byte opcode;

    OpCode(byte op) {
        opcode = op;
    }

    public static OpCode fromId(byte id) {
        for (OpCode type : values()) {
            if (type.opcode == id) {
                return type;
            }
        }
        return null;
    }
}

class ILChunk {
    public OpCode opCode; // the machine opcode
    public byte opArg; // usually a pointer to a larger piece of memory, or a const

    ILChunk(OpCode opCode, byte opArg)
    {
        this.opCode = opCode;
        this.opArg = opArg;
    }

    @Override
    public String toString(){
        return opCode.name() + String.format(": 0x%02X", opCode.opcode) + "(" + String.format("0x%02X", opArg) + ")";
    }

    public byte[] toBytes() {
        return new byte[] {opCode.opcode, opArg};
    }
}
