package f1_advanced.dto.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.springframework.stereotype.Component;
import f1_advanced.dto.DriverDto;
import f1_advanced.model.Driver;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GlobalMapper {

    private final ModelMapper modelMapper;

    public DriverDto convertToDto(Driver driver){

        return modelMapper.map(driver, DriverDto.class);
    }

    public List<DriverDto> convertToDto(List<Driver> driver){
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);
        return driver
                .stream()
                .map(eachdriver -> modelMapper.map(eachdriver, DriverDto.class))
                .collect(Collectors.toList());
    }

    public Driver convertToEntity(DriverDto driverdto){

        return modelMapper.map(driverdto, Driver.class);
    }
}
