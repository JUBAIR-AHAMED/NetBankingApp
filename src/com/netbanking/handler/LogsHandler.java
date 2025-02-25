package com.netbanking.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import com.netbanking.exception.CustomException;
import com.netbanking.functions.ActivityFunctions;
import com.netbanking.util.ApiHelper;
import com.netbanking.util.Parser;
import com.netbanking.util.Writer;

public class LogsHandler {
	public static void handleGet(HttpServletRequest request, HttpServletResponse response) throws IOException, Exception {
		Map<String, Object> responseMap = new HashMap<>();
		Map<String, Object> filters = new HashMap<String, Object>();
		JsonObject jsonObject = Parser.getJsonObject(request);
		getDetailsFromBody(jsonObject, filters);
		Boolean countReq = (Boolean) filters.get("count");
		
		Integer limit = Parser.getValue(jsonObject, "limit", Integer.class, "Limit", false);
		Integer currentPage = Parser.getValue(jsonObject, "currentPage", Integer.class, "Current Page", false);

		List<Map<String, Object>> logs = ActivityFunctions.getInstance().filteredGetActivity(filters, limit, currentPage);
		// Sending the count or account data as requested
		if (countReq != null && countReq) {
			Long count = ApiHelper.getCount(logs);
			responseMap.put("count", count);
		} else {
			responseMap.put("logs", logs);
		}

		Writer.responseMapWriter(response, HttpServletResponse.SC_OK, HttpServletResponse.SC_OK,
				"Accounts fetched successfully", responseMap);
	}
	
	private static void getDetailsFromBody(JsonObject jsonObject, Map<String, Object> filters) throws CustomException {
		Parser.storeIfPresent(jsonObject, filters, "actorId", Long.class, "Actor Id", false);
		Parser.storeIfPresent(jsonObject, filters, "subjectId", Long.class, "Subject Id", false);
		Parser.storeIfPresent(jsonObject, filters, "recordname", String.class, "Record Name", false);
		Parser.storeIfPresent(jsonObject, filters, "keyValue", String.class, "Id Value", false);
		Parser.storeIfPresent(jsonObject, filters, "action", String.class, "Action", false);
		Parser.storeIfPresent(jsonObject, filters, "count", Boolean.class, "Count", false);
		Parser.storeIfPresent(jsonObject, filters, "searchSimilarFields", Set.class, "Similar search fields", false);
	}
}
