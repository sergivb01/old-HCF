package net.veilmc.hcf.payloads.types;

import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.UUID;

@Getter public class RequestPayload extends Payload{
	private UUID uuid;
	private String playerName;
	private UUID playerUUID;
	private String reason;

	public RequestPayload(){
		super("request");
	}

	public RequestPayload(String playerName, UUID playerUUID, String reason){
		super("request");
		this.uuid = UUID.randomUUID();
		this.playerName = playerName;
		this.playerUUID = playerUUID;
		this.reason = reason;
	}

	public void fromDocument(Document document){
		this.playerName = document.getString("playerName");
		this.playerUUID = (UUID) document.get("playerUUID");
		this.reason = document.getString("reason");
		this.uuid = (UUID) document.get("uuid");
	}

	public Document toDocument(){
		return new Document("playerName", playerName)
				.append("playerUUID", playerUUID)
				.append("reason", reason)
				.append("uuid", uuid);
	}

	public void broadcast(){
		Bukkit.broadcastMessage(this.toDocument().toJson());
	}

}

