package nabiOopFinal.controller;


import nabiOopFinal.dto.AddRentalDto;
import nabiOopFinal.dto.ApiResponse;
import nabiOopFinal.dto.RentalResponse;
import nabiOopFinal.dto.UpdateRentalRequest;
import nabiOopFinal.exception.AuthenticationException;
import nabiOopFinal.exception.NotFoundException;
import nabiOopFinal.model.Rental;
import nabiOopFinal.nabiRentalApp;
import nabiOopFinal.repository.UserRepository;
import nabiOopFinal.security.JwtService;
import nabiOopFinal.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rental")
public class RentalController {

    private final RentalService rentalService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Object>> addRental(@RequestBody AddRentalDto dto) {
        try {
            rentalService.addRental(dto);
            return ResponseEntity.ok().body(new ApiResponse<>("Rental added successfully.", "ADDED", null));
        } catch (Exception e) {
            String errorMessage = "Failed to add rental. " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(errorMessage, "ERROR", null));
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ApiResponse<RentalResponse>> getRentalsById(@PathVariable("id") Integer id) {
        try {
            RentalResponse rentalResponse = rentalService.getRentalsById(id);
            ApiResponse<RentalResponse> apiResponse = new ApiResponse<>();
            apiResponse.setMessage("Rental retrieved successfully.");
            apiResponse.setCode("RETRIEVED");
            apiResponse.setData(rentalResponse);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (Exception e) {
            String errorMessage = "Failed to retrieve rental. " + e.getMessage();
            ApiResponse<Object> errorResponse = new ApiResponse<>();
            errorResponse.setMessage(errorMessage);
            errorResponse.setCode("ERROR");
            errorResponse.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to retrieve rental. " + e.getMessage(), "ERROR", null));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteRentalById(@PathVariable("id") Integer id) {
        try {
            rentalService.deleteRentalById(id);
            return ResponseEntity.ok().body(new ApiResponse<>("Rental deleted successfully", "DELETED", null));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), "NOT_FOUND", null));
        } catch (Error e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(e.getMessage(), "FORBIDDEN", null));
        }
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<RentalResponse>> updateRental(@PathVariable("id") Integer id, @RequestBody UpdateRentalRequest newRental) {
        try {
            RentalResponse updatedRental = rentalService.updateRental(id, newRental);
            ApiResponse<RentalResponse> apiResponse = new ApiResponse<>();
            apiResponse.setMessage("Rental updated successfully");
            apiResponse.setCode("UPDATED");
            apiResponse.setData(updatedRental);
            return ResponseEntity.ok().body(apiResponse);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), "NOT_FOUND", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to update rental. " + e.getMessage(), "ERROR", null));
        }
    }



    @PatchMapping("/partialUpdate/{id}")
    public ResponseEntity<ApiResponse<RentalResponse>> updateRentalPartially(@PathVariable("id") Integer id, @RequestBody UpdateRentalRequest newRental) {
        try {
            RentalResponse updatedRental = rentalService.updateRentalPartially(id, newRental);
            ApiResponse<RentalResponse> apiResponse = new ApiResponse<>();
            apiResponse.setMessage("Rental updated partially successfully");
            apiResponse.setCode("PARTIAL_UPDATED");
            apiResponse.setData(updatedRental);
            return ResponseEntity.ok().body(apiResponse);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), "NOT_FOUND", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to partially update rental. " + e.getMessage(), "ERROR", null));
        }
    }


    @GetMapping("/myRentals")
    public ResponseEntity<ApiResponse<List<RentalResponse>>> getMyRentals() {
        try {
            List<RentalResponse> myRentals = rentalService.getMyRentals();
            ApiResponse<List<RentalResponse>> apiResponse = new ApiResponse<>();
            apiResponse.setMessage("My rentals retrieved successfully");
            apiResponse.setCode("RETRIEVED");
            apiResponse.setData(myRentals);
            return ResponseEntity.ok().body(apiResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to retrieve my rentals. " + e.getMessage(), "ERROR", null));
        }
    }


    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<RentalResponse>>> getAllRentals() {
        try {
            List<RentalResponse> allRentals = rentalService.getAllRentals();
            ApiResponse<List<RentalResponse>> apiResponse = new ApiResponse<>();
            apiResponse.setMessage("All rentals retrieved successfully");
            apiResponse.setCode("RETRIEVED");
            apiResponse.setData(allRentals);
            return ResponseEntity.ok().body(apiResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to retrieve all rentals. " + e.getMessage(), "ERROR", null));
        }
    }

}
