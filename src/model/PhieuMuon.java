package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Lớp Phiếu Mượn - Khớp 100% với UML Class Diagram
 */
public class PhieuMuon implements Serializable {
    private static final long serialVersionUID = 1L;

    private int maPhieuMuon;
    private Date ngayMuon;
    private Date hanTra;
    private String ghiChu;
    private TrangThaiPhieu trangThai;

    // Quan hệ với các lớp khác theo Class Diagram
    private ThuThu thuThuDuyet;
    private QuyDinhMuon quyDinhMuon;
    private PhieuPhat phieuPhat;
    private List<ChiTietPhieuMuon> chiTietList;

    // Các thuộc tính bổ trợ giao diện
    private int maDocGia;
    private String tenDocGia;
    private Date ngayTraThucTe;

    public PhieuMuon() {
        this.trangThai = TrangThaiPhieu.CHO_DUYET;
        this.chiTietList = new ArrayList<ChiTietPhieuMuon>();
        this.quyDinhMuon = new QuyDinhMuon();
    }

    // ========================================================================
    // CÁC PHƯƠNG THỨC NGHIỆP VỤ (Business Logic Methods)
    // ========================================================================

    /**
     * Thủ thư phê duyệt yêu cầu mượn:
     * Chuyển trạng thái sang Đang mượn (DANG_MUON) và gán người duyệt.
     */
    public boolean duyetMuon(ThuThu thuThu) {
        if (thuThu == null) return false;
        this.thuThuDuyet = thuThu;
        this.trangThai = TrangThaiPhieu.DANG_MUON;
        return true;
    }

    /**
     * Thủ thư từ chối yêu cầu mượn kèm theo lý do cụ thể
     */
    public void tuChoi(String lyDo) {
        this.trangThai = TrangThaiPhieu.TU_CHOI;
        if (lyDo != null && !lyDo.trim().isEmpty()) {
            this.ghiChu = (this.ghiChu != null ? this.ghiChu + " | " : "") + "Từ chối: " + lyDo;
        }
    }

    /**
     * Tính số ngày quá hạn mượn sách:
     * - So sánh mốc tính (ngày trả thực tế hoặc thời điểm hiện tại) với hạn trả.
     * - Trả về số ngày quá hạn (làm tròn lên tối thiểu 1 ngày nếu đã vượt hạn).
     */
    public int tinhSoNgayQuaHan() {
        if (hanTra == null || trangThai == TrangThaiPhieu.DA_TRA || trangThai == TrangThaiPhieu.DA_HUY || trangThai == TrangThaiPhieu.TU_CHOI) {
            return 0;
        }
        Date mocTinh = (ngayTraThucTe != null) ? ngayTraThucTe : new Date();
        if (mocTinh.after(hanTra)) {
            long diffMs = mocTinh.getTime() - hanTra.getTime();
            int days = (int) (diffMs / (1000 * 60 * 60 * 24));
            return Math.max(1, days);
        }
        return 0;
    }

    /**
     * Kiểm tra nhanh phiếu mượn có đang bị trễ hạn hay không
     */
    public boolean isQuaHan() {
        if (hanTra == null || trangThai == TrangThaiPhieu.DA_TRA || trangThai == TrangThaiPhieu.DA_HUY || trangThai == TrangThaiPhieu.TU_CHOI) {
            return false;
        }
        Date mocTinh = (ngayTraThucTe != null) ? ngayTraThucTe : new Date();
        return mocTinh.after(hanTra);
    }

    /**
     * Tính số phút quá hạn (hỗ trợ kiểm thử nhanh các trường hợp mượn 1 phút, 2 phút)
     */
    public long tinhSoPhutQuaHan() {
        if (hanTra == null || trangThai == TrangThaiPhieu.DA_TRA || trangThai == TrangThaiPhieu.DA_HUY || trangThai == TrangThaiPhieu.TU_CHOI) {
            return 0;
        }
        Date mocTinh = (ngayTraThucTe != null) ? ngayTraThucTe : new Date();
        if (mocTinh.after(hanTra)) {
            long diffMs = mocTinh.getTime() - hanTra.getTime();
            return Math.max(1, diffMs / (1000 * 60));
        }
        return 0;
    }

    // Getters and Setters chuẩn Class Diagram
    public int getMaPhieuMuon() { return maPhieuMuon; }
    public void setMaPhieuMuon(int maPhieuMuon) { this.maPhieuMuon = maPhieuMuon; }

    public Date getNgayMuon() { return ngayMuon; }
    public void setNgayMuon(Date ngayMuon) { this.ngayMuon = ngayMuon; }

    public Date getHanTra() { return hanTra; }
    public void setHanTra(Date hanTra) { this.hanTra = hanTra; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public TrangThaiPhieu getTrangThaiEnum() { return trangThai; }
    public void setTrangThaiEnum(TrangThaiPhieu trangThai) { this.trangThai = trangThai; }

    public ThuThu getThuThuDuyet() { return thuThuDuyet; }
    public void setThuThuDuyet(ThuThu thuThuDuyet) { this.thuThuDuyet = thuThuDuyet; }

    public QuyDinhMuon getQuyDinhMuon() { return quyDinhMuon; }
    public void setQuyDinhMuon(QuyDinhMuon quyDinhMuon) { this.quyDinhMuon = quyDinhMuon; }

    public PhieuPhat getPhieuPhat() { return phieuPhat; }
    public void setPhieuPhat(PhieuPhat phieuPhat) { this.phieuPhat = phieuPhat; }

    public List<ChiTietPhieuMuon> getChiTietList() { return chiTietList; }
    public void setChiTietList(List<ChiTietPhieuMuon> chiTietList) { this.chiTietList = chiTietList; }

    // Tương thích ngược với EL tag trong JSP: ${borrow.maPhieu}, ${borrow.trangThai}
    public int getMaPhieu() { return maPhieuMuon; }
    public void setMaPhieu(int maPhieu) { this.maPhieuMuon = maPhieu; }

    public String getTrangThai() {
        return (trangThai != null) ? trangThai.name() : "CHO_DUYET";
    }

    public void setTrangThai(String trangThaiStr) {
        this.trangThai = TrangThaiPhieu.fromString(trangThaiStr);
    }

    public int getMaDocGia() { return maDocGia; }
    public void setMaDocGia(int maDocGia) { this.maDocGia = maDocGia; }

    public String getTenDocGia() { return tenDocGia; }
    public void setTenDocGia(String tenDocGia) { this.tenDocGia = tenDocGia; }

    public Date getNgayTraThucTe() { return ngayTraThucTe; }
    public void setNgayTraThucTe(Date ngayTraThucTe) { this.ngayTraThucTe = ngayTraThucTe; }
}
