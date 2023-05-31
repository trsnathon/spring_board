package homepage.service;

import com.zaxxer.hikari.HikariDataSource;
import homepage.domain.*;
import homepage.repository.MySqlBoardRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class MySqlBoardService {

    private final MySqlBoardRepository mySqlBoardRepository;
    private final DataSource dataSource;

    public MySqlBoardService(MySqlBoardRepository mySqlBoardRepository, DataSource dataSource) {
        this.mySqlBoardRepository = mySqlBoardRepository;
        this.dataSource =dataSource;
    }

    public SaveFormBoard save(Member member, SaveFormBoard saveFormBoard) throws SQLException {
        SaveFormBoard savedBoard = mySqlBoardRepository.save(member, saveFormBoard);
        return savedBoard;
    }

    public void updateWish(int postNo, Member loginMember) throws SQLException {
        Connection con = dataSource.getConnection();
        try {
            con.setAutoCommit(false); //트랜잭션 시작
            log.info("service connection = {}", con);
            //비즈니스 로직
            mySqlBoardRepository.updateWishToUpdateTable(postNo, loginMember.getMemberId(), con);
            con.commit(); //성공시 커밋
        } catch (Exception e) {
            con.rollback(); //실패시 롤백
            throw new IllegalStateException(e);
        } finally {
            release(con);
        }
    }


    public Board findByMemberId(String memberId) throws SQLException {
        Board findBoard = mySqlBoardRepository.findByMemberId(memberId);
        return findBoard;
    }

    public Board findByPostNo(int postNo) throws SQLException{
        Connection con = dataSource.getConnection();

        try {
            con.setAutoCommit(false); //트랜잭션 시작
            log.info("service connection = {}", con);
            //비즈니스 로직
            Board findBoard = mySqlBoardRepository.findByPostNo(postNo, con);
            con.commit(); //성공시 커밋
            return findBoard;
        } catch (Exception e) {
            con.rollback(); //실패시 롤백
            throw new IllegalStateException(e);
        } finally {
          //  release(con);
            JdbcUtils.closeConnection(con);
        }

    }
    public Board  findByPostNoConnection(int postNo) throws SQLException{
         Connection con = dataSource.getConnection();
        try {
            con.setAutoCommit(false); //트랜잭션 시작
            log.info("service connection = {}", con);
            //비즈니스 로직
            Board findBoard = mySqlBoardRepository.findByPostNo(postNo, con);
            con.commit(); //성공시 커밋
            return findBoard;
        } catch (Exception e) {
            con.rollback(); //실패시 롤백
            throw new IllegalStateException(e);
        } finally {
            JdbcUtils.closeConnection(con);
        }

    }


    private void release(Connection con) {
        if (con != null) {
            try {
                con.setAutoCommit(true); //커넥션 풀 고려
                con.close();
            } catch (Exception e) {
                log.info("error", e);
            }
        }
    }
    public List<FormBoard> allList(Search search, Sort sort) throws SQLException {
        List<FormBoard> all = mySqlBoardRepository.allList(search, sort);
        return all;
    }

    public int update(EditBoard editBoard) throws SQLException {
        return  mySqlBoardRepository.update(editBoard);
    }

    public int delete(String memberId) throws SQLException {
        int delete = mySqlBoardRepository.delete(memberId);
        return delete;
    }

//    public void viewUp(String memberId, int postNo) throws SQLException{
//        Connection con = null;
//        mySqlBoardRepository.ViewUp(memberId,postNo, con);
//    }
}
