package msgpack4z;

import org.msgpack.MessagePack;
import org.msgpack.unpacker.MessagePackBufferUnpacker;

public final class Msgpack06 {
    private Msgpack06() {
        throw new RuntimeException("should not create instance");
    }

    public static MsgPacker defaultPacker() {
        final MessagePack msgpack = new MessagePack();
        return packer(msgpack);
    }

    public static MsgPacker packer(MessagePack msgpack) {
        return new Msgpack06Packer(msgpack);
    }

    public static MsgUnpacker defaultUnpacker(byte[] bytes) {
        final MessagePack msgpack = new MessagePack();
        final MessagePackBufferUnpacker u = new MessagePackBufferUnpacker(msgpack);
        u.setArraySizeLimit(Integer.MAX_VALUE);
        u.setMapSizeLimit(Integer.MAX_VALUE);
        u.setRawSizeLimit(Integer.MAX_VALUE);
        u.wrap(bytes);
        return unpacker(u);
    }

    public static MsgUnpacker unpacker(MessagePackBufferUnpacker u) {
        return new Msgpack06Unpacker(u);
    }
}
