package RunningMate.backend.domain.User.controller;

import RunningMate.backend.domain.User.dto.UserDTO;
import RunningMate.backend.domain.User.entity.User;
import RunningMate.backend.domain.User.service.UserService;
import RunningMate.backend.domain.authorization.SessionUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final SessionUtils sessionUtils;

    @PostMapping("/signUp")
    @Operation(summary = "회원가입", description = "사용자에게 nickname, email, password, weight, height를 받아 회원가입을 진행한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "회원가입 실패")
    })
    public ResponseEntity<?> signUp(@RequestBody UserDTO.SignUpRequest request){ // 추후 response type, cookie or session 추가
        try {
            User user = userService.signUp(request);
            return ResponseEntity.ok("회원가입에 성공하였습니다.");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/logIn")
    @Operation(summary = "로그인", description = "사용자에게 email, password를 받아 로그인을 진행한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "로그인 실패")
    })
    public ResponseEntity<?> login(@RequestBody UserDTO.LoginRequest request, HttpSession session) {
        try {
            User user = userService.logIn(request);
            session.setAttribute("userId", user.getUserId());
            log.info("{} 님 로그인, userId = {}", user.getUserNickname(), user.getUserId());
            return ResponseEntity.ok().body(user.getUserNickname() + "님, 로그인 성공하셨습니다!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("로그인 실패: " + e.getMessage());
        }
    }

    @GetMapping("/profile")
    @Operation(summary = "프로필", description = "사용자의 프로필을 제공한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프로필 조회 성공"),
            @ApiResponse(responseCode = "400", description = "프로필 조회 실패")
    })
    public ResponseEntity<?> profile(HttpSession session){
        try {
            Long userId = sessionUtils.getUserIdFromSession(session);

            return ResponseEntity.ok().body(userService.profile(userId));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/profile/update")
    @Operation(summary = "프로필 업데이트", description = "사용자에게 체중, 신장을 받아 프로필 업데이트를 진행한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "업데이트 성공"),
            @ApiResponse(responseCode = "400", description = "업데이트 실패")
    })
    public ResponseEntity<?> updateProfile(@RequestBody UserDTO.UpdateProfileRequest request, HttpSession session){ // 추후 response type, cookie or session 추가
        try {
            Long userId = sessionUtils.getUserIdFromSession(session);
            User user = userService.updateProfile(request, userId);
            return ResponseEntity.ok().body(user.getUserNickname() + user.getUserWeight() + user.getUserHeight());
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}