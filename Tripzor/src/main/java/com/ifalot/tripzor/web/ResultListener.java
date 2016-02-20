package com.ifalot.tripzor.web;

import java.util.List;

public interface ResultListener {
	void onResultsSucceeded(String result, List<String> listResult);
}
