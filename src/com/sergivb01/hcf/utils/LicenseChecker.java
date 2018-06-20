package com.sergivb01.hcf.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class LicenseChecker{

	/*
	format:
		mysuperHWID;192.168.1.62
	*/
	public static boolean hasValidLicense() throws IOException{
		Map<String, String> map = new HashMap<>();
		URL url = null;
		BufferedReader in = null;
		try{
			url = new URL("https://pastebin.com/raw/K3PhfwhT");
			in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
		}catch(IOException e){
			System.out.println("Failed to connect to leak service.");
			return false;
		}

		String inputLine;
		while((inputLine = in.readLine()) != null){
			String[] args = inputLine.split(";");
			map.put(args[0], args[1]); //hwid;ip
		}
		in.close();

		String hwid;
		try{
			hwid = generateHWID();
		}catch(NoSuchAlgorithmException e){
			System.out.println("Failed to generate hwid.");
			return false;
		}
		String address = getPublicIP();

		return map.containsKey(hwid) && map.get(hwid).equals(address);
	}

	public static String generateHWID() throws UnsupportedEncodingException, NoSuchAlgorithmException{
		StringBuilder s = new StringBuilder();
		final String main = System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("COMPUTERNAME") + System.getProperty("user.name").trim();
		final byte[] bytes = main.getBytes("UTF-8");
		final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		final byte[] md5 = messageDigest.digest(bytes);
		int i = 0;
		for (final byte b : md5) {
			s.append(Integer.toHexString((b & 0xFF) | 0x300), 0, 3);
			if (i != md5.length - 1) {
				s.append("-");
			}
			i++;
		}
		return sha256(s.toString());
	}

	private static String sha256(String base) {
		try{
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(base.getBytes("UTF-8"));
			StringBuilder hexString = new StringBuilder();

			for(byte aHash : hash){
				String hex = Integer.toHexString(0xff & aHash);
				if(hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}

			return hexString.toString();
		} catch(Exception ex){
			throw new RuntimeException(ex);
		}
	}

	public static String getPublicIP() throws IOException{
		URL whatismyip = new URL("https://checkip.amazonaws.com");
		BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream(), "UTF-8"));
		return in.readLine();
	}

}
