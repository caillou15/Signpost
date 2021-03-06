package gollorum.signpost;

import gollorum.signpost.blockpartdata.types.Sign;
import gollorum.signpost.minecraft.Config;
import gollorum.signpost.minecraft.block.tiles.PostTile;
import gollorum.signpost.minecraft.gui.Colors;
import gollorum.signpost.minecraft.gui.ConfirmTeleportGui;
import gollorum.signpost.minecraft.utils.LangKeys;
import gollorum.signpost.minecraft.utils.Inventory;
import gollorum.signpost.networking.PacketHandler;
import gollorum.signpost.utils.TileEntityUtils;
import gollorum.signpost.utils.WaystoneLocationData;
import gollorum.signpost.utils.math.Angle;
import gollorum.signpost.utils.math.geometry.Vector3;
import gollorum.signpost.utils.serialization.OptionalSerializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Supplier;

public class Teleport {

    public static void toWaystone(WaystoneHandle waystone, PlayerEntity player) {
        assert Signpost.getServerType().isServer;
        WaystoneLocationData waystoneData = WaystoneLibrary.getInstance().getLocationData(waystone);
        toWaystone(waystoneData, player);
    }

    public static void toWaystone(WaystoneLocationData waystoneData, PlayerEntity player){
        assert Signpost.getServerType().isServer;
        waystoneData.block.world.mapLeft(Optional::of)
            .leftOr(i -> TileEntityUtils.findWorld(i, false))
        .ifPresent(unspecificWorld -> {
            if(!(unspecificWorld instanceof ServerWorld)) return;
            ServerWorld world = (ServerWorld) unspecificWorld;
            Vector3 location = waystoneData.spawn;
            Vector3 diff = Vector3.fromBlockPos(waystoneData.block.blockPos).add(new Vector3(0.5f, 0.5f, 0.5f))
                .subtract(location.withY(y -> y + player.getEyeHeight()));
            Angle yaw = Angle.between(
                0, 1,
                diff.x, diff.z
            );
            Angle pitch = Angle.fromRadians((float) (Math.PI / 2 + Math.atan(Math.sqrt(diff.x * diff.x + diff.z * diff.z) / diff.y)));
            if(!player.world.getDimensionType().equals(world.getDimensionType()))
                player.changeDimension(world, new ITeleporter() {});
            player.rotationYaw = yaw.degrees();
            player.rotationPitch = pitch.degrees();
            player.setPositionAndUpdate(location.x, location.y, location.z);
        });
    }

    public static void requestOnClient(
        String waystoneName,
        ItemStack cost,
        boolean isDiscovered,
        Optional<ConfirmTeleportGui.SignInfo> signInfo
    ) {
        ConfirmTeleportGui.display(waystoneName, cost, isDiscovered, signInfo);
    }

    public static ItemStack getCost(PlayerEntity player, Vector3 from, Vector3 to) {
        Item item = Registry.ITEM.getOrDefault(new ResourceLocation(Config.Server.costItem.get()));
        if(item.equals(Items.AIR) || player.isCreative() || player.isSpectator()) return ItemStack.EMPTY;
        int distancePerPayment = Config.Server.distancePerPayment.get();
        int distanceDependentCost = distancePerPayment < 0
            ? 0
            : (int)(from.distanceTo(to) / distancePerPayment);
        return new ItemStack(item, Config.Server.constantPayment.get() + distanceDependentCost);
    }

    public static final class Request implements PacketHandler.Event<Request.Package> {

        @Override
        public Class<Package> getMessageClass() {
            return Package.class;
        }

        @Override
        public void encode(Package message, PacketBuffer buffer) {
            buffer.writeString(message.waystoneName);
            buffer.writeBoolean(message.isDiscovered);
            buffer.writeItemStack(message.cost);
            new OptionalSerializer<>(PostTile.TilePartInfo.SERIALIZER).writeTo(message.tilePartInfo, buffer);
        }

        @Override
        public Package decode(PacketBuffer buffer) {
            return new Package(
                buffer.readString(32767),
                buffer.readBoolean(),
                buffer.readItemStack(),
                new OptionalSerializer<>(PostTile.TilePartInfo.SERIALIZER).readFrom(buffer)
            );
        }

        @Override
        public void handle(
            Package message, Supplier<NetworkEvent.Context> contextGetter
        ) {
            NetworkEvent.Context context = contextGetter.get();
            context.enqueueWork(() -> {
                if(context.getDirection().getReceptionSide().isServer()) {
                    ServerPlayerEntity player = context.getSender();
                    Optional<WaystoneHandle> waystone = WaystoneLibrary.getInstance().getHandleByName(message.waystoneName);
                    if(waystone.isPresent()) {
                        WaystoneHandle handle = waystone.get();
                        WaystoneLocationData waystoneData = WaystoneLibrary.getInstance().getLocationData(handle);
                        Inventory.tryPay(
                            player,
                            Teleport.getCost(player, Vector3.fromBlockPos(waystoneData.block.blockPos), waystoneData.spawn),
                            p -> Teleport.toWaystone(waystoneData, p)
                        );
                    } else player.sendMessage(
                        new TranslationTextComponent(LangKeys.waystoneNotFound, Colors.wrap(message.waystoneName, Colors.highlight)),
                        Util.DUMMY_UUID
                    );
                } else {
                    if(Config.Client.enableConfirmationScreen.get()) requestOnClient(
                        message.waystoneName,
                        message.cost,
                        message.isDiscovered,
                        message.tilePartInfo.flatMap(info -> TileEntityUtils.findTileEntity(
                            info.dimensionKey,
                            true,
                            info.pos,
                            PostTile.class
                        ).flatMap(tile -> tile.getPart(info.identifier)
                            .flatMap(part -> part.blockPart instanceof Sign
                                ? Optional.of(new ConfirmTeleportGui.SignInfo(tile, (Sign) part.blockPart, info, part.offset)) : Optional.empty()
                            ))));
                    else PacketHandler.sendToServer(new Teleport.Request.Package(
                        message.waystoneName,
                        message.isDiscovered,
                        message.cost,
                        message.tilePartInfo
                    ));
                }
            });
            context.setPacketHandled(true);
        }

        public static final class Package {
            public final String waystoneName;
            public final boolean isDiscovered;
            public final ItemStack cost;
            public final Optional<PostTile.TilePartInfo> tilePartInfo;
            public Package(
                String waystoneName,
                boolean isDiscovered, ItemStack cost,
                Optional<PostTile.TilePartInfo> tilePartInfo
            ) {
                this.waystoneName = waystoneName;
                this.isDiscovered = isDiscovered;
                this.cost = cost;
                this.tilePartInfo = tilePartInfo;
            }
        }

    }

}
