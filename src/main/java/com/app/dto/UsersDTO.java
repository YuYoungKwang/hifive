package com.app.dto;

import lombok.Data;

@Data
public class UsersDTO {

	private Long userNo;
    private String userType;
    private String userCreatedAt;
    private String userUpdatedAt;
    private String userStatus;
    private String userName;
    private String userGender;
    private String userPhoneNumber;
    private String userRegistrationNo;
    private String userNickName;
    private String userProfileImg;
    private Long userAddressNo;
    private String userZipCode;
    private String userBaseAddress;
    private String userDetailAddress;
    private String authId;
    private String authPassword;
    private String authEmail;
    private String authSnsType;
}
