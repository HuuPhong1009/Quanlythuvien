package model;

import java.io.Serializable;

/**
 * Lớp Bản sao sách (cuốn sách vật lý) - Khớp 100% với UML Class Diagram
 */
public class BanSaoSach implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maVach;
    private TinhTrangSach tinhTrang;
    private boolean sanSang;
    private int maSach;

    public BanSaoSach() {
        this.tinhTrang = TinhTrangSach.TOT;
        this.sanSang = true;
    }

    public BanSaoSach(String maVach, int maSach, TinhTrangSach tinhTrang, boolean sanSang) {
        this.maVach = maVach;
        this.maSach = maSach;
        this.tinhTrang = (tinhTrang != null) ? tinhTrang : TinhTrangSach.TOT;
        this.sanSang = sanSang;
    }

    public void capNhatTinhTrang(TinhTrangSach tt) {
        this.tinhTrang = tt;
        if (tt == TinhTrangSach.HU_HONG) {
            this.sanSang = false;
        }
    }

    public String getMaVach() {
        return maVach;
    }

    public void setMaVach(String maVach) {
        this.maVach = maVach;
    }

    public TinhTrangSach getTinhTrang() {
        return tinhTrang;
    }

    public void setTinhTrang(TinhTrangSach tinhTrang) {
        this.tinhTrang = tinhTrang;
    }

    public boolean isSanSang() {
        return sanSang;
    }

    public void setSanSang(boolean sanSang) {
        this.sanSang = sanSang;
    }

    public int getMaSach() {
        return maSach;
    }

    public void setMaSach(int maSach) {
        this.maSach = maSach;
    }
}
