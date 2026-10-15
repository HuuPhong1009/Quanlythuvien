package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp Giỏ Mượn - Khớp 100% với UML Class Diagram
 */
public class GioMuon implements Serializable {
    private static final long serialVersionUID = 1L;

    private int maGioMuon;
    private int tongSoSach;
    private List<ChiTietGioMuon> danhSachSach;

    public GioMuon() {
        danhSachSach = new ArrayList<ChiTietGioMuon>();
    }

    // Các phương thức theo Class Diagram
    public int tinhTongSoSach() {
        int sum = 0;
        for (ChiTietGioMuon item : danhSachSach) {
            sum += item.getSoLuong();
        }
        this.tongSoSach = sum;
        return this.tongSoSach;
    }

    public boolean themSach(int maSach, int soLuong) {
        if (maSach <= 0 || soLuong <= 0) return false;
        for (ChiTietGioMuon item : danhSachSach) {
            if (item.getSach() != null && item.getSach().getMaSach() == maSach) {
                int newQty = item.getSoLuong() + soLuong;
                item.setSoLuong(newQty);
                tinhTongSoSach();
                return true;
            }
        }
        return false;
    }

    public void themSach(Sach s, int soLuong) {
        if (s == null || s.getSoLuongCon() <= 0) {
            return;
        }
        int maxStock = s.getSoLuongCon();
        for (ChiTietGioMuon item : danhSachSach) {
            if (item.getSach() != null && item.getSach().getMaSach() == s.getMaSach()) {
                item.setSach(s);
                int newQty = item.getSoLuong() + soLuong;
                if (newQty > maxStock) {
                    newQty = maxStock;
                }
                item.setSoLuong(newQty);
                tinhTongSoSach();
                return;
            }
        }
        int initialQty = soLuong;
        if (initialQty < 1) initialQty = 1;
        if (initialQty > maxStock) initialQty = maxStock;
        danhSachSach.add(new ChiTietGioMuon(s, initialQty));
        tinhTongSoSach();
    }

    public void capNhatSoLuong(int maSach, int soLuong) {
        for (int i = 0; i < danhSachSach.size(); i++) {
            ChiTietGioMuon item = danhSachSach.get(i);
            if (item.getSach() != null && item.getSach().getMaSach() == maSach) {
                if (soLuong <= 0) {
                    danhSachSach.remove(i);
                } else {
                    int maxStock = item.getSach().getSoLuongCon();
                    if (maxStock > 0 && soLuong > maxStock) {
                        soLuong = maxStock;
                    }
                    item.setSoLuong(soLuong);
                }
                tinhTongSoSach();
                return;
            }
        }
    }

    public void xoaSach(int maSach) {
        capNhatSoLuong(maSach, 0);
    }

    public void xoaHet() {
        danhSachSach.clear();
        this.tongSoSach = 0;
    }

    // Tương thích ngược với JSP: ${borrowCart.tongSoLuong}
    public int getTongSoLuong() {
        return tinhTongSoSach();
    }

    public int getTongSoSachDangCo() {
        int sum = 0;
        for (ChiTietGioMuon item : danhSachSach) {
            if (item.getSach() != null) {
                sum += item.getSach().getSoLuongCon();
            }
        }
        return sum;
    }

    // Getters and Setters chuẩn
    public int getMaGioMuon() { return maGioMuon; }
    public void setMaGioMuon(int maGioMuon) { this.maGioMuon = maGioMuon; }

    public int getTongSoSach() {
        tinhTongSoSach();
        return this.tongSoSach;
    }
    public void setTongSoSach(int tongSoSach) { this.tongSoSach = tongSoSach; }

    public List<ChiTietGioMuon> getDanhSachSach() { return danhSachSach; }
    public void setDanhSachSach(List<ChiTietGioMuon> danhSachSach) {
        this.danhSachSach = danhSachSach;
        tinhTongSoSach();
    }
}
