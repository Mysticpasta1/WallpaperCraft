package net.mystic.wallpapercraft.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.mystic.wallpapercraft.Wallpapercraft;
import org.jetbrains.annotations.NotNull;

public record VariantScrollPayload(int delta) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<VariantScrollPayload> TYPE =
            new CustomPacketPayload.Type<>(Wallpapercraft.getId("variant_scroll"));

    public static final StreamCodec<ByteBuf, VariantScrollPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.VAR_INT, VariantScrollPayload::delta, VariantScrollPayload::new);

    @Override
    public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

