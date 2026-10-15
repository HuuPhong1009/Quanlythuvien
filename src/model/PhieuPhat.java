package model;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Lớp Phiếu phạt - Khớp 100% với UML Class Diagram
 */
public class PhieuPhat implements Serializable {
    private static final long serialVersionUID = 1L;

    private int maPhieuPhat;
    private double soTienPhat; // decimal
    private Date ngayLap;
    private TrangThaiPhat trangThai;
    private int maPhieuMuon;
    private String lyDo;

    public PhieuPhat() {
        this.ngayLap = new Date();
        this.trangThai = TrangThaiPhat.CHUA_NOP_PHAT;
        this.soTienPhat = 0.0;
    }

    public PhieuPhat(int maPhieuPhat, int maPhieuMuon, double soTienPhat, Date ngayLap, TrangThaiPhat trangThai, String lyDo) {
        this.maPhieuPhat = maPhieuPhat;
        this.maPhieuMuon = maPhieuMuon;
        this.soTienPhat = soTienPhat;
        this.ngayLap = (ngayLap != null) ? ngayLap : new Date();
        this.trangThai = (trangThai != null) ? trangThai : TrangThaiPhat.CHUA_NOP_PHAT;
        this.lyDo = lyDo;
    }

    /**
     * Xác nhận độc giả đã nộp tiền phạt (chuyển trạng thái sang DA_NOP_PHAT)
     */
    public boolean xacNhanNopPhat() {
        this.trangThai = TrangThaiPhat.DA_NOP_PHAT;
        return true;
    }

    /**
     * Định dạng số tiền phạt sang tiền tệ Việt Nam (VNĐ)
     */
    public String getSoTienPhatFormat() {
        Locale localeVN = Locale.forLanguageTag("vi-VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);
        return currencyVN.format(soTienPhat);
    }

    // Getters and Setters
    public int getMaPhieuPhat() { return maPhieuPhat; }
    public void setMaPhieuPhat(int maPhieuPhat) { this.maPhieuPhat = maPhieuPhat; }

    public double getSoTienPhat() { return soTienPhat; }
    public void setSoTienPhat(double soTienPhat) { this.soTienPhat = soTienPhat; }

    public Date getNgayLap() { return ngayLap; }
    public void setNgayLap(Date ngayLap) { this.ngayLap = ngayLap; }

    public TrangThaiPhat getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiPhat trangThai) { this.trangThai = trangThai; }

    public int getMaPhieuMuon() { return maPhieuMuon; }
    public void setMaPhieuMuon(int maPhieuMuon) { this.maPhieuMuon = maPhieuMuon; }

    public String getLyDo() { return lyDo; }
    public void setLyDo(String lyDo) { this.lyDo = lyDo; }
}
