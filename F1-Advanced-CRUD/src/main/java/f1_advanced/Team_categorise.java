package f1_advanced;

import f1_advanced.dto.DriverDto;
import f1_advanced.dto.mapper.GlobalMapper;
import f1_advanced.model.Team;
import f1_advanced.repo.DriverRepository;
import f1_advanced.repo.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import f1_advanced.model.Driver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class Team_categorise {
    private final TeamRepository teamRepository;
    private final GlobalMapper globalMapper;
    public List<DriverDto> driverListDto = new ArrayList<>();
    public List<String> arrayteamswithduplicates = new ArrayList();
    public List<String> arrayteam = new ArrayList<>();
    public Team team;
    public List<Team> teamList = new ArrayList<>();
    public List<Driver> driverListwithduplicates = new ArrayList<>();
    public List<Driver> driverList = new ArrayList<>();
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private ModelMapper modelMapper;

    public void cat_and_to_the_team(TeamRepository teamRepository, DriverRepository driverRepository) {


        List<Driver> allDrivers = driverRepository.findAll();
        List<DriverDto> result = allDrivers.stream()
                .map(globalMapper::convertToDto)
                .collect(Collectors.toList());

        driverRepository.deleteAll();

        teamRepository.deleteAll();

        arrayteamswithduplicates.clear();


        for (int i = 0; i < result.size(); i++) {
            arrayteamswithduplicates.add(result.get(i).getTeam());
        }

        arrayteam.clear();

        for (String team : arrayteamswithduplicates) {
            if (!arrayteam.contains(team)) {
                arrayteam.add(team);
            }
        }

        teamList.clear();
        for (int i = 0; i < arrayteam.size(); i++) {
            team = Team.builder().name(arrayteam.get(i)).driverSet(new HashSet<>()).build();
            teamList.add(team);
        }

        driverListwithduplicates.clear();
        for (int i = 0; i < result.size(); i++) {
            Driver x = globalMapper.convertToEntity(result.get(i));
            driverListwithduplicates.add(x);
        }


        driverList.clear();
        for (Driver driver : driverListwithduplicates) {
            if (!driverList.contains(driver)) {
                driverList.add(driver);
            }
        }

        List<Integer> added_elements = new ArrayList<>();
        List<Integer> all_elements = new ArrayList<>();
        List<Integer> jj = new ArrayList<>();

        for (int i = 0; i < driverList.size(); i++) {
            String gottenteamfromdriver = driverList.get(i).getTeam();
            all_elements.add(i);
            int j = 0;
            for (j = 0; j < teamList.size(); j++) {
                String teamname = teamList.get(j).getName();
                if (teamname != null) {
                    if (teamname.equals(gottenteamfromdriver)) {
                        added_elements.add(i);
                        driverList.get(i).setTeamdriver(teamList.get(j));
                        teamList.get(j).getDriverSet().add(driverList.get(i));
                        break;
                    } else {
                        continue;
                    }

                } else if (teamname == null) {
                    jj.add(j);
                    continue;
                }
            }
        }
        all_elements.removeAll(added_elements);


        for (int k = 0; k < all_elements.size(); k++) {
            driverList.get(all_elements.get(k)).setTeamdriver(teamList.get(jj.get(0)));
            teamList.get((jj.get(0))).getDriverSet().add(driverList.get(all_elements.get(k)));
        }

        for (int i = 0; i < teamList.size(); i++) {
            teamRepository.saveAll(Arrays.asList(teamList.get(i)));
        }

        for (int i = 0; i < driverList.size(); i++) {
            driverRepository.saveAll(Arrays.asList(driverList.get(i)));
        }
    }
}
