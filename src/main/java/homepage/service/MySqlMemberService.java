package homepage.service;

import homepage.domain.Member;
import homepage.repository.MySqlMemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@Service
public class MySqlMemberService {

    private final MySqlMemberRepository mySqlMemberRepository;

    public MySqlMemberService(MySqlMemberRepository mySqlMemberRepository) {
        this.mySqlMemberRepository = mySqlMemberRepository;
    }

    public Member save(Member member) throws SQLException {
        Member savedMember = mySqlMemberRepository.save(member);
        return savedMember;
    }

    public Member login(Member member) throws SQLException {
        return mySqlMemberRepository.login(member);
    }

    public Member findByMemberId(String memberId) throws SQLException {
        Member findMemberId = mySqlMemberRepository.findByMemberId(memberId);
        return findMemberId;
    }

    public List<String> allList() throws SQLException {
        List<String> members = mySqlMemberRepository.allList();
        return members;
    }

    public void update(String memberId, String password) throws SQLException {
        mySqlMemberRepository.update(memberId,password);
    }

    public void delete(String memberId) throws SQLException {
        mySqlMemberRepository.delete(memberId);
    }

}
