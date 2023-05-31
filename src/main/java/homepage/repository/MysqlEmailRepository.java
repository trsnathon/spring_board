package homepage.repository;

import homepage.domain.Board;
import homepage.domain.EditBoard;
import homepage.domain.Member;
import homepage.domain.SaveFormBoard;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

@Repository
@Slf4j
public class MysqlEmailRepository {

    private final DataSource dataSource;

    public MysqlEmailRepository(DataSource dataSource) {

        this.dataSource = dataSource;
    }

    public int viewEmailCheck(String email) throws SQLException {

        String sql = "select email_check FROM emailcheck where email = ? ";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            int emailNum = 0;
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();


            if(rs.next()){
               emailNum = rs.getInt("email_check");
                return emailNum;
            }else{
                throw new SQLException("인증 오류");
            }


        }catch (SQLException e){
            log.error("db error", e);
            throw e;
        }finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(pstmt);
        }

    }

    public int viewSendCode(String email) throws SQLException {

        String sql = "select sendcode FROM emailcheck where email = ? ";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            int emailNum = 0;
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();


            if(rs.next()){
                emailNum = rs.getInt("sendcode");
                return emailNum;
            }else{
                throw new SQLException("인증 오류");
            }


        }catch (SQLException e){
            log.error("db error", e);
            throw e;
        }finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(pstmt);
        }

    }

     public void save(String email, String epw) throws SQLException {

         String sql = "INSERT INTO emailcheck(email, sendcode) VALUES (?, ?)";
         Connection con = null;
         PreparedStatement pstmt = null;
         ResultSet rs = null;
         int view;
         try {
             con = getConnection();
             pstmt = con.prepareStatement(sql);
             pstmt.setString(1, email);
             pstmt.setString(2, epw);
             pstmt.executeUpdate();

         } catch (SQLException e) {
             log.error("db error", e);
             throw e;
         } finally {
             JdbcUtils.closeStatement(pstmt);
             JdbcUtils.closeConnection(con);
         }

     }

        public void update(String email) throws SQLException {
            String sql = "UPDATE emailcheck set email_check = ? WHERE email = ?";
            Connection con = null;
            PreparedStatement pstmt = null;
            try {
                con = getConnection();
                pstmt = con.prepareStatement(sql);
                pstmt.setInt(1, 1);
                pstmt.setString(2, email);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                log.error("db error", e);
                throw e;
            } finally {
                JdbcUtils.closeStatement(pstmt);
                JdbcUtils.closeConnection(con);
            }
        }

        public void delete(String email) throws SQLException {
            String sql = "delete from emailcheck where email=?";
            Connection con = null;

            PreparedStatement pstmt = null;
            try {
                con = getConnection();
                pstmt = con.prepareStatement(sql);
                pstmt.setString(1, email);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                log.error("db error", e);
                throw e;
            } finally {
                JdbcUtils.closeStatement(pstmt);
                JdbcUtils.closeConnection(con);
            }
        }

    private Connection getConnection() throws SQLException {
        Connection con = dataSource.getConnection();
        log.info("get connection={}, class={}", con, con.getClass());
        return con;
    }
    }
