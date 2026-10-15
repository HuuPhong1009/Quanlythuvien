package model;

import java.io.Serializable;

/**
 * Lớp Nhà Xuất Bản - Khớp 100% với UML Class Diagram
 */
public class NhaXuatBan implements Serializable {
    private static final long serialVersionUID = 1L;

    private int maNxb;
    private String tenNxb;
    private String email;
    private String soDienThoai;
    private boolean dangHopTac;
    private String diaChi;

    public NhaXuatBan() {
        this.dangHopTac = true;
    }

    public NhaXuatBan(int maNxb, String tenNxb, String email, String soDienThoai, String diaChi) {
        this.maNxb = maNxb;
        this.tenNxb = tenNxb;
        this.email = email;
        this.soDienThoai = soDienThoai;
        this.diaChi = diaChi;
        this.dangHopTac = true;
    }

    public void ngungHopTac() {
        this.dangHopTac = false;
    }

    public int getMaNxb() { return maNxb; }
    public void setMaNxb(int maNxb) { this.maNxb = maNxb; }

    public String getTenNxb() { return tenNxb; }
    public void setTenNxb(String tenNxb) { this.tenNxb = tenNxb; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public boolean isDangHopTac() { return dangHopTac; }
    public void setDangHopTac(boolean dangHopTac) { this.dangHopTac = dangHopTac; }

    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
}
