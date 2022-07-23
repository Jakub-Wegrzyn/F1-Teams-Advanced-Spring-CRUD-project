package f1_advanced.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.bind.annotation.PathVariable;
import f1_advanced.model.Driver;

import java.util.List;

public interface DriverRepository extends CrudRepository<Driver, Long> {
    List<Driver> findAll();
    List<Driver> findBycurrentDriverNumber(int currentDriverNumber);
    List<Driver> findByteam(String team);
    List<Driver> findBycountry(String country);
    List<Driver> findBylastName(String lastName);

    @Query("select c.driverSet from Team as c where c.id = :driver_id")
    List<Driver> findDriversByTeam(@PathVariable Long driver_id);


}
