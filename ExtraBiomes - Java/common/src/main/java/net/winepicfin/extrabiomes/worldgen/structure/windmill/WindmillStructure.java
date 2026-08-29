package net.winepicfin.extrabiomes.worldgen.structure.windmill;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pools.EmptyPoolElement;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.winepicfin.extrabiomes.platform.ExtraBiomesExpectPlatform;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Same generation mechanism as vanilla's {@link net.minecraft.world.level.levelgen.structure.structures.JigsawStructure}
 * - reimplemented as our own type only because that vanilla class is {@code final} and this needs two
 * things it doesn't expose: a fixed (never random) placement rotation, and picking between two different
 * start pools (plain windmill.nbt, or windmill_create.nbt - a hand-built Create contraption, complete with
 * its own Windmill Bearing - used instead when Create is loaded) plus an {@link #afterPlace} hook to queue
 * that bearing to start spinning. See {@link #findGenerationPoint} and {@link #afterPlace} respectively for
 * why each of those needed a from-scratch (if structurally similar) Structure subclass rather than reusing
 * JigsawStructure directly.
 */
public class WindmillStructure extends Structure {
    public static final MapCodec<WindmillStructure> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            settingsCodec(instance),
            StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
            StructureTemplatePool.CODEC.fieldOf("create_start_pool").forGetter(structure -> structure.createStartPool),
            Heightmap.Types.CODEC.fieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap)
    ).apply(instance, WindmillStructure::new));

    // getFirstFreeHeight (used below via projectStartToHeightmap) returns an ABSOLUTE world Y, and vanilla's
    // own JigsawPlacement adds this start height ON TOP of that - so this has to be 0, not a "seed" value,
    // or the structure places that many blocks above the real terrain. Every vanilla structure that pairs
    // project_start_to_heightmap with a flat ConstantHeight (villages, pillager outposts - see
    // data/worldgen/Structures.java) uses exactly VerticalAnchor.absolute(0) for this same reason.
    private static final HeightProvider START_HEIGHT = ConstantHeight.of(net.minecraft.world.level.levelgen.VerticalAnchor.absolute(0));

    private final Holder<StructureTemplatePool> startPool;
    private final Holder<StructureTemplatePool> createStartPool;
    private final Heightmap.Types projectStartToHeightmap;

    public WindmillStructure(Structure.StructureSettings settings, Holder<StructureTemplatePool> startPool, Holder<StructureTemplatePool> createStartPool, Heightmap.Types projectStartToHeightmap) {
        super(settings);
        this.startPool = startPool;
        this.createStartPool = createStartPool;
        this.projectStartToHeightmap = projectStartToHeightmap;
    }

    /**
     * A hand-inlined copy of {@link JigsawPlacement#addPieces} with two changes: a fixed
     * {@link Rotation#NONE} instead of that method's hardcoded {@code Rotation.getRandom(worldgenRandom)}
     * for the start piece, and a choice of which pool to sample from based on
     * {@link ExtraBiomesExpectPlatform#isCreateLoaded()}.
     * <p>
     * Fixed rotation: windmill.nbt's interior (doors, the corridor trapdoor) is all laid out assuming a
     * specific captured orientation - the old scatter feature this replaced said as much explicitly
     * ("Bedrock's facing_direction 'south' is not random") and always placed with {@code Rotation.NONE}.
     * Delegating to vanilla's own addPieces silently dropped that: it always rotates the start piece
     * randomly, which is exactly why a trapdoor read as "north" in windmill.nbt could show up facing "east"
     * (or any of the 4 cardinals) in a placed instance - the whole structure is a rigid body under a random
     * 90 deg-multiple rotation, so nothing about any single block's facing was actually broken, but nothing
     * matched the raw file either.
     * <p>
     * The one further simplification versus the real addPieces: this drops its recursive jigsaw-expansion
     * branch entirely rather than reimplementing that private step too - neither windmill.nbt nor
     * windmill_create.nbt has any jigsaw connector blocks in them, so that branch would never find anything
     * to recurse into regardless (confirmed against both .nbt's block data), making it dead code here.
     */
    @Override
    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext context) {
        ChunkPos chunkPos = context.chunkPos();
        int y = START_HEIGHT.sample(context.random(), new WorldGenerationContext(context.chunkGenerator(), context.heightAccessor()));
        BlockPos origin = new BlockPos(chunkPos.getMinBlockX(), y, chunkPos.getMinBlockZ());

        StructureTemplatePool pool = (ExtraBiomesExpectPlatform.isCreateLoaded() ? this.createStartPool : this.startPool).value();
        StructurePoolElement element = pool.getRandomTemplate(context.random());
        if (element == EmptyPoolElement.INSTANCE) {
            return Optional.empty();
        }

        StructureTemplateManager templateManager = context.structureTemplateManager();
        PoolElementStructurePiece piece = new PoolElementStructurePiece(
                templateManager, element, origin, element.getGroundLevelDelta(), Rotation.NONE,
                element.getBoundingBox(templateManager, origin, Rotation.NONE)
        );
        BoundingBox pieceBox = piece.getBoundingBox();
        int centerX = (pieceBox.maxX() + pieceBox.minX()) / 2;
        int centerZ = (pieceBox.maxZ() + pieceBox.minZ()) / 2;
        int placedY = origin.getY() + context.chunkGenerator().getFirstFreeHeight(centerX, centerZ, this.projectStartToHeightmap, context.heightAccessor(), context.randomState());
        piece.move(0, placedY - (pieceBox.minY() + piece.getGroundLevelDelta()), 0);

        return Optional.of(new Structure.GenerationStub(new BlockPos(centerX, placedY, centerZ), pieces -> pieces.addPiece(piece)));
    }

    /**
     * {@code afterPlace} fires once per chunk that intersects the structure (see
     * {@link net.minecraft.world.level.levelgen.structure.StructureStart#placeInChunk}), each time with
     * {@code box} restricted to that one chunk's column - not once for the whole structure. Both steps
     * below are scoped to just the overlap between the piece and this call's box, so together, repeated
     * calls across every intersecting chunk cover the whole structure exactly once.
     */
    @Override
    public void afterPlace(WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox box, ChunkPos chunkPos, PiecesContainer pieces) {
        if (pieces.pieces().isEmpty()) {
            return;
        }
        StructurePiece piece = pieces.pieces().get(0);
        if (!(piece instanceof PoolElementStructurePiece)) {
            return;
        }

        BoundingBox overlap = intersect(piece.getBoundingBox(), box);
        if (overlap == null) {
            return;
        }

        recomputeConnectableShapes(level, overlap);
        ExtraBiomesExpectPlatform.applyWindmillCreateCompat(level, overlap);
    }

    @Nullable
    private static BoundingBox intersect(BoundingBox a, BoundingBox b) {
        int minX = Math.max(a.minX(), b.minX());
        int minY = Math.max(a.minY(), b.minY());
        int minZ = Math.max(a.minZ(), b.minZ());
        int maxX = Math.min(a.maxX(), b.maxX());
        int maxY = Math.min(a.maxY(), b.maxY());
        int maxZ = Math.min(a.maxZ(), b.maxZ());
        if (minX > maxX || minY > maxY || minZ > maxZ) {
            return null;
        }
        return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    /**
     * SinglePoolElement (vanilla, used by our single-piece template pools) always places with
     * {@code StructurePlaceSettings.setKnownShape(true)}, which skips the neighbour-shape recompute pass
     * {@link net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate#placeInWorld}
     * would otherwise run - fine for a template captured (via structure block) from a rendered world, whose
     * fence/wall/pane connection booleans are already baked in correctly by the game itself, but not
     * guaranteed for every template. Re-running vanilla's own {@link Block#updateFromNeighbourShapes} here
     * - the exact call that pass would have made - is a cheap, harmless safety net either way.
     */
    private static void recomputeConnectableShapes(WorldGenLevel level, BoundingBox box) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = box.minX(); x <= box.maxX(); x++) {
            for (int y = box.minY(); y <= box.maxY(); y++) {
                for (int z = box.minZ(); z <= box.maxZ(); z++) {
                    pos.set(x, y, z);
                    BlockState state = level.getBlockState(pos);
                    BlockState updated = Block.updateFromNeighbourShapes(state, level, pos);
                    if (state != updated) {
                        level.setBlock(pos, updated, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
                    }
                }
            }
        }
    }

    @Override
    public StructureType<?> type() {
        return ModStructureTypes.WINDMILL.get();
    }

    static ResourceLocation id(String path) {
        return new ResourceLocation(net.winepicfin.extrabiomes.ExtraBiomes.MOD_ID, path);
    }
}
