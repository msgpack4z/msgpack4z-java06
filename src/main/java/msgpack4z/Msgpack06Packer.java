package msgpack4z;

import org.msgpack.MessagePack;
import org.msgpack.packer.MessagePackBufferPacker;
import org.msgpack.packer.MessagePackPacker;
import org.msgpack.packer.PackerStack;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigInteger;

final class Msgpack06Packer extends MessagePackBufferPacker implements MsgPacker {
    public Msgpack06Packer(MessagePack msgpack, int bufferSize) {
        super(msgpack, bufferSize);
    }

    public Msgpack06Packer(MessagePack msgpack) {
        super(msgpack);
    }

    private static final Field stackField;
    private PackerStack stack;

    private synchronized PackerStack stack() {
        if (stack == null) {
            try {
                synchronized (this) {
                    stack = (PackerStack) stackField.get(this);
                }
                return stack;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        } else {
            return stack;
        }
    }

    static {
        try {
            stackField = MessagePackPacker.class.getDeclaredField("stack");
            stackField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
    }

    @Override
    public void packByte(byte a) throws IOException {
        write(a);
    }

    @Override
    public void packShort(short a) throws IOException {
        write(a);
    }

    @Override
    public void packInt(int a) throws IOException {
        write(a);
    }

    @Override
    public void packLong(long a) throws IOException {
        write(a);
    }

    @Override
    public void packDouble(double a) throws IOException {
        write(a);
    }

    @Override
    public void packFloat(float a) throws IOException {
        write(a);
    }

    @Override
    public void packBigInteger(BigInteger a) throws IOException {
        write(a);
    }

    @Override
    public void packArrayHeader(int a) throws IOException {
        writeArrayBegin(a);
    }

    @Override
    public void arrayEnd() throws IOException {
        writeArrayEnd();
    }

    @Override
    public void packMapHeader(int a) throws IOException {
        writeMapBegin(a);
    }

    @Override
    public void mapEnd() throws IOException {
        writeMapEnd();
    }

    @Override
    public void packBoolean(boolean a) throws IOException {
        write(a);
    }

    @Override
    public void packNil() throws IOException {
        writeNil();
    }

    @Override
    public void packString(String a) throws IOException {
        write(a);
    }

    @Override
    public void packBinary(byte[] a) throws IOException {
        final int len = a.length;
        if (len < 256) {
            out.writeByteAndByte((byte) 0xc4, (byte) len);
        } else if (len < 65536) {
            out.writeByteAndShort((byte) 0xc5, (short) len);
        } else {
            out.writeByteAndInt((byte) 0xc6, len);
        }
        out.write(a, 0, len);
        stack().reduceCount();
    }

    @Override
    public byte[] result() {
        return toByteArray();
    }
}
