package homepage.service;

import homepage.domain.Email;

public interface EmailService {

    String sendSimpleMessage(String to)throws Exception;
    String smsCHeck(String email, String inputEmailCode) throws Exception;
    void save(String email, String epw) throws Exception;
    void update(String email) throws Exception;
    void delete(String email) throws Exception;
    int viewEmailCheck(String email) throws Exception;
}
