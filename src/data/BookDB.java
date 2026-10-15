package data;

import model.BanSaoSach;
import model.Sach;
import model.TheLoai;
import model.TinhTrangSach;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 * LỚP BookDB (Data Access Object - DAO cho Quản lý Sách)
 * ----------------------------------------------------------------------------
 * Chức năng:
 * 1. Truy vấn danh sách sách từ MySQL hoặc danh sách dự phòng (getAll).
 * 2. Tìm kiếm sách theo từ khóa, lọc theo thể loại (search, getByCategory).
 * 3. Thêm mới, chỉnh sửa và xóa sách (insert, update, delete).
 * 4. Tự động cập nhật số lượng tồn kho khả dụng khi mượn/trả sách (adjustStock).
 * 5. Tự động sinh danh sách bản sao sách (BanSaoSach) kèm mã vạch (barcode).
 * ============================================================================
 */
public class BookDB {

    /**
     * Tự động sinh các bản sao vật lý của cuốn sách (kèm mã vạch và trạng thái sẵn sàng)
     */
    public static void generateCopiesForBook(Sach s) {
        if (s == null) return;
        if (s.getIsbn() == null || s.getIsbn().isEmpty()) {
            s.setIsbn("978-604-" + (1000 + s.getMaSach()) + "-" + (s.getMaSach() % 10));
        }
        List<BanSaoSach> copies = new ArrayList<BanSaoSach>();
        int total = s.getSoLuongTong() > 0 ? s.getSoLuongTong() : 1;
        int available = s.getSoLuongCon();
        for (int i = 1; i <= total; i++) {
            String barcode = String.format("BC-%04d-%02d", s.getMaSach(), i);
            boolean ready = (i <= available);
            copies.add(new BanSaoSach(barcode, s.getMaSach(), TinhTrangSach.TOT, ready));
        }
        s.setDanhSachBanSao(copies);
    }

    private static Sach mapRow(ResultSet rs) throws SQLException {
        Sach s = new Sach();
        s.setMaSach(rs.getInt("ma_sach"));
        try {
            String isbn = rs.getString("isbn");
            if (isbn != null && !isbn.trim().isEmpty()) {
                s.setIsbn(isbn);
            }
        } catch (Exception ignored) {}
        s.setTieuDe(rs.getString("tieu_de"));
        s.setTacGia(rs.getString("tac_gia"));
        s.setMaTheLoai(rs.getInt("ma_the_loai"));
        String catName = null;
        try {
            catName = rs.getString("ten_the_loai");
        } catch (Exception ignored) {}
        if (catName != null && !catName.trim().isEmpty()) {
            s.setTenTheLoai(catName);
        } else if (s.getMaTheLoai() > 0) {
            for (TheLoai tl : CategoryDB.getAll()) {
                if (tl.getMaTheLoai() == s.getMaTheLoai()) {
                    s.setTenTheLoai(tl.getTenTheLoai());
                    s.setTheLoai(tl);
                    break;
                }
            }
        }
        s.setMaNxb(rs.getInt("ma_nxb"));
        s.setTenNxb(rs.getString("ten_nxb"));
        s.setNamXuatBan(rs.getInt("nam_xuat_ban"));
        s.setGiaBia(rs.getDouble("gia_bia"));
        s.setSoLuongTong(rs.getInt("so_luong_tong"));
        s.setSoLuongCon(rs.getInt("so_luong_con"));
        s.setViTriKe(rs.getString("vi_tri_ke"));
        s.setTinhTrang(rs.getString("tinh_trang"));
        s.setMoTa(rs.getString("mo_ta"));
        try {
            String img = rs.getString("hinh_anh");
            if (img != null && !img.trim().isEmpty()) {
                s.setHinhAnh(img);
            }
        } catch (Exception ignored) {}
        generateCopiesForBook(s);
        return s;
    }

    private static final String BASE_SELECT =
        "SELECT s.*, t.ten_the_loai, n.ten_nxb " +
        "FROM sach s " +
        "LEFT JOIN the_loai t ON s.ma_the_loai = t.ma_the_loai " +
        "LEFT JOIN nha_xuat_ban n ON s.ma_nxb = n.ma_nxb ";

    public static List<Sach> getAll() {
        List<Sach> list = new ArrayList<Sach>();
        String sql = BASE_SELECT + "ORDER BY s.ma_sach ASC";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = DBUtil.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (Exception e) {
            return new ArrayList<Sach>(getFallbackBooks());
        } finally {
            DBUtil.close(rs);
            DBUtil.close(ps);
            DBUtil.close(con);
        }

        if (list.isEmpty()) {
            return new ArrayList<Sach>(getFallbackBooks());
        }

        // Merge any additional books (#28..#48 or newly added) from fallbackCache
        for (Sach fb : getFallbackBooks()) {
            boolean exists = false;
            for (Sach dbItem : list) {
                if (dbItem.getMaSach() == fb.getMaSach()) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                list.add(fb);
            }
        }
        return list;
    }

    public static List<Sach> search(String keyword, int categoryId) {
        List<Sach> all = getAll();
        List<Sach> result = new ArrayList<Sach>();
        String kw = (keyword != null) ? keyword.trim().toLowerCase() : "";
        for (Sach s : all) {
            boolean matchKey = kw.isEmpty() ||
                (s.getTieuDe() != null && s.getTieuDe().toLowerCase().contains(kw)) ||
                (s.getTacGia() != null && s.getTacGia().toLowerCase().contains(kw));
            boolean matchCat = (categoryId <= 0) || (s.getMaTheLoai() == categoryId);
            if (matchKey && matchCat) {
                result.add(s);
            }
        }
        return result;
    }

    public static Sach getById(int id) {
        String sql = BASE_SELECT + "WHERE s.ma_sach = ?";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = DBUtil.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (Exception ignored) {
        } finally {
            DBUtil.close(rs);
            DBUtil.close(ps);
            DBUtil.close(con);
        }

        for (Sach s : getFallbackBooks()) {
            if (s.getMaSach() == id) {
                return s;
            }
        }
        return null;
    }

    public static boolean insert(Sach s) {
        String sql = "INSERT INTO sach (tieu_de, tac_gia, ma_the_loai, ma_nxb, nam_xuat_ban, gia_bia, so_luong_tong, so_luong_con, vi_tri_ke, tinh_trang, mo_ta, hinh_anh) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int generatedId = 0;
        try {
            con = DBUtil.getConnection();
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, s.getTieuDe());
            ps.setString(2, s.getTacGia());
            ps.setInt(3, s.getMaTheLoai());
            ps.setInt(4, s.getMaNxb() > 0 ? s.getMaNxb() : 1);
            ps.setInt(5, s.getNamXuatBan());
            ps.setDouble(6, s.getGiaBia());
            ps.setInt(7, s.getSoLuongTong());
            ps.setInt(8, s.getSoLuongTong());
            ps.setString(9, s.getViTriKe());
            ps.setString(10, s.getTinhTrang());
            ps.setString(11, s.getMoTa());
            ps.setString(12, s.getHinhAnh());
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                generatedId = rs.getInt(1);
            }
            if (generatedId > 0) {
                try {
                    PreparedStatement psBs = con.prepareStatement("INSERT INTO ban_sao_sach (ma_vach, ma_sach, tinh_trang, san_sang) VALUES (?, ?, 'TOT', TRUE)");
                    for (int i = 1; i <= s.getSoLuongTong(); i++) {
                        psBs.setString(1, String.format("BC-%04d-%02d", generatedId, i));
                        psBs.setInt(2, generatedId);
                        psBs.addBatch();
                    }
                    psBs.executeBatch();
                    psBs.close();
                } catch (SQLException bsEx) {
                    System.err.println("Notice: BookDB.insert ban_sao_sach: " + bsEx.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi BookDB.insert (MySQL): " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(rs);
            DBUtil.close(ps);
            DBUtil.close(con);
        }
        List<Sach> cache = getFallbackBooks();
        int maxId = 27;
        for (Sach item : cache) {
            if (item.getMaSach() > maxId) maxId = item.getMaSach();
        }
        if (generatedId > 0) {
            s.setMaSach(generatedId);
        } else if (s.getMaSach() <= 0) {
            s.setMaSach(maxId + 1);
        }
        if (s.getTenTheLoai() == null || s.getTenTheLoai().isEmpty()) {
            for (TheLoai tl : CategoryDB.getAll()) {
                if (tl.getMaTheLoai() == s.getMaTheLoai()) {
                    s.setTenTheLoai(tl.getTenTheLoai());
                    break;
                }
            }
        }
        cache.add(0, s);
        return true;
    }

    public static boolean update(Sach s) {
        String sql = "UPDATE sach SET tieu_de=?, tac_gia=?, ma_the_loai=?, nam_xuat_ban=?, gia_bia=?, so_luong_tong=?, so_luong_con=?, vi_tri_ke=?, tinh_trang=?, mo_ta=?, hinh_anh=? WHERE ma_sach=?";
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtil.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, s.getTieuDe());
            ps.setString(2, s.getTacGia());
            ps.setInt(3, s.getMaTheLoai());
            ps.setInt(4, s.getNamXuatBan());
            ps.setDouble(5, s.getGiaBia());
            ps.setInt(6, s.getSoLuongTong());
            ps.setInt(7, s.getSoLuongCon());
            ps.setString(8, s.getViTriKe());
            ps.setString(9, s.getTinhTrang());
            ps.setString(10, s.getMoTa());
            ps.setString(11, s.getHinhAnh());
            ps.setInt(12, s.getMaSach());
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Lỗi BookDB.update (MySQL): " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(ps);
            DBUtil.close(con);
        }
        for (Sach item : getFallbackBooks()) {
            if (item.getMaSach() == s.getMaSach()) {
                item.setTieuDe(s.getTieuDe());
                item.setTacGia(s.getTacGia());
                item.setMaTheLoai(s.getMaTheLoai());
                item.setTenTheLoai(s.getTenTheLoai());
                item.setTheLoai(s.getTheLoai());
                item.setNamXuatBan(s.getNamXuatBan());
                item.setGiaBia(s.getGiaBia());
                item.setSoLuongTong(s.getSoLuongTong());
                item.setSoLuongCon(s.getSoLuongCon());
                item.setViTriKe(s.getViTriKe());
                item.setTinhTrang(s.getTinhTrang());
                item.setMoTa(s.getMoTa());
                if (s.getHinhAnh() != null && !s.getHinhAnh().isEmpty()) {
                    item.setHinhAnh(s.getHinhAnh());
                }
                break;
            }
        }
        return true;
    }

    public static boolean delete(int id) {
        String sql = "DELETE FROM sach WHERE ma_sach = ?";
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtil.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Lỗi BookDB.delete (MySQL): " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(ps);
            DBUtil.close(con);
        }
        List<Sach> cache = getFallbackBooks();
        for (int i = 0; i < cache.size(); i++) {
            if (cache.get(i).getMaSach() == id) {
                cache.remove(i);
                break;
            }
        }
        return true;
    }

    /**
     * PHƯƠNG THỨC ĐIỀU CHỈNH TỒN KHO TỰ ĐỘNG: adjustStock()
     * ----------------------------------------------------------------------------
     * VAI TRÒ NGHIỆP VỤ:
     * - Khi độc giả mượn sách: delta mang giá trị ÂM (ví dụ -1, -2) -> Giảm số lượng còn lại.
     * - Khi độc giả trả sách: delta mang giá trị DƯƠNG (ví dụ +1, +2) -> Cộng lại vào kho.
     * 
     * KỸ THUẬT PHÒNG THỦ DỮ LIỆU ĐỈNH CAO (DEFENSIVE SQL):
     * Câu lệnh SQL:
     *   UPDATE sach SET so_luong_con = GREATEST(0, LEAST(so_luong_tong, so_luong_con + ?)) WHERE ma_sach = ?
     * Giải thích hàm toán học trong MySQL:
     *   1. LEAST(so_luong_tong, so_luong_con + ?): Ngăn chặn việc cộng dồn vượt quá tổng số sách nhập vào kho.
     *   2. GREATEST(0, ...): Ngăn chặn tuyệt đối việc số lượng bị ÂM (Negative Stock) nếu có nhiều luồng mượn cùng lúc.
     * Kỹ thuật này giúp hệ thống chống được lỗi Race Condition ngay ở tầng CSDL.
     */
    public static synchronized void adjustStock(int maSach, int delta) {
        for (Sach s : getFallbackBooks()) {
            if (s.getMaSach() == maSach) {
                int newCon = s.getSoLuongCon() + delta;
                if (newCon < 0) newCon = 0;
                if (newCon > s.getSoLuongTong()) newCon = s.getSoLuongTong();
                s.setSoLuongCon(newCon);
                generateCopiesForBook(s);
                break;
            }
        }
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtil.getConnection();
            ps = con.prepareStatement("UPDATE sach SET so_luong_con = GREATEST(0, LEAST(so_luong_tong, so_luong_con + ?)) WHERE ma_sach = ?");
            ps.setInt(1, delta);
            ps.setInt(2, maSach);
            ps.executeUpdate();
        } catch (Exception ignored) {
        } finally {
            DBUtil.close(ps);
            DBUtil.close(con);
        }
    }

    private static Sach createBook(int id, String title, String author, int catId, String catName, String nxb, int year, double price, int total, int available, String shelf, String desc, String img) {
        Sach s = new Sach();
        s.setMaSach(id);
        s.setIsbn("978-604-" + (1000 + id) + "-" + (id % 10));
        s.setTieuDe(title);
        s.setTacGia(author);
        s.setMaTheLoai(catId);
        s.setTenTheLoai(catName);
        s.setTheLoai(new TheLoai(catId, catName, ""));
        s.setTenNxb(nxb);
        s.setNamXuatBan(year);
        s.setGiaBia(price);
        s.setSoLuongTong(total);
        s.setSoLuongCon(available);
        s.setViTriKe(shelf);
        s.setMoTa(desc);
        s.setHinhAnh(img);
        generateCopiesForBook(s);
        return s;
    }

    private static List<Sach> fallbackCache = null;
    public static List<Sach> getFallbackBooks() {
        if (fallbackCache != null) return fallbackCache;
        List<Sach> list = new ArrayList<Sach>();

        // 1. CNTT
        list.add(createBook(1, "Murachs Java Servlets and JSP", "Joel Murach", 1, "Công Nghệ Thông Tin", "NXB OReilly Media", 2014, 180000, 6, 5, "Kệ A1-01", "Giáo trình chuẩn về lập trình Web với Java Servlet và JSP theo mô hình MVC 3 tầng chuyên sâu.", "images/books/book_1.jpg"));
        list.add(createBook(2, "Clean Code: A Handbook of Agile Software Craftsmanship", "Robert C. Martin", 1, "Công Nghệ Thông Tin", "NXB Prentice Hall", 2008, 220000, 5, 4, "Kệ A1-02", "Nghệ thuật viết mã nguồn sạch, dễ đọc, dễ bảo trì và mở rộng cho lập trình viên chuyên nghiệp.", "images/books/book_2.jpg"));
        list.add(createBook(3, "Design Patterns: Elements of Reusable Software", "Erich Gamma et al.", 1, "Công Nghệ Thông Tin", "NXB Addison-Wesley", 2016, 250000, 4, 3, "Kệ A1-03", "23 mẫu thiết kế hướng đối tượng kinh điển giúp giải quyết các bài toán kiến trúc phần mềm phức tạp.", "images/books/book_3.jpg"));
        list.add(createBook(4, "Introduction to Algorithms (CLRS)", "Thomas H. Cormen et al.", 1, "Công Nghệ Thông Tin", "NXB MIT Press", 2022, 350000, 4, 2, "Kệ A1-04", "Bách khoa toàn thư về cấu trúc dữ liệu và giải thuật từ cơ bản đến nâng cao toàn diện nhất thế giới.", "images/books/book_4.jpg"));

        // 2. AI & Data Science
        list.add(createBook(5, "Artificial Intelligence: A Modern Approach", "Stuart Russell & Peter Norvig", 2, "AI & Khoa Học Dữ Liệu", "NXB Pearson", 2020, 390000, 5, 4, "Kệ A2-01", "Giáo trình nhập môn và chuyên sâu số 1 thế giới về trí tuệ nhân tạo, tác tử thông minh và học máy.", "images/books/book_5.jpg"));
        list.add(createBook(6, "Deep Learning", "Ian Goodfellow et al.", 2, "AI & Khoa Học Dữ Liệu", "NXB MIT Press", 2016, 320000, 4, 3, "Kệ A2-02", "Tài liệu nền tảng toàn diện về mạng nơ-ron nhân tạo, tích chập, hồi quy và mô hình sinh sâu sắc.", "images/books/book_6.jpg"));
        list.add(createBook(7, "Hands-On Machine Learning with Scikit-Learn", "Aurélien Géron", 2, "AI & Khoa Học Dữ Liệu", "NXB OReilly Media", 2022, 280000, 5, 5, "Kệ A2-03", "Hướng dẫn thực hành xây dựng các hệ thống học máy thông minh từ ý tưởng tới môi trường production.", "images/books/book_7.jpg"));

        // 3. Khoa học tự nhiên
        list.add(createBook(8, "A Brief History of Time (Lược Sử Thời Gian)", "Stephen Hawking", 3, "Khoa Học Tự Nhiên", "NXB Bantam Books", 1998, 125000, 8, 7, "Kệ B1-01", "Khám phá các bí ẩn của vũ trụ, lỗ đen, thuyết tương đối và nguồn gốc không-thời gian kỳ vĩ.", "images/books/book_8.jpg"));
        list.add(createBook(9, "Cosmos (Vũ Trụ)", "Carl Sagan", 3, "Khoa Học Tự Nhiên", "NXB Random House", 2013, 165000, 6, 5, "Kệ B1-02", "Hành trình tráng lệ khám phá 15 tỷ năm tiến hóa của vũ trụ và tương lai của loài người.", "images/books/book_9.jpg"));
        list.add(createBook(10, "The Feynman Lectures on Physics", "Richard P. Feynman", 3, "Khoa Học Tự Nhiên", "NXB Basic Books", 2011, 450000, 3, 2, "Kệ B1-03", "Bộ bài giảng vật lý huyền thoại của nhà vật lý đoạt giải Nobel Richard Feynman dành cho mọi thế hệ.", "images/books/book_10.jpg"));

        // 4. Kinh tế - Quản trị
        list.add(createBook(11, "The Intelligent Investor (Nhà Đầu Tư Thông Minh)", "Benjamin Graham", 4, "Kinh Tế - Quản Trị", "NXB Harper Business", 2006, 195000, 7, 6, "Kệ B2-01", "Kinh thánh về đầu tư giá trị, nguyên tắc an toàn vốn và tư duy thị trường tài chính vững vàng.", "images/books/book_11.jpg"));
        list.add(createBook(12, "Principles: Life and Work (Những Nguyên Tắc)", "Ray Dalio", 4, "Kinh Tế - Quản Trị", "NXB Simon & Schuster", 2017, 280000, 5, 4, "Kệ B2-02", "Những nguyên tắc sống và làm việc thực tế tạo nên thành công của quỹ đầu tư Bridgewater Associates.", "images/books/book_12.jpg"));
        list.add(createBook(13, "Zero to One: Notes on Startups", "Peter Thiel & Blake Masters", 4, "Kinh Tế - Quản Trị", "NXB Crown Business", 2014, 115000, 9, 8, "Kệ B2-03", "Tư duy đột phá về xây dựng tương lai, tạo ra giá trị độc quyền và khởi nghiệp công nghệ.", "images/books/book_13.jpg"));
        list.add(createBook(14, "Good to Great (Từ Tốt Đến Vĩ Đại)", "Jim Collins", 4, "Kinh Tế - Quản Trị", "NXB Harper Business", 2001, 145000, 6, 5, "Kệ B2-04", "Nghiên cứu khoa học giải mã cách các công ty bình thường bứt phá trở thành vĩ đại bền vững.", "images/books/book_14.jpg"));

        // 5. Kỹ năng - Tâm lý
        list.add(createBook(15, "Đắc Nhân Tâm (How to Win Friends)", "Dale Carnegie", 5, "Kỹ Năng - Tâm Lý", "NXB Trí Việt", 2021, 88000, 12, 10, "Kệ C1-01", "Nghệ thuật giao tiếp, thấu hiểu tâm lý và tạo dựng mối quan hệ chân thành, tốt đẹp trọn đời.", "images/books/book_15.jpg"));
        list.add(createBook(16, "Atomic Habits (Thay Đổi Tí Hon)", "James Clear", 5, "Kỹ Năng - Tâm Lý", "NXB Avery", 2018, 139000, 10, 8, "Kệ C1-02", "Phương pháp khoa học xây dựng thói quen tốt và loại bỏ thói quen xấu từng bước nhỏ mỗi ngày.", "images/books/book_16.jpg"));
        list.add(createBook(17, "Thinking, Fast and Slow (Tư Duy Nhanh Và Chậm)", "Daniel Kahneman", 5, "Kỹ Năng - Tâm Lý", "NXB FSG", 2011, 210000, 6, 4, "Kệ C1-03", "Kiệt tác tâm lý học nhận thức giải mã hai hệ thống vận hành tư duy chi phối mọi quyết định.", "images/books/book_17.jpg"));
        list.add(createBook(18, "Deep Work (Làm Ra Làm, Chơi Ra Chơi)", "Cal Newport", 5, "Kỹ Năng - Tâm Lý", "NXB Grand Central", 2016, 128000, 7, 5, "Kệ C1-04", "Rèn luyện khả năng tập trung cao độ để làm chủ các kỹ năng khó và tạo ra thành quả đột phá.", "images/books/book_18.jpg"));

        // 6. Văn học kinh điển
        list.add(createBook(19, "Nhà Giả Kim (The Alchemist)", "Paulo Coelho", 6, "Văn Học Kinh Điển", "NXB Nhã Nam", 2020, 79000, 15, 12, "Kệ C2-01", "Câu chuyện ngụ ngôn tuyệt đẹp về hành trình theo đuổi vận mệnh và lắng nghe tiếng gọi trái tim.", "images/books/book_19.jpg"));
        list.add(createBook(20, "Hoàng Tử Bé (Le Petit Prince)", "Antoine de Saint-Exupéry", 6, "Văn Học Kinh Điển", "NXB Kim Đồng", 2019, 65000, 10, 9, "Kệ C2-02", "Tác phẩm văn học bất hủ nhắc nhở người lớn về tình bạn, tình yêu và những điều cốt lõi vô hình.", "images/books/book_20.jpg"));
        list.add(createBook(21, "Trăm Năm Cô Đơn", "Gabriel García Márquez", 6, "Văn Học Kinh Điển", "NXB Văn Học", 2018, 175000, 5, 3, "Kệ C2-03", "Đỉnh cao của chủ nghĩa hiện thực huyền ảo về dòng họ Buendía và thị trấn Macondo huyền thoại.", "images/books/book_21.jpg"));
        list.add(createBook(22, "1984 (Một Chín Tám Tư)", "George Orwell", 6, "Văn Học Kinh Điển", "NXB Hội Nhà Văn", 2020, 95000, 8, 6, "Kệ C2-04", "Tiểu thuyết phản địa đàng kinh điển cảnh báo về tự do tư tưởng, sự thật và quyền lực xã hội.", "images/books/book_22.jpg"));

        // 7. Lịch sử - Xã hội
        list.add(createBook(23, "Sapiens: Lược Sử Loài Người", "Yuval Noah Harari", 7, "Lịch Sử - Xã Hội", "NXB Tri Thức", 2017, 230000, 8, 7, "Kệ D1-01", "Cái nhìn bao quát đầy kinh ngạc về lịch sử tiến hóa của loài người từ vượn cổ đến thời hiện đại.", "images/books/book_23.jpg"));
        list.add(createBook(24, "Súng, Vi Trùng và Thép", "Jared Diamond", 7, "Lịch Sử - Xã Hội", "NXB Thế Giới", 2019, 210000, 5, 4, "Kệ D1-02", "Tác phẩm đoạt giải Pulitzer lý giải số phận và sự chênh lệch phát triển giữa các xã hội loài người.", "images/books/book_24.jpg"));
        list.add(createBook(25, "Lịch Sử Thế Giới", "H. G. Wells", 7, "Lịch Sử - Xã Hội", "NXB Văn Hóa", 2016, 260000, 4, 3, "Kệ D1-03", "Bức tranh toàn cảnh về những bước ngoặt vĩ đại định hình nền văn minh nhân loại qua các thời kỳ.", "images/books/book_25.jpg"));

        // 8. Ngoại ngữ
        list.add(createBook(26, "English Grammar in Use", "Raymond Murphy", 8, "Ngoại Ngữ", "Cambridge University Press", 2019, 160000, 12, 10, "Kệ D2-01", "Cuốn sách ngữ pháp tiếng Anh tự học kinh điển và được tin dùng nhiều nhất trên toàn cầu.", "images/books/real_cover_26.jpg"));
        list.add(createBook(27, "Oxford Advanced Learner Dictionary", "Oxford University Press", 8, "Ngoại Ngữ", "Oxford University Press", 2020, 420000, 5, 4, "Kệ D2-02", "Từ điển Anh - Anh chuẩn mực dành cho người học ngôn ngữ chuyên sâu và nâng cao trình độ.", "images/books/real_cover_27.jpg"));

        // Bổ sung thêm 21 tác phẩm kinh điển thế giới (#28 -> #48)
        list.add(createBook(28, "The Pragmatic Programmer (Lập Trình Viên Thực Dụng)", "Andrew Hunt & David Thomas", 1, "Công Nghệ Thông Tin", "Addison-Wesley", 2019, 260000, 7, 6, "Kệ A1-05", "Hành trình từ thợ code trở thành bậc thầy phát triển phần mềm chuyên nghiệp.", "images/books/real_cover_28.jpg"));
        list.add(createBook(29, "Refactoring: Improving the Design of Existing Code", "Martin Fowler", 1, "Công Nghệ Thông Tin", "Addison-Wesley", 2018, 290000, 6, 5, "Kệ A1-06", "Kỹ thuật tái cấu trúc mã nguồn chuẩn mực giúp phần mềm sạch và dễ mở rộng.", "images/books/real_cover_29.jpg"));
        list.add(createBook(30, "Head First Design Patterns", "Eric Freeman & Elisabeth Robson", 1, "Công Nghệ Thông Tin", "OReilly Media", 2020, 240000, 8, 7, "Kệ A1-07", "Học các mẫu thiết kế hướng đối tượng qua hình ảnh trực quan, sinh động và thực tế.", "images/books/real_cover_30.jpg"));

        list.add(createBook(31, "Python for Data Analysis (Phân Tích Dữ Liệu Với Python)", "Wes McKinney", 2, "AI & Khoa Học Dữ Liệu", "OReilly Media", 2022, 275000, 9, 8, "Kệ A2-04", "Cẩm nang xử lý và phân tích dữ liệu thực chiến với Pandas, NumPy và IPython.", "images/books/real_cover_31.jpg"));
        list.add(createBook(32, "Pattern Recognition and Machine Learning", "Christopher M. Bishop", 2, "AI & Khoa Học Dữ Liệu", "Springer", 2016, 360000, 5, 4, "Kệ A2-05", "Tác phẩm kinh điển về nhận dạng mẫu thống kê và các thuật toán học máy hiện đại.", "images/books/real_cover_32.jpg"));
        list.add(createBook(33, "Superintelligence (Siêu Trí Tuệ)", "Nick Bostrom", 2, "AI & Khoa Học Dữ Liệu", "Oxford Press", 2016, 210000, 6, 5, "Kệ A2-06", "Phân tích chiến lược về tương lai khi trí tuệ nhân tạo vượt qua trí thông minh loài người.", "images/books/real_cover_33.jpg"));

        list.add(createBook(34, "The Selfish Gene (Gen Vị Kỷ)", "Richard Dawkins", 3, "Khoa Học Tự Nhiên", "Oxford Press", 2016, 185000, 7, 6, "Kệ B1-04", "Góc nhìn đột phá về tiến hóa sinh học lấy gen làm trung tâm của sự sống.", "images/books/real_cover_34.jpg"));
        list.add(createBook(35, "Astrophysics for People in a Hurry", "Neil deGrasse Tyson", 3, "Khoa Học Tự Nhiên", "W. W. Norton", 2017, 155000, 10, 9, "Kệ B1-05", "Vật lý thiên văn học súc tích, hấp dẫn dành cho những người bận rộn yêu thích vũ trụ.", "images/books/real_cover_35.jpg"));
        list.add(createBook(36, "The Origin of Species (Nguồn Gốc Các Loài)", "Charles Darwin", 3, "Khoa Học Tự Nhiên", "Signet Classics", 2015, 190000, 6, 5, "Kệ B1-06", "Công trình khoa học vĩ đại đặt nền móng cho thuyết tiến hóa và sinh học hiện đại.", "images/books/real_cover_36.jpg"));

        list.add(createBook(37, "Rich Dad Poor Dad (Dạy Con Làm Giàu)", "Robert T. Kiyosaki", 4, "Kinh Tế - Quản Trị", "Plata Publishing", 2017, 110000, 14, 11, "Kệ B2-05", "Thay đổi tư duy tài chính cá nhân, đầu tư tài sản và làm chủ dòng tiền.", "images/books/real_cover_37.jpg"));
        list.add(createBook(38, "The Psychology of Money (Tâm Lý Học Về Tiền)", "Morgan Housel", 4, "Kinh Tế - Quản Trị", "Harriman House", 2020, 148000, 12, 10, "Kệ B2-06", "19 câu chuyện sắc sảo về cách con người suy nghĩ và ứng xử với tiền bạc, tài sản.", "images/books/real_cover_38.jpg"));
        list.add(createBook(39, "The Lean Startup (Khởi Nghiệp Tinh Gọn)", "Eric Ries", 4, "Kinh Tế - Quản Trị", "Crown Business", 2015, 165000, 9, 8, "Kệ B2-07", "Phương pháp xây dựng sản phẩm đổi mới sáng tạo liên tục và tối ưu hóa nguồn lực.", "images/books/real_cover_39.jpg"));

        list.add(createBook(40, "The 7 Habits of Highly Effective People (7 Thói Quen Hiệu Quả)", "Stephen R. Covey", 5, "Kỹ Năng - Tâm Lý", "Simon & Schuster", 2020, 175000, 11, 9, "Kệ C1-05", "7 nguyên tắc vàng giúp phát triển bản thân và lãnh đạo đội nhóm thành công bền vững.", "images/books/real_cover_40.jpg"));
        list.add(createBook(41, "Man's Search for Meaning (Đi Tìm Lẽ Sống)", "Viktor E. Frankl", 5, "Kỹ Năng - Tâm Lý", "Beacon Press", 2018, 98000, 10, 8, "Kệ C1-06", "Cuốn sách truyền cảm hứng mãnh liệt về ý nghĩa cuộc sống và nghị lực vượt qua nghịch cảnh.", "images/books/real_cover_41.jpg"));
        list.add(createBook(42, "The Subtle Art of Not Giving a F*ck", "Mark Manson", 5, "Kỹ Năng - Tâm Lý", "HarperOne", 2019, 135000, 9, 7, "Kệ C1-07", "Nghệ thuật tập trung vào những giá trị thực sự quan trọng để sống hạnh phúc và tự do.", "images/books/real_cover_42.jpg"));

        list.add(createBook(43, "To Kill a Mockingbird (Giết Con Chim Nhại)", "Harper Lee", 6, "Văn Học Kinh Điển", "Harper Perennial", 2015, 125000, 8, 7, "Kệ C2-05", "Tiểu thuyết đoạt giải Pulitzer đầy nhân văn về công lý, lòng trắc ẩn và tuổi thơ.", "images/books/real_cover_43.jpg"));
        list.add(createBook(44, "The Great Gatsby (Đại Gia Gatsby)", "F. Scott Fitzgerald", 6, "Văn Học Kinh Điển", "Scribner", 2018, 105000, 9, 8, "Kệ C2-06", "Kiệt tác văn học Mỹ khắc họa giấc mơ phù hoa và bi kịch tình yêu thập niên 1920.", "images/books/real_cover_44.jpg"));
        list.add(createBook(45, "Crime and Punishment (Tội Ác Và Hình Phạt)", "Fyodor Dostoevsky", 6, "Văn Học Kinh Điển", "Penguin Classics", 2017, 195000, 6, 5, "Kệ C2-07", "Đỉnh cao tiểu thuyết tâm lý triết học thế giới về lương tri và sự cứu rỗi tâm hồn.", "images/books/real_cover_45.jpg"));

        list.add(createBook(46, "Homo Deus: Lược Sử Tương Lai", "Yuval Noah Harari", 7, "Lịch Sử - Xã Hội", "NXB Tri Thức", 2018, 240000, 8, 7, "Kệ D1-04", "Dự phóng táo bạo về tương lai nhân loại trong kỷ nguyên công nghệ sinh học và AI.", "images/books/real_cover_46.jpg"));
        list.add(createBook(47, "21 Lessons for the 21st Century (21 Bài Học Thế Kỷ 21)", "Yuval Noah Harari", 7, "Lịch Sử - Xã Hội", "Spiegel & Grau", 2019, 220000, 7, 6, "Kệ D1-05", "Những vấn đề cấp bách nhất của thế giới hiện đại từ công việc, giáo dục đến dữ liệu.", "images/books/real_cover_47.jpg"));
        list.add(createBook(48, "Word Power Made Easy", "Norman Lewis", 8, "Ngoại Ngữ", "Anchor Books", 2016, 150000, 10, 9, "Kệ D2-03", "Phương pháp mở rộng vốn từ vựng tiếng Anh thông minh và hiệu quả nhất thế giới.", "images/books/real_cover_48.jpg"));

        fallbackCache = list;
        return list;
    }
}
