package com.messaging.messagingapp.data.models.bindingModel;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class RegisterUserBindingModel {
    @NotBlank
    @Length(min = 3, max = 16)
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String confirmPassword;
    @NotBlank
    //@Pattern()
    private String email;
    @NotBlank
    private String publicName;

    public RegisterUserBindingModel(){}
    public RegisterUserBindingModel(String username, String password, String confirmPassword, String email, String publicName){
        setUsername(username);
        setPassword(password);
        setEmail(email);
        setPublicName(publicName);
    }
}
