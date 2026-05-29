package io.github.cvrunmin.lanfasie.benderson.utils;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.StreamCodec;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CodecUtils {
    public static final StreamCodec<ByteBuf, HashMap<UUID, Float>> UUID_FLOAT_MAP_STREAM_CODEC = new StreamCodec<>() {
        @Override
        public HashMap<UUID, Float> decode(ByteBuf input) {
            var size = input.readInt();
            HashMap<UUID, Float> map = new HashMap<>(size);
            for (int i = 0; i < size; i++) {
                var uuid = UUIDUtil.STREAM_CODEC.decode(input);
                var value = input.readFloat();
                map.put(uuid, value);
            }
            return map;
        }

        @Override
        public void encode(ByteBuf output, HashMap<UUID, Float> value) {
            output.writeInt(value.size());
            for (Map.Entry<UUID, Float> entry : value.entrySet()) {
                UUIDUtil.STREAM_CODEC.encode(output, entry.getKey());
                output.writeFloat(entry.getValue());
            }
        }
    };
}
