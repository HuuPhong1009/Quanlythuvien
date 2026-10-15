package model;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Lớp Sách - Khớp 100% với UML Class Diagram
 */
public class Sach implements Serializable {
    private static final long serialVersionUID = 1L;

    private int maSach;
    private String isbn;
    private String tieuDe;
    private String tacGia;
    private double giaBia; // decimal

    // Quan hệ với các lớp khác theo Class Diagram
    private List<BanSaoSach> danhSachBanSao;
    private TheLoai theLoai;
    private NhaXuatBan nhaXuatBan;
    private List<DanhGia> danhSachDanhGia;

    // Thuộc tính bổ trợ giao diện và cơ sở dữ liệu
    private int maTheLoai;
    private String tenTheLoai;
    private int maNxb;
    private String tenNxb;
    private int namXuatBan;
    private int soLuongTong;
    private int soLuongCon;
    private String viTriKe;
    private String tinhTrang;
    private String moTa;
    private String hinhAnh;

    public Sach() {
        this.isbn = "";
        this.tieuDe = "";
        this.tacGia = "";
        this.giaBia = 0.0;
        this.viTriKe = "Khu A";
        this.tinhTrang = "TOT";
        this.moTa = "";
        this.hinhAnh = "images/books/book_1.jpg";
        this.danhSachBanSao = new ArrayList<BanSaoSach>();
        this.danhSachDanhGia = new ArrayList<DanhGia>();
    }

    // Các phương thức nghiệp vụ theo Class Diagram
    public int demBanSaoCon() {
        if (danhSachBanSao != null && !danhSachBanSao.isEmpty()) {
            int count = 0;
            for (BanSaoSach bss : danhSachBanSao) {
                if (bss.isSanSang() && bss.getTinhTrang() != TinhTrangSach.HU_HONG) {
                    count++;
                }
            }
            return count;
        }
        return soLuongCon;
    }

    public boolean kiemTraConSach() {
        return demBanSaoCon() > 0;
    }

    // Tương thích ngược với EL tag ${book.conSach} trong JSP
    public boolean isConSach() {
        return kiemTraConSach();
    }

    public String getGiaBiaFormat() {
        Locale localeVN = Locale.forLanguageTag("vi-VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);
        return currencyVN.format(giaBia);
    }

    // Getters and Setters chuẩn Class Diagram
    public int getMaSach() { return maSach; }
    public void setMaSach(int maSach) { this.maSach = maSach; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getTieuDe() { return tieuDe; }
    public void setTieuDe(String tieuDe) { this.tieuDe = tieuDe; }

    public String getTacGia() { return tacGia; }
    public void setTacGia(String tacGia) { this.tacGia = tacGia; }

    public double getGiaBia() { return giaBia; }
    public void setGiaBia(double giaBia) { this.giaBia = giaBia; }

    public List<BanSaoSach> getDanhSachBanSao() { return danhSachBanSao; }
    public void setDanhSachBanSao(List<BanSaoSach> danhSachBanSao) { this.danhSachBanSao = danhSachBanSao; }

    public TheLoai getTheLoai() {
        if (theLoai == null && maTheLoai > 0) {
            for (TheLoai tl : data.CategoryDB.getAll()) {
                if (tl.getMaTheLoai() == maTheLoai) {
                    this.theLoai = tl;
                    if (this.tenTheLoai == null || this.tenTheLoai.trim().isEmpty()) {
                        this.tenTheLoai = tl.getTenTheLoai();
                    }
                    break;
                }
            }
        }
        return theLoai;
    }
    public void setTheLoai(TheLoai theLoai) {
        this.theLoai = theLoai;
        if (theLoai != null && theLoai.getTenTheLoai() != null && !theLoai.getTenTheLoai().trim().isEmpty()) {
            this.tenTheLoai = theLoai.getTenTheLoai();
        }
    }

    public NhaXuatBan getNhaXuatBan() { return nhaXuatBan; }
    public void setNhaXuatBan(NhaXuatBan nhaXuatBan) { this.nhaXuatBan = nhaXuatBan; }

    public List<DanhGia> getDanhSachDanhGia() { return danhSachDanhGia; }
    public void setDanhSachDanhGia(List<DanhGia> danhSachDanhGia) { this.danhSachDanhGia = danhSachDanhGia; }

    // Getters and Setters phục vụ giao diện JSP và DAO
    public int getMaTheLoai() { return maTheLoai; }
    public void setMaTheLoai(int maTheLoai) {
        this.maTheLoai = maTheLoai;
        if (this.tenTheLoai == null || this.tenTheLoai.trim().isEmpty()) {
            for (TheLoai tl : data.CategoryDB.getAll()) {
                if (tl.getMaTheLoai() == maTheLoai) {
                    this.tenTheLoai = tl.getTenTheLoai();
                    this.theLoai = tl;
                    break;
                }
            }
        }
    }

    public String getTenTheLoai() {
        if (tenTheLoai != null && !tenTheLoai.trim().isEmpty()) {
            return tenTheLoai;
        }
        if (theLoai != null && theLoai.getTenTheLoai() != null && !theLoai.getTenTheLoai().trim().isEmpty()) {
            this.tenTheLoai = theLoai.getTenTheLoai();
            return this.tenTheLoai;
        }
        if (maTheLoai > 0) {
            for (TheLoai tl : data.CategoryDB.getAll()) {
                if (tl.getMaTheLoai() == maTheLoai) {
                    this.tenTheLoai = tl.getTenTheLoai();
                    this.theLoai = tl;
                    return this.tenTheLoai;
                }
            }
        }
        return "Chưa phân loại";
    }
    public void setTenTheLoai(String tenTheLoai) { this.tenTheLoai = tenTheLoai; }

    public int getMaNxb() { return maNxb; }
    public void setMaNxb(int maNxb) { this.maNxb = maNxb; }

    public String getTenNxb() { return tenNxb; }
    public void setTenNxb(String tenNxb) { this.tenNxb = tenNxb; }

    public int getNamXuatBan() { return namXuatBan; }
    public void setNamXuatBan(int namXuatBan) { this.namXuatBan = namXuatBan; }

    public int getSoLuongTong() { return soLuongTong; }
    public void setSoLuongTong(int soLuongTong) { this.soLuongTong = soLuongTong; }

    public int getSoLuongCon() { return soLuongCon; }
    public void setSoLuongCon(int soLuongCon) { this.soLuongCon = soLuongCon; }

    public String getViTriKe() { return viTriKe; }
    public void setViTriKe(String viTriKe) { this.viTriKe = viTriKe; }

    public String getTinhTrang() { return tinhTrang; }
    public void setTinhTrang(String tinhTrang) { this.tinhTrang = tinhTrang; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public String getHinhAnh() {
        if (hinhAnh != null && (hinhAnh.startsWith("http://") || hinhAnh.startsWith("https://"))) {
            return hinhAnh;
        }
        if (maSach >= 1 && maSach <= 48) {
            return "images/books/real_cover_" + maSach + ".jpg";
        }
        if (hinhAnh == null || hinhAnh.trim().isEmpty()) {
            return "images/books/real_cover_1.jpg";
        }
        if (hinhAnh.endsWith(".svg")) {
            return hinhAnh.replace(".svg", ".jpg").replace("book_", "real_cover_");
        }
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }
}
