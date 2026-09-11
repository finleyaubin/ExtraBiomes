package net.winepicfin.extrabiomes;

/**
 * Shared mod identity, referenced by every registrar/config class regardless of loader.
 * Each platform module has its own entry point class (e.g. forge's {@code ExtraBiomesForge})
 * that performs the actual mod-loading bootstrap and calls into common's {@code register()} methods.
 */
public class ExtraBiomes {
    public static final String MOD_ID = "extrabiomes";
}
