package com.backend.auth.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.relational.core.mapping.MappedCollection;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

@Getter
@Setter
@ToString
@Data
@NoArgsConstructor
public class User {
    @Id
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String role;
    @MappedCollection private final Set<Token> tokens = new HashSet<>();
    @MappedCollection private final Set<PasswordRecovery> passwordRecoveries = new HashSet<>();

    @PersistenceConstructor
    public User(Long id, String firstname, String lastname, String email, String password, String role, Collection<Token> tokens, Collection<PasswordRecovery> passwordRecoveries) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.role = role;
        this.tokens.addAll(tokens);
        this.passwordRecoveries.addAll(passwordRecoveries);
    }

    public static User of(String firstName, String lastName, String email, String password, String role) {
        return new User(null, firstName, lastName, email, password, role, Collections.emptyList(), Collections.emptyList());
    }

    public void addToken(Token token) {
        this.tokens.add(token);
    }

    public Boolean removeToken(Token token) {
        return this.tokens.remove(token);
    }

    public Boolean removeTokenIf(Predicate<? super Token> predicate) {
        return this.tokens.removeIf(predicate);
    }

    public void addPasswordRecovery(PasswordRecovery passwordRecovery) {
        this.passwordRecoveries.add(passwordRecovery);
    }



    public Boolean removePasswordRecovery(PasswordRecovery passwordRecovery) {
        return this.passwordRecoveries.remove(passwordRecovery);
    }

    public Boolean removePasswordRecoveryIf(Predicate<? super PasswordRecovery> predicate) {
        return this.passwordRecoveries.removeIf(predicate);
    }

    public Set<PasswordRecovery> PasswordRecovery(PasswordRecovery passwordRecovery) {
        return this.passwordRecoveries;
    }
}
