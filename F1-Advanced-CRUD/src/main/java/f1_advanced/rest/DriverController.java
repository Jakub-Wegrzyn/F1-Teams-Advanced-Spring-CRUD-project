package f1_advanced.rest;

import f1_advanced.Team_categorise;
import f1_advanced.dto.DriverDto;
import f1_advanced.dto.mapper.GlobalMapper;
import f1_advanced.repo.DriverRepository;
import f1_advanced.repo.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.modelmapper.ModelMapper;
import f1_advanced.model.Driver;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController //metody będą wywoływane przy odpowiednich żądaniach HTTP
@RequestMapping("/api/driver")
public class DriverController {
    @Autowired
    private ModelMapper modelMapper;
    private GlobalMapper globalMapper;
    @Autowired
    public DriverRepository driverRepository;
    public TeamRepository teamRepository;

    List<DriverDto> driverListDto = new ArrayList<>();
    Team_categorise team_categorise;

    public DriverController(DriverRepository driverRepository, ModelMapper modelMapper, TeamRepository teamRepository, GlobalMapper globalMapper){
        this.driverRepository = driverRepository;
        this.modelMapper = modelMapper;
        this.teamRepository = teamRepository;
        this.globalMapper = globalMapper;
    }

    //GET ALL DRIVERS
    @GetMapping("/getalldrivers")
    public ResponseEntity<Collection<DriverDto>> getAllDrivers(){
        List<Driver> allDrivers = driverRepository.findAll();
        List<DriverDto> result = allDrivers.stream()
                .map(globalMapper::convertToDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    //GET DRIVER/DRIVERS BY CURRENT DRIVER NUMBER
    @GetMapping("/drivernumber/")
    public ResponseEntity<Object>
    getDriverById(@RequestParam (value = "value") List<Integer> number) {
        List<Driver> driver =  null;
        List<Driver> driverArray = new ArrayList<>();
        for(int i = 0; i<number.size(); i++){
            driver = driverRepository.findBycurrentDriverNumber(number.get(i));
            driverArray.addAll(driver);
        }

        if(!driverArray.isEmpty()) {
            List<DriverDto> driversdto = globalMapper.convertToDto(driverArray);
            return new ResponseEntity<Object>(driversdto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    //GET DRIVER/DRIVERS BY TEAM
    @GetMapping("/team/")
    public ResponseEntity<Object> getDriverByTeam(@RequestParam(value = "value") List<String> team) {
        List<Driver> driver =  null;
        List<Driver> driverArray = new ArrayList<>();
        for(int i = 0; i<team.size(); i++){
            driver = driverRepository.findByteam(team.get(i));
            driverArray.addAll(driver);
        }
        if(!driverArray.isEmpty()){
            List<DriverDto> driversdto = globalMapper.convertToDto(driverArray);

            return new ResponseEntity<Object>(driversdto, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(null,
                    HttpStatus.NOT_FOUND);
        }
    }

    //GET DRIVER/DRIVERS BY COUNTRY
    @GetMapping("/country/")
    public ResponseEntity<Object> getDriverBycountry(@RequestParam(value="value") List<String> country) {
        List<Driver> driver = null;
        List<Driver> driverArray = new ArrayList<>();
        for(int i =0; i<country.size(); i++){
            driver =  driverRepository.findBycountry(country.get(i));
            driverArray.addAll(driver);
        }
        if(!driverArray.isEmpty()){
            List<DriverDto> driversdto = globalMapper.convertToDto(driverArray);
            return new ResponseEntity<Object>(driversdto, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        }

    //POST ONE DRIVER (DOESN'T MATTER IF DRIVER ALREADY EXISTS - WILL BE ADDED WITH DIFFERENT ID)
    @PostMapping("/postonedriver")
    public ResponseEntity saveNewDriver(@Valid @RequestBody DriverDto imputdriver){
        driverListDto.removeAll(driverListDto);
        Driver entity = globalMapper.convertToEntity(imputdriver);
        driverRepository.save(entity);
         ToDatabase();
        return new ResponseEntity(null, HttpStatus.CREATED);
    }

    //POST DRIVERS ARRAY (DOESN'T MATTER IF DRIVER ALREADY EXISTS - WILL BE ADDED WITH DIFFERENT ID)
    @PostMapping("/postalldrivers")
    public ResponseEntity saveNewDrivers(@Valid @RequestBody DriverDto[] imputdrivers){

        driverListDto.removeAll(driverListDto);
        for(int i = 0 ; i<imputdrivers.length; i++){
            driverListDto.add(imputdrivers[i]);
        }
        Driver entity = null;
        HttpHeaders headers = new HttpHeaders();

        for(int i = 0 ; i<driverListDto.size();i++){
            entity = globalMapper.convertToEntity(driverListDto.get(i));
            driverRepository.save(entity);
        }
        ToDatabase();
        return new ResponseEntity(headers ,HttpStatus.CREATED);

    }

    //!!!!! IMPORTANT !!!!!!!!!!!!!  PUT ONE DRIVER (IF EXIST WITH IMPLEMENTED ID, WILL BE UPDATED. IF NOT WILL BE ADDED WITH THE NEW (AUTO GENERATED) ID
    @PutMapping("/updatedriver/{id}")
    public ResponseEntity updateOrInsertDriver(@Valid @PathVariable Long id, @RequestBody DriverDto driverDto){
        Optional<Driver> currentDriver = driverRepository.findById(id);
        if(currentDriver.isPresent()){
            driverDto.setId(id);
            Driver entity = globalMapper.convertToEntity(driverDto);
            driverRepository.save(entity);
            ToDatabase();
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }else{
            driverDto.setId(id);
            Driver entity = globalMapper.convertToEntity(driverDto);
            driverRepository.save(entity);
            ToDatabase();
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
    }

    //PUT DRIVER ARRAY (IF SINGLE RECORD EXIST WILL BE UPDATED. IF NOT WILL BE ADDED WITH THE NEW (AUTO GENERATED) ID
    @PutMapping("/putalldrivers")
    public ResponseEntity updateOrInsertDrivers(@Valid @RequestBody DriverDto[] imputdrivers){
        driverListDto.removeAll(driverListDto);
        List<Driver> currentDriver = null;
        ArrayList<Driver> existdriver = new ArrayList<>();
        Driver entity;
        for(int i = 0 ; i<imputdrivers.length; i++){
            driverListDto.add(imputdrivers[i]);
        }
        for(int i = 0 ; i<driverListDto.size(); i++){
            currentDriver = driverRepository.findBylastName(driverListDto.get(i).getLastName());
            existdriver.addAll(currentDriver);
            if(currentDriver.isEmpty()){
                entity = globalMapper.convertToEntity(driverListDto.get(i));
                driverRepository.save(entity);
                currentDriver.removeAll(currentDriver);
            }
            else{
                continue;
            }
        }
        if(existdriver.size()>0){
            for (int i = 0; i<existdriver.size(); i++){
                driverRepository.save(existdriver.get(i));
                currentDriver.removeAll(currentDriver);
            }
        }else {
            ToDatabase();
            return new ResponseEntity(HttpStatus.CREATED);
        }
        ToDatabase();
        return new ResponseEntity(HttpStatus.NO_CONTENT);

    }

    //DELETE DRIVER/DRIVERS BY LAST NAME
    @DeleteMapping("/delete/")
    public ResponseEntity deleteDriver(@RequestParam(value = "value") List<String> lastName){
        List<Driver> driver = null;
        List<Driver> driverArray = new ArrayList<>();
        for(int i = 0; i< lastName.size(); i++){
            driver =  driverRepository.findBylastName(lastName.get(i));
            driverArray.addAll(driver);
        }
        for(int i = 0; i<driverArray.size(); i++){
            driverRepository.deleteById(driverArray.get(i).getId());
        }

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    //DELETE ALL DRIVERS
    @DeleteMapping("deleteall")
    public ResponseEntity deleteAllDrivers(){
        driverRepository.deleteAll();
        return new ResponseEntity<>( HttpStatus.OK);
    }

    public void ToDatabase(){
        team_categorise = new Team_categorise(teamRepository, globalMapper);
        team_categorise.cat_and_to_the_team(teamRepository, driverRepository);
    }
}
