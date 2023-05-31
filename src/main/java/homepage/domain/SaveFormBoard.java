package homepage.domain;

import lombok.Data;

import java.sql.Date;

@Data
public class SaveFormBoard {

    private String title;
    private String body;
    private String memberId;
    private Date postDate;
    private int view;

}
