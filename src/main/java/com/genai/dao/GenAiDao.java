package com.genai.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.genai.constants.Constants;
import com.genai.model.ChatTransaction;
import com.genai.model.ImageTransaction;
import com.genai.model.User;
import com.genai.rowmapper.ChatTransactionRowMapper;
import com.genai.rowmapper.ImageTransactionRowMapper;
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
	
	public int updateProfilePic(String userId, byte[] image){
		try {
			int update = jdbc.update(Constants.UPLOAD_PROFILE_PIC , image , userId);
			return update;
		}catch(Exception e) {
			return 0;
		}
	}
	
	public User getUserObject(String email, String password) {
		User user = jdbc.queryForObject(Constants.GET_USER_OBJECT, new UserRowMapper(),new Object[] {email,password});
		return user;
	}
	
	public void saveChatTransaction(ChatTransaction tran) {
		try {
			jdbc.update(Constants.INSERT_CHAT_TRANSACTION,tran.getUserid(),tran.getQuestion(),
					tran.getAnswer());
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<ChatTransaction> getChatTransactions(String userId) {
		List<ChatTransaction> chatTrans = null;
		try {
			chatTrans = jdbc.query(Constants.GET_USER_CHAT_TRANSACTION, new ChatTransactionRowMapper(), new Object[] {userId});
		}catch(Exception e) {
			e.printStackTrace();
		}
		return chatTrans;
	}
	
	public void saveImageGptTransaction(ImageTransaction t) {
		try {
			jdbc.update(Constants.INSERT_IMAGE_TRANSACTION, t.getUserid(),t.getQuestion(),t.getAnswer());
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<ImageTransaction> getImageTransactions(String userId) {
		List<ImageTransaction> chatTrans = null;
		try {
			chatTrans = jdbc.query(Constants.GET_USER_IMAGE_TRANSACTION, new ImageTransactionRowMapper(), new Object[] {userId});
		}catch(Exception e) {
			e.printStackTrace();
		}
		return chatTrans;
	}

}
