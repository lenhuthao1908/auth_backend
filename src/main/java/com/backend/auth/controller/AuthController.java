package com.backend.auth.controller;

import com.backend.auth.Repo.IUserRepo;
import com.backend.auth.entity.User;
import com.backend.auth.entity.reponse.ServiceReponse;
import com.backend.auth.service.IAuthService;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "/api")
public class AuthController {
    @Autowired
    private IUserRepo userRepo;
    @Autowired
    private IAuthService iAuthService;

    @GetMapping(value = "/hello")
    public String hello(){
        return "hello";
    }

    //otd register
    record RegisterRequest(
            @JsonProperty("first_name") String firstname,
            @JsonProperty("last_name") String lastname,
            String email,
            String password,
            @JsonProperty("password_confirm") String passwordConfirm,
            String role){}
    record RegisterResponse(Long id, @JsonProperty("first_name") String firstname, @JsonProperty("last_name") String lastname, String email, String role){}

    @PostMapping(value = "/register")
    public RegisterResponse register(@RequestBody RegisterRequest registerRequest){
        ServiceReponse serviceReponse = new ServiceReponse();

        var result = iAuthService.register(
                registerRequest.firstname(),
                registerRequest.lastname(),
                registerRequest.email(),
                registerRequest.password(),
                registerRequest.passwordConfirm(),
                registerRequest.role()
        );
//        if(result==1){
//            serviceReponse.setMessage("register success!");
//        }else{
//            serviceReponse.setMessage("register failed!");
//        }
        return new RegisterResponse(result.getId(), result.getFirstname(), result.getLastname(), result.getEmail(), result.getRole());
    }

    record LoginRequest(String email, String password){}
    record LoginResponse(String token, String role){}

    @PostMapping(value = "/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest, HttpServletResponse response){
        var login = iAuthService.login(loginRequest.email(), loginRequest.password());

        Cookie cookie = new Cookie("refresh_token", login.getRefreshToken().getToken());
        cookie.setMaxAge(3600);
        cookie.setHttpOnly(true);

        response.addCookie(cookie);

        var isUser = userRepo.findById(login.getAccessToken().getUserId());

        return new LoginResponse(login.getAccessToken().getToken(), isUser.get().getRole());
    }

    record UserResponse(Long id, @JsonProperty("first_name") String firstname, @JsonProperty("last_name") String lastname, String email, String role){}

    @GetMapping(value = "/user")
    public UserResponse user(HttpServletRequest request){
        var user = (User) request.getAttribute("user");
        return new UserResponse(user.getId(), user.getFirstname(),user.getLastname(), user.getEmail(), user.getRole());
    }

    record RefreshResponse(String token){}

    @PostMapping(value = "/refresh")
    public  RefreshResponse refresh(@CookieValue("refresh_token") String refreshToken){
        return new RefreshResponse(iAuthService.refreshAccess(refreshToken).getAccessToken().getToken());
    }

    record LogoutResponse(String message){}

    @PostMapping(value = "/logout")
    public LogoutResponse logout(@CookieValue("refresh_token") String refreshToken, HttpServletResponse response){
        iAuthService.logout(refreshToken);

        Cookie cookie = new Cookie("refresh_token", null);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);

        response.addCookie(cookie);

        return new LogoutResponse("success");
    }

    record ForgotRequest(String email){}
    record ForgotResponse(String message){}

    @PostMapping(value = "/forgot")
    public ForgotResponse forgot(@RequestBody ForgotRequest forgotRequest, HttpServletRequest request){
        var originUrl = request.getHeader("Origin");

        iAuthService.forgot(forgotRequest.email, originUrl);


        return new ForgotResponse("success");
    }

    record ResetResponse(String message) {}
    record ResetRequest(String password, @JsonProperty("password_confirm") String passwordConfirm) {}

    @PostMapping(value = "/reset/{token}")
    public ResetResponse reset(@RequestBody ResetRequest request, @PathVariable(value = "token") String token) {
        if (!iAuthService.reset(request.password(), request.passwordConfirm(), token)) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "cannot reset pasword");
        }

        return new ResetResponse("success");
    }
}
