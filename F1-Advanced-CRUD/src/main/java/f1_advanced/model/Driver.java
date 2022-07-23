package f1_advanced.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.Period;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank
    @Size(min = 2, max = 255)
    private String firstName;
    @NotBlank
    @Size(min = 2, max = 255)
    private String lastName;
    @Past(message = "ERROR! WRONG DATE OF BIRTH!!!!")
    private LocalDate birthDate;
    private String country;
    private String team;
    private Integer numberOfWorldChampionships;
    private Integer currentDriverNumber = null;

    @Transient
    private Integer age;
    //CALCULATE AGE FROM DATE OF BIRTH
    public Integer getAge() {
        return Period.between(this.birthDate, LocalDate.now()).getYears();
    }
    public void setAge(Integer age){
        this.age = age;
    }

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Team teamdriver;


}
