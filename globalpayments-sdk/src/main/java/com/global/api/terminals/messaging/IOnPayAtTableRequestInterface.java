package com.global.api.terminals.messaging;

import com.global.api.terminals.ingenico.pat.PATRequest;

public interface IOnPayAtTableRequestInterface {
	void onPayAtTableRequest(PATRequest payAtTableRequest);
}
