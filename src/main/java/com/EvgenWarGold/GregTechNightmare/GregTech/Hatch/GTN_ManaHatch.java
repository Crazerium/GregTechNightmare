package com.EvgenWarGold.GregTechNightmare.GregTech.Hatch;

import static com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil.formatNumber;

import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.EvgenWarGold.GregTechNightmare.GregTech.Mana.GTN_ManaBuses;
import com.EvgenWarGold.GregTechNightmare.GregTech.Textures.GTN_BlockIcons;
import com.EvgenWarGold.GregTechNightmare.Utils.Constants;
import com.EvgenWarGold.GregTechNightmare.Utils.GTN_Utils;

import gregtech.api.enums.Textures;
import gregtech.api.interfaces.IIconContainer;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.BaseMetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;
import io.netty.buffer.ByteBuf;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import vazkii.botania.api.mana.IManaPool;
import vazkii.botania.api.mana.IManaReceiver;
import vazkii.botania.api.mana.ManaNetworkEvent;
import vazkii.botania.api.wand.IWandHUD;
import vazkii.botania.client.core.handler.HUDHandler;
import vazkii.botania.common.block.ModBlocks;

@IMetaTileEntity.SkipGenerateDescription
public class GTN_ManaHatch extends MTEHatch implements IManaPool, IWandHUD {

    private final static int MAX_MANA = 1_000_000;
    private int mana = 0;
    private int texturePage = 0;
    private int textureIndex = 0;
    private boolean registered = false;
    /** Mana the client knows about, to resend only changes. */
    private int syncedMana = -1;
    /** Sends the mana into the Botania blocks next to the hatch, for machines that make mana. */
    private boolean outputMode = false;
    private static final String TAG_MANA = "mana";
    private static final String TAG_OUTPUT_MODE = "outputMode";
    private static final String TAG_TEXTURE_PAGE = "texturePage";
    private static final String TAG_TEXTURE_INDEX = "textureIndex";

    private static final IIconContainer textureFont = GTN_BlockIcons.MANA_HATCH_OVERLAY;
    private static final IIconContainer textureFont_Glow = GTN_BlockIcons.MANA_HATCH_OVERLAY;
    private static ITexture cachedLivingRockTexture = null;

    public GTN_ManaHatch(int aID, String aName) {
        super(aID, aName, aName, 3, 0, "");
    }

    public GTN_ManaHatch(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, 0, aDescription, aTextures);
    }

    @Override
    public boolean isValidSlot(int aIndex) {
        return false;
    }

    @Override
    public boolean isFacingValid(ForgeDirection facing) {
        return true;
    }

    @Override
    public boolean allowPullStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, ForgeDirection Side,
        ItemStack aStack) {
        return false;
    }

    @Override
    public boolean allowPutStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, ForgeDirection side,
        ItemStack aStack) {
        return false;
    }

    @Override
    public void writeToStream(ByteBuf buffer) {
        super.writeToStream(buffer);
        buffer.writeInt(mana);
        buffer.writeInt(getTextureIndex());
        buffer.writeInt(getTexturePage());
    }

    @Override
    public void readFromStream(ByteBuf buffer) {
        super.readFromStream(buffer);
        mana = buffer.readInt();
        textureIndex = buffer.readInt();
        texturePage = buffer.readInt();
    }

    @Override
    public String[] getDescription() {
        return new String[] { GTN_Utils.tr("GTN.Hatch.ManaHatch.tooltip.00"),
            GTN_Utils.tr("GTN.Hatch.ManaHatch.tooltip.01"), GTN_Utils.tr("GTN.Hatch.ManaHatch.tooltip.02"),
            GTN_Utils.tr("GTN.Hatch.ManaHatch.tooltip.03"), GTN_Utils.tr("GTN.Hatch.ManaHatch.tooltip.04"),
            GTN_Utils.tr("GTN.Hatch.common.addedBy", Constants.MOD_NAME) };
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        mana = aNBT.getInteger(TAG_MANA);
        texturePage = aNBT.getInteger(TAG_TEXTURE_PAGE);
        textureIndex = aNBT.getInteger(TAG_TEXTURE_INDEX);
        outputMode = aNBT.getBoolean(TAG_OUTPUT_MODE);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger(TAG_MANA, mana);
        aNBT.setInteger(TAG_TEXTURE_INDEX, textureIndex);
        aNBT.setBoolean(TAG_OUTPUT_MODE, outputMode);
        aNBT.setInteger(TAG_TEXTURE_PAGE, texturePage);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GTN_ManaHatch(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public ITexture[] getTexturesActive(ITexture aBaseTexture) {
        return new ITexture[] { aBaseTexture, TextureFactory.of(textureFont), TextureFactory.builder()
            .addIcon(textureFont_Glow)
            .glow()
            .build() };
    }

    @Override
    public ITexture[] getTexturesInactive(ITexture aBaseTexture) {
        return new ITexture[] { aBaseTexture, TextureFactory.of(textureFont) };
    }

    private int getTexturePage() {
        try {
            Field field = MTEHatch.class.getDeclaredField("texturePage");
            field.setAccessible(true);
            return field.getInt(this);
        } catch (Exception e) {
            return 0;
        }
    }

    private int getTextureIndex() {
        try {
            Field field = MTEHatch.class.getDeclaredField("textureIndex");
            field.setAccessible(true);
            return field.getInt(this);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {

        try {
            ITexture background;

            if (texturePage == 0) texturePage = getTexturePage();
            if (textureIndex == 0) textureIndex = getTextureIndex();

            if (texturePage > 0 || textureIndex > 0) {
                background = Textures.BlockIcons.casingTexturePages[texturePage][textureIndex];
            } else {
                background = getLivingRockTexture();
            }

            if (side == aFacing) {
                if (aActive) {
                    return new ITexture[] { background, TextureFactory.of(textureFont), TextureFactory.builder()
                        .addIcon(textureFont_Glow)
                        .glow()
                        .build() };
                } else {
                    return new ITexture[] { background, TextureFactory.of(textureFont) };
                }
            }

            return new ITexture[] { background };

        } catch (NullPointerException npe) {
            return new ITexture[] { Textures.BlockIcons.MACHINE_CASINGS[0][0] };
        }
    }

    private ITexture getLivingRockTexture() {
        if (cachedLivingRockTexture != null) {
            return cachedLivingRockTexture;
        }

        try {
            final IIcon livingRockIcon = ModBlocks.livingrock.getIcon(0, 0);

            cachedLivingRockTexture = TextureFactory.of(new IIconContainer() {

                @Override
                public IIcon getIcon() {
                    return livingRockIcon;
                }

                @Override
                public IIcon getOverlayIcon() {
                    return null;
                }

                @Override
                public ResourceLocation getTextureFile() {
                    return null;
                }
            });

            return cachedLivingRockTexture;
        } catch (Exception e) {
            return TextureFactory.of(textureFont);
        }
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if (aBaseMetaTileEntity.isServerSide() && outputMode && aTick % 20 == 0) pushMana(aBaseMetaTileEntity);
        // the wand HUD reads the client copy of the mana, it only comes with the block description
        if (aBaseMetaTileEntity.isServerSide() && aTick % 20 == 0 && mana != syncedMana) {
            syncedMana = mana;
            aBaseMetaTileEntity.getWorld()
                .markBlockForUpdate(
                    aBaseMetaTileEntity.getXCoord(),
                    aBaseMetaTileEntity.getYCoord(),
                    aBaseMetaTileEntity.getZCoord());
        }
    }

    /** Gives the mana to the pools and other receivers next to the hatch that have space for it. */
    private void pushMana(IGregTechTileEntity base) {
        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            if (mana <= 0) return;
            TileEntity tile = base.getTileEntityAtSide(side);
            if (!(tile instanceof IManaReceiver receiver) || !GTN_ManaBuses.isReadyForMana(tile)) continue;
            int moved = (int) Math.min(mana, GTN_ManaBuses.getFreeSpace(tile));
            if (moved <= 0) continue;
            receiver.recieveMana(moved);
            mana -= moved;
            tile.getWorldObj()
                .markBlockForUpdate(tile.xCoord, tile.yCoord, tile.zCoord);
        }
    }

    @Override
    public void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        outputMode = !outputMode;
        GTUtility
            .sendChatTrans(aPlayer, outputMode ? "GTN.Hatch.ManaHatch.mode.output" : "GTN.Hatch.ManaHatch.mode.input");
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        tag.setInteger(TAG_MANA, mana);
        tag.setBoolean(TAG_OUTPUT_MODE, outputMode);
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        currentTip.add(
            GTN_Utils.tr(
                "GTN.Hatch.ManaHatch.waila.mana",
                formatNumber(
                    accessor.getNBTData()
                        .getInteger(TAG_MANA)),
                formatNumber(MAX_MANA)));
        if (accessor.getNBTData()
            .getBoolean(TAG_OUTPUT_MODE)) currentTip.add(GTN_Utils.tr("GTN.Hatch.ManaHatch.waila.output"));
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        if (aBaseMetaTileEntity.isServerSide() && !registered) {
            ManaNetworkEvent.addPool((TileEntity) aBaseMetaTileEntity);
            registered = true;
        }
    }

    @Override
    public void inValidate() {
        if (getBaseMetaTileEntity() != null && getBaseMetaTileEntity().isServerSide()) {
            ManaNetworkEvent.removePool((BaseMetaTileEntity) getBaseMetaTileEntity());
            registered = false;
        }
        super.inValidate();
    }

    @Override
    public void onBlockDestroyed() {
        inValidate();
        super.onBlockDestroyed();
    }

    public int getMaxMana() {
        return MAX_MANA;
    }

    @Override
    public boolean isFull() {
        return getCurrentMana() >= MAX_MANA;
    }

    @Override
    public void recieveMana(int mana) {
        this.mana = Math.max(0, Math.min(getCurrentMana() + mana, MAX_MANA));
    }

    @Override
    public boolean canRecieveManaFromBursts() {
        return !isFull();
    }

    @Override
    public int getCurrentMana() {
        return this.mana;
    }

    @Override
    public boolean isOutputtingPower() {
        return false;
    }

    public boolean extractMana(int amount, boolean simulate) {
        if (amount <= 0) {
            return false;
        }

        int extracted = Math.min(amount, mana);
        boolean canExtract = amount <= mana;

        if (!simulate) {
            mana -= extracted;
        }

        return canExtract;
    }

    public boolean extractMana(int amount) {
        return extractMana(amount, false);
    }

    @Override
    public void renderHUD(Minecraft mc, ScaledResolution res, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof BaseMetaTileEntity baseTE) {
            if (baseTE.getMetaTileEntity() instanceof GTN_ManaHatch hatch) {
                int color = 0x00BFFF;
                HUDHandler.drawSimpleManaHUD(
                    color,
                    hatch.mana,
                    MAX_MANA,
                    StatCollector.translateToLocal("GTN.TooltipBuilder.ManaHatch"),
                    res);
            }
        }
    }
}
