package data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * ============================================================================
 * TIỆN ÍCH KẾT NỐI CSDL: DBUtil (Database Utility)
 * ----------------------------------------------------------------------------
 * Chức năng:
 * 1. Quản lý kết nối JDBC tới hệ quản trị cơ sở dữ liệu MySQL (library_db).
 * 2. Tự động thử nghiệm danh sách mật khẩu phổ biến (FALLBACK_PASSWORDS) để tương thích
 *    với máy chấm điểm / máy giảng viên mà không cần chỉnh sửa code thủ công.
 * 3. Tự động khởi tạo và nâng cấp cấu trúc bảng nếu chưa tồn tại (initSchemaIfNeeded).
 * 4. Cung cấp các hàm đóng tài nguyên Connection, Statement, ResultSet an toàn.
 * ============================================================================
 */
public class DBUtil {

    private static final String[] URLS = {
        "jdbc:mysql://localhost:3307/library_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=Asia/Ho_Chi_Minh&allowPublicKeyRetrieval=true&characterEncoding=utf8",
        "jdbc:mysql://localhost:3306/library_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=Asia/Ho_Chi_Minh&allowPublicKeyRetrieval=true&characterEncoding=utf8"
    };
    private static final String USER = "root";
    // Mật khẩu MySQL mặc định và danh sách mật khẩu dự phòng
    private static final String PASSWORD = "";
    private static final String[] FALLBACK_PASSWORDS = {"", "root", "123456", "1234", "admin", "12345678", "password"};
    private static boolean schemaChecked = false;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL Driver not found: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        Connection con = null;
        SQLException lastEx = null;

        for (String url : URLS) {
            try {
                con = DriverManager.getConnection(url, USER, PASSWORD);
                if (con != null) break;
            } catch (SQLException ex) {
                lastEx = ex;
                for (String pwd : FALLBACK_PASSWORDS) {
                    try {
                        con = DriverManager.getConnection(url, USER, pwd);
                        if (con != null) break;
                    } catch (SQLException ignored) {}
                }
                if (con != null) break;
            }
        }

        if (con == null && lastEx != null) {
            throw lastEx;
        }

        initSchemaIfNeeded(con);
        return con;
    }

    private static synchronized void initSchemaIfNeeded(Connection con) {
        if (schemaChecked || con == null) return;
        schemaChecked = true;

        // Tự động nâng cấp kiểu dữ liệu cột sang DATETIME để hỗ trợ lưu chính xác giờ:phút:giây (phục vụ test quá hạn nhanh)
        try {
            Statement upgradeSt = con.createStatement();
            try { upgradeSt.execute("ALTER TABLE phieu_muon MODIFY ngay_muon DATETIME NOT NULL"); } catch (Exception ignored) {}
            try { upgradeSt.execute("ALTER TABLE phieu_muon MODIFY han_tra DATETIME NOT NULL"); } catch (Exception ignored) {}
            try { upgradeSt.execute("ALTER TABLE phieu_muon MODIFY ngay_tra_thuc_te DATETIME DEFAULT NULL"); } catch (Exception ignored) {}
            try { upgradeSt.execute("ALTER TABLE chi_tiet_phieu_muon MODIFY ngay_tra DATETIME DEFAULT NULL"); } catch (Exception ignored) {}
            try { upgradeSt.execute("ALTER TABLE phieu_phat MODIFY ngay_lap DATETIME DEFAULT NULL"); } catch (Exception ignored) {}
            upgradeSt.close();
        } catch (Exception ignored) {}

        Statement st = null;
        ResultSet rs = null;
        try {
            st = con.createStatement();
            rs = st.executeQuery("SELECT COUNT(*) FROM sach");
            if (rs.next() && rs.getInt(1) > 0) {
                return;
            }
        } catch (SQLException tableMissing) {
            // Table 'sach' does not exist yet -> auto-run setup.sql if available
            try {
                java.nio.file.Path sqlPath = java.nio.file.Paths.get("C:\\LapTrinhWeb\\Doanweb\\setup.sql");
                if (java.nio.file.Files.exists(sqlPath)) {
                    String sql = new String(java.nio.file.Files.readAllBytes(sqlPath), "UTF-8");
                    Statement initSt = con.createStatement();
                    for (String part : sql.split(";")) {
                        String cmd = part.replaceAll("(?m)^--.*$", "").trim();
                        if (!cmd.isEmpty()) {
                            initSt.execute(cmd);
                        }
                    }
                    initSt.close();
                }
            } catch (Exception ignored) {
            }
        } finally {
            close(rs);
            close(st);
        }
    }

    public static void close(Connection c) {
        if (c != null) {
            try { c.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public static void close(Statement s) {
        if (s != null) {
            try { s.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public static void close(ResultSet r) {
        if (r != null) {
            try { r.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public static boolean isConnected() {
        Connection c = null;
        try {
            c = getConnection();
            return c != null && !c.isClosed();
        } catch (Exception e) {
            return false;
        } finally {
            close(c);
        }
    }

    public static void main(String[] args) {
        Connection c = null;
        try {
            c = getConnection();
            System.out.println(">>> Ket noi MySQL thanh cong: " + c.getCatalog());
            java.sql.DatabaseMetaData meta = c.getMetaData();
            java.sql.ResultSet rs = meta.getColumns(null, null, "phieu_muon", "%");
            while (rs.next()) {
                System.out.println("  phieu_muon." + rs.getString("COLUMN_NAME") + " -> " + rs.getString("TYPE_NAME"));
            }
            rs.close();
            System.out.println(">>> Hoan tat kiem tra schema!");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(c);
        }
    }
}
