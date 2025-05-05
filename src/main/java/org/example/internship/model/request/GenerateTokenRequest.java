package org.example.internship.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenerateTokenRequest {
    private String username;
    private String password;
}
