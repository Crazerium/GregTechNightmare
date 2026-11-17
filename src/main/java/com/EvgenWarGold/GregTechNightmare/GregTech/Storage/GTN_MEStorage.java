package com.EvgenWarGold.GregTechNightmare.GregTech.Storage;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockArea;
import com.EvgenWarGold.GregTechNightmare.GregTech.Api.MultiblockOffsets;
import com.EvgenWarGold.GregTechNightmare.GregTech.Blocks.GTN_Blocks;
import com.EvgenWarGold.GregTechNightmare.GregTech.GTN_ItemList;
import com.EvgenWarGold.GregTechNightmare.GregTech.Hatch.GTN_MEDataAccessHatch;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockBase;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_MultiBlockTooltipBuilder;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.StructureVariant;
import com.EvgenWarGold.GregTechNightmare.Utils.Authors;
import com.EvgenWarGold.GregTechNightmare.Utils.GTN_Utils;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.widget.ButtonWidget;
import com.gtnewhorizons.modularui.common.widget.DynamicPositionedColumn;
import com.gtnewhorizons.modularui.common.widget.SlotWidget;
import com.gtnewhorizons.modularui.common.widget.TextWidget;

import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.GTUtility;

public class GTN_MEStorage extends GTN_MultiBlockBase<GTN_MEStorage> {

    public static final long[] CORE_BYTES = { 1L << 20, 1L << 26, 1L << 32, 1L << 38 };
    private UUID machineId;
    private boolean playerMode = true;
    private long bytes;
    private GTN_MEDataAccessHatch access;
    private MEStorageData cachedData;
    private UUID cachedId;
    private boolean unloaded;

    public GTN_MEStorage(int id, String name) {
        super(id, name);
    }

    public GTN_MEStorage(String name) {
        super(name);
    }

    @Override
    public GTN_MEStorage createNewMetaEntity() {
        return new GTN_MEStorage(mName);
    }

    @Override
    public List<StructureVariant<GTN_MEStorage>> getStructureVariants() {
        List<StructureVariant<GTN_MEStorage>> variants = new ArrayList<>();
        for (int length = 3; length <= 10; length++) {
            String[][] shape = new String[3][length];
            for (int z = 0; z < length; z++) {
                shape[0][z] = "CCC";
                shape[1][z] = z == 0 ? "CHC" : z == length - 1 ? "CCC" : "CKC";
                shape[2][z] = z == 0 ? "C~C" : "CCC";
            }
            variants.add(
                new StructureVariant<>(
                    "MEStorage" + length,
                    shape,
                    new MultiblockOffsets(1, 2, 0),
                    new MultiblockArea(3, 3, length),
                    length - 2,
                    GTN_Casings.DataDriveMachineCasing));
        }
        return variants;
    }

    @Override
    @SuppressWarnings("unchecked")
    public IStructureDefinition<GTN_MEStorage> getStructureDefinition() {
        return buildStructureDefinition(builder -> {
            builder.addCasing('C', GTN_Casings.DataDriveMachineCasing);
            builder.addElement(
                'H',
                buildHatchAdder(GTN_MEStorage.class).adder(GTN_MEStorage::addAccess)
                    .hatchIds(
                        GTN_ItemList.MEDataAccessHatch.get(1)
                            .getItemDamage())
                    .casingIndex(GTN_Casings.DataDriveMachineCasing.textureId)
                    .hint(1)
                    .build());
            IStructureElement<GTN_MEStorage>[] cores = new IStructureElement[CORE_BYTES.length];
            for (int tier = 0; tier < cores.length; tier++) {
                final int index = tier;
                cores[tier] = onElementPass(
                    machine -> machine.bytes += CORE_BYTES[index],
                    ofBlock(GTN_Blocks.META_BLOCK_CASING_01, 4 + tier));
            }
            builder.addElement('K', ofChain(cores));
        });
    }

    private boolean addAccess(IGregTechTileEntity tile, int texture) {
        if (access != null || tile == null || !(tile.getMetaTileEntity() instanceof GTN_MEDataAccessHatch hatch))
            return false;
        if (!hatch.bind(this)) return false;
        access = hatch;
        hatch.updateTexture(texture);
        return true;
    }

    @Override
    public void clearHatches() {
        if (access != null) access.unbind(this);
        access = null;
        bytes = 0;
        super.clearHatches();
    }

    @Override
    protected boolean GTN_checkMachine(IGregTechTileEntity tile, ItemStack stack) {
        return access != null && bytes > 0;
    }

    public boolean storageOnline() {
        return !unloaded && getBaseMetaTileEntity() != null
            && !getBaseMetaTileEntity().isDead()
            && mMachine
            && getBaseMetaTileEntity().isAllowedToWork();
    }

    public long capacity() {
        return bytes;
    }

    public boolean infinite() {
        ItemStack upgrade = mInventory[1];
        return bytes > 1_000_000_000_000L && upgrade != null
            && upgrade.stackSize == 64
            && GTUtility.areStacksEqual(upgrade, GTN_ItemList.InfiniteStorageComponent.get(1));
    }

    public MEStorageData storageData() {
        if (machineId == null) {
            machineId = UUID.randomUUID();
            getBaseMetaTileEntity().markDirty();
        }
        UUID id = playerMode ? getBaseMetaTileEntity().getOwnerUuid() : machineId;
        if (id == null) return null;
        if (!id.equals(cachedId)) {
            cachedData = MEStorageData.get(id);
            cachedId = id;
        }
        return cachedData;
    }

    private void switchMode(EntityPlayer player) {
        IGregTechTileEntity base = getBaseMetaTileEntity();
        if (!base.isServerSide() || !player.getUniqueID()
            .equals(base.getOwnerUuid())) return;
        if (access != null) access.detach();
        playerMode = !playerMode;
        cachedData = null;
        cachedId = null;
        base.markDirty();
        GTUtility.sendChatToPlayer(player, GTN_Utils.tr("GTN.MEStorage.mode", modeName()));
    }

    private String modeName() {
        return GTN_Utils.tr(playerMode ? "GTN.MEStorage.player" : "GTN.MEStorage.machine");
    }

    @Override
    public void onScrewdriverRightClick(ForgeDirection side, EntityPlayer player, float x, float y, float z,
        ItemStack tool) {
        switchMode(player);
    }

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext context) {
        super.addUIWidgets(builder, context);
        builder.widget(
            new ButtonWidget().setOnClick((click, widget) -> switchMode(context.getPlayer()))
                .setBackground(GTUITextures.BUTTON_STANDARD, GTUITextures.OVERLAY_BUTTON_SHUFFLE)
                .addTooltip(GTN_Utils.tr("GTN.MEStorage.switch"))
                .setPos(174, 112)
                .setSize(16, 16));
    }

    @Override
    public String[] getInfoData() {
        if (getBaseMetaTileEntity() == null || !getBaseMetaTileEntity().isServerSide()) return new String[0];
        MEStorageData data = storageData();
        if (data == null) return new String[] { GTN_Utils.tr("GTN.MEStorage.no_owner") };
        return new String[] {
            GTN_Utils.tr("GTN.MEStorage.mode", modeName()), GTN_Utils.tr(
                "GTN.MEStorage.capacity",
                data.getUsedBytes()
                    .toString(),
                infinite() ? "∞" : Long.toString(bytes)),
            GTN_Utils.tr("GTN.MEStorage.types", data.items.types(), data.fluids.types()), GTN_Utils
                .tr(access != null && access.connected() ? "GTN.MEStorage.connected" : "GTN.MEStorage.disconnected") };
    }

    @Override
    protected void drawTexts(DynamicPositionedColumn column, SlotWidget inventorySlot) {
        column.setSpace(2);
        column.widget(
            TextWidget.dynamicString(() -> String.join("\n", getInfoData()))
                .setDefaultColor(0xFFFFFF));
        column.widget(
            TextWidget
                .dynamicString(() -> GTN_Utils.tr(storageOnline() ? "GTN.MEStorage.formed" : "GTN.MEStorage.offline"))
                .setDefaultColor(0x55FFFF));
    }

    @Override
    public void onRemoval() {
        if (access != null) access.unbind(this);
        super.onRemoval();
    }

    @Override
    public void onUnload() {
        unloaded = true;
        if (access != null) access.unbind(this);
        super.onUnload();
    }

    @Override
    public void onFirstTick(IGregTechTileEntity base) {
        unloaded = false;
        super.onFirstTick(base);
    }

    private void saveIdentity(NBTTagCompound tag) {
        if (machineId == null) machineId = UUID.randomUUID();
        tag.setString("storageId", machineId.toString());
        tag.setBoolean("storagePlayerMode", playerMode);
    }

    @Override
    public void setItemNBT(NBTTagCompound tag) {
        super.setItemNBT(tag);
        saveIdentity(tag);
    }

    @Override
    public void saveNBTData(NBTTagCompound tag) {
        super.saveNBTData(tag);
        saveIdentity(tag);
    }

    @Override
    public void loadNBTData(NBTTagCompound tag) {
        super.loadNBTData(tag);
        machineId = tag.hasKey("storageId") ? UUID.fromString(tag.getString("storageId")) : null;
        playerMode = !tag.hasKey("storagePlayerMode") || tag.getBoolean("storagePlayerMode");
    }

    @Override
    public CheckRecipeResult checkProcessing() {
        return CheckRecipeResultRegistry.NO_RECIPE;
    }

    @Override
    public boolean isNoMaintenanceIssue() {
        return true;
    }

    @Override
    public boolean isEnergyMultiBlock() {
        return false;
    }

    @Override
    public Authors getAuthor() {
        return Authors.CRAZER;
    }

    @Override
    public void createGtnTooltip(GTN_MultiBlockTooltipBuilder builder) {}
}
