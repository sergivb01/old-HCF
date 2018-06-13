package net.veilmc.hcf.payloads;

import net.veilmc.hcf.payloads.types.ReportPayload;
import net.veilmc.hcf.payloads.types.RequestPayload;
import net.veilmc.hcf.payloads.types.Payload;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Cache{
	public static Map<UUID, Long> commandDelay = new HashMap<>();
	public static List<Payload> payloads = new ArrayList<>();

	public static boolean canExecute(Player player){
		return !commandDelay.containsKey(player.getUniqueId()) || System.currentTimeMillis() - commandDelay.get(player.getUniqueId()) > TimeUnit.MINUTES.toMillis(5);
	}

	public static void addPlayerDelay(Player player){
		commandDelay.put(player.getUniqueId(), System.currentTimeMillis());
	}

	public static boolean addPayload(Payload payload){
		return payloads.add(payload);
	}

	public List<Payload> getPayloads(String type){
		switch(type.toLowerCase()){
			case "report":
				return payloads.stream().filter(payload -> payload instanceof ReportPayload).collect(Collectors.toList());

			case "request":
				return payloads.stream().filter(payload -> payload instanceof RequestPayload).collect(Collectors.toList());

			case "serverswitch":
				return payloads.stream().filter(payload -> payload instanceof RequestPayload).collect(Collectors.toList());

		}
		return null;
	}


}
