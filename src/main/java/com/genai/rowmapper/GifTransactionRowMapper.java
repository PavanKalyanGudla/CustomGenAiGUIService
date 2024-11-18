package com.genai.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.genai.model.GifTransaction;

public class GifTransactionRowMapper implements RowMapper<GifTransaction>{

	@Override
	public GifTransaction mapRow(ResultSet rs, int rowNum) throws SQLException {
		GifTransaction transaction = new GifTransaction();
		transaction.setTransactionId(rs.getInt("transactionId"));
		transaction.setUserid(rs.getString("userid"));
		transaction.setDateOfChat(rs.getDate("dateOfChat")+"");
		transaction.setQuestion(rs.getString("question"));
		transaction.setAnswer(rs.getBytes("answer"));
		return transaction;
	}

}
