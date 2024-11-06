package com.genai.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import com.genai.model.ResumeAnalysisTransaction;

public class ResumeAnalysisTransactionRowMapper implements RowMapper<ResumeAnalysisTransaction>{

	@Override
	public ResumeAnalysisTransaction mapRow(ResultSet rs, int rowNum) throws SQLException {
		ResumeAnalysisTransaction obj = new ResumeAnalysisTransaction();
		obj.setTransactionId(rs.getInt("transactionId"));
		obj.setDateOfChat(rs.getDate("dateOfChat")+"");
		obj.setUserid(rs.getString("userid"));
		obj.setRoleType(rs.getString("roleType"));
		obj.setResumeFile(rs.getBytes("resumeFile"));
		obj.setAnswer(rs.getString("answer"));
		obj.setFileName(rs.getString("fileName"));
		return obj;
	}

}
