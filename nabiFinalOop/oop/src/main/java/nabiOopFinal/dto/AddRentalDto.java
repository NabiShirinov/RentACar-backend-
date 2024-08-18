package nabiOopFinal.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class AddRentalDto {

    private String carName;

    private String carYear;

    private String carModel;

    private String drivenKm;

    private Integer price;
}
