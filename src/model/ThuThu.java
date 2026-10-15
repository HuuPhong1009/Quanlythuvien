package model;

import java.util.Date;

/**
 * Lớp Thủ thư kế thừa Người dùng - Khớp 100% với UML Class Diagram
 */
public class ThuThu extends NguoiDung {
    private static final long serialVersionUID = 1L;

    private int maThuThu;
    private Date ngayVaoLam;

    public ThuThu() {
        super();
        setVaiTro("QUAN_TRI");
        this.ngayVaoLam = new Date();
    }

    public ThuThu(int maThuThu, String hoTen, String email, String soDienThoai, String matKhau, Date ngayVaoLam) {
        super(maThuThu, hoTen, email, soDienThoai, matKhau, "QUAN_TRI");
        this.maThuThu = maThuThu;
        this.ngayVaoLam = (ngayVaoLam != null) ? ngayVaoLam : new Date();
    }

    public int getMaThuThu() {
        return maThuThu;
    }

    public void setMaThuThu(int maThuThu) {
        this.maThuThu = maThuThu;
        setMaNguoiDung(maThuThu);
    }

    public Date getNgayVaoLam() {
        return ngayVaoLam;
    }

    public void setNgayVaoLam(Date ngayVaoLam) {
        this.ngayVaoLam = ngayVaoLam;
    }
}
