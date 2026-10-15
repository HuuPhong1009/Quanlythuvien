package model;

import java.io.Serializable;
import java.util.Date;

/**
 * Lớp Chi Tiết Phiếu Mượn - Khớp 100% với UML Class Diagram
 */
public class ChiTietPhieuMuon implements Serializable {
    private static final long serialVersionUID = 1L;

    private int maCt;
    private int maPhieu;
    private BanSaoSach banSaoSach;
    private Date ngayTra;
    private TinhTrangSach tinhTrangKhiMuon;
    private TinhTrangSach tinhTrangKhiTra;

    // Thuộc tính bổ trợ
    private int maSach;
    private String tieuDeSach;
    private int soLuong;
    private double tienPhat;

    public ChiTietPhieuMuon() {
        this.soLuong = 1;
        this.tinhTrangKhiMuon = TinhTrangSach.TOT;
        this.tienPhat = 0.0;
    }

    // Phương thức nghiệp vụ theo Class Diagram
    public void traSach(TinhTrangSach tinhTrang) {
        this.ngayTra = new Date();
        this.tinhTrangKhiTra = (tinhTrang != null) ? tinhTrang : TinhTrangSach.TOT;
        if (this.banSaoSach != null) {
            this.banSaoSach.capNhatTinhTrang(this.tinhTrangKhiTra);
            this.banSaoSach.setSanSang(this.tinhTrangKhiTra != TinhTrangSach.HU_HONG);
        }
    }

    public boolean isDaTra() {
        return this.ngayTra != null;
    }

    // Getters and Setters chuẩn Class Diagram
    public BanSaoSach getBanSaoSach() { return banSaoSach; }
    public void setBanSaoSach(BanSaoSach banSaoSach) { this.banSaoSach = banSaoSach; }

    public Date getNgayTra() { return ngayTra; }
    public void setNgayTra(Date ngayTra) { this.ngayTra = ngayTra; }

    public TinhTrangSach getTinhTrangKhiMuon() { return tinhTrangKhiMuon; }
    public void setTinhTrangKhiMuon(TinhTrangSach tinhTrangKhiMuon) { this.tinhTrangKhiMuon = tinhTrangKhiMuon; }

    public TinhTrangSach getTinhTrangKhiTra() { return tinhTrangKhiTra; }
    public void setTinhTrangKhiTra(TinhTrangSach tinhTrangKhiTra) { this.tinhTrangKhiTra = tinhTrangKhiTra; }

    // Tương thích ngược với JSP và DAO
    public int getMaCt() { return maCt; }
    public void setMaCt(int maCt) { this.maCt = maCt; }

    public int getMaPhieu() { return maPhieu; }
    public void setMaPhieu(int maPhieu) { this.maPhieu = maPhieu; }

    public int getMaSach() { return maSach; }
    public void setMaSach(int maSach) { this.maSach = maSach; }

    public String getTieuDeSach() { return tieuDeSach; }
    public void setTieuDeSach(String tieuDeSach) { this.tieuDeSach = tieuDeSach; }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }

    public String getTinhTrangMuon() {
        return (tinhTrangKhiMuon != null) ? tinhTrangKhiMuon.name() : "TOT";
    }

    public void setTinhTrangMuon(String tt) {
        this.tinhTrangKhiMuon = TinhTrangSach.fromString(tt);
    }

    public String getTinhTrangTra() {
        return (tinhTrangKhiTra != null) ? tinhTrangKhiTra.name() : null;
    }

    public void setTinhTrangTra(String tt) {
        if (tt != null) {
            this.tinhTrangKhiTra = TinhTrangSach.fromString(tt);
        }
    }

    public double getTienPhat() { return tienPhat; }
    public void setTienPhat(double tienPhat) { this.tienPhat = tienPhat; }
}
