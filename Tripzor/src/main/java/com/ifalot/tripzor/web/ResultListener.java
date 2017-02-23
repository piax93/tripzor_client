package com.ifalot.tripzor.web;

import org.json.JSONException;
import org.json.JSONObject;

public interface ResultListener {
	void onResultsSucceeded(JSONObject result) throws JSONException;
}
