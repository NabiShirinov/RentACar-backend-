package nabiOopFinal.service;


import nabiOopFinal.dto.AddRentalDto;
import nabiOopFinal.dto.RentalResponse;
import nabiOopFinal.dto.UpdateRentalRequest;
import nabiOopFinal.exception.NotFoundException;
import nabiOopFinal.model.Rental;
import nabiOopFinal.model.User;
import nabiOopFinal.repository.RentalRepository;
import nabiOopFinal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RentalService {

    private static final Logger logger = LoggerFactory.getLogger(RentalService.class);

    private final RentalRepository rentalRepository;

    private final UserRepository userRepository;


    public void addRental(AddRentalDto dto) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(userDetails.getUsername());
        Optional<User> user = userRepository.findByEmail(userDetails.getUsername());

        if (user.isPresent()) {
            Rental rental = new Rental();
            rental.setCarName(dto.getCarName());
            rental.setCarModel(dto.getCarModel());
            rental.setCarYear(dto.getCarYear());
            rental.setDrivenKm(dto.getDrivenKm());
            rental.setPrice(dto.getPrice());
            rental.setUser(user.get());

            rentalRepository.save(rental);
            logger.info("New Rental Added: {} {}", rental.getCarName(), rental.getCarModel());

        }
    }


    public RentalResponse getRentalsById(Integer id) {
        return rentalRepository.findById(id)
                .map(rental -> new RentalResponse(
                        rental.getId(), // Include id here
                        rental.getCarName(),
                        rental.getCarYear(),
                        rental.getCarModel(),
                        rental.getDrivenKm(),
                        rental.getPrice()
                ))
                .orElseThrow(() -> new RuntimeException("Rental not found"));
    }


    public void deleteRentalById(Integer rentalId) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> userOptional = userRepository.findByEmail(userDetails.getUsername());

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Check if the rental belongs to the logged-in user
            Optional<Rental> rentalOptional = rentalRepository.findById(rentalId);

            if (rentalOptional.isPresent()) {
                Rental rental = rentalOptional.get();

                if (rental.getUser().getId().equals(user.getId())) {
                    // Delete the rental if it belongs to the logged-in user
                    rentalRepository.deleteById(rentalId);
                    logger.info("Rental Deleted: {} {}", rental.getCarName(), rental.getCarModel());

                } else {
                    throw new Error("You do not have permission to delete this rental.");
                }
            } else {
                throw new NotFoundException("Rental not found.");
            }
        } else {
            throw new NotFoundException("User not found.");
        }
    }

    public RentalResponse updateRental(Integer id, UpdateRentalRequest newRental) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> userOptional = userRepository.findByEmail(userDetails.getUsername());

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Check if the rental belongs to the logged-in user
            Optional<Rental> rentalOptional = rentalRepository.findById(id);

            if (rentalOptional.isPresent()) {
                Rental rental = rentalOptional.get();

                if (rental.getUser().getId().equals(user.getId())) {

                    var requestedRental = rentalRepository.findById(id).orElseThrow(() -> new RuntimeException("Rental not found"));
                    requestedRental.setCarName(newRental.getCarName());
                    requestedRental.setCarModel(newRental.getCarModel());
                    requestedRental.setCarYear(newRental.getCarYear());
                    requestedRental.setDrivenKm(newRental.getDrivenKm());
                    requestedRental.setPrice(newRental.getPrice());
                    logger.info("Rental {} {} Updated.", newRental.getCarName(),newRental.getCarModel());
                    rentalRepository.save(requestedRental);
                    return new RentalResponse(rental);

                }else {
                throw new Error("You do not have permission to delete this rental.");
            }
        } else {
            throw new NotFoundException("Rental not found.");
        }
    } else {
        throw new NotFoundException("User not found.");
    }
    }

    public RentalResponse updateRentalPartially(Integer id, UpdateRentalRequest newRental) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> userOptional = userRepository.findByEmail(userDetails.getUsername());

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Check if the rental belongs to the logged-in user
            Optional<Rental> rentalOptional = rentalRepository.findById(id);

            if (rentalOptional.isPresent()) {
                Rental rental = rentalOptional.get();

                if (rental.getUser().getId().equals(user.getId())) {
                    // Apply partial update logic
                    if (newRental.getCarName() != null) {
                        rental.setCarName(newRental.getCarName());
                    }
                    if (newRental.getCarModel() != null) {
                        rental.setCarModel(newRental.getCarModel());
                    }
                    if (newRental.getCarYear() != null) {
                        rental.setCarYear(newRental.getCarYear());
                    }
                    if (newRental.getDrivenKm() != null) {
                        rental.setDrivenKm(newRental.getDrivenKm());
                    }
                    if (newRental.getPrice() != null) {
                        rental.setPrice(newRental.getPrice());
                    }

                    // Save the updated rental to the database
                    rentalRepository.save(rental);

                    // Log the update
                    logger.info("Rental {} Partially Updated.", rental.getId());

                    // Return the updated rental response
                    return new RentalResponse(rental); // You need to create a RentalResponse constructor that takes a Rental object
                } else {
                    throw new Error("You do not have permission to update this rental.");
                }
            } else {
                throw new NotFoundException("Rental not found.");
            }
        } else {
            throw new NotFoundException("User not found.");
        }
    }



    public List<RentalResponse> getMyRentals() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(userDetails.getUsername());

        Optional<User> userOptional = userRepository.findByEmail(userDetails.getUsername());

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Fetch rentals for the user
            List<Rental> rentals = rentalRepository.findByUserEmail(user.getEmail());

            // Map rentals to RentalResponse DTO
            return rentals.stream()
                    .map(rental -> new RentalResponse(
                            rental.getId(),
                            rental.getCarName(),
                            rental.getCarYear(),
                            rental.getCarModel(),
                            rental.getDrivenKm(),
                            rental.getPrice()
                    ))
                    .collect(Collectors.toList());
        }

        // Return an empty list if the user is not found
        return Collections.emptyList();
    }
    public List<RentalResponse> getAllRentals() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(userDetails.getUsername());

        Optional<User> userOptional = userRepository.findByEmail(userDetails.getUsername());

        if (userOptional.isPresent()) {

            List<Rental> allRentals = rentalRepository.findAll();

            // Map rentals to RentalResponse DTO
            return allRentals.stream()
                    .map(rental -> new RentalResponse(
                            rental.getId(),
                            rental.getCarName(),
                            rental.getCarYear(),
                            rental.getCarModel(),
                            rental.getDrivenKm(),
                            rental.getPrice()
                    ))
                    .collect(Collectors.toList());
        }

        // Return an empty list if the user is not found
        return Collections.emptyList();
    }
}
