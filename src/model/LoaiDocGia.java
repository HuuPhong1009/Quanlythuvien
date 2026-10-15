package model;

/**
 * Enum loại độc giả - Khớp 100% với UML Class Diagram
 */
public enum LoaiDocGia {
    SINH_VIEN("Sinh viên", 3, 14),
    GIANG_VIEN("Giảng viên", 5, 30),
    NGHIEN_CUU_SINH("Nghiên cứu sinh", 4, 21);

    private final String tenHienThi;
    private final int soSachToiDa;
    private final int soNgayMuonToiDa;

    LoaiDocGia(String tenHienThi, int soSachToiDa, int soNgayMuonToiDa) {
        this.tenHienThi = tenHienThi;
        this.soSachToiDa = soSachToiDa;
        this.soNgayMuonToiDa = soNgayMuonToiDa;
    }

    public String getTenHienThi() {
        return tenHienThi;
    }

    public int getSoSachToiDa() {
        return soSachToiDa;
    }

    public int getSoNgayMuonToiDa() {
        return soNgayMuonToiDa;
    }

    public static LoaiDocGia fromString(String val) {
        if (val == null) return SINH_VIEN;
        for (LoaiDocGia l : values()) {
            if (l.name().equalsIgnoreCase(val.trim())) {
                return l;
            }
        }
        return SINH_VIEN;
    }
}
