package homepage.repository;

import homepage.domain.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.sql.*;
import java.sql.Date;
import java.util.*;

@Repository
@Slf4j
public class MySqlBoardRepository {

    private final DataSource dataSource;

    private int view = 0;

    public MySqlBoardRepository(DataSource dataSource) {

        this.dataSource = dataSource;
    }

    public void ViewUp(String memberId, int postNo, Connection con) throws SQLException {

        Board board = findByMemberId(memberId);
        System.out.println(board.getView());
        String sql = "UPDATE board SET 조회수 = ? WHERE 글번호 = ?";
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, board.getView() +1);
            pstmt.setInt(2, postNo);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}", resultSize);
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            JdbcUtils.closeStatement(pstmt);
        }

    }

    public Board findByPostNo(int postNo, Connection con) throws SQLException {

        log.info("repository connection = {}", con);
        Board byPostNoConnection = findByPostNoConnection(postNo);
        if(byPostNoConnection.getLock().equals("허용")){
        ViewUp(byPostNoConnection.getMemberId(), postNo, con);
        }
        String sql = "select * from board where 글번호 = ?";

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, postNo);
            rs = pstmt.executeQuery();

            if(rs.next()){
                Board board = new Board();
                board.setTitle(rs.getString("제목"));
                board.setBody(rs.getString("내용"));
                board.setMemberId(rs.getString("작성자"));
                board.setPostDate(rs.getDate("작성일"));
                board.setView(rs.getInt("조회수"));
                board.setLock(rs.getString("비밀글"));
                board.setWish(rs.getInt("wish"));
                return board;
            }else{
                throw new NoSuchElementException("ERROR");
            }

        }catch (SQLException e){
            log.error("db error", e);
            throw e;
        }finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(pstmt);
        }

    }

    public Board findByPostNoConnection(int postNo) throws SQLException {

        String sql = "select * from board where 글번호 = ?";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, postNo);
            rs = pstmt.executeQuery();

            if(rs.next()){
                Board board = new Board();
                board.setTitle(rs.getString("제목"));
                board.setBody(rs.getString("내용"));
                board.setMemberId(rs.getString("작성자"));
                board.setPostDate(rs.getDate("작성일"));
                board.setView(rs.getInt("조회수"));
                board.setLock(rs.getString("비밀글"));
                return board;
            }else{
                throw new NoSuchElementException("ERROR");
            }

        }catch (SQLException e){
            log.error("db error", e);
            throw e;
        }finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(pstmt);
            JdbcUtils.closeConnection(con);
        }

    }


    public SaveFormBoard save(Member member, SaveFormBoard saveFormBoard) throws SQLException {
        String sql = "insert into board (제목, 내용, 작성자, 작성일, 조회수) values(?,?,?,?,?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            log.info("db  = {} " , con );
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, saveFormBoard.getTitle());
            pstmt.setString(2, saveFormBoard.getBody());
            pstmt.setString(3, saveFormBoard.getMemberId());
            pstmt.setDate(4, saveFormBoard.getPostDate());
            pstmt.setInt(5, view);
            pstmt.executeUpdate();
            return saveFormBoard;
        }catch (SQLException e){
            log.error("db error", e);
            throw e;
        }finally {
            JdbcUtils.closeStatement(pstmt);
        }

    }

    public Board findByMemberId(String memberId) throws SQLException {
        String sql = "select * from board where 작성자 = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            rs = pstmt.executeQuery();

            if(rs.next()){
                Board board = new Board();
                board.setTitle(rs.getString("제목"));
                board.setBody(rs.getString("내용"));
                board.setMemberId(rs.getString("작성자"));
                board.setPostDate(rs.getDate("작성일"));
                board.setView(rs.getInt("조회수"));
                return board;
            }else{
                throw new NoSuchElementException("member not found memberId=" + memberId);
            }

        }catch (SQLException e){
            log.error("db error", e);
            throw e;
        }finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(pstmt);
            JdbcUtils.closeConnection(con);
        }

    }

    public void updateWishToUpdateTable(int postNo, String member, Connection con) throws SQLException {

        String sql = "";
        int dub = 0;
        System.out.println("넘어오는 숫자 = " + findWishMemberCount(member,postNo,con));

        if(findWishMemberCount(member,postNo,con) == 0){
            sql = "INSERT INTO wish (글번호, wishcheck) VALUES (?, ?)";
        }else if(findWishMemberCount(member,postNo,con) == 1) {
            sql = "DELETE FROM wish WHERE 글번호 = ? AND wishcheck = ?";
            dub =1;
        }else {
            throw new IllegalStateException();
        }

        System.out.println("dup = " + dub);

        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement(sql);
            if(dub ==0){
            pstmt.setInt(1, postNo);
            pstmt.setString(2, member);
            }else if(dub ==1){
            pstmt.setInt(1, postNo);
            pstmt.setString(2, member);
            }
            pstmt.executeUpdate();

            updateWishToBoardTable(postNo,con);

        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            JdbcUtils.closeStatement(pstmt);
        }

    }

    public void updateWishToBoardTable(int postNo, Connection con) throws SQLException {

        String sql = "UPDATE board SET wish = ? WHERE 작성자 = ? AND 글번호 = ?";

        int wishCount = findWish(postNo, con);
        System.out.println("wish count = " + wishCount);
        Board board = findByPostNo(postNo, con);
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, wishCount);
            pstmt.setString(2, board.getMemberId());
            pstmt.setInt(3, postNo);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            JdbcUtils.closeStatement(pstmt);
        }

    }

    public int findWishMemberCount(String member, int postNo, Connection con) throws SQLException {

        String sql = "SELECT COUNT(*) FROM wish WHERE 글번호 = ? AND wishcheck = ?";
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, postNo);
            pstmt.setString(2,member);
            rs =pstmt.executeQuery();

            if(rs.next()){
               if(rs.getInt(1) ==1){
                   return 1;
               }else {
                   return 0;
               }
            }

            return 2;

        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(pstmt);
        }

    }


    public int findWish(int postNo, Connection con) throws SQLException {

        String sql = "SELECT COUNT(*) FROM wish WHERE 글번호 = ?";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int view ;
        try {
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, postNo);
            rs = pstmt.executeQuery();

            if(rs.next()){
                view = rs.getInt(1);

            }else{
                throw new NoSuchElementException("not found ");
            }

            return  view;
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(pstmt);
        }

    }

    public List<FormBoard> allList(Search search, Sort sort) throws SQLException {

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
            try {

                con = getConnection();
                if (search.getSearchTitle() == null || search.getSearchTitle().isEmpty()) {
                    String sql = "select * from board";
                    pstmt = con.prepareStatement(sql);
                } else {

                    String sql  = "SELECT * FROM board WHERE 제목 like ?";
                    pstmt = con.prepareStatement(sql);
                    pstmt.setString(1, "%" + search.getSearchTitle() + "%");
                }

                rs = pstmt.executeQuery();
                ArrayList<FormBoard> all = new ArrayList<>();
                while(rs.next()){
                    FormBoard board = new FormBoard();
                    board.setPostNo(rs.getInt("글번호"));
                    board.setTitle(rs.getString("제목"));
                    board.setMemberId(rs.getString("작성자"));
                    board.setPostDate(rs.getDate("작성일"));
                    board.setView(rs.getInt("조회수"));
                    board.setWish(findWish(board.getPostNo(),con));
                    all.add(board);

                }

//            // 글 번호 정렬
//            Collections.sort(all, new Comparator<Board>() {
//                @Override
//                public int compare(Board b1, Board b2) {
//                    return Integer.compare(b1.getPostNo(), b2.getPostNo());
//                }
//            });

                if(sort.getSort() == null || sort.getSort().isEmpty()){

                    return all;
                } else if (sort.getSort().equals("조회순")) {

            Collections.sort(all, new Comparator<FormBoard>() {
                @Override
                public int compare(FormBoard b1, FormBoard b2) {
                    return Integer.compare(b1.getView(), b2.getView());
                }
            });
                    return all;

                }else if(sort.getSort().equals("날짜순")){
                    Collections.sort(all, new Comparator<FormBoard>() {
                        @Override
                        public int compare(FormBoard b1, FormBoard b2) {
                            return b1.getPostDate().compareTo(b2.getPostDate());
                        }
                    });
                    return all;

                }else {
                    return all;
                }



            }catch (SQLException e){
                log.error("db error", e);
                throw e;
            }finally {
                JdbcUtils.closeConnection(con);
                JdbcUtils.closeResultSet(rs);
                JdbcUtils.closeStatement(pstmt);

            }



    }

    public int update(EditBoard editBoard) throws SQLException {
        String sql = "UPDATE board SET 제목 = ?,내용 = ? WHERE 작성자 = ?";
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, editBoard.getTitle());
            pstmt.setString(2, editBoard.getBody());
            pstmt.setString(3, editBoard.getMemberId());
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}", resultSize);
            return resultSize;
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            JdbcUtils.closeStatement(pstmt);
        }
    }

    public int delete(String memberId) throws SQLException {
        String sql = "delete from board where 작성자=?";
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            int resultSize = pstmt.executeUpdate();
            return resultSize;
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            JdbcUtils.closeStatement(pstmt);
        }
    }



    private Connection getConnection() throws SQLException {
        Connection con = dataSource.getConnection();
        log.info("get connection={}, class={}", con, con.getClass());
        return con;
    }


//    private void close(Connection con, Statement stmt, ResultSet rs){
//        JdbcUtils.closeResultSet(rs);
//        JdbcUtils.closeStatement(stmt);
//        JdbcUtils.closeConnection(con);
//    }


//    public void updateWish(int postNo, Connection con, Member loginMember) throws SQLException {
//        System.out.println("log인 멤버 = " + loginMember.getMemberId());
//        Board board = findByPostNo(postNo, con);
//        log.info("보드 정보  = {}", board.getMemberId());
//        log.info("보드 정보  = {}", board.getWish());
//        log.info("보드 정보  = {}", board.getWishCheck());
//        String sql ="";
//        String wishCheck = board.getWishCheck();
//        System.out.println("check 멤버  = " + wishCheck);
//        String[] splitWish = wishCheck.split(",");
//        String member = "";
//
//        int num=0;
//        for (String id : splitWish) {
//            if(loginMember.getMemberId().equals(id)){
//                num = -1;
//                sql = "UPDATE board SET wish = ?, wishcheck = REPLACE(wishcheck, ?, '') WHERE 작성자 = ?";
//                member = loginMember.getMemberId();
//            }else {
//                num =+1;
//                sql = "UPDATE board SET wish = ?, wishcheck = CONCAT(wishcheck, ?) WHERE 작성자 = ?";
//                member = loginMember.getMemberId();
//            }
//        }
//
//
//
//        PreparedStatement pstmt = null;
//        try {
//            pstmt = con.prepareStatement(sql);
//            pstmt.setInt(1, board.getWish() +num);
//            pstmt.setString(2, "," + member);
//            pstmt.setString(3,board.getMemberId());
//            int resultSize = pstmt.executeUpdate();
//            log.info("resultSize={}", resultSize);
//        } catch (SQLException e) {
//            log.error("db error", e);
//            throw e;
//        } finally {
//            JdbcUtils.closeStatement(pstmt);
//        }
//    }
}
