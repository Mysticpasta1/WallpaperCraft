package net.mystic.wallpapercraft;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.mystic.wallpapercraft.blocks.ModBlocks;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class ModTabs {
    private ModTabs() {}

    public static void register(IEventBus modBus) {
        TABS.register(modBus);
    }

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Wallpapercraft.MODID);

    public static final List<Supplier<? extends ItemLike>> MAIN_BLOCKS = new ArrayList<>();
    public static final List<Supplier<? extends ItemLike>> MAIN_ITEMS  = new ArrayList<>();

    public static void bootstrap() {
        TABS.register("main",
                () -> CreativeModeTab.builder()
                        .title(Component.translatable("itemGroup.wallpapercraft"))
                        .icon(() -> {
                            Item icon = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(Wallpapercraft.MODID, "pressstonebrick"));
                            return icon.getDefaultInstance();
                        })
                        .displayItems((params, out) -> {
                            List<ItemLike> all = new ArrayList<>();
                            MAIN_BLOCKS.forEach(s -> all.add(s.get()));
                            MAIN_ITEMS.forEach(s -> all.add(s.get()));

                            all.stream()
                                    .sorted((a, b) -> compareForTab(a.asItem(), b.asItem()))
                                    .forEach(out::accept);
                        })
                        .build()
        );
    }

    private static int compareForTab(Item ia, Item ib) {
        ResourceLocation ida = BuiltInRegistries.ITEM.getKey(ia);
        ResourceLocation idb = BuiltInRegistries.ITEM.getKey(ib);

        if (!ida.getNamespace().equals(idb.getNamespace())) {
            int n = ida.getNamespace().compareTo(idb.getNamespace());
            if (n != 0) return n;
        }
        if (!ida.getNamespace().equals(Wallpapercraft.MODID) || !idb.getNamespace().equals(Wallpapercraft.MODID)) {
            return ida.getPath().compareTo(idb.getPath());
        }

        Parsed pa = parse(ida.getPath());
        Parsed pb = parse(idb.getPath());

        int g = Integer.compare(pa.group, pb.group);
        if (g != 0) return g;

        int p = Integer.compare(indexOf(ModBlocks.PATTERNS, pa.pattern), indexOf(ModBlocks.PATTERNS, pb.pattern));
        if (p != 0) return p;

        int c = Integer.compare(indexOf(ModBlocks.COLOURS, pa.colour), indexOf(ModBlocks.COLOURS, pb.colour));
        if (c != 0) return c;

        int s = Integer.compare(pa.suffix, pb.suffix);
        if (s != 0) return s;

        return ida.getPath().compareTo(idb.getPath());
    }

    private static int indexOf(String[] arr, String s) {
        for (int i = 0; i < arr.length; i++) if (arr[i].equals(s)) return i;
        return Integer.MAX_VALUE;
    }

    private static final class Parsed {
        String pattern = "";
        String colour  = "";
        int suffix     = Integer.MAX_VALUE;
        int group      = 9; // unknown last
    }

    private static Parsed parse(String path) {
        Parsed p = new Parsed();
        if (path.startsWith("press")) { p.pattern = "press"; p.group = 8; return p; }

        boolean carpet = path.endsWith("_carpet");
        if (carpet) p.group = 5;

        String core = carpet ? path.substring(0, path.length() - "_carpet".length()) : path;
        int dash = core.lastIndexOf('-');
        if (dash >= 0 && dash + 1 < core.length()) {
            try { p.suffix = Integer.parseInt(core.substring(dash + 1)); } catch (NumberFormatException ignored) {}
            core = core.substring(0, dash);
        }

        String colour = "";
        for (String c : ModBlocks.COLOURS) {
            if (core.endsWith(c)) { colour = c; break; }
        }
        p.colour = colour;
        String pattern = colour.isEmpty() ? core : core.substring(0, core.length() - colour.length());
        p.pattern = pattern;

        if (pattern.equals("auralamp") || pattern.equals("stonelamp")) p.group = 0;
        else if (pattern.contains("glass")) p.group = 1;
        else if (pattern.equals("woodplank")) p.group = 3;
        else if (pattern.equals("wool") || pattern.equals("checkeredwool")) p.group = 4;
        else p.group = Math.min(2, p.group);

        return p;
    }

    public static <T extends ItemLike> void addToMainTab(Supplier<T> supplier)     { MAIN_BLOCKS.add(supplier); }
    public static <T extends ItemLike> void addToMainTabItems(Supplier<T> supplier){ MAIN_ITEMS.add(supplier); }
}
