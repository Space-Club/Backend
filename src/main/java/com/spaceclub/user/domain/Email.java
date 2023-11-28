package com.spaceclub.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.util.regex.Pattern;

import static com.spaceclub.user.UserExceptionMessage.INVALID_EMAIL;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = PROTECTED)
public class Email {

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", CASE_INSENSITIVE);

    @Column(nullable = false, unique = true)
    private String email;

    public Email(String email) {
        validate(email);
        this.email = email;
    }

    private void validate(String email) {
        Assert.hasText(email, INVALID_EMAIL.toString());
        Assert.isTrue(isValid(email), INVALID_EMAIL.toString());
    }

    private boolean isValid(String email) {
        return VALID_EMAIL_ADDRESS_REGEX.matcher(email).find();
    }

}
