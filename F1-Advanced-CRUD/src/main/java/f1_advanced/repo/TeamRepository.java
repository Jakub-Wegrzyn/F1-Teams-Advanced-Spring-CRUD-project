package f1_advanced.repo;

import f1_advanced.model.Team;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends CrudRepository<Team, Long>{
    List<Team> findAll();

    @Query("from Team as c left join fetch c.driverSet where c.id =:teamId")
    Optional<Team> getTeamDetailsById(@Param("teamId") Long teamId);



}
