package com.genai.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.genai.model.ImageTransaction;

public class ImageTransactionRowMapper implements RowMapper<ImageTransaction>{

	@Override
	public ImageTransaction mapRow(ResultSet rs, int rowNum) throws SQLException {
		ImageTransaction transaction = new ImageTransaction();
		transaction.setTransactionId(rs.getInt("transactionId"));
		transaction.setUserid(rs.getString("userid"));
		transaction.setDateOfChat(rs.getDate("dateOfChat")+"");
		transaction.setQuestion(rs.getString("question"));
		transaction.setAnswer(rs.getBytes("answer"));
		return transaction;
	}

}
