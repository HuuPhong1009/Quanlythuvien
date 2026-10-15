package model;

import java.io.Serializable;

public class TheLoai implements Serializable {
    private static final long serialVersionUID = 1L;

    private int maTheLoai;
    private String tenTheLoai;
    private String moTa;
    private String icon;

    public TheLoai() {
        this.icon = "bi-book";
    }

    public TheLoai(int maTheLoai, String tenTheLoai, String moTa) {
        this(maTheLoai, tenTheLoai, moTa, "bi-book");
    }

    public TheLoai(int maTheLoai, String tenTheLoai, String moTa, String icon) {
        this.maTheLoai = maTheLoai;
        this.tenTheLoai = tenTheLoai;
        this.moTa = moTa;
        this.icon = (icon != null && !icon.trim().isEmpty()) ? icon : "bi-book";
    }

    public int getMaTheLoai() { return maTheLoai; }
    public void setMaTheLoai(int maTheLoai) { this.maTheLoai = maTheLoai; }

    public String getTenTheLoai() { return tenTheLoai; }
    public void setTenTheLoai(String tenTheLoai) { this.tenTheLoai = tenTheLoai; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public String getIcon() {
        if (icon == null || icon.trim().isEmpty()) {
            return "bi-book";
        }
        return icon;
    }
    public void setIcon(String icon) { this.icon = icon; }
}
