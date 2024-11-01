package com.genai.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.genai.model.ImageAnalysisTransaction;

public class ImageAnalysisTransactionRowMapper implements RowMapper<ImageAnalysisTransaction>{

	@Override
	public ImageAnalysisTransaction mapRow(ResultSet rs, int rowNum) throws SQLException {
		ImageAnalysisTransaction trans = new ImageAnalysisTransaction();
		trans.setUserid(rs.getString("userid"));
		trans.setTransactionId(rs.getInt("transactionId"));
		trans.setQuestion(rs.getString("question"));
		trans.setAnswer(rs.getString("answer"));
		trans.setImage(rs.getBytes("image"));
		trans.setDateOfChat(rs.getString("dateOfChat"));
		return trans;
	}

}
