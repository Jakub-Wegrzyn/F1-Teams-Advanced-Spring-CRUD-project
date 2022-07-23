package f1_advanced.rest;

import f1_advanced.dto.mapper.GlobalMapper;
import f1_advanced.dto.mapper.TeamDtoMapper;
import f1_advanced.model.Team;
import f1_advanced.repo.DriverRepository;
import f1_advanced.repo.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import f1_advanced.Team_categorise;
import f1_advanced.dto.DriverDto;
import f1_advanced.dto.TeamDetailsDto;
import f1_advanced.dto.TeamDto;
import f1_advanced.model.Driver;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController //metody będą wywoływane przy odpowiednich żądaniach HTTP
@RequestMapping("/api/formula1/teams")
@RequiredArgsConstructor
public class TeamController {

    @Autowired
    private final ModelMapper modelMapper;
    @Autowired
    private DriverRepository driverRepository;

    private final TeamDtoMapper teamDtoMapper;
    private final TeamRepository teamRepository;

    private final GlobalMapper globalMapper;

    //GET ALL Teams
    @GetMapping
    public ResponseEntity<CollectionModel<TeamDto>> getTeams(){
        List<Team> allTeamsEntities = teamRepository.findAll();
        List<TeamDto> result = allTeamsEntities.stream()
                .map(teamDtoMapper::convertToDto)
                .collect(Collectors.toList());
        for(TeamDto dto : result){
            dto.add(createTeamSelfLink(dto.getId()));
            dto.add(createEmployeesSelfLinkSelfLink(dto.getId()));
        }
        Link linkSelf = linkTo(methodOn(TeamController.class).getTeams()).withSelfRel();
        CollectionModel<TeamDto> res = CollectionModel.of(result, linkSelf);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/{teamId}") //pobranie wybranego obiektu wraz z powiazanymi obiektami
    public ResponseEntity<TeamDetailsDto> getTeamById(@PathVariable Long teamId) {
        Optional<Team> team = teamRepository.findById(teamId);
        if(team.isPresent()) {
            TeamDetailsDto teamDetailsDto = teamDtoMapper.convertToDtoDetails(team.get());
            Link linkSelf = createTeamSelfLink(teamId);
            teamDetailsDto.add(linkSelf);
            return new ResponseEntity<>(teamDetailsDto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{teamId}/drivers")//zwracanie listy kierowców dla danego teamu
    public ResponseEntity<CollectionModel<DriverDto>> getDriversByTeamId(@PathVariable Long teamId) {
        List<Driver> allDrivers = driverRepository.findDriversByTeam(teamId);
        List<DriverDto> result = allDrivers.stream()
                .map(globalMapper::convertToDto)
                .collect(Collectors.toList());
        for(DriverDto dto : result){
            dto.add(createEmployeesSelfLinkSelfLink(dto.getId()));
        }
        Link linkSelf = linkTo(methodOn(TeamController.class).getTeams()).withSelfRel();
        CollectionModel<DriverDto> res = CollectionModel.of(result, linkSelf);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/{teamToPush_Id}/drivers")
    //
    public ResponseEntity<Collection<DriverDto>> postDriverToTeam(@Valid @PathVariable Long teamToPush_Id, @RequestBody DriverDto imputdriver) {
        List<Driver> finddriver = driverRepository.findBylastName(imputdriver.getLastName()); //SEARCH BY LAST NAME I KNOW THAT THIS IS NOT PERFECT SEARCH
        Optional<Team> team = teamRepository.findById(teamToPush_Id);
        if(team.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        else{
            Driver entity;
            DriverDto dto;
            if(!finddriver.isEmpty()){
                dto = globalMapper.convertToDto(finddriver.get(0));
                entity = globalMapper.convertToEntity(dto);
                entity.setTeam(team.get().getName());
                entity.setTeamdriver(team.get());
                team.get().getDriverSet().add(entity);
                driverRepository.save(entity);
                teamRepository.save(team.get());
            }
            else {
                entity = globalMapper.convertToEntity(imputdriver);
                entity.setTeam(team.get().getName());
                entity.setTeamdriver(team.get());
                team.get().getDriverSet().add(entity);
                driverRepository.save(entity);
                teamRepository.save(team.get());
            }
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }
    @PostMapping("/postrelationships")
    public ResponseEntity postTheRelationships(){
        Team_categorise team_categorise = new Team_categorise(teamRepository, globalMapper);
        team_categorise.cat_and_to_the_team(teamRepository,driverRepository);

        return new ResponseEntity<>( HttpStatus.OK);
    }

    @DeleteMapping("deleteall")
    public ResponseEntity deleteallAssociations() {
        List<Driver> allDrivers = driverRepository.findAll();
        for(int i = 0 ; i<allDrivers.size() ; i++){
            allDrivers.get(i).setTeamdriver(null);
            driverRepository.save(allDrivers.get(i));
        }

        return new ResponseEntity<>( HttpStatus.OK);
    }

    private Link createTeamSelfLink(Long teamId){
        Link linkSelf = linkTo(methodOn(TeamController.class).getTeamById(teamId)).withSelfRel();
        return linkSelf;
    }
    private Link createEmployeesSelfLinkSelfLink(Long teamId){
        Link linkSelf = linkTo(methodOn(TeamController.class).getDriversByTeamId(teamId)).withSelfRel();
        return linkSelf;
    }
}
