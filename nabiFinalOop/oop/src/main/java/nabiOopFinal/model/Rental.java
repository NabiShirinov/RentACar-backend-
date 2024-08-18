package nabiOopFinal.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor@NoArgsConstructor
@Entity
@Builder
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String carName;

    private String carYear;

    private String carModel;


    private String drivenKm;

    private Integer price;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;  // Many rentals belong to one user
}
