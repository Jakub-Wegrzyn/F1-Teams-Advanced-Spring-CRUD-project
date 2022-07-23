package f1_advanced.model;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "teamdriver")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Driver> driverSet;
}
