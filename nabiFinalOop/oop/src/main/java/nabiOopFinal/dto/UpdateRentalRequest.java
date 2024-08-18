package nabiOopFinal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRentalRequest {

    private String carName;

    private String carYear;

    private String carModel;

    private String drivenKm;

    private Integer price;
}
