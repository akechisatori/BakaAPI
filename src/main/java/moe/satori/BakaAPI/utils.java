package moe.satori.BakaAPI;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import org.bukkit.Bukkit;

import com.google.gson.*;
import java.lang.reflect.Method;

public class utils {

	public static boolean isClass(String className) {
		try {
			Class.forName(className);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	public static String getFileContent(String filePath) {
		try {
			File file = new File(filePath);
			byte bt[] = new byte[(int) file.length()];
			FileInputStream fis = new FileInputStream(file);
			fis.read(bt);
			fis.close();

			return new String(bt, "UTF-8");
		} catch (FileNotFoundException e) {
			System.out.print("error:" + e.getMessage());
		} catch (IOException e) {
			System.out.print("error:" + e.getMessage());
		} catch (Exception e) {
			System.out.print("error:" + e.getMessage());
		}

		return "";
	}

	public static String stringMD5(String input) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			byte[] inputByteArray = input.getBytes();
			messageDigest.update(inputByteArray);
			byte[] resultByteArray = messageDigest.digest();
			return byteArrayToHex(resultByteArray);
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}

	public static String byteArrayToHex(byte[] byteArray) {
		char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char[] resultCharArray = new char[byteArray.length * 2];
		int index = 0;
		for (byte b : byteArray) {
			resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
			resultCharArray[index++] = hexDigits[b & 0xf];
		}
		return new String(resultCharArray);
	}

	 public static String HttpBuildQuery(Map<String, List<String>> map) {
		 final List<String> list = new ArrayList<>();
		 for (String key : map.keySet()) {
			 list.add(key + "=" + map.get(key).get(0)+ "&");
		 }
		 final int size = list.size();
		 final String[] arrayToSort = list.toArray(new String[size]);
		 Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
		 final StringBuilder sb = new StringBuilder();
		 for (int i = 0; i < size; i++) {
			 sb.append(arrayToSort[i]);
		 }
		 final String result = sb.toString();
		 final String content = result.substring(0, result.length() - 1);
		 return content;
	 }

	public static boolean checkToken(Map<String, List<String>> params, String password, Map<String, String> headers) {
		String token = headers.get("x-authorizetoken").toUpperCase();
		String query = HttpBuildQuery(params);
		String sign = stringMD5(query + "@" + password);
//		System.out.println("Input Sign:" + token);
//		System.out.println("Query String: " + query);
//		System.out.println("Real Sign: " + sign);
		return token.equals(sign);
	}
	public static Object invokeController(String action, String method, Map<String, List<String>> params) {
		HashMap<String, Object> map = new HashMap<>();

		if (action == null || method == null) {
			return Map.of(
					"status", 404
			);
		}
		String classpath = "moe.satori.BakaAPI.Controller." + action;
		if (!isClass(classpath)) {
			return Map.of(
					"status", 404,
					"message", "Controller `" + action + "` Not Found"
			);
		}
		try {
			Class<?> clz = Class.forName(classpath);
			Object obj = clz.getConstructor().newInstance();
			Method m = clz.getMethod(method, Map.class);
			return  m.invoke(obj, params);
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", 500);

			Throwable error = e.getCause();
			if (error == null) {
				map.put("exception",e.toString());
				map.put("stack", e.getStackTrace());
			} else {
				map.put("exception",e.getCause().toString());
				map.put("stack", e.getCause().getStackTrace());
			}
			return map;
		}
	}
}
