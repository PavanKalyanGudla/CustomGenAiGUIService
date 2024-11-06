package com.genai.constants;

public class Constants {
	
	public static final String SUCCESS_CODE = "200";
	
	public static final String SUCCESS_MSG = "SUCCESS";
	
	public static final String FAILURE_CODE = "400";
	
	public static final String FAILURE_MSG = "FAILURE";
	
	public static final String USER_NOT_FOUND = "USER NOT FOUND";
 	
	public static final String ERROR = "APPLICATION ERROR";
	
	public static final String IMAGE_ANALYSIS = "Explain what do you see in this Image";
	
	public static final String USER_REGISTRATION_SUCCESS = "USER REGISTERED SUCCESSFULLY";
	
	public static final String USER_REGISTRATION_FAIL = "USER REGISTRATION FAILED";
	
	public static final String USER_SAVE_SUCCESS = "USER UPDATED SUCCESSFULLY";
	
	public static final String USER_SAVE_FAIL = "USER UPDATE FAILED";
	
	public static final String USER_EXISTS = "USER ALREADY REGISTERED WITH THIS MAIL-Id";
	
	public static final String USER_NOT_EXISTS = "USER NOT YET REGISTERED WITH THE PROVIDED MAIL-ID";
	
	public static final String ACCOUNT_DOESNT_EXISTS = "NOT FOUND, PLEASE DO REGISTER AND SIGNIN";
	
	public static final String EMAIL_Registration_SUCCESSFUL_SUBJECT = "Custom Gen Ai User Registration Successful ...";
	
	public static final String INVALID_PASSWORD = "INVALID PASSWORD";
	
	public static final String FORGOT_PASSWORD = "Custom Gen Ai User Forgot Password Request ...";
	
	public static final String CHECK_MAIL = "Password will be notified to Registered Email";
	
	public static final String RESUME_ANALYSIS_PROMPT = "Analyze this resume in the field of %s and let me know suggestions %s";
	
	public static final String GET_USER_COUNT = "select count(*) from User where userid=?";
	
	public static final String GET_USER_PASSWORD = "select password from User where userid=?";
	
	public static final String GET_USER_OBJECT = "select * from User where email=? and password=?";

	public static final String GET_USER_CHAT_TRANSACTION = "select * from chatTransaction where userid=?";
	
	public static final String GET_USER_IMAGE_TRANSACTION = "select * from imageTransaction where userid=?";
	
	public static final String GET_IMAGE_ANALYSIS_TRANSACTION = "select * from imageAnalysisTransaction where userid=?";
	
	public static final String GET_TRANSLATION_TRANSACTION = "select * from translateTransaction where userid=?";
	
	public static final String GET_RESUME_ANALYSIS_TRANSACTION = "select * from resumeAnalysisTransaction where userid=?";
	
	// Insert
	public static final String INSERT_USER = "insert into User(userid,firstName,lastName,email,password,dateOfJoin)"
			+ "values(?,?,?,?,?,?)";
	
	public static final String INSERT_CHAT_TRANSACTION = "insert into chatTransaction(userid,question,answer,dateOfChat)"
			+ "values(?,?,?,CURRENT_DATE)";
	
	public static final String INSERT_IMAGE_TRANSACTION = "insert into imageTransaction(userid,question,answer,dateOfChat)"
			+ "values(?,?,?,CURRENT_DATE)";
	
	public static final String INSERT_IMAGE_ANALYSIS_TRANSACTION = "insert into imageAnalysisTransaction(userid,question,image,answer,dateOfChat)"
			+"values(?,?,?,?,CURRENT_DATE)";
	
	public static final String INSERT_INTO_TRANSLATION_TRANSACTION = "insert into translateTransaction(userid,sourceText,sourceLang,targetText,targetLang,dateOfChat)"
			+"values(?,?,?,?,?,CURRENT_DATE)";
	
	public static final String INSERT_INTO_RESUME_ANALYSIS_TRANS = "insert into resumeAnalysisTransaction(userid,roleType,resumeFile,answer,fileName,dateOfChat)"
			+"values(?,?,?,?,?,CURRENT_DATE)";
	
	//Update
	public static final String UPLOAD_PROFILE_PIC = "update User set profilePic = ? where userid = ?";
	
	public static final String UPDATE_USER_INFO = "update User set firstName=?, lastName=?, password=? where userid = ?";

	public static String getUserRegistrationBody(String firstName, String LastName, String userId, String email, String date) {
	    String PATIENT_REGISTRATION_CONTENT = String.format(
	            "Dear %s,%n%n" +
	            "Congratulations! Your registration with our Custom Gen Ai Portal has been successfully completed. Welcome to our Generative Ai community!%n%n" +
	            "Below are your registration details:%n%n" +
	            "- **UserId:** %s%n" +
	            "- **Email Address:** %s%n" +
	            "- **Registration Date:** %s%n" +
	            "We are dedicated to providing you with comprehensive Ai services and support. If you have any inquiries or require assistance, do not hesitate to contact our support team.%n%n" +
	            "Thank you for choosing our Portal. We are committed to offering you exceptional Ai experiences.%n%n" +
	            "Best regards,%n" +
	            "Generative Ai Team%n" +
	            "Address Line%n" +
	            "Beaumont, Texas, 77705%n" +
	            "Phone: +1 123-456-7890%n" +
	            "Email: info@customGenAi.com",
	            firstName+" "+LastName, userId, email, date
	    );
	    return PATIENT_REGISTRATION_CONTENT;
	}
	
	public static String forgotpassword(String password) {
		return String.format(
				"Dear User,\r\n"
				+ "\r\n"
				+ "We received a request to get the password associated with your account. Please find the details below to securely reset your password:\r\n"
				+ "\r\n"
				+ "Password: %s\r\n"
				+ "\r\n"
				+ "We recommend that you change this password after logging in.\r\n"
				+ "\r\n"
				+ "To change your password:\r\n"
				+ "\r\n"
				+ "Log in to your account using the password.\r\n"
				+ "Navigate to the \"Account Profile Settings\" section.\r\n"
				+ "Update your password and confirm the change.\r\n"
				+ "If you did not request a password reset, please ignore this email or contact our support team.\r\n"
				+ "\r\n"
				+ "Thank you for choosing our service.\r\n"
				+ "\r\n"
				+ "Best regards,\r\n"
				+ "Generative Ai Team\r\n"
				+"Beaumont, Texas, 77705%n"
	            +"Phone: +1 123-456-7890%n"
	            +"Email: info@customGenAi.com",
				password);
	}
	
}
