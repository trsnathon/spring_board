package homepage.domain;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

@Data
public class Member {

    @NotNull
    private String memberId;

    @NotNull
    private String password;

    private int number;

    private int authority;

  //  private int wishCheck;

    public Member() {
    }


    public Member(String memberId, String password) {
        this.memberId = memberId;
        this.password = password;
    }
}
