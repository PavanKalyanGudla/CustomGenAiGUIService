package com.genai.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.genai.constants.Constants;
import com.genai.model.User;
import com.genai.rowmapper.UserRowMapper;

@Service
public class GenAiDao {
	
	@Autowired
	JdbcTemplate jdbc;
	
	GenAiDao(JdbcTemplate jdbc){
		this.jdbc = jdbc;
	}
	
	public int getUserCount(String userId) {
		int count = 0;
		try {
			count = jdbc.queryForObject(Constants.GET_USER_COUNT,Integer.class,userId);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	
	public String getUserPassword(String userId) {
		return jdbc.queryForObject(Constants.GET_USER_PASSWORD, String.class,userId);
	}
	
	public String addUserRegistration(User user){
		if(getUserCount(user.getUserId())>0) {
			return Constants.USER_EXISTS;
		}else {
			int update = jdbc.update(Constants.INSERT_USER,user.getUserId(),user.getFirstName(),user.getLastName(),
					user.getEmail(),user.getPassword(),user.getDateOfJoin());
			if(update > 0) {
				return Constants.USER_REGISTRATION_SUCCESS;
			}else {
				return Constants.USER_REGISTRATION_FAIL;
			}
		}
	}
	
	public User getUserObject(String email, String password) {
		User user = jdbc.queryForObject(Constants.GET_USER_OBJECT, new UserRowMapper(),new Object[] {email,password});
		return user;
	}

}
