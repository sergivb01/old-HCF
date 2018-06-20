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
	public boolean hasValidLicense() throws IOException{
		Map<String, String> map = new HashMap<>();
		URL url;
		BufferedReader in;
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

		String address = getPublicIP();
		String hwid;
		try{
			hwid = generateHWID();
		}catch(NoSuchAlgorithmException e){
			System.out.println("Failed to generate hwid.");
			return false;
		}

		System.out.println("=#=#=#=#=#=#=#=#=#=#=#=#=#=#=#=#=#=#=#=#=#=#=#=#=#=#=#=#=#=#=#=#=#=#=#=#");
		System.out.println("License checker - Here are your personal details:");
		System.out.println("Public IP Address: " + address);
		System.out.println("HWID: " + hwid);
		System.out.println("Please send this details to a plugin developer to get your plugin version activated.");
		System.out.println("=#=#=#=#=#=#=#=#=#=#=#=#=#=#=#=#=#=#=#=#=#=#=#=#=#=#=#=#=#=#=#=#=#=#=#=#");

		return map.containsKey(hwid) && map.get(hwid).equals(address);
	}

	private String generateHWID() throws UnsupportedEncodingException, NoSuchAlgorithmException{
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

	private String sha256(String base) {
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

	private String getPublicIP() throws IOException{
		//return new BufferedReader(new InputStreamReader(new URL("https://checkip.amazonaws.com").openStream())).readLine();
		URL whatismyip = new URL("https://checkip.amazonaws.com");
		BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream(), "UTF-8"));
		return in.readLine();
	}

}
