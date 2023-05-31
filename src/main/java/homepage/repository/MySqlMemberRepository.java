package homepage.repository;

import homepage.domain.Board;
import homepage.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@Repository
@Slf4j
public class MySqlMemberRepository {

    private final DataSource dataSource;

    public MySqlMemberRepository(DataSource dataSource) {

        this.dataSource = dataSource;
    }

    public Member save(Member member) throws SQLException {

        String sql = "insert into members(member_id, password) values(?,?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            log.info("db  = {} " , con );
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setString(2, member.getPassword());
            pstmt.executeUpdate();
            return member;
        }catch (SQLException e){
            log.error("db error", e);
            throw e;
        }finally {
            close(con, pstmt, null);
        }

    }

    public Member login(Member member) throws SQLException {
        Member findMember = findByMemberId(member.getMemberId());
        log.info("member = {}", member.getPassword());
        log.info("findMember = {}", findMember.getPassword());
        if(findMember == null){

            return null;
        }

        if(findMember != null) {
            if (member.getPassword().equals(findMember.getPassword())) {
                String sql = "select * from members where member_id = ?";
                Connection con = null;
                PreparedStatement pstmt = null;
                ResultSet rs = null;

                try {
                    con = getConnection();
                    pstmt = con.prepareStatement(sql);
                    pstmt.setString(1, member.getMemberId());
                    rs = pstmt.executeQuery();
                    if(rs.next()){
                        Member loginMember = new Member();
                        loginMember.setMemberId(rs.getString("member_id"));
                        loginMember.setNumber(rs.getInt("active"));
                        loginMember.setPassword(rs.getString("password"));
                        loginMember.setAuthority(rs.getInt("authority"));
                        return loginMember;
                    }else{
                        return null;
                    }

                }catch (SQLException e){
                    log.error("db error", e);
                    throw e;
                }finally {
                    close(con, pstmt, rs);
                }

            }else {
                return null;
            }

        }

        return null;
    }



    public Member findByMemberId(String memberId) throws SQLException {
        String sql = "select * from members where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            rs = pstmt.executeQuery();

            if(rs.next()){
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setNumber(rs.getInt("active"));
                member.setPassword(rs.getString("password"));
                member.setAuthority(rs.getInt("authority"));
                return member;
            }else{
                return null;
            }

        }catch (SQLException e){
            log.error("db error", e);
            throw e;
        }finally {
            close(con, pstmt, rs);
        }

    }

    public List<String> allList() throws SQLException {
        String sql = "select * from members";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            ArrayList<String> all = new ArrayList<>();

            if(!rs.next()){
                return null;
            }

            while(rs.next()){
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                all.add(member.getMemberId());

            }


            // 글 번호 정렬
            Collections.sort(all, new Comparator<String>() {
                @Override
                public int compare(String id1, String id2) {
                    return (id1.toLowerCase()).compareTo((id2).toLowerCase());
                }
            });

            return all;

        }catch (SQLException e){
            log.error("db error", e);
            throw e;
        }finally {
            close(con, pstmt, rs);

        }

    }

    public void update(String memberId, String password) throws SQLException {
        String sql = "update members set password=? where member_id=?";
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, password);
            pstmt.setString(2, memberId);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}", resultSize);
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    public void delete(String memberId) throws SQLException {
        String sql = "update members set active = ? where member_id=?";
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, 0);
            pstmt.setString(2, memberId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }


    private void close(Connection con, Statement stmt, ResultSet rs){
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        JdbcUtils.closeConnection(con);
    }

    private Connection getConnection() throws SQLException {
        Connection con = dataSource.getConnection();
        log.info("get connection={}, class={}", con, con.getClass());
        return con;
    }
}
