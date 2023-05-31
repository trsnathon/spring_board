package homepage.emplement;

import homepage.domain.Email;
import homepage.repository.MysqlEmailRepository;
import homepage.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    private static Map <String,String> numberStore = new HashMap<>();

    public EmailServiceImpl(JavaMailSender emailSender, MysqlEmailRepository mysqlEmailRepository) {

        this.emailSender = emailSender;
        this.mysqlEmailRepository = mysqlEmailRepository;
    }

    private JavaMailSender emailSender;
    private MysqlEmailRepository mysqlEmailRepository;

    private MimeMessage createMessage(String to, String ePw) throws Exception{

        System.out.println("보내는 대상 : "+ to);
        System.out.println("인증 번호 : "+ePw);
        MimeMessage  message = emailSender.createMimeMessage();

        message.addRecipients(MimeMessage.RecipientType.TO, to);//보내는 대상
        message.setSubject("이메일 인증 테스트");//제목

        String msgg="";
        msgg+= "<div style='margin:20px;'>";
        msgg+= "<h1> 안녕하세요 회원가입을 환영합니다 </h1>";
        msgg+= "<br>";
        msgg+= "<p>아래 코드를 복사해 입력해주세요<p>";
        msgg+= "<br>";
        msgg+= "<p>감사합니다.<p>";
        msgg+= "<br>";
        msgg+= "<div align='center' style='border:1px solid black; font-family:verdana';>";
        msgg+= "<h3 style='color:blue;'>회원가입 인증 코드입니다.</h3>";
        msgg+= "<div style='font-size:130%'>";
        msgg+= "CODE : <strong>";
        msgg+= ePw+"</strong><div><br/> ";
        msgg+= "</div>";
        message.setText(msgg, "utf-8", "html");//내용
        message.setFrom(new InternetAddress("springtest","lee"));//보내는 사람
        return message;
    }

    public String createKey() {
        StringBuffer key = new StringBuffer();

        int i= 0;
        Random random = new Random();
        while (i<5) {
            key.append(random.nextInt(10));
            i++;
        }
        return key.toString();
    }

    @Override
    public String sendSimpleMessage(String to)throws Exception {
        String ePw = createKey();
        MimeMessage message = createMessage(to,ePw);
        try{//예외처리
            emailSender.send(message);
            mysqlEmailRepository.save(to,ePw);
        }catch(MailException es){
            es.printStackTrace();
            throw new IllegalArgumentException();
        }
        return ePw;
    }


    @Override
    public String smsCHeck(String email, String inputEmailCode) throws SQLException {

        int emailCheck = mysqlEmailRepository.viewSendCode(email);
        System.out.println(" view send code " + email);
        if(inputEmailCode == inputEmailCode){
            return "확인";
        }else {
            return "오류";
        }

    }

    @Override
    public void save(String email, String epw) throws SQLException {
        mysqlEmailRepository.save(email,epw);
    }
    @Override
    public void update(String email) throws SQLException {
        mysqlEmailRepository.update(email);
    }
    @Override
    public void delete(String email) throws SQLException {
        mysqlEmailRepository.delete(email);
    }
    @Override
    public int viewEmailCheck(String email) throws SQLException {
        return mysqlEmailRepository.viewEmailCheck(email);
    }
}