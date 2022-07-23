package f1_advanced.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamDetailsDto extends RepresentationModel<TeamDetailsDto> {
    private Long id;
    private String name;
    private Set<DriverDto> driverSet;
}
