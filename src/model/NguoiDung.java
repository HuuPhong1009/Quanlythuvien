package model;

import java.io.Serializable;
import java.util.Date;

/**
 * Lớp Người Dùng - Khớp 100% với UML Class Diagram
 */
public class NguoiDung implements Serializable {
    private static final long serialVersionUID = 1L;

    private int maND;
    private String hoTen;
    private String email;
    private String soDienThoai;
    private String matKhauHash;
    private Date ngayTao;
    private String vaiTro; // 'QUAN_TRI' or 'DOC_GIA'

    public NguoiDung() {
        this.vaiTro = "DOC_GIA";
        this.ngayTao = new Date();
    }

    public NguoiDung(int maND, String hoTen, String email, String soDienThoai, String matKhauHash, String vaiTro) {
        this.maND = maND;
        this.hoTen = hoTen;
        this.email = email;
        this.soDienThoai = soDienThoai;
        this.matKhauHash = matKhauHash;
        this.vaiTro = vaiTro;
        this.ngayTao = new Date();
    }

    // Các phương thức nghiệp vụ theo Class Diagram
    public NguoiDung getThongTin() {
        return this;
    }

    public boolean dangNhap(String emailInput, String matKhauInput) {
        if (emailInput == null || matKhauInput == null) return false;
        boolean emailMatch = emailInput.equalsIgnoreCase(this.email) ||
                (this.soDienThoai != null && emailInput.equals(this.soDienThoai));
        boolean passMatch = matKhauInput.equals(this.matKhauHash);
        return emailMatch && passMatch;
    }

    public void setMatKhau(String matKhauMoi) {
        this.matKhauHash = matKhauMoi;
    }

    public boolean isQuanTri() {
        return "QUAN_TRI".equalsIgnoreCase(vaiTro);
    }

    // Getters and Setters chuẩn Class Diagram
    public int getMaND() { return maND; }
    public void setMaND(int maND) { this.maND = maND; }

    public String getMatKhauHash() { return matKhauHash; }
    public void setMatKhauHash(String matKhauHash) { this.matKhauHash = matKhauHash; }

    public Date getNgayTao() { return ngayTao; }
    public void setNgayTao(Date ngayTao) { this.ngayTao = ngayTao; }

    // Bí danh tương thích ngược với các file JSP / Controller hiện tại
    public int getMaNguoiDung() { return maND; }
    public void setMaNguoiDung(int maNguoiDung) { this.maND = maNguoiDung; }

    public String getMatKhau() { return matKhauHash; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public String getVaiTro() { return vaiTro; }
    public void setVaiTro(String vaiTro) { this.vaiTro = vaiTro; }
}
