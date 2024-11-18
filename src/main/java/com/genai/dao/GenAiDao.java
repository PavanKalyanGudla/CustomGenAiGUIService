package com.genai.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.genai.constants.Constants;
import com.genai.model.ChatTransaction;
import com.genai.model.GifTransaction;
import com.genai.model.ImageAnalysisTransaction;
import com.genai.model.ImageTransaction;
import com.genai.model.ResumeAnalysisTransaction;
import com.genai.model.TranslationTransaction;
import com.genai.model.User;
import com.genai.rowmapper.ChatTransactionRowMapper;
import com.genai.rowmapper.ImageTransactionRowMapper;
import com.genai.rowmapper.ImageAnalysisTransactionRowMapper;
import com.genai.rowmapper.UserRowMapper;
import com.genai.rowmapper.TranslationTransactionRowMapper;
import com.genai.rowmapper.ResumeAnalysisTransactionRowMapper;
import com.genai.rowmapper.GifTransactionRowMapper;

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
	
	public String addUpdateUserInfo(User user) {
		int update = jdbc.update(Constants.UPDATE_USER_INFO,user.getFirstName(),user.getLastName(),user.getPassword(),user.getUserId());
		if(update > 0) {
			return Constants.SUCCESS_MSG;
		}else {
			return Constants.FAILURE_MSG;
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
		User user = null;
		try {
			user = jdbc.queryForObject(Constants.GET_USER_OBJECT, new UserRowMapper(),new Object[] {email,password});
		}catch(Exception e) {
			e.printStackTrace();
		}
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
	
	public void saveImageAnalysisGptTransaction(ImageAnalysisTransaction t) {
		try {
			jdbc.update(Constants.INSERT_IMAGE_ANALYSIS_TRANSACTION, t.getUserid(),t.getQuestion(),t.getImage(),t.getAnswer());
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<ImageAnalysisTransaction> getImageAnalysisTransactions(String userId) {
		List<ImageAnalysisTransaction> trans = null;
		try {
			trans = jdbc.query(Constants.GET_IMAGE_ANALYSIS_TRANSACTION, new ImageAnalysisTransactionRowMapper(), new Object[] {userId});
		}catch(Exception e) {
			e.printStackTrace();
		}
		return trans;
	}
	
	public void saveTranslaterTransaction(TranslationTransaction trans) {
		try {
			jdbc.update(Constants.INSERT_INTO_TRANSLATION_TRANSACTION, trans.getUserid(),trans.getQuestion(),trans.getSourceLang(),trans.getAnswer(),trans.getTargetLang());
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<TranslationTransaction> getTranslationTransaction(String userId) {
		List<TranslationTransaction> translationTrans = null;
		try {
			translationTrans = jdbc.query(Constants.GET_TRANSLATION_TRANSACTION, new TranslationTransactionRowMapper(), new Object[] {userId});
		}catch(Exception e) {
			e.printStackTrace();
		}
		return translationTrans;
	}
	
	public void insertResumeAnalysis(ResumeAnalysisTransaction obj){
		try {
			jdbc.update(Constants.INSERT_INTO_RESUME_ANALYSIS_TRANS , obj.getUserid() , obj.getRoleType(),obj.getResumeFile(),obj.getAnswer(),obj.getFileName());
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<ResumeAnalysisTransaction> getResumeAnalysisTransactions(String userId) {
		List<ResumeAnalysisTransaction> trans = null;
		try {
			trans = jdbc.query(Constants.GET_RESUME_ANALYSIS_TRANSACTION, new ResumeAnalysisTransactionRowMapper(), new Object[] {userId});
		}catch(Exception e) {
			e.printStackTrace();
		}
		return trans;
	}
	
	public int saveGifGptTransaction(GifTransaction t) {
		try {
			return jdbc.update(Constants.INSERT_GIF_TRANSACTION, t.getUserid(),t.getQuestion(),t.getAnswer());
		}catch(Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public List<GifTransaction> getGifTransactions(String userId) {
		List<GifTransaction> trans = null;
		try {
			trans = jdbc.query(Constants.GET_USER_GIF_TRANSACTION, new GifTransactionRowMapper(), new Object[] {userId});
		}catch(Exception e) {
			e.printStackTrace();
		}
		return trans;
	}
	
}
