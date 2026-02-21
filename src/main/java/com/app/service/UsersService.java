package com.app.service;

import java.util.List;

import com.app.dto.TagMasterDTO;
import com.app.dto.UserTagMapDTO;
import com.app.dto.UsersDTO;
	
public interface UsersService {
	UsersDTO getUserByAuthId(String authId);

	UsersDTO updateProfileByAuthId(String authId, UsersDTO users);

	void changePasswordByAuthId(String authId, String currentPassword, String newPassword);

	String findUserName(int userNo);

	List<UserTagMapDTO> getUserTagsByAuthId(String authId);

	List<TagMasterDTO> getTagMasterList();

	boolean addUserTagByAuthId(String authId, String tagCode);

	boolean removeUserTagByAuthId(String authId, String tagCode);
}
