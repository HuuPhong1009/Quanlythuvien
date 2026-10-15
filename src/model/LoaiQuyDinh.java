package model;

/**
 * Enum loại quy định mượn - Khớp 100% với UML Class Diagram
 */
public enum LoaiQuyDinh {
    MUON_NGAN_HAN("Mượn ngắn hạn (theo ngày)"),
    MUON_DAI_HAN_HOC_KY("Mượn dài hạn (theo học kỳ)");

    private final String moTa;

    LoaiQuyDinh(String moTa) {
        this.moTa = moTa;
    }

    public String getMoTa() {
        return moTa;
    }

    public static LoaiQuyDinh fromString(String val) {
        if (val == null) return MUON_NGAN_HAN;
        for (LoaiQuyDinh lqd : values()) {
            if (lqd.name().equalsIgnoreCase(val.trim())) {
                return lqd;
            }
        }
        return MUON_NGAN_HAN;
    }
}
