package nabiOopFinal.repository;


import nabiOopFinal.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentalRepository extends JpaRepository<Rental, Integer> {

    List<Rental> findByUserEmail(String userEmail);

    void deleteByUserId(Integer userId);
}

