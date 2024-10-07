package com.genai.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.genai.model.ChatTransaction;

public class ChatTransactionRowMapper implements RowMapper<ChatTransaction>{

	@Override
	public ChatTransaction mapRow(ResultSet rs, int rowNum) throws SQLException {
		ChatTransaction transaction = new ChatTransaction();
		transaction.setTransactionId(rs.getInt("transactionId"));
		transaction.setUserid(rs.getString("userid"));
		transaction.setDateOfChat(rs.getDate("dateOfChat")+"");
		transaction.setQuestion(rs.getString("question"));
		transaction.setAnswer(rs.getString("answer"));
		return transaction;
	}

}
