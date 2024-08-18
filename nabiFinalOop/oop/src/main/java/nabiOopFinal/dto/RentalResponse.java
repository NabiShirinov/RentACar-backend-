package nabiOopFinal.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nabiOopFinal.model.Rental;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RentalResponse {
    private Integer id;
    private String carName;

    private String carYear;

    private String carModel;

    private String drivenKm;

    private Integer price;

    public RentalResponse(Rental rental) {

        this.id = rental.getId();
        this.carName = rental.getCarName();
        this.carYear = rental.getCarYear();
        this.carModel = rental.getCarModel();
        this.drivenKm = rental.getDrivenKm();
        this.price = rental.getPrice();
    }
}
