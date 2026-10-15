package model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Lớp Thẻ thư viện - Khớp 100% với UML Class Diagram
 */
public class TheThuVien implements Serializable {
    private static final long serialVersionUID = 1L;

    private int maThe;
    private String khoaVien;
    private String lopHoc;
    private Date ngayCap;
    private Date ngayHetHan;
    private boolean dangHoatDong;

    public TheThuVien() {
        this.ngayCap = new Date();
        this.dangHoatDong = true;
    }

    public TheThuVien(int maThe, String khoaVien, String lopHoc, Date ngayCap, Date ngayHetHan, boolean dangHoatDong) {
        this.maThe = maThe;
        this.khoaVien = khoaVien;
        this.lopHoc = lopHoc;
        this.ngayCap = ngayCap;
        this.ngayHetHan = ngayHetHan;
        this.dangHoatDong = dangHoatDong;
    }

    public String getThongTinThe() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String han = (ngayHetHan != null) ? sdf.format(ngayHetHan) : "Vô thời hạn";
        return "Mã thẻ: TV-" + maThe + " | Khoa: " + (khoaVien != null ? khoaVien : "N/A")
                + " | Lớp: " + (lopHoc != null ? lopHoc : "N/A")
                + " | Hạn dùng: " + han
                + " | Trạng thái: " + (dangHoatDong ? "Đang hoạt động" : "Bị khóa");
    }

    public int getMaThe() {
        return maThe;
    }

    public void setMaThe(int maThe) {
        this.maThe = maThe;
    }

    public String getKhoaVien() {
        return khoaVien;
    }

    public void setKhoaVien(String khoaVien) {
        this.khoaVien = khoaVien;
    }

    public String getLopHoc() {
        return lopHoc;
    }

    public void setLopHoc(String lopHoc) {
        this.lopHoc = lopHoc;
    }

    public Date getNgayCap() {
        return ngayCap;
    }

    public void setNgayCap(Date ngayCap) {
        this.ngayCap = ngayCap;
    }

    public Date getNgayHetHan() {
        return ngayHetHan;
    }

    public void setNgayHetHan(Date ngayHetHan) {
        this.ngayHetHan = ngayHetHan;
    }

    public boolean isDangHoatDong() {
        return dangHoatDong;
    }

    public void setDangHoatDong(boolean dangHoatDong) {
        this.dangHoatDong = dangHoatDong;
    }
}
