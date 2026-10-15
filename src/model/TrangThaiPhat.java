package model;

/**
 * Enum trạng thái phạt - Khớp 100% với UML Class Diagram
 */
public enum TrangThaiPhat {
    DA_NOP_PHAT("Đã nộp phạt"),
    CHUA_NOP_PHAT("Chưa nộp phạt"),
    MIEN_PHAT("Miễn phạt");

    private final String moTa;

    TrangThaiPhat(String moTa) {
        this.moTa = moTa;
    }

    public String getMoTa() {
        return moTa;
    }

    public static TrangThaiPhat fromString(String val) {
        if (val == null) return CHUA_NOP_PHAT;
        for (TrangThaiPhat ttp : values()) {
            if (ttp.name().equalsIgnoreCase(val.trim())) {
                return ttp;
            }
        }
        return CHUA_NOP_PHAT;
    }
}
