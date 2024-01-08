package entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@NoArgsConstructor
@Setter
@Getter
@ToString
@Entity
public class UserHistory {

    @Id
    private String userId;
    private String date;
    private String time;
    private int lastindex;
}
