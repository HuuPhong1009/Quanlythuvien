package data;

import model.DanhGia;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDB {

    private static List<DanhGia> fallbackReviews = null;

    private static synchronized List<DanhGia> getFallbackReviews() {
        if (fallbackReviews != null) {
            return fallbackReviews;
        }
        fallbackReviews = new ArrayList<DanhGia>();

        DanhGia d1 = new DanhGia();
        d1.setMaDanhGia(1);
        d1.setMaSach(1);
        d1.setMaNguoiDung(2);
        d1.setTenNguoiDung("Nguyễn Văn An");
        d1.setSoSao(5);
        d1.setNoiDung("Sách rất hay và dễ hiểu, chuẩn kiến thức Java Servlet & JSP theo mô hình MVC!");
        d1.setNgayDang(new java.util.Date());
        fallbackReviews.add(d1);

        DanhGia d2 = new DanhGia();
        d2.setMaDanhGia(2);
        d2.setMaSach(8);
        d2.setMaNguoiDung(3);
        d2.setTenNguoiDung("Trần Văn Phong");
        d2.setSoSao(5);
        d2.setNoiDung("Cuốn sách kinh điển về vũ trụ và thời gian của Stephen Hawking, rất đáng đọc.");
        d2.setNgayDang(new java.util.Date());
        fallbackReviews.add(d2);

        return fallbackReviews;
    }

    public static synchronized List<DanhGia> getByBook(int bookId) {
        List<DanhGia> cache = getFallbackReviews();
        List<DanhGia> result = new ArrayList<DanhGia>();

        // 1. Add all in-memory reviews for this book first (newest at top)
        for (DanhGia d : cache) {
            if (d.getMaSach() == bookId) {
                result.add(d);
            }
        }

        // 2. Also load from MySQL if available and not already in result
        String sql = "SELECT d.*, u.ho_ten FROM danh_gia_sach d JOIN nguoi_dung u ON d.ma_nguoi_dung = u.ma_nguoi_dung WHERE d.ma_sach = ? ORDER BY d.ma_danh_gia DESC";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = DBUtil.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, bookId);
            rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("ma_danh_gia");
                String content = rs.getString("noi_dung");
                boolean exists = false;
                for (DanhGia existing : result) {
                    if (existing.getMaDanhGia() == id ||
                        (existing.getNoiDung() != null && existing.getNoiDung().equals(content))) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    DanhGia dg = new DanhGia();
                    dg.setMaDanhGia(id);
                    dg.setMaSach(rs.getInt("ma_sach"));
                    dg.setMaNguoiDung(rs.getInt("ma_nguoi_dung"));
                    dg.setTenNguoiDung(rs.getString("ho_ten"));
                    dg.setSoSao(rs.getInt("so_sao"));
                    dg.setNoiDung(content);
                    dg.setNgayDang(rs.getTimestamp("ngay_dang"));
                    result.add(dg);
                }
            }
        } catch (Exception ignored) {
        } finally {
            DBUtil.close(rs);
            DBUtil.close(ps);
            DBUtil.close(con);
        }

        return result;
    }

    public static synchronized boolean add(DanhGia dg) {
        if (dg == null || dg.getNoiDung() == null || dg.getNoiDung().trim().isEmpty()) {
            return false;
        }

        List<DanhGia> cache = getFallbackReviews();
        int nextId = 1;
        for (DanhGia item : cache) {
            if (item.getMaDanhGia() >= nextId) {
                nextId = item.getMaDanhGia() + 1;
            }
        }
        dg.setMaDanhGia(nextId);
        if (dg.getNgayDang() == null) {
            dg.setNgayDang(new java.util.Date());
        }
        if (dg.getTenNguoiDung() == null || dg.getTenNguoiDung().trim().isEmpty()) {
            dg.setTenNguoiDung("Bạn đọc");
        }

        // Always save to in-memory cache at index 0 so it appears immediately
        cache.add(0, dg);

        // Also try saving to MySQL if available
        String sql = "INSERT INTO danh_gia_sach (ma_sach, ma_nguoi_dung, so_sao, noi_dung) VALUES (?,?,?,?)";
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtil.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, dg.getMaSach());
            ps.setInt(2, dg.getMaNguoiDung());
            ps.setInt(3, dg.getSoSao());
            ps.setString(4, dg.getNoiDung());
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Lỗi ReviewDB.add (MySQL): " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(ps);
            DBUtil.close(con);
        }

        return true;
    }
}
