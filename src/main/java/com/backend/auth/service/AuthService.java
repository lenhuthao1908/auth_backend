package com.backend.auth.service;

import com.backend.auth.Repo.IPasswordRecoveryRepo;
import com.backend.auth.Repo.ITokenRepo;
import com.backend.auth.Repo.IUserRepo;
import com.backend.auth.entity.Jwt;
import com.backend.auth.entity.Login;
import com.backend.auth.entity.User;
import com.backend.auth.entity.reponse.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;


@Service
public class AuthService implements  IAuthService{
    @Autowired
    private IUserRepo iUserRepo;

    @Autowired
    private ITokenRepo iTokenRepo;

    @Autowired
    private IPasswordRecoveryRepo iPasswordRecoveryRepo;

    @Autowired
    private IMailService iMailService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    private final String accessTokenSecret;
    private final String refreshTokenSecret;

    public AuthService(@Value("${application.security.access-token-secret") String accessTokenSecret,
                       @Value("${application.security.refresh-token-secret")String refreshTokenSecret) {
        this.accessTokenSecret = accessTokenSecret;
        this.refreshTokenSecret = refreshTokenSecret;
    }


    public User register(String firstName, String lastName, String email, String password, String passwordConfirm, String role){
        if (!Objects.equals(password,passwordConfirm)){
            throw new PasswordDoNotMatchError();
        }
        User user;
        try{
            user =  iUserRepo.save(User.of(firstName, lastName, email, passwordEncoder.encode(password), role));

        }catch (DbActionExecutionException exception) {
            throw new EmailAlreadyExistsError();
        }
        return user;
    }


    public Login login(String email, String password){
        var user = iUserRepo.findByEmail(email);
        if(!passwordEncoder.matches(password, user.getPassword()))
            throw new InvalidCredentialsError();

        var login = Login.of(
                user.getId(),
                accessTokenSecret,
                refreshTokenSecret,
                user.getRole()
        );
        var refreshJwt = login.getRefreshToken();

        iTokenRepo.addToken(refreshJwt.getToken(), refreshJwt.getIssueAt(), refreshJwt.getExpiration(), user.getId());
        return login;
    }

    public Login refreshAccess(String refreshToken) {
        var refreshJwt = Jwt.from(refreshToken, refreshTokenSecret);

        iUserRepo.findByIdAndTokensRefreshTokenAndTokensExpiredAtGreaterThan(refreshJwt.getUserId(), refreshJwt.getToken(), refreshJwt.getExpiration())
                .orElseThrow(UnauthenticatedError::new);

        return Login.of(refreshJwt.getUserId(), accessTokenSecret, refreshJwt, refreshJwt.getRole());
    }

    public User getUserFromToken(String token) {
        Jwt jwt = Jwt.from(token, accessTokenSecret);
        Long userId = jwt.getUserId();
        Optional<User> optionalUser = iUserRepo.findById(userId);
        if(optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new UserNotFoundError();
        }
    }


    public Boolean logout(String refreshToken) {
        var refreshJwt = Jwt.from(refreshToken, refreshTokenSecret);
        Optional<User> userOpt = iUserRepo.findByIdAndTokensRefreshTokenAndTokensExpiredAtGreaterThan(refreshJwt.getUserId(), refreshJwt.getToken(), refreshJwt.getExpiration());
        var istrue = iTokenRepo.isFindToken(refreshJwt.getToken());
//        var result = iTokenRepo.removeToken(refreshJwt.getToken());
        if (istrue==true){
            iTokenRepo.removeToken(refreshJwt.getToken());
//            User user = userOpt.get();
//            iUserRepo.save(user);
            return true;
        }else {
            return false;
        }
    }

//    public Boolean logout(String refreshToken) {
//        Jwt refreshJwt = Jwt.from(refreshToken, refreshTokenSecret);
//        Optional<User> userOpt = iUserRepo.findByIdAndTokensRefreshTokenAndTokensExpiredAtGreaterThan(refreshJwt.getUserId(), refreshJwt.getToken(), refreshJwt.getExpiration());
//        boolean isTokenValid = iTokenRepo.isFindToken(refreshJwt.getToken());
//
//        if (isTokenValid && userOpt.isPresent()) {
//            User user = userOpt.get();
//            iTokenRepo.removeToken(refreshJwt.getToken());
//            iUserRepo.save(user);
//            return true;
//        } else {
//            return false;
//        }
//    }

    @Override
    public void forgot(String email, String originUrl) {
        String token = UUID.randomUUID().toString().replace("-", "");
        User user = iUserRepo.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundError();
        }

//        user.addPasswordRecovery(new PasswordRecovery(token));
        iPasswordRecoveryRepo.addPasswordRecovery(token, user.getId());
        iMailService.sendForgotMessage(email, token, originUrl);

//        iUserRepo.save(user);
    }

    public boolean reset(String password, String passwordConfirm, String token) {
        if (!Objects.equals(password, passwordConfirm)) {
            throw new PasswordDoNotMatchError();
        }

        User user = iUserRepo.findByPasswordRecoveriesToken(token)
                .orElseThrow(InvalidLinkError::new);

        boolean passwordRecoveryIsRemoved = iUserRepo.isTokenpr(token);

        if (passwordRecoveryIsRemoved) {
            user.setPassword(passwordEncoder.encode(password));
            iUserRepo.update(user.getPassword(), user.getId());
            iUserRepo.delete(user.getId());
        }

        return passwordRecoveryIsRemoved;
    }



}
