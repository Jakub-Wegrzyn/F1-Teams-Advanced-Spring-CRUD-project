package f1_advanced.dto.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import f1_advanced.dto.TeamDetailsDto;
import f1_advanced.dto.TeamDto;
import f1_advanced.model.Team;

@Component
@RequiredArgsConstructor
public class TeamDtoMapper {
    private final ModelMapper modelMapper;

    public TeamDetailsDto convertToDtoDetails(Team e){

        return modelMapper.map(e, TeamDetailsDto.class);
    }

    public TeamDto convertToDto(Team e){

        return modelMapper.map(e, TeamDto.class);
    }

    private Team convertToEntity(TeamDto e){

        return modelMapper.map(e, Team.class);
    }
}
