package com.EvgenWarGold.GregTechNightmare.Mixins.Late;

import java.util.List;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import vazkii.botania.api.mana.IManaReceiver;
import vazkii.botania.client.core.handler.HUDHandler;
import vazkii.botania.client.lib.LibResources;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.block.tile.TileSimpleInventory;
import vazkii.botania.common.block.tile.mana.TileSpreader;
import vazkii.botania.common.entity.EntityManaBurst;

@Mixin(value = TileSpreader.class, remap = false)
public abstract class TileSpreaderMixin extends TileSimpleInventory {

    @Shadow
    private IManaReceiver receiver;

    @Shadow
    private List<EntityManaBurst.PositionProperties> lastTentativeBurst;

    @Shadow
    private UUID identity;

    @Shadow
    private int mana;

    @Shadow
    private float rotationX;

    @Shadow
    private float rotationY;

    @Shadow
    private boolean requestsClientUpdate;

    @Shadow
    private int paddingColor;

    @Shadow
    private boolean canShootBurst;

    @Shadow
    private int pingbackTicks;

    @Shadow
    private double lastPingbackX;

    @Shadow
    private double lastPingbackY;

    @Shadow
    private double lastPingbackZ;

    @Shadow
    private String inputKey;

    @Shadow
    private String outputKey;

    @Shadow
    private boolean mapmakerOverride;

    @Shadow
    private int mmForcedColor;

    @Shadow
    private int mmForcedManaPayload;

    @Shadow
    private int mmForcedTicksBeforeManaLoss;

    @Shadow
    private float mmForcedManaLossPerTick;

    @Shadow
    private float mmForcedGravity;

    @Shadow
    private float mmForcedVelocityMultiplier;

    @Shadow
    private int knownMana;

    @Shadow
    private boolean hasReceivedInitialPacket;

    @Shadow
    public abstract UUID getIdentifier();

    @Shadow
    public abstract UUID getIdentifierUnsafe();

    @Shadow
    public abstract boolean isRedstone();

    @Shadow
    public abstract boolean isDreamwood();

    @Shadow
    public abstract int getMaxMana();

    private static final String TAG_HAS_IDENTITY = "hasIdentity";
    private static final String TAG_UUID_MOST = "uuidMost";
    private static final String TAG_UUID_LEAST = "uuidLeast";
    private static final String TAG_MANA = "mana";
    private static final String TAG_KNOWN_MANA = "knownMana";
    private static final String TAG_REQUEST_UPDATE = "requestUpdate";
    private static final String TAG_ROTATION_X = "rotationX";
    private static final String TAG_ROTATION_Y = "rotationY";
    private static final String TAG_PADDING_COLOR = "paddingColor";
    private static final String TAG_CAN_SHOOT_BURST = "canShootBurst";
    private static final String TAG_PINGBACK_TICKS = "pingbackTicks";
    private static final String TAG_LAST_PINGBACK_X = "lastPingbackX";
    private static final String TAG_LAST_PINGBACK_Y = "lastPingbackY";
    private static final String TAG_LAST_PINGBACK_Z = "lastPingbackZ";
    private static final String TAG_FORCE_CLIENT_BINDING_X = "forceClientBindingX";
    private static final String TAG_FORCE_CLIENT_BINDING_Y = "forceClientBindingY";
    private static final String TAG_FORCE_CLIENT_BINDING_Z = "forceClientBindingZ";
    private static final String TAG_INPUT_KEY = "inputKey";
    private static final String TAG_OUTPUT_KEY = "outputKey";
    private static final String TAG_MAPMAKER_OVERRIDE = "mapmakerOverrideEnabled";
    private static final String TAG_FORCED_COLOR = "mmForcedColor";
    private static final String TAG_FORCED_MANA_PAYLOAD = "mmForcedManaPayload";
    private static final String TAG_FORCED_TICKS_BEFORE_MANA_LOSS = "mmForcedTicksBeforeManaLoss";
    private static final String TAG_FORCED_MANA_LOSS_PER_TICK = "mmForcedManaLossPerTick";
    private static final String TAG_FORCED_GRAVITY = "mmForcedGravity";
    private static final String TAG_FORCED_VELOCITY_MULTIPLIER = "mmForcedVelocityMultiplier";

    private TileEntity getReceiverTile() {
        if (receiver == null) return null;

        if (receiver instanceof TileEntity) {
            return (TileEntity) receiver;
        }

        return null;
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "checkForReceiver", at = @At("TAIL"))
    private void onCheckForReceiver(CallbackInfo ci) {
        TileSpreader self = (TileSpreader) (Object) this;

        if (receiver != null) return;

        EntityManaBurst fakeBurst = self.getBurst(true);
        fakeBurst.setScanBeam();

        TileEntity tile = fakeBurst.getCollidedTile(true);

        if (tile instanceof IGregTechTileEntity gtTile) {
            if (gtTile.getMetaTileEntity() instanceof IManaReceiver manaReceiver) {
                this.receiver = manaReceiver;
                this.lastTentativeBurst = fakeBurst.propsList;
            }
        }
    }

    /**
     * Writes custom NBT data for the spreader.
     *
     * @author EvgenWarGold
     * @reason Overwritten to save additional fields required for GregTech integration.
     *
     * @param compound The NBT compound to write data to
     */
    @SuppressWarnings("ConstantConditions")
    @Overwrite
    public void writeCustomNBT(NBTTagCompound compound) {
        TileSpreader self = (TileSpreader) (Object) this;

        try {
            java.lang.reflect.Method superMethod = TileSpreader.class.getSuperclass()
                .getDeclaredMethod("writeCustomNBT", NBTTagCompound.class);
            superMethod.setAccessible(true);
            superMethod.invoke(self, compound);
        } catch (Exception ignored) {}

        UUID identity = getIdentifier();
        compound.setBoolean(TAG_HAS_IDENTITY, true);
        compound.setLong(TAG_UUID_MOST, identity.getMostSignificantBits());
        compound.setLong(TAG_UUID_LEAST, identity.getLeastSignificantBits());

        compound.setInteger(TAG_MANA, mana);
        compound.setFloat(TAG_ROTATION_X, rotationX);
        compound.setFloat(TAG_ROTATION_Y, rotationY);
        compound.setBoolean(TAG_REQUEST_UPDATE, requestsClientUpdate);
        compound.setInteger(TAG_PADDING_COLOR, paddingColor);
        compound.setBoolean(TAG_CAN_SHOOT_BURST, canShootBurst);

        compound.setInteger(TAG_PINGBACK_TICKS, pingbackTicks);
        compound.setDouble(TAG_LAST_PINGBACK_X, lastPingbackX);
        compound.setDouble(TAG_LAST_PINGBACK_Y, lastPingbackY);
        compound.setDouble(TAG_LAST_PINGBACK_Z, lastPingbackZ);

        compound.setString(TAG_INPUT_KEY, inputKey);
        compound.setString(TAG_OUTPUT_KEY, outputKey);

        TileEntity receiverTile = getReceiverTile();
        compound.setInteger(TAG_FORCE_CLIENT_BINDING_X, receiverTile == null ? 0 : receiverTile.xCoord);
        compound.setInteger(TAG_FORCE_CLIENT_BINDING_Y, receiverTile == null ? -1 : receiverTile.yCoord);
        compound.setInteger(TAG_FORCE_CLIENT_BINDING_Z, receiverTile == null ? 0 : receiverTile.zCoord);

        compound.setBoolean(TAG_MAPMAKER_OVERRIDE, mapmakerOverride);
        compound.setInteger(TAG_FORCED_COLOR, mmForcedColor);
        compound.setInteger(TAG_FORCED_MANA_PAYLOAD, mmForcedManaPayload);
        compound.setInteger(TAG_FORCED_TICKS_BEFORE_MANA_LOSS, mmForcedTicksBeforeManaLoss);
        compound.setFloat(TAG_FORCED_MANA_LOSS_PER_TICK, mmForcedManaLossPerTick);
        compound.setFloat(TAG_FORCED_GRAVITY, mmForcedGravity);
        compound.setFloat(TAG_FORCED_VELOCITY_MULTIPLIER, mmForcedVelocityMultiplier);

        requestsClientUpdate = false;
    }

    /**
     * Reads custom NBT data for the spreader.
     *
     * @author EvgenWarGold
     * @reason Overwritten to load additional fields required for GregTech integration.
     *
     * @param compound The NBT compound to read data from
     */
    @SuppressWarnings("ConstantConditions")
    @Overwrite
    public void readCustomNBT(NBTTagCompound compound) {
        TileSpreader self = (TileSpreader) (Object) this;

        try {
            java.lang.reflect.Method superMethod = TileSpreader.class.getSuperclass()
                .getDeclaredMethod("readCustomNBT", NBTTagCompound.class);
            superMethod.setAccessible(true);
            superMethod.invoke(self, compound);
        } catch (Exception ignored) {}

        if (compound.getBoolean(TAG_HAS_IDENTITY)) {
            long most = compound.getLong(TAG_UUID_MOST);
            long least = compound.getLong(TAG_UUID_LEAST);
            UUID currentIdentity = getIdentifierUnsafe();
            if (currentIdentity == null || most != currentIdentity.getMostSignificantBits()
                || least != currentIdentity.getLeastSignificantBits()) {
                this.identity = new UUID(most, least);
            }
        } else {
            getIdentifier();
        }

        mana = compound.getInteger(TAG_MANA);
        rotationX = compound.getFloat(TAG_ROTATION_X);
        rotationY = compound.getFloat(TAG_ROTATION_Y);
        requestsClientUpdate = compound.getBoolean(TAG_REQUEST_UPDATE);

        if (compound.hasKey(TAG_INPUT_KEY)) inputKey = compound.getString(TAG_INPUT_KEY);
        if (compound.hasKey(TAG_OUTPUT_KEY)) outputKey = compound.getString(TAG_OUTPUT_KEY);

        mapmakerOverride = compound.getBoolean(TAG_MAPMAKER_OVERRIDE);
        mmForcedColor = compound.getInteger(TAG_FORCED_COLOR);
        mmForcedManaPayload = compound.getInteger(TAG_FORCED_MANA_PAYLOAD);
        mmForcedTicksBeforeManaLoss = compound.getInteger(TAG_FORCED_TICKS_BEFORE_MANA_LOSS);
        mmForcedManaLossPerTick = compound.getFloat(TAG_FORCED_MANA_LOSS_PER_TICK);
        mmForcedGravity = compound.getFloat(TAG_FORCED_GRAVITY);
        mmForcedVelocityMultiplier = compound.getFloat(TAG_FORCED_VELOCITY_MULTIPLIER);

        if (compound.hasKey(TAG_KNOWN_MANA)) knownMana = compound.getInteger(TAG_KNOWN_MANA);
        if (compound.hasKey(TAG_PADDING_COLOR)) paddingColor = compound.getInteger(TAG_PADDING_COLOR);
        if (compound.hasKey(TAG_CAN_SHOOT_BURST)) canShootBurst = compound.getBoolean(TAG_CAN_SHOOT_BURST);

        pingbackTicks = compound.getInteger(TAG_PINGBACK_TICKS);
        lastPingbackX = compound.getDouble(TAG_LAST_PINGBACK_X);
        lastPingbackY = compound.getDouble(TAG_LAST_PINGBACK_Y);
        lastPingbackZ = compound.getDouble(TAG_LAST_PINGBACK_Z);

        if (requestsClientUpdate && self.getWorldObj() != null) {
            int x = compound.getInteger(TAG_FORCE_CLIENT_BINDING_X);
            int y = compound.getInteger(TAG_FORCE_CLIENT_BINDING_Y);
            int z = compound.getInteger(TAG_FORCE_CLIENT_BINDING_Z);

            if (y != -1) {
                TileEntity tile = self.getWorldObj()
                    .getTileEntity(x, y, z);

                if (tile instanceof IGregTechTileEntity gtTile) {
                    if (gtTile.getMetaTileEntity() instanceof IManaReceiver manaReceiver) {
                        this.receiver = manaReceiver;
                    }
                }
            } else {
                this.receiver = null;
            }
        } else {
            this.receiver = null;
        }

        if (self.getWorldObj() != null && self.getWorldObj().isRemote) hasReceivedInitialPacket = true;
    }

    /**
     * Gets the binding coordinates of the mana receiver.
     *
     * @author EvgenWarGold
     * @reason Overwritten to properly handle GregTech tile entities as IManaReceiver.
     *
     * @return ChunkCoordinates of the bound receiver, or {@code null} if no receiver is bound
     */
    @Overwrite
    public ChunkCoordinates getBinding() {
        if (receiver == null) return null;

        TileEntity tile = getReceiverTile();

        if (tile == null) return null;

        return new ChunkCoordinates(tile.xCoord, tile.yCoord, tile.zCoord);
    }

    /**
     * Renders the HUD overlay for the spreader.
     *
     * @author EvgenWarGold
     * @reason Overwritten to display GregTech receiver information and maintain visual consistency.
     *
     * @param mc  The Minecraft instance
     * @param res The current screen resolution
     */
    @Overwrite
    public void renderHUD(Minecraft mc, ScaledResolution res) {
        String name = StatCollector.translateToLocal(
            new ItemStack(ModBlocks.spreader, 1, getBlockMetadata()).getUnlocalizedName()
                .replaceAll("tile.", "tile." + LibResources.PREFIX_MOD) + ".name");
        int color = isRedstone() ? 0xFF0000 : isDreamwood() ? 0xFF00AE : 0x00FF00;
        HUDHandler.drawSimpleManaHUD(color, knownMana, getMaxMana(), name, res);

        ItemStack lens = getStackInSlot(0);
        if (lens != null) {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            String lensName = lens.getDisplayName();
            int width = 16 + mc.fontRenderer.getStringWidth(lensName) / 2;
            int x = res.getScaledWidth() / 2 - width;
            int y = res.getScaledHeight() / 2 + 50;

            mc.fontRenderer.drawStringWithShadow(lensName, x + 20, y + 5, color);
            RenderHelper.enableGUIStandardItemLighting();
            RenderItem.getInstance()
                .renderItemAndEffectIntoGUI(mc.fontRenderer, mc.renderEngine, lens, x, y);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_BLEND);
        }

        if (receiver != null) {
            TileEntity receiverTile = getReceiverTile();

            if (receiverTile == null) return;

            ItemStack recieverStack = new ItemStack(
                worldObj.getBlock(receiverTile.xCoord, receiverTile.yCoord, receiverTile.zCoord),
                1,
                receiverTile.getBlockMetadata());
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            if (recieverStack.getItem() != null) {
                String stackName = recieverStack.getDisplayName();
                int width = 16 + mc.fontRenderer.getStringWidth(stackName) / 2;
                int x = res.getScaledWidth() / 2 - width;
                int y = res.getScaledHeight() / 2 + 30;

                mc.fontRenderer.drawStringWithShadow(stackName, x + 20, y + 5, color);
                RenderHelper.enableGUIStandardItemLighting();
                RenderItem.getInstance()
                    .renderItemAndEffectIntoGUI(mc.fontRenderer, mc.renderEngine, recieverStack, x, y);
                RenderHelper.disableStandardItemLighting();
            }

            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_BLEND);
        }

        GL11.glColor4f(1F, 1F, 1F, 1F);
    }
}
