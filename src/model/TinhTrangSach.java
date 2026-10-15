package model;

/**
 * Enum tình trạng sách - Khớp 100% với UML Class Diagram
 */
public enum TinhTrangSach {
    MOI("Mới 100%"),
    TOT("Tốt"),
    CU("Cũ"),
    HU_HONG("Hư hỏng");

    private final String moTa;

    TinhTrangSach(String moTa) {
        this.moTa = moTa;
    }

    public String getMoTa() {
        return moTa;
    }

    public static TinhTrangSach fromString(String val) {
        if (val == null) return TOT;
        for (TinhTrangSach tt : values()) {
            if (tt.name().equalsIgnoreCase(val.trim())) {
                return tt;
            }
        }
        return TOT;
    }
}
