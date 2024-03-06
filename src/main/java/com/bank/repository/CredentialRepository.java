package com.bank.repository;

import com.bank.controller.CodeController;
import com.bank.entities.UserTOTP;
import com.warrenstrange.googleauth.ICredentialRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CredentialRepository implements ICredentialRepository {

    private static final Logger logger = LoggerFactory.getLogger(CredentialRepository.class);

    @Autowired
    private UserTOTPRepository userTOTPRepository;

    @Override
    public String getSecretKey(String username) {
        List<UserTOTP> userTOTP =  userTOTPRepository.findByUserName(username);
        List<UserTOTP> sortedUserTOTP = userTOTP.stream()
                .sorted(Comparator.comparing(UserTOTP::getTimestamp).reversed())
                .toList();
        if (sortedUserTOTP.isEmpty() || sortedUserTOTP.get(0).getSecretKey() == null) {
            throw new IllegalArgumentException("Secret key not found for user: " + username);
            // Or return a default value or handle it in another appropriate way
        }
        logger.debug("sortedUserTOTP in repo :::::{}",sortedUserTOTP);
        return sortedUserTOTP.get(0).getSecretKey();
    }

//    @Override
//    public String getSecretKey(String userName) {
//        UserTOTP userTOTP = userTOTPRepository.findByUserName(userName);
//        return userTOTP != null ? userTOTP.getSecretKey() : null;
//    }

    @Override
    public void saveUserCredentials(String userName, String secretKey, int validationCode, List<Integer> scratchCodes) {

        long currentTimestampEpoch = Instant.now().getEpochSecond();

        UserTOTP userTOTP = new UserTOTP(userName, secretKey, validationCode, scratchCodes,String.valueOf(currentTimestampEpoch));
        userTOTPRepository.save(userTOTP);
    }
       // userTOTPRepository.save(new UserTOTP(userName, secretKey, validationCode, scratchCodes));
                //usersKeys.put(userName, new UserTOTP(userName, secretKey, validationCode, scratchCodes));


    public UserTOTP getUser(String username) {
        List<UserTOTP> userTOTP =  userTOTPRepository.findByUserName(username);
        List<UserTOTP> sortedUserTOTP = userTOTP.stream()
                .sorted(Comparator.comparing(UserTOTP::getTimestamp))
                .toList();
        if (sortedUserTOTP.isEmpty() || sortedUserTOTP.get(0).getSecretKey() == null) {
            throw new IllegalArgumentException("Secret key not found for user: " + username);
            // Or return a default value or handle it in another appropriate way
        }
        return sortedUserTOTP.get(0);
    }
}