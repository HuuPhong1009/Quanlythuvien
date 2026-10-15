package model;

import java.util.Date;

/**
 * Lớp Độc Giả kế thừa Người Dùng - Khớp 100% với UML Class Diagram
 */
public class DocGia extends NguoiDung {
    private static final long serialVersionUID = 1L;

    private int maDocGia;
    private LoaiDocGia loaiDocGia;
    private TheThuVien theThuVien;
    private GioMuon gioMuon;
    private Date ngayHetHan;

    public DocGia() {
        super();
        setVaiTro("DOC_GIA");
        this.loaiDocGia = LoaiDocGia.SINH_VIEN;
    }

    public DocGia(int maDocGia, String hoTen, String email, String soDienThoai, String matKhau, LoaiDocGia loaiDocGia) {
        super(maDocGia, hoTen, email, soDienThoai, matKhau, "DOC_GIA");
        this.maDocGia = maDocGia;
        this.loaiDocGia = (loaiDocGia != null) ? loaiDocGia : LoaiDocGia.SINH_VIEN;
    }

    public int getSoSachMuonToiDa() {
        if (loaiDocGia != null) {
            return loaiDocGia.getSoSachToiDa();
        }
        return 3;
    }

    public int getSoNgayMuonToiDa() {
        if (loaiDocGia != null) {
            return loaiDocGia.getSoNgayMuonToiDa();
        }
        return 14;
    }

    // Getters and Setters chuẩn Class Diagram
    public int getMaDocGia() {
        return (maDocGia > 0) ? maDocGia : getMaND();
    }

    public void setMaDocGia(int maDocGia) {
        this.maDocGia = maDocGia;
        setMaND(maDocGia);
    }

    public LoaiDocGia getLoaiDocGiaEnum() {
        return loaiDocGia;
    }

    public void setLoaiDocGiaEnum(LoaiDocGia loaiDocGia) {
        this.loaiDocGia = loaiDocGia;
    }

    // Getter/Setter String để JSP và Form HTML không bị lỗi
    public String getLoaiDocGia() {
        return (loaiDocGia != null) ? loaiDocGia.name() : "SINH_VIEN";
    }

    public void setLoaiDocGia(String loaiDocGia) {
        this.loaiDocGia = LoaiDocGia.fromString(loaiDocGia);
    }

    public TheThuVien getTheThuVien() {
        return theThuVien;
    }

    public void setTheThuVien(TheThuVien theThuVien) {
        this.theThuVien = theThuVien;
    }

    public GioMuon getGioMuon() {
        return gioMuon;
    }

    public void setGioMuon(GioMuon gioMuon) {
        this.gioMuon = gioMuon;
    }

    public Date getNgayHetHan() {
        return ngayHetHan;
    }

    public void setNgayHetHan(Date ngayHetHan) {
        this.ngayHetHan = ngayHetHan;
    }
}
