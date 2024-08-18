package nabiOopFinal.controller;

import lombok.RequiredArgsConstructor;
import nabiOopFinal.dto.ApiResponse;
import nabiOopFinal.exception.ForbiddenException;
import nabiOopFinal.exception.NotFoundException;
import nabiOopFinal.security.JwtService;
import nabiOopFinal.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<Object>> deleteUser() {
        try {
            userService.deleteUser();
            return ResponseEntity.ok().body(new ApiResponse<>("User deleted successfully", "DELETED", null));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), "NOT_FOUND", null));
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(e.getMessage(), "FORBIDDEN", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An error occurred while processing the request", "ERROR", null));
        }
    }

}
