package model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * Lớp Quy định mượn sách - Khớp 100% với UML Class Diagram
 */
public class QuyDinhMuon implements Serializable {
    private static final long serialVersionUID = 1L;

    private int maQuyDinh;
    private String tenQuyDinh;
    private LoaiQuyDinh loaiQuyDinh;
    private LoaiDocGia apDungCho;
    private int soSachToiDa;
    private int soNgayMuonToiDa;
    private double mucPhatMoiNgay; // decimal
    private boolean trangThai;
    private String moTa;

    public QuyDinhMuon() {
        this.loaiQuyDinh = LoaiQuyDinh.MUON_NGAN_HAN;
        this.apDungCho = LoaiDocGia.SINH_VIEN;
        this.soSachToiDa = 3;
        this.soNgayMuonToiDa = 14;
        this.mucPhatMoiNgay = 5000.0;
        this.trangThai = true;
        this.tenQuyDinh = "Quy định mượn chuẩn cho Sinh viên";
        this.moTa = "Áp dụng cho mọi sinh viên chính quy";
    }

    public QuyDinhMuon(int maQuyDinh, String tenQuyDinh, LoaiQuyDinh loaiQuyDinh, LoaiDocGia apDungCho,
                       int soSachToiDa, int soNgayMuonToiDa, double mucPhatMoiNgay, boolean trangThai, String moTa) {
        this.maQuyDinh = maQuyDinh;
        this.tenQuyDinh = tenQuyDinh;
        this.loaiQuyDinh = loaiQuyDinh;
        this.apDungCho = apDungCho;
        this.soSachToiDa = soSachToiDa;
        this.soNgayMuonToiDa = soNgayMuonToiDa;
        this.mucPhatMoiNgay = mucPhatMoiNgay;
        this.trangThai = trangThai;
        this.moTa = moTa;
    }

    /**
     * Kiểm tra quy định mượn có đang hợp lệ và có hiệu lực hay không
     */
    public boolean kiemTraHopLe() {
        return trangThai && soSachToiDa > 0 && soNgayMuonToiDa > 0 && mucPhatMoiNgay >= 0;
    }

    /**
     * Tính ngày hạn trả dựa trên ngày mượn và số ngày mượn tối đa cho phép
     */
    public Date tinhHanTra(Date ngayMuon) {
        if (ngayMuon == null) {
            ngayMuon = new Date();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(ngayMuon);
        cal.add(Calendar.DAY_OF_YEAR, soNgayMuonToiDa);
        return cal.getTime();
    }

    /**
     * Tính tổng số tiền phạt dựa trên số ngày trễ hạn và mức phạt mỗi ngày (5.000đ/ngày)
     */
    public double tinhTienPhat(int soNgayTre) {
        if (soNgayTre <= 0) return 0.0;
        return soNgayTre * mucPhatMoiNgay;
    }

    // Getters and Setters
    public int getMaQuyDinh() { return maQuyDinh; }
    public void setMaQuyDinh(int maQuyDinh) { this.maQuyDinh = maQuyDinh; }

    public String getTenQuyDinh() { return tenQuyDinh; }
    public void setTenQuyDinh(String tenQuyDinh) { this.tenQuyDinh = tenQuyDinh; }

    public LoaiQuyDinh getLoaiQuyDinh() { return loaiQuyDinh; }
    public void setLoaiQuyDinh(LoaiQuyDinh loaiQuyDinh) { this.loaiQuyDinh = loaiQuyDinh; }

    public LoaiDocGia getApDungCho() { return apDungCho; }
    public void setApDungCho(LoaiDocGia apDungCho) { this.apDungCho = apDungCho; }

    public int getSoSachToiDa() { return soSachToiDa; }
    public void setSoSachToiDa(int soSachToiDa) { this.soSachToiDa = soSachToiDa; }

    public int getSoNgayMuonToiDa() { return soNgayMuonToiDa; }
    public void setSoNgayMuonToiDa(int soNgayMuonToiDa) { this.soNgayMuonToiDa = soNgayMuonToiDa; }

    public double getMucPhatMoiNgay() { return mucPhatMoiNgay; }
    public void setMucPhatMoiNgay(double mucPhatMoiNgay) { this.mucPhatMoiNgay = mucPhatMoiNgay; }

    public boolean isTrangThai() { return trangThai; }
    public void setTrangThai(boolean trangThai) { this.trangThai = trangThai; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
}
