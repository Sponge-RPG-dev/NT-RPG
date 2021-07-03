package cz.neumimto.rpg.common.utils.model;

public enum InstallOptions {
    CLASSES(true, false, false),
    GUIS(false, true, false),
    LOCALIZATION(false, false, true),
    ALL(true, true, true);

    private final boolean installClasses;
    private final boolean installGuis;
    private final boolean installLocalizations;

    InstallOptions(boolean installClasses, boolean installGuis, boolean installLocalizations) {

        this.installClasses = installClasses;
        this.installGuis = installGuis;
        this.installLocalizations = installLocalizations;
    }

    public boolean installClasses() {
        return installClasses;
    }

    public boolean installGuis() {
        return installGuis;
    }

    public boolean installLocalizations() {
        return installLocalizations;
    }
}
