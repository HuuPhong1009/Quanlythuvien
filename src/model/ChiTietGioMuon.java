package model;

import java.io.Serializable;

/**
 * Lớp Chi tiết giỏ mượn - Khớp 100% với UML Class Diagram
 */
public class ChiTietGioMuon implements Serializable {
    private static final long serialVersionUID = 1L;

    private Sach sach;
    private int soLuong;

    public ChiTietGioMuon() {
        this.soLuong = 1;
    }

    public ChiTietGioMuon(Sach sach, int soLuong) {
        this.sach = sach;
        this.soLuong = soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public Sach getSach() {
        return sach;
    }

    public void setSach(Sach sach) {
        this.sach = sach;
    }
}
