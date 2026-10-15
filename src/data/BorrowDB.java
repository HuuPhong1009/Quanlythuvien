package data;

import model.BanSaoSach;
import model.ChiTietGioMuon;
import model.ChiTietPhieuMuon;
import model.GioMuon;
import model.PhieuMuon;
import model.PhieuPhat;
import model.QuyDinhMuon;
import model.ThuThu;
import model.TinhTrangSach;
import model.TrangThaiPhat;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * ============================================================================
 * LỚP BorrowDB (Data Access Object - DAO cho Mượn / Trả Sách)
 * ----------------------------------------------------------------------------
 * Chức năng chính:
 * 1. Lưu trữ và xử lý toàn bộ nghiệp vụ Mượn - Trả sách của Thư viện.
 * 2. Hỗ trợ 2 tầng lưu trữ:
 *    - Tầng 1 (Chính): CSDL MySQL (bảng phieu_muon, chi_tiet_phieu_muon, phieu_phat).
 *    - Tầng 2 (Dự phòng): In-memory cache (fallbackCache) giúp webapp luôn chạy mượt mà.
 * 3. Tự động kiểm tra thời hạn và phát sinh Phiếu phạt (PhieuPhat) khi trả sách trễ hạn.
 * 4. Hỗ trợ các chế độ thử nghiệm quá hạn nhanh (1 phút, 2 phút, tùy chỉnh ngày giờ).
 * ============================================================================
 */
public class BorrowDB {

    // Danh sách phiếu mượn lưu tạm trong bộ nhớ RAM (dự phòng khi CSDL chưa sẵn sàng)
    private static List<PhieuMuon> fallbackCache = null;

    /**
     * Khởi tạo và trả về danh sách phiếu mượn mẫu trong bộ nhớ RAM
     */
    public static synchronized List<PhieuMuon> getFallbackBorrows() {
        if (fallbackCache != null) {
            return fallbackCache;
        }
        fallbackCache = new ArrayList<PhieuMuon>();

        // Phieu mau 1: Dang muon
        PhieuMuon pm1 = new PhieuMuon();
        pm1.setMaPhieu(1);
        pm1.setMaDocGia(2);
        pm1.setTenDocGia("Nguyễn Văn An");
        Calendar c1 = Calendar.getInstance();
        c1.add(Calendar.DAY_OF_YEAR, -4);
        pm1.setNgayMuon(c1.getTime());
        Calendar h1 = Calendar.getInstance();
        h1.add(Calendar.DAY_OF_YEAR, 10);
        pm1.setHanTra(h1.getTime());
        pm1.setTrangThai("DANG_MUON");
        pm1.setGhiChu("Mượn tài liệu học tập môn Lập trình Web");

        List<ChiTietPhieuMuon> cts1 = new ArrayList<ChiTietPhieuMuon>();
        ChiTietPhieuMuon ct1 = new ChiTietPhieuMuon();
        ct1.setMaCt(1);
        ct1.setMaPhieu(1);
        ct1.setMaSach(1);
        ct1.setTieuDeSach("Murachs Java Servlets and JSP");
        ct1.setSoLuong(1);
        ct1.setTinhTrangMuon("Tốt");
        ct1.setTinhTrangKhiMuon(TinhTrangSach.TOT);
        ct1.setBanSaoSach(new BanSaoSach("BC-0001-01", 1, TinhTrangSach.TOT, false));
        cts1.add(ct1);
        pm1.setChiTietList(cts1);

        // Phieu mau 2: Cho duyet
        PhieuMuon pm2 = new PhieuMuon();
        pm2.setMaPhieu(2);
        pm2.setMaDocGia(2);
        pm2.setTenDocGia("Nguyễn Văn An");
        Calendar c2 = Calendar.getInstance();
        pm2.setNgayMuon(c2.getTime());
        Calendar h2 = Calendar.getInstance();
        h2.add(Calendar.DAY_OF_YEAR, 14);
        pm2.setHanTra(h2.getTime());
        pm2.setTrangThai("CHO_DUYET");
        pm2.setGhiChu("Em xin đăng ký mượn sách Clean Code để làm đồ án ạ");

        List<ChiTietPhieuMuon> cts2 = new ArrayList<ChiTietPhieuMuon>();
        ChiTietPhieuMuon ct2 = new ChiTietPhieuMuon();
        ct2.setMaCt(2);
        ct2.setMaPhieu(2);
        ct2.setMaSach(2);
        ct2.setTieuDeSach("Clean Code: A Handbook of Agile Software Craftsmanship");
        ct2.setSoLuong(1);
        ct2.setTinhTrangMuon("Tốt");
        ct2.setTinhTrangKhiMuon(TinhTrangSach.TOT);
        ct2.setBanSaoSach(new BanSaoSach("BC-0002-01", 2, TinhTrangSach.TOT, false));
        cts2.add(ct2);
        pm2.setChiTietList(cts2);

        fallbackCache.add(pm2);
        fallbackCache.add(pm1);
        return fallbackCache;
    }

    /**
     * Tạo phiếu mượn mới (mặc định 14 ngày)
     */
    public static synchronized boolean createBorrow(int readerId, GioMuon cart, String ghiChu) {
        return createBorrow(readerId, "Nguyễn Văn An", cart, ghiChu, "14_DAYS", null);
    }

    /**
     * Tạo phiếu mượn mới kèm tên độc giả (mặc định 14 ngày)
     */
    public static synchronized boolean createBorrow(int readerId, String readerName, GioMuon cart, String ghiChu) {
        return createBorrow(readerId, readerName, cart, ghiChu, "14_DAYS", null);
    }

    /**
     * PHƯƠNG THỨC TẠO PHIẾU MƯỢN ĐẦY ĐỦ: createBorrow()
     * ----------------------------------------------------------------------------
     * ĐÂY LÀ PHƯƠNG THỨC QUAN TRỌNG NHẤT CỦA NGHIỆP VỤ MƯỢN SÁCH:
     * 1. Hỗ trợ nhiều chế độ thời hạn mượn: 14 ngày tiêu chuẩn, 1 phút / 2 phút / 5 phút test nhanh,
     *    quá hạn tức thì (-1 phút) hoặc tự chọn ngày giờ cụ thể (CUSTOM).
     * 2. KỸ THUẬT GIAO DỊCH DATABASE (ACID TRANSACTION):
     *    - con.setAutoCommit(false): Tắt tự động lưu để gộp nhiều lệnh SQL thành 1 giao dịch nguyên tử.
     *    - Statement.RETURN_GENERATED_KEYS: Lấy ID tự tăng vừa sinh ra của bảng phieu_muon
     *      để dùng làm khóa ngoại (foreign key) cho chi_tiet_phieu_muon.
     *    - BATCH PROCESSING (addBatch & executeBatch): Gom tất cả sách trong giỏ để chèn đồng loạt,
     *      tăng hiệu năng tối đa so với việc thực thi từng câu lệnh đơn lẻ.
     *    - con.commit(): Lưu vĩnh viễn khi mọi câu lệnh thành công 100%.
     *    - con.rollback(): Hoàn tác toàn bộ nếu có bất kỳ lỗi nào xảy ra.
     * 3. Tự động trừ số lượng sách khả dụng trong kho và đồng bộ bộ nhớ cache.
     */
    public static synchronized boolean createBorrow(int readerId, String readerName, GioMuon cart, String ghiChu, String loanDuration, String customDueStr) {
        if (cart == null || cart.getDanhSachSach().isEmpty()) return false;

        if (readerName == null || readerName.trim().isEmpty()) {
            readerName = "Độc giả #" + readerId;
        }

        // BƯỚC 1: XÁC ĐỊNH MỐC THỜI GIAN MƯỢN VÀ HẠN TRẢ
        Calendar calNow = Calendar.getInstance();
        java.util.Date ngayMuon = calNow.getTime();
        Calendar calHan = Calendar.getInstance();

        // Xử lý các chế độ thời hạn mượn (phục vụ cả thực tế và demo nghiệm thu đồ án)
        if ("1_MINUTE".equals(loanDuration)) {
            calHan.add(Calendar.MINUTE, 1); // Hết hạn sau đúng 60 giây (test phiếu phạt siêu tốc)
        } else if ("2_MINUTES".equals(loanDuration)) {
            calHan.add(Calendar.MINUTE, 2);
        } else if ("5_MINUTES".equals(loanDuration)) {
            calHan.add(Calendar.MINUTE, 5);
        } else if ("OVERDUE_NOW".equals(loanDuration)) {
            calHan.add(Calendar.MINUTE, -1); // Quá hạn ngay tức thì lúc vừa mượn
        } else if ("1_DAY".equals(loanDuration)) {
            calHan.add(Calendar.DAY_OF_YEAR, 1);
        } else if ("7_DAYS".equals(loanDuration)) {
            calHan.add(Calendar.DAY_OF_YEAR, 7);
        } else if ("CUSTOM".equals(loanDuration) && customDueStr != null && !customDueStr.trim().isEmpty()) {
            try {
                String cleanStr = customDueStr.trim().replace("T", " ");
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                    cleanStr.length() > 16 ? "yyyy-MM-dd HH:mm:ss" : "yyyy-MM-dd HH:mm"
                );
                calHan.setTime(sdf.parse(cleanStr));
            } catch (Exception ex) {
                System.err.println("Notice: Không parse được customDue: " + customDueStr + " -> dùng mặc định 14 ngày");
                calHan.add(Calendar.DAY_OF_YEAR, 14);
            }
        } else {
            // Mặc định tiêu chuẩn của thư viện: Mượn 14 ngày
            calHan.add(Calendar.DAY_OF_YEAR, 14);
        }
        java.util.Date hanTra = calHan.getTime();

        int generatedMaPhieu = 0;

        // BƯỚC 2: CÁC CÂU LỆNH SQL THAM SỐ HÓA (CHỐNG SQL INJECTION)
        String sqlPhieu = "INSERT INTO phieu_muon (ma_doc_gia, ma_quy_dinh, ngay_muon, han_tra, ghi_chu, trang_thai) VALUES (?, 1, ?, ?, ?, 'CHO_DUYET')";
        String sqlCt = "INSERT INTO chi_tiet_phieu_muon (ma_phieu_muon, ma_sach, ma_vach, so_luong, tinh_trang_khi_muon) VALUES (?, ?, ?, ?, 'TOT')";
        String sqlUpdateBanSao = "UPDATE ban_sao_sach SET san_sang = FALSE WHERE ma_vach = ?";

        Connection con = null;
        PreparedStatement psPhieu = null;
        PreparedStatement psCt = null;
        PreparedStatement psBs = null;
        ResultSet rs = null;

        try {
            con = DBUtil.getConnection();
            // BẬT CHẾ ĐỘ QUẢN LÝ TRANSACTION THỦ CÔNG
            con.setAutoCommit(false);

            // THAO TÁC 1: TẠO BẢN GHI PHIẾU MƯỢN VÀ LẤY MÃ PHIẾU TỰ SINH
            psPhieu = con.prepareStatement(sqlPhieu, Statement.RETURN_GENERATED_KEYS);
            psPhieu.setInt(1, readerId);
            psPhieu.setTimestamp(2, new java.sql.Timestamp(ngayMuon.getTime()));
            psPhieu.setTimestamp(3, new java.sql.Timestamp(hanTra.getTime()));
            psPhieu.setString(4, ghiChu);
            psPhieu.executeUpdate();

            // Đọc mã phiếu tự tăng (Primary Key auto_increment)
            rs = psPhieu.getGeneratedKeys();
            if (rs.next()) {
                generatedMaPhieu = rs.getInt(1);
            }

            // THAO TÁC 2: CHÈN TỪNG CUỐN SÁCH VÀO CHI TIẾT PHIẾU BẰNG BATCH EXECUTION
            if (generatedMaPhieu > 0) {
                psCt = con.prepareStatement(sqlCt);
                psBs = con.prepareStatement(sqlUpdateBanSao);
                for (ChiTietGioMuon item : cart.getDanhSachSach()) {
                    int maSach = item.getSach().getMaSach();
                    String barcode = String.format("BC-%04d-01", maSach);
                    psCt.setInt(1, generatedMaPhieu);
                    psCt.setInt(2, maSach);
                    psCt.setString(3, barcode);
                    psCt.setInt(4, item.getSoLuong());
                    psCt.addBatch(); // Gom lệnh chèn chi tiết

                    psBs.setString(1, barcode);
                    psBs.addBatch(); // Gom lệnh cập nhật trạng thái bản sao sách
                }
                psCt.executeBatch(); // Thực thi đồng loạt chi tiết mượn
                try { psBs.executeBatch(); } catch (SQLException bsEx) {
                    System.err.println("Notice: Update ban_sao_sach batch: " + bsEx.getMessage());
                }
            }

            // XÁC NHẬN TOÀN BỘ GIAO DỊCH THÀNH CÔNG
            con.commit();
        } catch (Exception e) {
            System.err.println("Lỗi BorrowDB.createBorrow (MySQL): " + e.getMessage());
            e.printStackTrace();
            // NẾU CÓ LỖI XẢY RA Ở BẤT CỨ BƯỚC NÀO -> ROLLBACK VỀ NGUYÊN TRẠNG BAN ĐẦU
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) {
                    System.err.println("Lỗi rollback BorrowDB.createBorrow: " + ex.getMessage());
                }
            }
        } finally {
            // ĐÓNG TẤT CẢ TÀI NGUYÊN KẾT NỐI
            DBUtil.close(rs);
            DBUtil.close(psPhieu);
            DBUtil.close(psCt);
            DBUtil.close(psBs);
            DBUtil.close(con);
        }

        // 2. Cập nhật In-memory cache & BookDB stock
        List<PhieuMuon> cache = getFallbackBorrows();
        if (generatedMaPhieu <= 0) {
            int maxId = 0;
            for (PhieuMuon existing : cache) {
                if (existing.getMaPhieu() > maxId) {
                    maxId = existing.getMaPhieu();
                }
            }
            generatedMaPhieu = maxId + 1;
        }

        PhieuMuon newPm = new PhieuMuon();
        newPm.setMaPhieu(generatedMaPhieu);
        newPm.setMaDocGia(readerId);
        newPm.setTenDocGia(readerName);
        newPm.setNgayMuon(ngayMuon);
        newPm.setHanTra(hanTra);
        newPm.setGhiChu(ghiChu);
        newPm.setTrangThai("CHO_DUYET");

        List<ChiTietPhieuMuon> ctList = new ArrayList<ChiTietPhieuMuon>();
        int ctId = 1;
        for (ChiTietGioMuon item : cart.getDanhSachSach()) {
            ChiTietPhieuMuon ct = new ChiTietPhieuMuon();
            ct.setMaCt(ctId++);
            ct.setMaPhieu(generatedMaPhieu);
            ct.setMaSach(item.getSach().getMaSach());
            ct.setTieuDeSach(item.getSach().getTieuDe());
            ct.setSoLuong(item.getSoLuong());
            ct.setTinhTrangMuon("Tốt");
            ct.setTinhTrangKhiMuon(TinhTrangSach.TOT);
            String barcode = String.format("BC-%04d-01", item.getSach().getMaSach());
            ct.setBanSaoSach(new BanSaoSach(barcode, item.getSach().getMaSach(), TinhTrangSach.TOT, false));
            ctList.add(ct);

            // Giảm số lượng sách còn lại
            BookDB.adjustStock(item.getSach().getMaSach(), -item.getSoLuong());
        }
        newPm.setChiTietList(ctList);
        newPm.setQuyDinhMuon(new QuyDinhMuon());

        cache.add(0, newPm);
        return true;
    }

    /**
     * Lấy toàn bộ phiếu mượn của một độc giả theo mã độc giả (phục vụ trang Lịch sử mượn)
     */
    public static synchronized List<PhieuMuon> getByReader(int readerId) {
        List<PhieuMuon> all = getAll();
        List<PhieuMuon> result = new ArrayList<PhieuMuon>();
        for (PhieuMuon pm : all) {
            if (pm.getMaDocGia() == readerId) {
                result.add(pm);
            }
        }
        return result;
    }

    /**
     * Lấy toàn bộ danh sách phiếu mượn trong hệ thống (phục vụ trang Quản trị Admin):
     * - Kết hợp dữ liệu từ MySQL (nếu có) và danh sách bộ nhớ tạm.
     * - Đọc đầy đủ danh sách sách mượn (chi tiết phiếu) và phiếu phạt đi kèm.
     */
    public static synchronized List<PhieuMuon> getAll() {
        List<PhieuMuon> cache = getFallbackBorrows();

        String sql = "SELECT p.*, u.ho_ten, q.ten_quy_dinh, q.muc_phat_moi_ngay FROM phieu_muon p " +
                     "JOIN nguoi_dung u ON p.ma_doc_gia = u.ma_nguoi_dung " +
                     "LEFT JOIN quy_dinh_muon q ON p.ma_quy_dinh = q.ma_quy_dinh " +
                     "ORDER BY p.ma_phieu_muon DESC";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = DBUtil.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<PhieuMuon> dbList = new ArrayList<PhieuMuon>();
            while (rs.next()) {
                PhieuMuon pm = mapPhieu(rs);
                pm.setChiTietList(getChiTiet(con, pm.getMaPhieu()));
                pm.setPhieuPhat(getPhieuPhatByPhieuMuon(con, pm.getMaPhieu()));
                dbList.add(pm);
            }
            if (!dbList.isEmpty()) {
                for (PhieuMuon memPm : cache) {
                    boolean exists = false;
                    for (PhieuMuon dbPm : dbList) {
                        if (dbPm.getMaPhieu() == memPm.getMaPhieu()) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        dbList.add(0, memPm);
                    }
                }
                fallbackCache = dbList;
                return dbList;
            }
        } catch (Exception e) {
            System.err.println("Lỗi BorrowDB.getAll (MySQL): " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(rs);
            DBUtil.close(ps);
            DBUtil.close(con);
        }

        return cache;
    }

    /**
     * Đếm số lượng phiếu mượn đang ở trạng thái Chờ Duyệt (CHO_DUYET) để hiển thị thông báo
     */
    public static synchronized int countPendingBorrows() {
        int count = 0;
        for (PhieuMuon pm : getAll()) {
            if ("CHO_DUYET".equals(pm.getTrangThai())) {
                count++;
            }
        }
        return count;
    }

    public static synchronized boolean updateStatus(int maPhieu, String trangThaiMoi) {
        String sql = "UPDATE phieu_muon SET trang_thai = ? WHERE ma_phieu_muon = ?";
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtil.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, trangThaiMoi);
            ps.setInt(2, maPhieu);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Lỗi BorrowDB.updateStatus: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(ps);
            DBUtil.close(con);
        }

        for (PhieuMuon pm : getFallbackBorrows()) {
            if (pm.getMaPhieu() == maPhieu) {
                if (("DA_HUY".equals(trangThaiMoi) || "HUY".equals(trangThaiMoi) || "TU_CHOI".equals(trangThaiMoi))
                        && !"DA_TRA".equals(pm.getTrangThai())
                        && !"DA_HUY".equals(pm.getTrangThai())
                        && !"TU_CHOI".equals(pm.getTrangThai())) {
                    for (ChiTietPhieuMuon ct : pm.getChiTietList()) {
                        BookDB.adjustStock(ct.getMaSach(), ct.getSoLuong());
                    }
                }
                pm.setTrangThai(trangThaiMoi);
                return true;
            }
        }
        return false;
    }

    /**
     * Thủ thư phê duyệt yêu cầu mượn:
     * - Cập nhật trạng thái phiếu mượn thành DANG_MUON.
     * - Ghi nhận mã thủ thư đã duyệt phiếu.
     */
    public static synchronized boolean approve(int maPhieu, ThuThu thuThu) {
        String sql = "UPDATE phieu_muon SET trang_thai = 'DANG_MUON', ma_thu_thu = ? WHERE ma_phieu_muon = ?";
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtil.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, (thuThu != null) ? thuThu.getMaThuThu() : 1);
            ps.setInt(2, maPhieu);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Lỗi BorrowDB.approve (MySQL): " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(ps);
            DBUtil.close(con);
        }

        for (PhieuMuon pm : getFallbackBorrows()) {
            if (pm.getMaPhieu() == maPhieu) {
                if (thuThu == null) {
                    thuThu = new ThuThu(1, "Thủ Thư Quản Trị", "admin@library.edu.vn", "0901234567", "123456", new java.util.Date());
                }
                pm.duyetMuon(thuThu);
                return true;
            }
        }
        return false;
    }

    /**
     * Thủ thư từ chối yêu cầu mượn:
     * - Cập nhật trạng thái thành TU_CHOI kèm lý do từ chối.
     * - Hoàn trả lại số lượng sách vào kho khả dụng (adjustStock).
     */
    public static synchronized boolean reject(int maPhieu, String lyDo) {
        String sql = "UPDATE phieu_muon SET trang_thai = 'TU_CHOI', ghi_chu = CONCAT(IFNULL(ghi_chu, ''), ?) WHERE ma_phieu_muon = ?";
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtil.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, " | Lý do từ chối: " + lyDo);
            ps.setInt(2, maPhieu);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Lỗi BorrowDB.reject (MySQL): " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(ps);
            DBUtil.close(con);
        }

        for (PhieuMuon pm : getFallbackBorrows()) {
            if (pm.getMaPhieu() == maPhieu) {
                if (!"DA_TRA".equals(pm.getTrangThai()) && !"TU_CHOI".equals(pm.getTrangThai()) && !"DA_HUY".equals(pm.getTrangThai())) {
                    for (ChiTietPhieuMuon ct : pm.getChiTietList()) {
                        BookDB.adjustStock(ct.getMaSach(), ct.getSoLuong());
                    }
                }
                pm.tuChoi(lyDo);
                return true;
            }
        }
        return false;
    }

    /**
     * PHƯƠNG THỨC THU HỒI / XÁC NHẬN TRẢ SÁCH: returnBooks()
     * ----------------------------------------------------------------------------
     * QUY TRÌNH NGHIỆP VỤ 4 BƯỚC:
     * 1. CẬP NHẬT CSDL:
     *    - Đổi trạng thái phieu_muon sang 'DA_TRA' và ghi nhận ngay_tra_thuc_te = NOW().
     *    - Cập nhật chi_tiet_phieu_muon ngày trả và tình trạng sách khi trả.
     *    - Đổi trạng thái bản sao sách (ban_sao_sach) thành san_sang = TRUE.
     * 2. HOÀN TRẢ TỒN KHO (Stock Replenishment):
     *    - Gọi BookDB.adjustStock(+soLuong) để cộng lại số lượng sách vào kho khả dụng.
     * 3. THUẬT TOÁN TÍNH PHẠT QUÁ HẠN TỰ ĐỘNG:
     *    - So sánh thời điểm hiện tại `now` với `han_tra`.
     *    - Nếu now.after(han_tra):
     *      + Tính độ lệch thời gian diffMs = now.getTime() - hanTra.getTime().
     *      + Nếu trễ từ 1 ngày trở lên: Tiền phạt = lateDays * 5.000 VNĐ.
     *      + Nếu trễ dưới 1 ngày (chế độ test nhanh theo phút): Phạt tối thiểu 5.000 VNĐ.
     * 4. TẠO PHIẾU PHẠT (phieu_phat):
     *    - Tự động sinh bản ghi trong bảng phieu_phat với trạng thái 'CHUA_NOP_PHAT'.
     */
    public static synchronized boolean returnBooks(int maPhieu) {
        java.util.Date now = new java.util.Date();
        java.sql.Timestamp nowTs = new java.sql.Timestamp(now.getTime());

        String sql1 = "UPDATE phieu_muon SET trang_thai = 'DA_TRA', ngay_tra_thuc_te = ? WHERE ma_phieu_muon = ?";
        String sql2 = "UPDATE chi_tiet_phieu_muon SET ngay_tra = ?, tinh_trang_khi_tra = 'TOT' WHERE ma_phieu_muon = ?";
        String sql3 = "UPDATE ban_sao_sach SET san_sang = TRUE WHERE ma_vach IN (SELECT ma_vach FROM chi_tiet_phieu_muon WHERE ma_phieu_muon = ?)";
        String sql4 = "INSERT INTO phieu_phat (ma_phieu_muon, so_tien_phat, ngay_lap, trang_thai, ly_do) VALUES (?, ?, ?, 'CHUA_NOP_PHAT', ?)";

        Connection con = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        PreparedStatement ps3 = null;
        PreparedStatement ps4 = null;

        java.util.Date dbHanTra = null;
        try {
            con = DBUtil.getConnection();

            // Đọc hạn trả thực tế từ MySQL để đảm bảo tính chính xác nếu người dùng sửa ngày trong CSDL
            String sqlCheck = "SELECT han_tra FROM phieu_muon WHERE ma_phieu_muon = ?";
            PreparedStatement psCheck = null;
            ResultSet rsCheck = null;
            try {
                psCheck = con.prepareStatement(sqlCheck);
                psCheck.setInt(1, maPhieu);
                rsCheck = psCheck.executeQuery();
                if (rsCheck.next()) {
                    dbHanTra = rsCheck.getTimestamp("han_tra");
                }
            } catch (Exception exCheck) {
                System.err.println("Notice: returnBooks read han_tra: " + exCheck.getMessage());
            } finally {
                DBUtil.close(rsCheck);
                DBUtil.close(psCheck);
            }

            ps1 = con.prepareStatement(sql1);
            ps1.setTimestamp(1, nowTs);
            ps1.setInt(2, maPhieu);
            ps1.executeUpdate();

            ps2 = con.prepareStatement(sql2);
            ps2.setTimestamp(1, nowTs);
            ps2.setInt(2, maPhieu);
            ps2.executeUpdate();

            try {
                ps3 = con.prepareStatement(sql3);
                ps3.setInt(1, maPhieu);
                ps3.executeUpdate();
            } catch (SQLException bsEx) {
                System.err.println("Notice: returnBooks ban_sao_sach update: " + bsEx.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Lỗi BorrowDB.returnBooks (MySQL): " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(ps3);
            DBUtil.close(ps2);
            DBUtil.close(ps1);
        }

        // Cập nhật In-memory cache & tính phạt nếu trễ hạn
        double fineAmount = 0.0;
        String fineReason = null;

        for (PhieuMuon pm : getFallbackBorrows()) {
            if (pm.getMaPhieu() == maPhieu) {
                if (!"DA_TRA".equals(pm.getTrangThai())) {
                    for (ChiTietPhieuMuon ct : pm.getChiTietList()) {
                        BookDB.adjustStock(ct.getMaSach(), ct.getSoLuong());
                        ct.traSach(TinhTrangSach.TOT);
                    }
                    
                    // Đồng bộ hạn trả từ CSDL nếu có
                    if (dbHanTra != null) {
                        pm.setHanTra(dbHanTra);
                    }

                    pm.setNgayTraThucTe(now);

                    // Kiểm tra quá hạn
                    if (pm.getHanTra() != null && now.after(pm.getHanTra())) {
                        long diffMs = now.getTime() - pm.getHanTra().getTime();
                        if (diffMs > 0) {
                            long lateMinutes = diffMs / (60 * 1000);
                            int lateDays = (int) (diffMs / (24 * 60 * 60 * 1000));

                            if (lateDays >= 1) {
                                fineAmount = (pm.getQuyDinhMuon() != null) ? pm.getQuyDinhMuon().tinhTienPhat(lateDays) : lateDays * 5000.0;
                                fineReason = "Quá hạn " + lateDays + " ngày";
                            } else {
                                // Quá hạn theo phút (dưới 1 ngày, ví dụ test 1 phút) -> phạt tối thiểu 5.000đ
                                fineAmount = 5000.0;
                                fineReason = "Quá hạn " + Math.max(1, lateMinutes) + " phút (Chế độ test nhanh)";
                            }

                            PhieuPhat pp = new PhieuPhat(pm.getMaPhieu(), pm.getMaPhieu(), fineAmount, now, TrangThaiPhat.CHUA_NOP_PHAT, fineReason);
                            pm.setPhieuPhat(pp);
                        }
                    }

                    pm.setTrangThai("DA_TRA");
                }
                break;
            }
        }

        // Lưu phiếu phạt vào bảng phieu_phat của MySQL nếu có phát sinh tiền phạt
        if (fineAmount > 0) {
            try {
                con = DBUtil.getConnection();
                ps4 = con.prepareStatement(sql4);
                ps4.setInt(1, maPhieu);
                ps4.setDouble(2, fineAmount);
                ps4.setTimestamp(3, nowTs);
                ps4.setString(4, fineReason);
                ps4.executeUpdate();
            } catch (Exception fineEx) {
                System.err.println("Lỗi lưu phieu_phat (MySQL): " + fineEx.getMessage());
                fineEx.printStackTrace();
            } finally {
                DBUtil.close(ps4);
                DBUtil.close(con);
            }
        }

        return true;
    }

    private static PhieuMuon mapPhieu(ResultSet rs) throws SQLException {
        PhieuMuon pm = new PhieuMuon();
        pm.setMaPhieu(rs.getInt("ma_phieu_muon"));
        pm.setMaDocGia(rs.getInt("ma_doc_gia"));
        pm.setTenDocGia(rs.getString("ho_ten"));
        pm.setNgayMuon(rs.getTimestamp("ngay_muon"));
        pm.setHanTra(rs.getTimestamp("han_tra"));
        pm.setNgayTraThucTe(rs.getTimestamp("ngay_tra_thuc_te"));
        pm.setGhiChu(rs.getString("ghi_chu"));
        pm.setTrangThai(rs.getString("trang_thai"));

        try {
            int maQd = rs.getInt("ma_quy_dinh");
            if (maQd > 0) {
                QuyDinhMuon qd = new QuyDinhMuon();
                qd.setMaQuyDinh(maQd);
                qd.setTenQuyDinh(rs.getString("ten_quy_dinh"));
                qd.setMucPhatMoiNgay(rs.getDouble("muc_phat_moi_ngay"));
                pm.setQuyDinhMuon(qd);
            }
        } catch (Exception ignored) {}

        return pm;
    }

    private static List<ChiTietPhieuMuon> getChiTiet(Connection con, int maPhieu) {
        List<ChiTietPhieuMuon> list = new ArrayList<ChiTietPhieuMuon>();
        String sql = "SELECT ct.*, s.tieu_de FROM chi_tiet_phieu_muon ct JOIN sach s ON ct.ma_sach = s.ma_sach WHERE ct.ma_phieu_muon = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, maPhieu);
            rs = ps.executeQuery();
            while (rs.next()) {
                ChiTietPhieuMuon ct = new ChiTietPhieuMuon();
                try {
                    ct.setMaCt(rs.getInt("ma_chi_tiet"));
                } catch (Exception ex) {
                    ct.setMaCt(rs.getInt("ma_ct"));
                }
                ct.setMaPhieu(rs.getInt("ma_phieu_muon"));
                ct.setMaSach(rs.getInt("ma_sach"));
                ct.setTieuDeSach(rs.getString("tieu_de"));
                ct.setSoLuong(rs.getInt("so_luong"));
                ct.setNgayTra(rs.getTimestamp("ngay_tra"));

                String mv = rs.getString("ma_vach");
                if (mv != null && !mv.isEmpty()) {
                    ct.setBanSaoSach(new BanSaoSach(mv, ct.getMaSach(), TinhTrangSach.TOT, false));
                }
                ct.setTinhTrangMuon("Tốt");
                list.add(ct);
            }
        } catch (Exception e) {
            System.err.println("Lỗi BorrowDB.getChiTiet (MySQL): " + e.getMessage());
        } finally {
            DBUtil.close(rs);
            DBUtil.close(ps);
        }
        return list;
    }

    private static PhieuPhat getPhieuPhatByPhieuMuon(Connection con, int maPhieu) {
        String sql = "SELECT * FROM phieu_phat WHERE ma_phieu_muon = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, maPhieu);
            rs = ps.executeQuery();
            if (rs.next()) {
                PhieuPhat pp = new PhieuPhat();
                pp.setMaPhieuPhat(rs.getInt("ma_phieu_phat"));
                pp.setMaPhieuMuon(rs.getInt("ma_phieu_muon"));
                pp.setSoTienPhat(rs.getDouble("so_tien_phat"));
                pp.setNgayLap(rs.getTimestamp("ngay_lap"));
                pp.setLyDo(rs.getString("ly_do"));
                String tt = rs.getString("trang_thai");
                pp.setTrangThai(TrangThaiPhat.fromString(tt));
                return pp;
            }
        } catch (Exception e) {
            System.err.println("Lỗi BorrowDB.getPhieuPhatByPhieuMuon: " + e.getMessage());
        } finally {
            DBUtil.close(rs);
            DBUtil.close(ps);
        }
        return null;
    }
}
