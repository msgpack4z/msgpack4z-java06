package msgpack4z;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import org.msgpack.unpacker.MessagePackUnpacker;

public class Msgpack06Unpacker implements MsgUnpacker {
    private final MessagePackUnpacker self;

    public Msgpack06Unpacker(MessagePackUnpacker self) {
        this.self = self;
    }

    private static final Method getHeadByteMethod;

    static {
        try {
            getHeadByteMethod = MessagePackUnpacker.class.getDeclaredMethod("getHeadByte");
            getHeadByteMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private byte getHeadByte() {
        try {
            return (byte) getHeadByteMethod.invoke(self);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public MsgType nextType() throws IOException {
        switch (self.getNextType()) {
            case NIL:
                return MsgType.NIL;
            case BOOLEAN:
                return MsgType.BOOLEAN;
            case INTEGER:
                return MsgType.INTEGER;
            case FLOAT:
                return MsgType.FLOAT;
            case ARRAY:
                return MsgType.ARRAY;
            case MAP:
                return MsgType.MAP;
            case RAW:
                final int b = ((int) getHeadByte()) & 0xff;
                if ((b & 0xe0) == 0xa0) {
                    return MsgType.STRING;
                }
                switch (b) {
                    case 0xc4:
                    case 0xc5:
                    case 0xc6:
                        return MsgType.BINARY;
                    case 0xd9:
                    case 0xda:
                    case 0xdb:
                        return MsgType.STRING;
                    default:
                        throw new RuntimeException("impossible " + b);
                }
            default:
                throw new RuntimeException("impossible");
        }
    }

    @Override
    public byte unpackByte() throws IOException {
        return self.readByte();
    }

    @Override
    public short unpackShort() throws IOException {
        return self.readShort();
    }

    @Override
    public int unpackInt() throws IOException {
        return self.readInt();
    }

    @Override
    public long unpackLong() throws IOException {
        return self.readLong();
    }

    @Override
    public BigInteger unpackBigInteger() throws IOException {
        return self.readBigInteger();
    }

    @Override
    public double unpackDouble() throws IOException {
        return self.readDouble();
    }

    @Override
    public float unpackFloat() throws IOException {
        return self.readFloat();
    }

    @Override
    public int unpackArrayHeader() throws IOException {
        return self.readArrayBegin();
    }

    @Override
    public void arrayEnd() throws IOException {
        self.readArrayEnd();
    }

    @Override
    public void mapEnd() throws IOException {
        self.readMapEnd();
    }

    @Override
    public int unpackMapHeader() throws IOException {
        return self.readMapBegin();
    }

    @Override
    public boolean unpackBoolean() throws IOException {
        return self.readBoolean();
    }

    @Override
    public void unpackNil() throws IOException {
        self.readNil();
    }

    @Override
    public String unpackString() throws IOException {
        return self.readString();
    }

    @Override
    public byte[] unpackBinary() throws IOException {
        return self.readByteArray();
    }

    @Override
    public void close() throws IOException {
        self.close();
    }
}
