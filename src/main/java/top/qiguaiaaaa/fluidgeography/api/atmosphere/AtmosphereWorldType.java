package top.qiguaiaaaa.fluidgeography.api.atmosphere;

public enum AtmosphereWorldType {
    NORMAL(false, false),
    TEMP_CONSTANT(true, false),
    CLOSED(true, true);
    public final boolean isTempConstant;
    public final boolean isWorldClosed;

    AtmosphereWorldType(boolean isTempConstant, boolean isWorldClosed) {
        this.isTempConstant = isTempConstant;
        this.isWorldClosed = isWorldClosed;
    }
}
