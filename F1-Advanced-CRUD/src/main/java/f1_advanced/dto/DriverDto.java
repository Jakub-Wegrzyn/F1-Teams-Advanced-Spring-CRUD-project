package f1_advanced.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverDto extends RepresentationModel<DriverDto> {

    private Long id;
    @NotBlank(message = "ERROR! You are posting empty value")
    @Size(min = 2, max = 255)
    private String firstName;
    @NotBlank(message = "ERROR! You are posting empty value")
    @Size(min = 2, max = 255)
    private String lastName;
    @Past(message = "ERROR! WRONG DATE OF BIRTH!!!!")
    private LocalDate birthDate;
    private String country;
    private int age;
    private String team;
    private Integer numberOfWorldChampionships;
    private Integer currentDriverNumber;

}
