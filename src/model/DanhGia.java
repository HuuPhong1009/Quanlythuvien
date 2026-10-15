package model;

import java.io.Serializable;
import java.util.Date;

public class DanhGia implements Serializable {
    private static final long serialVersionUID = 1L;

    private int maDanhGia;
    private int maSach;
    private int maNguoiDung;
    private String tenNguoiDung;
    private int soSao;
    private String noiDung;
    private Date ngayDang;

    public DanhGia() {
        soSao = 5;
        ngayDang = new Date();
    }

    public int getMaDanhGia() { return maDanhGia; }
    public void setMaDanhGia(int maDanhGia) { this.maDanhGia = maDanhGia; }

    public int getMaSach() { return maSach; }
    public void setMaSach(int maSach) { this.maSach = maSach; }

    public int getMaNguoiDung() { return maNguoiDung; }
    public void setMaNguoiDung(int maNguoiDung) { this.maNguoiDung = maNguoiDung; }

    public String getTenNguoiDung() { return tenNguoiDung; }
    public void setTenNguoiDung(String tenNguoiDung) { this.tenNguoiDung = tenNguoiDung; }

    public int getSoSao() { return soSao; }
    public void setSoSao(int soSao) { this.soSao = soSao; }

    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }

    public Date getNgayDang() { return ngayDang; }
    public void setNgayDang(Date ngayDang) { this.ngayDang = ngayDang; }
}
