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
@SequenceGenerator(name = "item_sequence", sequenceName = "item_sequence", allocationSize = 1 )
public class Item {
//    public Item(Long itemId, String name, String category, String standardFee) {
//        this.itemId = itemId;
//        this.name = name;
//        this.category = category;
//        this.standardFee = standardFee;
//    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_sequence")
    private Long itemId;
    private String name;
    private String category;
    private String standardFee;
}
