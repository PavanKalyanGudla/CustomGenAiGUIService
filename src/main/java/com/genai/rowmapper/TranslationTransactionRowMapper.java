package com.genai.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.genai.model.TranslationTransaction;

public class TranslationTransactionRowMapper implements RowMapper<TranslationTransaction>{

	@Override
	public TranslationTransaction mapRow(ResultSet rs, int rowNum) throws SQLException {
		TranslationTransaction trans = new TranslationTransaction();
		trans.setTransactionId(rs.getInt("transactionId"));
		trans.setUserid(rs.getString("userid"));
		trans.setDateOfChat(rs.getDate("dateOfChat")+"");
		trans.setSourceLang(rs.getString("sourceLang"));
		trans.setQuestion(rs.getString("sourceText"));
		trans.setTargetLang(rs.getString("targetLang"));
		trans.setAnswer(rs.getString("targetText"));
		return trans;
	}

}
