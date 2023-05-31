package homepage.controller;

import homepage.domain.Email;
import homepage.domain.Member;
import homepage.emplement.EmailServiceImpl;
import homepage.service.EmailService;
import homepage.service.MySqlMemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;


    public EmailController(EmailServiceImpl emailService) {
        this.emailService = emailService;
    }

    @ResponseBody
    @GetMapping("/emailConfirm")
    public String emailConfirm(@RequestParam String inputEmail) throws Exception {
        System.out.println("받은 이메일 "+ inputEmail);
        emailService.sendSimpleMessage(inputEmail);
        return "email 발송";
    }

    @ResponseBody
    @PostMapping("/emailConfirm/check")
    public ResponseEntity<?> emailConfirmCheck(@ModelAttribute("member") Member member, String email, String inputEmailCode) throws Exception {

        String check = emailService.smsCHeck(email,inputEmailCode);

        if(check.equals("확인")){
                emailService.update(email);
            return ResponseEntity.status(HttpStatus.OK).body("완료");
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("불일치");
        }
    }
}
