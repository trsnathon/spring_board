package homepage.controller;

import homepage.domain.*;
import homepage.service.MySqlBoardService;
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
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Controller
@RequestMapping("/board")
@Slf4j
public class BoardController {

    private final MySqlBoardService mySqlBoardService;


    public BoardController(MySqlBoardService mySqlBoardService) {

        this.mySqlBoardService = mySqlBoardService;

    }



    @ResponseBody
    @PostMapping("/write")
    public ResponseEntity<?> save(@Valid @ModelAttribute("board") SaveFormBoard saveFormBoard , BindingResult bindingResult, HttpServletRequest request) throws SQLException {

        if(bindingResult.hasErrors()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("올바른 값을 입력하세요 ");
        }

        HttpSession session = request.getSession(false);

        if (session == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("로그인 하세요");
        }else{

            Member loginMember = (Member)session.getAttribute(SessionConst.LOGIN_MEMBER);
            System.out.println(loginMember);
            mySqlBoardService.save(loginMember, saveFormBoard);
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("작성 완료");

        }
    }

    @ResponseBody
    @GetMapping("/{postNo}")
    public ResponseEntity<?> readOne(@PathVariable int postNo) throws SQLException {



        Board findBoard = mySqlBoardService.findByPostNo(postNo);

        if(findBoard.getLock().equals("허용")){
            System.out.println(findBoard.getMemberId());
           // mySqlBoardService.viewUp(findBoard.getMemberId(), postNo);
            return ResponseEntity.ok(findBoard);
        }else {
            // 비허용인 경우 본인 확인 여부 확
            return ResponseEntity.ok("비밀글 입니다");
        }

    }

    @ResponseBody
    @PostMapping("/{postNo}/update")
    public ResponseEntity<?> update(@PathVariable int postNo ,@ModelAttribute("editBoard") EditBoard editBoard, BindingResult bindingResult, HttpServletRequest request) throws SQLException {

        if(bindingResult.hasErrors()){
            log.info("error = {}", bindingResult);
        }

        HttpSession session = request.getSession(false);

        if (session == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("로그인 하세요");
        }else{
            Member loginMember = (Member)session.getAttribute(SessionConst.LOGIN_MEMBER);
            String memberId = editBoard.getMemberId();
            System.out.println(loginMember);
            log.info("editInfo = {}", editBoard);
            if(loginMember.getAuthority() ==1 || loginMember.getMemberId().equals(memberId)){
                mySqlBoardService.update(editBoard);
                return ResponseEntity.ok("수정 완료");
            }else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("관리자 또는 본인만 조회 가능합니다.");
            }

        }

    }

    @ResponseBody
    @PostMapping("/{postNo}/delete")
    public ResponseEntity<?> delete(@ModelAttribute("editBoard") EditBoard editBoard, BindingResult bindingResult ,HttpServletRequest request) throws SQLException {

        if(bindingResult.hasErrors()){
            log.info("error = {}", bindingResult);
        }

        HttpSession session = request.getSession(false);

        if (session == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("로그인 하세요");
        }else{
            Member loginMember = (Member)session.getAttribute(SessionConst.LOGIN_MEMBER);
            String memberId = editBoard.getMemberId();
            System.out.println(loginMember);
            if(loginMember.getAuthority() ==1 || loginMember.getMemberId().equals(memberId)){
                mySqlBoardService.delete(memberId);
                return ResponseEntity.ok("삭제 완료");
            }else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("관리자 또는 본인만 조회 가능합니다.");
            }

        }

    }

    @ResponseBody
    @GetMapping("")
    public ResponseEntity<?> listAll(@ModelAttribute("search") Search search, BindingResult bindingResult,
                                     @ModelAttribute("sort") Sort sort, BindingResult bindingResult1) throws SQLException {

        if(bindingResult.hasErrors() || bindingResult1.hasErrors()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("올바른 값을 입력하세요 ");
        }
        System.out.println(search.getSearchTitle());
        System.out.println(sort.getSort());
        List<FormBoard> allBoard = mySqlBoardService.allList(search, sort);

        if(allBoard == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("게시글이 없습니다");
        }else{
            return ResponseEntity.ok(allBoard);
        }

    }

    @ResponseBody
    @GetMapping("/{postNo}/wish")
    public ResponseEntity<?> wish(@PathVariable int postNo, HttpServletRequest request) throws SQLException {

        HttpSession session = request.getSession(false);
        Member loginMember = (Member)session.getAttribute(SessionConst.LOGIN_MEMBER);

        if (session == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("로그인 하세요");
        }else if(loginMember.getMemberId().equals(mySqlBoardService.findByPostNoConnection(postNo).getMemberId())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("본인 글은 좋아요 할수 없습니다");
        }else{
            mySqlBoardService.updateWish(postNo,loginMember);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("좋아요");
        }




    }


}