package net.mystic.wallpapercraft.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Locale;

public final class Translator {
    private Translator() {}

    /** Client-only: returns the localized string for the current client language. */
    @OnlyIn(Dist.CLIENT)
    public static String translate(String key, Object... args) {
        return Component.translatable(key, args).getString();
    }

    /** Lowercase using the client language's locale, with safe fallback. */
    @OnlyIn(Dist.CLIENT)
    public static String toLowercaseClientLocale(String input) {
        return input.toLowerCase(getClientLocaleOrRoot());
    }

    /** Locale-agnostic lowercase (good default for IDs / registry names). */
    public static String toLowercaseStable(String input) {
        return input.toLowerCase(Locale.ROOT);
    }

    @OnlyIn(Dist.CLIENT)
    private static Locale getClientLocaleOrRoot() {
        Minecraft mc = Minecraft.getInstance();
        if (mc != null) {
            LanguageManager lm = mc.getLanguageManager();
            if (lm != null) {
                String code = lm.getSelected(); // e.g. "en_us"
                if (code != null && !code.isEmpty()) {
                    return parseLocaleCode(code);
                }
            }
        }
        return Locale.ROOT;
    }

    /** Converts "en_us" → new Locale("en","US"), "pt_br" → ("pt","BR"), etc. */
    private static Locale parseLocaleCode(String code) {
        String[] parts = code.toLowerCase(Locale.ROOT).split("_", 3);
        String lang = parts.length > 0 ? parts[0] : "";
        String country = parts.length > 1 ? parts[1].toUpperCase(Locale.ROOT) : "";
        String variant = parts.length > 2 ? parts[2] : "";

        if (!variant.isEmpty()) return new Locale(lang, country, variant);
        if (!country.isEmpty())  return new Locale(lang, country);
        if (!lang.isEmpty())     return new Locale(lang);
        return Locale.ROOT;
    }
}
