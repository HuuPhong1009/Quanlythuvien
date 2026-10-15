package data;

import model.TheLoai;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDB {

    public static List<TheLoai> getAll() {
        List<TheLoai> list = new ArrayList<TheLoai>();
        String sql = "SELECT * FROM the_loai ORDER BY ma_the_loai";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = DBUtil.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                TheLoai tl = new TheLoai();
                tl.setMaTheLoai(rs.getInt("ma_the_loai"));
                tl.setTenTheLoai(rs.getString("ten_the_loai"));
                tl.setMoTa(rs.getString("mo_ta"));
                try {
                    String ic = rs.getString("icon");
                    if (ic != null && !ic.isEmpty()) {
                        tl.setIcon(ic);
                    }
                } catch (Exception e) {
                    // Column icon might not exist in old schema
                }
                list.add(tl);
            }
        } catch (Exception e) {
            // Memory fallback if DB is not reachable
            return getFallbackCategories();
        } finally {
            DBUtil.close(rs);
            DBUtil.close(ps);
            DBUtil.close(con);
        }

        if (list.isEmpty()) {
            return getFallbackCategories();
        }
        return list;
    }

    private static List<TheLoai> getFallbackCategories() {
        List<TheLoai> list = new ArrayList<TheLoai>();
        list.add(new TheLoai(1, "Công Nghệ Thông Tin", "Lập trình web, phần mềm, giải thuật", "bi-laptop"));
        list.add(new TheLoai(2, "AI & Khoa Học Dữ Liệu", "Trí tuệ nhân tạo, Machine Learning", "bi-cpu"));
        list.add(new TheLoai(3, "Khoa Học Tự Nhiên", "Vật lý lý thuyết, toán học, vũ trụ", "bi-atom"));
        list.add(new TheLoai(4, "Kinh Tế - Quản Trị", "Kinh doanh, đầu tư, khởi nghiệp", "bi-graph-up-arrow"));
        list.add(new TheLoai(5, "Kỹ Năng - Tâm Lý", "Tâm lý học, phát triển bản thân", "bi-person-heart"));
        list.add(new TheLoai(6, "Văn Học Kinh Điển", "Tiểu thuyết, tác phẩm bất hủ", "bi-feather"));
        list.add(new TheLoai(7, "Lịch Sử - Xã Hội", "Lịch sử nhân loại, triết học", "bi-hourglass-split"));
        list.add(new TheLoai(8, "Ngoại Ngữ", "Tiếng Anh, ngôn ngữ học", "bi-translate"));
        return list;
    }
}
