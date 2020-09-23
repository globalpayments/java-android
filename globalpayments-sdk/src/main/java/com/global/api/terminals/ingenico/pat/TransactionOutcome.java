package com.global.api.terminals.ingenico.pat;

import com.global.api.entities.exceptions.ApiException;
import com.global.api.terminals.ingenico.responses.DataResponse;
import com.global.api.terminals.ingenico.variables.TransactionStatus;

import java.nio.charset.StandardCharsets;

public class TransactionOutcome {
	private DataResponse _repFields;
	private TransactionStatus _transactionStatus;

	public TransactionOutcome(byte[] buffer) throws ApiException {
		parseData(buffer);
	}

	public DataResponse getRepFields() {
		return _repFields;
	}

	public TransactionStatus getTransactionStatus() {
		return _transactionStatus;
	}

	private void parseData(byte[] buffer) throws ApiException {
		try {
			String strBuffer = new String(buffer, StandardCharsets.UTF_8);
			
			_repFields = new DataResponse(strBuffer.substring(12, 67).getBytes());
			_transactionStatus = TransactionStatus.getEnumName(Integer.parseInt(strBuffer.substring(2, 3)));
		} catch (Exception e) {
			throw new ApiException(e.getMessage());
		}
	}
}