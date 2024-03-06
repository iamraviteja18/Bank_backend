package com.bank.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("User_2fa")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserTOTP {
    private String userName;
    private String secretKey;
    private int validationCode;
    private List<Integer> scratchCodes;
    private String timestamp;
}