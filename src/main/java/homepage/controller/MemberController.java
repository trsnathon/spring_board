package homepage.controller;

import homepage.domain.EditMemberForm;
import homepage.domain.Email;
import homepage.domain.Member;
import homepage.emplement.EmailServiceImpl;
import homepage.service.EmailService;
import homepage.service.MySqlMemberService;
import homepage.session.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/members")
public class MemberController {

    private final MySqlMemberService mySqlMemberService;
    private final EmailService emailService;
    public MemberController(MySqlMemberService mySqlMemberService, EmailService emailService) {
        this.mySqlMemberService = mySqlMemberService;
        this.emailService = emailService;
    }
    @ResponseBody
    @PostMapping("/join")
    public ResponseEntity<?> join(@Valid @ModelAttribute("member") Member member, @RequestParam String inputEmail, BindingResult bindingResult, HttpServletRequest request) throws Exception {


        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("올바른 입력값을 입력하세요");
        }

        Member findMember = mySqlMemberService.findByMemberId(member.getMemberId());
        log.info("login = {}", member);
        if(findMember != null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("중복된 회원 입니다");
        }else if(emailService.viewEmailCheck(inputEmail) == 1){
            Member savedMember = mySqlMemberService.save(member);
            emailService.delete(inputEmail);
            return ResponseEntity.ok("회원 가입 완료");
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이메일 인증을 확인해 주세요");
        }

    }

//    @ResponseBody
//    @PostMapping("/join/emailCheck")
//    public ResponseEntity<?> joinCheck(@RequestParam int emailNo, HttpServletRequest request) throws Exception {
//
//        Member member = (Member) request.getAttribute("TempMember");
//        int emailCode = (Integer) request.getAttribute("emailCode");
//        if(emailNo == emailCode){
//            Member savedMember = mySqlMemberService.save(member);
//            return  ResponseEntity.ok("email 인증이 안료되었습니다");
//        }else{
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("올바른 입력값을 입력하세요");
//        }
//    }

    @ResponseBody
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @ModelAttribute("member") Member member, BindingResult bindingResult, HttpServletRequest request) throws SQLException {

        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("올바른 입력값을 입력하세요");
        }

        Member findMember = mySqlMemberService.findByMemberId(member.getMemberId());
        log.info("findMember = {}", findMember);

        if(findMember == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("존재 하지 않는 회원 이거나 비밀번호가 틀립니다");
        }else if(findMember.getNumber() == 0){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("삭제된 회원 입니다");
        }else{

            Member loginMember = mySqlMemberService.login(member);
            log.info("loginMember = {}", loginMember);

            HttpSession session = request.getSession();
            session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
            System.out.println(session);
            return ResponseEntity.ok("로그인 되었습니다");
        }

    }


    // 회원 번호로 조회

    @ResponseBody
    @GetMapping("/{memberId}")
    public ResponseEntity<?> selectOne(@PathVariable String memberId, HttpServletRequest request) throws SQLException {

        HttpSession session = request.getSession(false);

        if (session == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("로그인 하세요");
        }else{

            Member loginMember = (Member)session.getAttribute(SessionConst.LOGIN_MEMBER);
            System.out.println(loginMember);
            if(loginMember.getAuthority() ==1 || loginMember.getMemberId().equals(memberId) ){
                Member findMember = mySqlMemberService.findByMemberId(memberId);

                if(findMember == null){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("회원이 없습니다");
                }else{
                    return ResponseEntity.ok(findMember);
                }

            }else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("관리자만 조회 가능합니다.");
            }

        }



    }

    @ResponseBody
    @PostMapping("/{memberId}/edit")
    public ResponseEntity<?> update(@PathVariable String memberId, @Valid @ModelAttribute("editMemberForm") EditMemberForm editMemberForm, BindingResult bindingResult, HttpServletRequest request) throws SQLException {




        if(bindingResult.hasErrors()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("올바른 값을 입력 하세요");
        }

        HttpSession session = request.getSession(false);

        if (session == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("로그인 하세요");
        }else{

            Member loginMember = (Member)session.getAttribute(SessionConst.LOGIN_MEMBER);
            System.out.println(loginMember);
            if(loginMember.getAuthority() ==1 || loginMember.getMemberId().equals(memberId)){
                mySqlMemberService.update(memberId, editMemberForm.getPassword());
                return ResponseEntity.ok("수정 완료");
            }else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("관리자 또는 본인만 조회 가능합니다.");
            }

        }
    }

    @ResponseBody
    @PostMapping("/{memberId}/delete")
    public ResponseEntity<?> delete(@PathVariable String memberId, @Valid @ModelAttribute("member") Member member, BindingResult bindingResult, HttpServletRequest request) throws SQLException {



        HttpSession session = request.getSession(false);

        if (session == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("로그인 하세요");
        }else{
            Member loginMember = (Member)session.getAttribute(SessionConst.LOGIN_MEMBER);
            System.out.println(loginMember);
            if(loginMember.getAuthority() ==1 || loginMember.getMemberId().equals(memberId)){
                mySqlMemberService.delete(memberId);
                return ResponseEntity.ok("수정 완료");
            }else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("관리자 또는 본인만 조회 가능합니다.");
            }

        }

    }


    @ResponseBody
    @GetMapping()
    public ResponseEntity<?> allList(HttpServletRequest request) throws SQLException {

        HttpSession session = request.getSession(false);
        if (session == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("로그인 하세요");
        }else{

            Member member = (Member)session.getAttribute(SessionConst.LOGIN_MEMBER);
            System.out.println(member);
            if(member.getAuthority() == 1){
                List<String> members = mySqlMemberService.allList();
                return ResponseEntity.ok(members);
            }else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("관리자만 조회 가능합니다.");
            }

        }

    }

}
