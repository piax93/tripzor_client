package com.ifalot.tripzor.web;

import org.json.JSONException;
import org.json.JSONObject;

public interface MediaListener extends ResultListener {
    void onMediaReceived(JSONObject result) throws JSONException;
}