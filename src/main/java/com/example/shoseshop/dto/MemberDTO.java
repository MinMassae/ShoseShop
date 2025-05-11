package com.example.shoseshop.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class MemberDTO {
    private String userId;
    private String password;
    private String confirmPassword;
    private String name;
    private String address;
    private String email;
    private String phone;
}
