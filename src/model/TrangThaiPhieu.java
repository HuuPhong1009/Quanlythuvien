package model;

/**
 * Enum trạng thái phiếu mượn - Khớp 100% với UML Class Diagram
 */
public enum TrangThaiPhieu {
    CHO_DUYET("Chờ duyệt", "bg-warning text-dark"),
    DANG_MUON("Đang mượn", "bg-primary text-white"),
    DA_TRA("Đã trả", "bg-success text-white"),
    QUA_HAN("Quá hạn", "bg-danger text-white"),
    DA_HUY("Đã hủy", "bg-secondary text-white"),
    TU_CHOI("Từ chối", "bg-danger text-white");

    private final String moTa;
    private final String badgeClass;

    TrangThaiPhieu(String moTa, String badgeClass) {
        this.moTa = moTa;
        this.badgeClass = badgeClass;
    }

    public String getMoTa() {
        return moTa;
    }

    public String getBadgeClass() {
        return badgeClass;
    }

    public static TrangThaiPhieu fromString(String val) {
        if (val == null) return CHO_DUYET;
        for (TrangThaiPhieu tt : values()) {
            if (tt.name().equalsIgnoreCase(val.trim())) {
                return tt;
            }
        }
        return CHO_DUYET;
    }
}
