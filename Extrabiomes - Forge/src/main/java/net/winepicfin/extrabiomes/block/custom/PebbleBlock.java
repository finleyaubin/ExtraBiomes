package net.winepicfin.extrabiomes.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CarrotBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.winepicfin.extrabiomes.ExtraBiomes;
import net.winepicfin.extrabiomes.item.ModItems;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(modid = ExtraBiomes.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PebbleBlock extends Block {
    public static final IntegerProperty SIZE1 = IntegerProperty.create("size", 0, 1);
    public static final IntegerProperty SIZE2 = IntegerProperty.create("size", 0, 2);
    public static final IntegerProperty SIZE3 = IntegerProperty.create("size", 0, 3);
    public PebbleBlock(Properties p_49795_) {
        super(p_49795_);
    }
    public static IntegerProperty SIZE=IntegerProperty.create("size",
            1,3);

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SIZE);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext p_49820_) {
        return this.defaultBlockState().setValue(SIZE,1);
    }

    public IntegerProperty getSizeProperty() {
        return SIZE;
    }

    @SubscribeEvent
    public static void onBlockInteract(PlayerInteractEvent.RightClickBlock event){
        int state;
        if (SIZE.getValue("size").isPresent()){
             state =SIZE.getValue("size").get();
            if(state==1){
                if (event.getEntity()!=null && event.getItemStack().getItem()== ModItems.MOSSY_PEBBLE.get()){
                    event.getLevel().setBlock(event.getPos(), SIZE,2);
                }
            }
        }
    }
    @Override
    public @NotNull ItemStack getCloneItemStack( BlockGetter p_52254_,  BlockPos p_52255_,  BlockState p_52256_) {
        return new ItemStack(ModItems.PEBBLE.get());
    }
}
