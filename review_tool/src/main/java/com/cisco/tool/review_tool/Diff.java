package com.cisco.tool.review_tool;

//import com.Regex;
//import com.KeyExist;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class Diff {
	public static Map<String, HashSet<String>> keyMap = new HashMap<String, HashSet<String>>();
	public static Map<String, HashSet<String>> bundleMap = new HashMap<String, HashSet<String>>();
	public static Map<String, HashSet<String>> resultMap = new HashMap<String, HashSet<String>>();

	public static void main(String[] args) {
		Diff dObj = new Diff();
//		Regex regObj = new Regex();
//		KeyExist keyObj = new KeyExist();

		Scanner sc = new Scanner(System.in);
		String msg = "";
		boolean prURL = true;
		String inpURL = null;
		try {
			String splitURL = "", restURL = "";
			String inputLine, jsonData = "";
			String fileLine, json = "";

			do {
				System.out.println("Enter the bitbucket PR link: ");
				inpURL = sc.nextLine();
				if (!inpURL.contains("bitbucket") && !inpURL.contains("git")) {
					System.out.println("URL incorrect, please enter a valid bitbucket URL");
					prURL = false;
				} else {
					prURL = true;
				}
			} while (prURL == false);
			try {
				URL u = new URL(inpURL);
			} catch (MalformedURLException e) {
				System.out.println("Invalid URL, Please check the PR link and rerun");
				System.exit(0);
			}
			String authString = null;
			if (inpURL.contains("git")) {
				splitURL = "/files/";
				restURL = inpURL + splitURL;
				// authString = "aypandia:Aug78@1493";

				authString = "rammaria:Australia@6";
			} else {

				int projInd = inpURL.indexOf("projects");
				splitURL = inpURL.substring(projInd, inpURL.length());

				if (!splitURL.contains("diff")) {
					splitURL = splitURL.replace("overview", "diff");
					splitURL = splitURL.replace("commits", "diff");
				}
				restURL = "https://bitbucket-eng-sjc1.cisco.com/bitbucket/rest/api/latest/" + splitURL;
			    authString = "aypandia:Aug78@1493";
				//authString = "rammaria:Australia@6";
			}
			// restURL=splitURL;
			URL url = new URL(restURL);

			// Enter authString in the following format "username:password"

			if (authString == "" && authString.length() == 0) {
				System.out.println("Please add your CEC username and password in code (authString)");
				// System.exit(0);
			}
			byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
			String authStringEnc = new String(authEncBytes);

			URLConnection urlConnection = url.openConnection();
			urlConnection.addRequestProperty("Authorization", "Basic " + authStringEnc);
			// urlConnection.addRequestProperty("Authorization", "Bearer
			// OTU1Nzg4MjQ5MjY1Oqe1hwlionVUDFcJ33pUAIb7QC5C");
			BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			// System.out.println("Please wait ... " + in.readLine());

			while ((inputLine = in.readLine()) != null) {
				// jsonData += inputLine+"\n";
				jsonData += inputLine;
			}
			// System.out.println("PR data ... "+jsonData);
			// Read json
			BufferedReader br = new BufferedReader(new FileReader(
					"C:\\Users\\aypandia\\workspace\\cisco\\review_tool\\src\\main\\java\\com\\cisco\\tool\\review_tool\\input.json"));
			while ((fileLine = br.readLine()) != null) {
				json += fileLine + "\n";
			}
			Map<String, String> keybundle = new HashMap<String, String>();
			BufferedReader keybr = new BufferedReader(new FileReader(
					"C:\\Users\\aypandia\\workspace\\cisco\\review_tool\\src\\main\\java\\com\\cisco\\tool\\review_tool\\key-module.json"));
			while ((fileLine = keybr.readLine()) != null) {
				keybundle.put(StringUtils.substringBefore(fileLine, ":").replaceAll("\"", ""),
						StringUtils.substringAfter(fileLine, ":"));
			}
			System.out.println("Key List " + keybundle.keySet());

			JSONObject jsonReadObj = new JSONObject(json);
			JSONArray jsonReadArr = new JSONArray(jsonReadObj.getJSONArray("files").toString());

			// System.out.println(jsonData);
			JSONObject obj = new JSONObject(jsonData);
			JSONArray jsonarray = (JSONArray) obj.get("diffs");
			List<String> addlines = new ArrayList<String>();
			HashSet<String> addsets = new HashSet<String>();
			String keyPattern, filterstartex,filterendex = "";
			// Retrieve from bitbucket JSON
			for (int i = 0; i < jsonarray.length(); i++) {

				JSONObject jsonobject = jsonarray.getJSONObject(i);

				// JSONObject source = (JSONObject) jsonobject.get("source");
				// System.out.println("FILE: "+source.get("toString"));

				JSONObject destination = (JSONObject) jsonobject.get("destination");
				String fileName = destination.getString("toString");
				JSONObject keyspath = null;

				for (int ij = 0; ij < jsonReadArr.length(); ij++) {
					JSONObject jsonObject = jsonReadArr.getJSONObject(ij);
					Iterator<String> keys = jsonObject.keys();

					while (keys.hasNext()) {
						String jsonRead = keys.next();
						if (jsonRead.equals("bundlePath")) {
							JSONArray innerArrCond = (JSONArray) jsonObject.get(jsonRead);
							for (int kt = 0; kt < innerArrCond.length(); kt++) {
								keyspath = innerArrCond.getJSONObject(kt);

							}
						}
					}
				}
//                if(jsonReadArr!=null ) {
//                	jsonRead.equals("bundlePath")) {
//                }
//    				contains = jsonCond.get("contains").toString();
//    			System.out.println("Bundlepath"+jsonCond);	
//    			}else {
				HashSet<String> resultSet = new HashSet<String>();

				if (!(keyspath != null && fileName.contains(keyspath.get("contains").toString()))) {
					// System.out.println("filename" + fileName);
					// System.out.println("Bundlepath" + keyspath.get("contains").toString());
					JSONArray hunkArray = (JSONArray) jsonobject.get("hunks");
					String equals = "", regex = "", suggestion = "", regexChk = "", file = "", retrieveKey = "";
					for (int j = 0; j < hunkArray.length(); j++) {

						JSONObject hunkObj = hunkArray.getJSONObject(j);
						JSONArray segmentArray = (JSONArray) hunkObj.get("segments");
						// String contains = "", ends = "";

						for (int k = 0; k < segmentArray.length(); k++) {
							JSONObject segmentObj = segmentArray.getJSONObject(k);
							JSONArray lineArray = (JSONArray) segmentObj.get("lines");
							String type = segmentObj.getString("type");

							for (int l = 0; l < lineArray.length(); l++) {
								JSONObject lineObj = lineArray.getJSONObject(l);
								if (type.equalsIgnoreCase("REMOVED")) {
								}
								if (type.equalsIgnoreCase("ADDED")) {

									String lineNo = lineObj.get("destination").toString();
									String add = lineObj.getString("line");
									addlines.add(add);
									if (!add.equals("") && add.trim().length() > 0) {
										if (hunkArray.length() == 1 && segmentArray.length() == 3
												&& lineArray.length() == 1) {
											boolean check = balancedParentheses(add);
											if (check == false) {
												msg = "<font color=\"red\">ERROR</font> Parentheses missed in line "
														+ lineNo + " " + add.trim();
												resultSet.add(msg);
												addsets.add(msg);
											}
										}
										// Retrieve from input JSON
										for (int ii = 0; ii < jsonReadArr.length(); ii++) {
											JSONObject jsonObject = jsonReadArr.getJSONObject(ii);
											Iterator<String> keys = jsonObject.keys();

											while (keys.hasNext()) {
												String jsonRead = keys.next();
												JSONArray innerArrCond = (JSONArray) jsonObject.get(jsonRead);
												for (int kk = 0; kk < innerArrCond.length(); kk++) {
													JSONObject jsonCond = innerArrCond.getJSONObject(kk);
													if (jsonCond.has("i18key")) {
														keyPattern = jsonCond.get("i18key").toString();
														filterstartex = jsonCond.get("filterstart").toString();
														filterendex = jsonCond.get("filterend").toString();

														// resultSet.add(contains);
														// resultSet.add("~~~~~~~");
														msg = dObj.checkBundleSyntax(fileName,
																lineNo + " " + add.trim(), keyPattern, "containsCheck");
														if (msg != null) {

															if (filterendex.equals("+")) {

																if (StringUtils.contains(msg, keyPattern)) {
																	String multiMsg = StringUtils.substringAfter(msg,
																			keyPattern);
																	String message = keyPattern + multiMsg;
																	while (checkKeyContains(multiMsg, keyPattern)) {
																		message = keyPattern + StringUtils
																				.substringAfter(multiMsg, keyPattern);

																		resultSet.add(getCommaFilter(StringUtils
																				.substringBefore(message, "+")));

																		addsets.add(getCommaFilter(StringUtils
																				.substringBefore(message, "+")));

																		multiMsg = StringUtils.substringAfter(multiMsg,
																				keyPattern);
																	}

																	message = StringUtils.substringBefore(message, "+");

																	resultSet.add(getCommaFilter(message));
																	addsets.add(getCommaFilter(message));
																}
															} else {
															String[] matcharray = StringUtils.substringsBetween(msg,
																	filterstartex, filterendex);
															if (matcharray != null && matcharray.length > 0) {
																for (String match : matcharray) {
																	if (StringUtils.contains(match, keyPattern)) {
																		resultSet.add(getCommaFilter(match));
																		addsets.add(getCommaFilter(match));
																	}
																}
															}
															}
															
															resultSet.remove("");
															addsets.remove("");
															if (msg != "") {
																if(resultMap.containsKey("<b>" + fileName + "</b>")) {
																HashSet<String> updateSet = resultMap.get("<b>" + fileName + "</b>");
																updateSet.addAll(resultSet);
																resultMap.put("<b>" + fileName + "</b>", updateSet);
																}else {
																resultMap.put("<b>" + fileName + "</b>", resultSet);
																}

															}
														}
														

												}
											}
										}
									}
								}
							}
						}
					}
					if (suggestion != "") {
						msg = "<font color=\"red\">WARNING</font> " + suggestion;
						resultSet.add(msg);
						addsets.add(msg);
					}
//					resultSet.remove("");
//					addsets.remove("");
//					if (msg != "") {
//						resultMap.put("<b>" + fileName + "</b>", resultSet);
//
//					}
				}
			}
			}
			System.out.println("KeyMap " + keyMap);
			System.out.println("BundleMap " + bundleMap);

//			String keyCh = keyObj.keyExist(keyMap, bundleMap);
//			if (keyCh != "") {
//				HashSet<String> resultSet1 = new HashSet<String>();
//				resultSet1.add(keyCh);
//				resultMap.put("<b>KEY MISSED</b>", resultSet1);
//			}
//            HashSet<String> lineSet= new HashSet<String>();
//            lineSet.addAll(addlines);
//            resultMap.put("New_Line", lineSet);
//			for(String mapKey:resultMap.keySet()) {
//				HashSet<String> result = resultMap.get(mapKey);
//				for(String key:result) {
//					if(key.contains(",")) {
//						result.add(StringUtils.substringBefore(key, ","));
//					}
//				}
//				resultMap.put(mapKey, result);
//			}
			System.out.println("added key " + addsets);
			HashSet<String> missingkey = new HashSet<String>();
			for (String i18key : addsets) {

				if (!keybundle.keySet().contains(i18key)) {
					missingkey.add(i18key);

				}
			}
			if(missingkey!=null && missingkey.size()>0) {
			resultMap.put("<b> Missing Keys </b>", missingkey);
			}else {
				missingkey.add("no missing key");
				resultMap.put("<b> Missing Keys </b>", missingkey);
			}
			if (resultMap != null && !resultMap.isEmpty()) {
				MailReport rep = new MailReport();
				rep.sendEmail(inpURL, resultMap);
			} else {
				System.out.println("-------------------No diff found -----------------------");
			}
			System.out.println("================================END====================================");

		} catch (MalformedURLException e) {
			System.out.println("REST URL is not found");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sc.close();
		}

	}

	private static String getCommaFilter(String message) {
		if (message.contains(",")) {
			return StringUtils.substringBefore(message, ",");
		}
		if (message.contains("'")) {
			return StringUtils.replace(message, "'", "");
		}
		return message;
	}

	private static boolean checkKeyContains(String multiMsg, String key) {
		// TODO Auto-generated method stub
		return multiMsg.contains(key);
	}

	// Balanced parentheses
	public static boolean balancedParentheses(String s) {
		Stack<Character> stack = new Stack<Character>();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == '[' || c == '(' || c == '{') {
				stack.push(c);
			} else if (c == ']') {
				if (stack.isEmpty() || stack.pop() != '[') {
					return false;
				}
			} else if (c == ')') {
				if (stack.isEmpty() || stack.pop() != '(') {
					return false;
				}
			} else if (c == '}') {
				if (stack.isEmpty() || stack.pop() != '{') {
					return false;
				}
			}

		}
		return stack.isEmpty();
	}

	public String checkBundleSyntax(String file, String line, String syntax, String flag) {
		String msg = "";
		try {
			if (!line.trim().startsWith("//") && (line.contains("\"") || line.contains("\'"))) {
				if (flag.equals("containsCheck")) {
					if (line.contains(syntax)) {
						// msg = "<font color=\"red\">ERROR</font> " + syntax + " missing in line " +
						// line;
						msg = line;
					}
				} else {
					if (!line.trim().endsWith(syntax)) {
						// msg = "<font color=\"red\">ERROR</font> " + syntax + " missing at the end of
						// the line " + line;
						msg = line;

					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}

	public void addFileToMap(String fileName, String key, Map keysMap) {
		if (!key.isEmpty()) {
			if (keysMap.containsKey(fileName)) {
				((HashSet<String>) keysMap.get(fileName)).add(key);
			} else {
				HashSet<String> keyAdd = new HashSet<String>();
				keyAdd.add(key);
				keysMap.put(fileName, keyAdd);
			}
		}
	}

}