package nabiOopFinal.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nabiOopFinal.exception.NotFoundException;
import nabiOopFinal.model.User;
import nabiOopFinal.repository.RentalRepository;
import nabiOopFinal.repository.TokenRepository;
import nabiOopFinal.repository.UserRepository;
import nabiOopFinal.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final RentalRepository rentalRepository; // Add RentalRepository here
    private final JwtService jwtService;

    @Transactional
    public void deleteTokensByUserId(Integer userId) {
        tokenRepository.deleteByUserId(userId);
    }


    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    @Transactional
    public void deleteUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<User> userOptional = userRepository.findByEmail(userDetails.getUsername());

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Delete rentals associated with the user
            rentalRepository.deleteByUserId(user.getId());

            // Delete tokens associated with the user
            deleteTokensByUserId(user.getId());

            // Delete the user
            userRepository.delete(user);

            logger.info("User Deleted: " + user.getEmail());
            // You can add logging or any other post-deletion logic here
        } else {
            throw new NotFoundException("User not found.");
            // You may want to throw an exception or handle the case where the user is not found
        }
    }
}
