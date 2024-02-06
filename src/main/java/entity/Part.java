package entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@Entity
@SequenceGenerator(name = "part_sequence", sequenceName = "part_sequence", allocationSize = 1 )
public class Part {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "part_sequence")
    private Long partId;
    private String name;
    private String category;
    private String standardFee;
}
