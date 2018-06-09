package net.veilmc.hcf.utils;

import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class ChatUtil{
	public static String translateColors(String paramString){
		return StringEscapeUtils.unescapeJava(ChatColor.translateAlternateColorCodes('&', paramString));
	}

	public static List<String> translateColors(List<String> paramStrings){
		List<String> strings = new ArrayList<>();
		for(String string : paramStrings){
			strings.add(translateColors(string));
		}
		return strings;
	}
}