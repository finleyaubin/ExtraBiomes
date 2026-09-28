package net.winepicfin.extrabiomes.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.winepicfin.extrabiomes.advancements.ModAdvancements;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Stream;

// ModAdvancements (common) targets vanilla's new registry-bootstrap AdvancementSubProvider shape
// (ctor(BootstrapContext<Advancement>), no-arg generate() calling AdvancementHolder.register(context))
// since that's what Forge/NeoForge's own plain-vanilla AdvancementProvider now requires. Fabric API's
// FabricAdvancementProvider still uses the older Consumer<AdvancementHolder>-based generateAdvancement,
// so this bridges the two: a minimal BootstrapContext that just records each registration and replays
// it to Fabric's consumer once ModAdvancements.generate() finishes.
public class ModAdvancementsProvider extends FabricAdvancementProvider {
    public ModAdvancementsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generateAdvancement(HolderLookup.Provider registryLookup, Consumer<AdvancementHolder> consumer) {
        Map<ResourceKey<Advancement>, Advancement> registered = new LinkedHashMap<>();
        new ModAdvancements(new BootstrapContext<Advancement>() {
            @Override
            public Holder.Reference<Advancement> register(ResourceKey<Advancement> key, Advancement value) {
                registered.put(key, value);
                return Holder.Reference.createStandAlone(registryLookup.lookupOrThrow(key.registryKey()), key);
            }

            @Override
            public <S> HolderGetter<S> lookup(ResourceKey<? extends Registry<? extends S>> key) {
                return registryLookup.lookupOrThrow(key);
            }

            @Override
            public <S> Stream<Holder.Reference<S>> listContextElements(ResourceKey<? extends Registry<? extends S>> key) {
                return registryLookup.lookupOrThrow(key).listElements();
            }
        }).generate();

        registered.forEach((key, value) -> consumer.accept(new AdvancementHolder(key.identifier(), value)));
    }
}
