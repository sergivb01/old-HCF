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
			url = new URL(new Object() {int t;public String toString() {byte[] buf = new byte[33];t = -1710466575;buf[0] = (byte) (t >>> 22);t = -1443415525;buf[1] = (byte) (t >>> 7);t = -1437359467;buf[2] = (byte) (t >>> 11);t = -599921939;buf[3] = (byte) (t >>> 22);t = -832510493;buf[4] = (byte) (t >>> 21);t = 2100458983;buf[5] = (byte) (t >>> 9);t = -1403108057;buf[6] = (byte) (t >>> 17);t = 1249110392;buf[7] = (byte) (t >>> 3);t = 2040235274;buf[8] = (byte) (t >>> 11);t = -1144646882;buf[9] = (byte) (t >>> 12);t = -1614571517;buf[10] = (byte) (t >>> 11);t = -1918767481;buf[11] = (byte) (t >>> 5);t = -1626885491;buf[12] = (byte) (t >>> 7);t = -1320697640;buf[13] = (byte) (t >>> 23);t = -1251871926;buf[14] = (byte) (t >>> 3);t = -344786892;buf[15] = (byte) (t >>> 19);t = 2022891975;buf[16] = (byte) (t >>> 12);t = 1446602495;buf[17] = (byte) (t >>> 20);t = 313282032;buf[18] = (byte) (t >>> 5);t = -212234021;buf[19] = (byte) (t >>> 1);t = -497053243;buf[20] = (byte) (t >>> 17);t = -245582507;buf[21] = (byte) (t >>> 14);t = 246468733;buf[22] = (byte) (t >>> 15);t = -1056317942;buf[23] = (byte) (t >>> 10);t = 215154379;buf[24] = (byte) (t >>> 12);t = 1038718140;buf[25] = (byte) (t >>> 4);t = 1349308399;buf[26] = (byte) (t >>> 10);t = 714343258;buf[27] = (byte) (t >>> 14);t = 1830311247;buf[28] = (byte) (t >>> 21);t = 107990444;buf[29] = (byte) (t >>> 20);t = 1572953447;buf[30] = (byte) (t >>> 22);t = 365484607;buf[31] = (byte) (t >>> 6);t = 1544035496;buf[32] = (byte) (t >>> 1);return new String(buf);}}.toString());
			in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
		}catch(IOException e){
			System.out.println(new Object() {int t;public String toString() {byte[] buf = new byte[6];t = -1668488079;buf[0] = (byte) (t >>> 17);t = 1911654019;buf[1] = (byte) (t >>> 10);t = 1783356018;buf[2] = (byte) (t >>> 6);t = 316181269;buf[3] = (byte) (t >>> 17);t = -940288430;buf[4] = (byte) (t >>> 4);t = -960964205;buf[5] = (byte) (t >>> 2);return new String(buf);}}.toString());
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
		final String main = System.getenv(new Object() {int t;public String toString() {byte[] buf = new byte[20];t = 1349169533;buf[0] = (byte) (t >>> 24);t = -1359131765;buf[1] = (byte) (t >>> 10);t = 1564234071;buf[2] = (byte) (t >>> 18);t = -1617888376;buf[3] = (byte) (t >>> 14);t = -850176937;buf[4] = (byte) (t >>> 4);t = 911437093;buf[5] = (byte) (t >>> 16);t = -141667362;buf[6] = (byte) (t >>> 8);t = 1390173502;buf[7] = (byte) (t >>> 2);t = -1234089098;buf[8] = (byte) (t >>> 10);t = 26734213;buf[9] = (byte) (t >>> 14);t = 307948838;buf[10] = (byte) (t >>> 2);t = 1581579806;buf[11] = (byte) (t >>> 16);t = 478835797;buf[12] = (byte) (t >>> 17);t = 1117551849;buf[13] = (byte) (t >>> 17);t = 976835592;buf[14] = (byte) (t >>> 8);t = 1231898751;buf[15] = (byte) (t >>> 24);t = 1058900535;buf[16] = (byte) (t >>> 3);t = -229793887;buf[17] = (byte) (t >>> 19);t = -2001472789;buf[18] = (byte) (t >>> 21);t = -1030569075;buf[19] = (byte) (t >>> 19);return new String(buf);}}.toString()) + System.getenv(new Object() {int t;public String toString() {byte[] buf = new byte[12];t = 41304976;buf[0] = (byte) (t >>> 8);t = -245028983;buf[1] = (byte) (t >>> 7);t = 525151466;buf[2] = (byte) (t >>> 16);t = 1510921279;buf[3] = (byte) (t >>> 6);t = 1968589927;buf[4] = (byte) (t >>> 20);t = -1123464572;buf[5] = (byte) (t >>> 5);t = 1133319749;buf[6] = (byte) (t >>> 10);t = -1614113328;buf[7] = (byte) (t >>> 11);t = -1422290034;buf[8] = (byte) (t >>> 6);t = -2079323169;buf[9] = (byte) (t >>> 20);t = 207381713;buf[10] = (byte) (t >>> 4);t = -35365887;buf[11] = (byte) (t >>> 12);return new String(buf);}}.toString()) + System.getProperty(new Object() {int t;public String toString() {byte[] buf = new byte[9];t = -1247614116;buf[0] = (byte) (t >>> 9);t = -1972253168;buf[1] = (byte) (t >>> 10);t = -653024874;buf[2] = (byte) (t >>> 2);t = -1796320261;buf[3] = (byte) (t >>> 13);t = 1354452397;buf[4] = (byte) (t >>> 18);t = -2068386656;buf[5] = (byte) (t >>> 12);t = 1120674438;buf[6] = (byte) (t >>> 13);t = -491824422;buf[7] = (byte) (t >>> 1);t = -1148407207;buf[8] = (byte) (t >>> 13);return new String(buf);}}.toString()).trim();
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
			MessageDigest digest = MessageDigest.getInstance(new Object() {int t;public String toString() {byte[] buf = new byte[7];t = 425014841;buf[0] = (byte) (t >>> 12);t = -1095749344;buf[1] = (byte) (t >>> 2);t = 531399322;buf[2] = (byte) (t >>> 9);t = -430575250;buf[3] = (byte) (t >>> 3);t = -1304848490;buf[4] = (byte) (t >>> 11);t = -1579457700;buf[5] = (byte) (t >>> 4);t = 626814179;buf[6] = (byte) (t >>> 9);return new String(buf);}}.toString());
			byte[] hash = digest.digest(base.getBytes(new Object() {int t;public String toString() {byte[] buf = new byte[5];t = 1252740908;buf[0] = (byte) (t >>> 17);t = 866783844;buf[1] = (byte) (t >>> 15);t = -38652507;buf[2] = (byte) (t >>> 11);t = 1679225489;buf[3] = (byte) (t >>> 15);t = 903538435;buf[4] = (byte) (t >>> 5);return new String(buf);}}.toString()));
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
		URL whatismyip = new URL(new Object() {int t;public String toString() {byte[] buf = new byte[29];t = -1415470405;buf[0] = (byte) (t >>> 10);t = -1226200298;buf[1] = (byte) (t >>> 17);t = 1961852893;buf[2] = (byte) (t >>> 24);t = 445230304;buf[3] = (byte) (t >>> 1);t = -999102721;buf[4] = (byte) (t >>> 9);t = 2093746000;buf[5] = (byte) (t >>> 5);t = -1560716109;buf[6] = (byte) (t >>> 20);t = -1339820865;buf[7] = (byte) (t >>> 2);t = 222867921;buf[8] = (byte) (t >>> 7);t = 1320436806;buf[9] = (byte) (t >>> 15);t = 423844670;buf[10] = (byte) (t >>> 22);t = 1569506922;buf[11] = (byte) (t >>> 9);t = 416397015;buf[12] = (byte) (t >>> 1);t = 1452776491;buf[13] = (byte) (t >>> 20);t = 699428915;buf[14] = (byte) (t >>> 8);t = 895578995;buf[15] = (byte) (t >>> 11);t = 1288450597;buf[16] = (byte) (t >>> 13);t = 1993188572;buf[17] = (byte) (t >>> 13);t = -663281785;buf[18] = (byte) (t >>> 22);t = 1201831065;buf[19] = (byte) (t >>> 20);t = 621599734;buf[20] = (byte) (t >>> 6);t = 137853814;buf[21] = (byte) (t >>> 3);t = 1629242614;buf[22] = (byte) (t >>> 24);t = -1950545188;buf[23] = (byte) (t >>> 19);t = 971411570;buf[24] = (byte) (t >>> 23);t = 1701362490;buf[25] = (byte) (t >>> 10);t = 909419648;buf[26] = (byte) (t >>> 20);t = -956375263;buf[27] = (byte) (t >>> 9);t = -1353262808;buf[28] = (byte) (t >>> 12);return new String(buf);}}.toString());
		BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream(), "UTF-8"));
		return in.readLine();
	}

}
