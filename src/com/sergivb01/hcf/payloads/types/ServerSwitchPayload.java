package com.sergivb01.hcf.payloads.types;

import lombok.Getter;
import org.bson.Document;

import java.util.UUID;

@Getter
public class ServerSwitchPayload extends Payload{
	private UUID uuid;

	private String playerName;
	private UUID playerUUID;
	private String status;

	public ServerSwitchPayload(){
		super("serverswitch");
	}

	public ServerSwitchPayload(UUID playerUUID, String playerName, String status){
		super("serverswitch");
		this.uuid = UUID.randomUUID();
		this.playerUUID = playerUUID;
		this.playerName = playerName;
		this.status = status;
	}

	@Override
	public void fromDocument(Document document){
		this.uuid = (UUID) document.get("uuid");
		this.playerName = document.getString("playerName");
		this.playerUUID = (UUID) document.get("playerUUID");
		this.status = document.getString("status");
		this.server = document.getString("server");
	}

	@Override
	public Document toDocument(){
		return new Document("playerName", playerName)
				.append("playerUUID", playerUUID)
				.append("status", status)
				.append("uuid", uuid)
				.append("server", server);
	}

	public void broadcast(){
		//Bukkit.broadcastMessage(this.toDocument().toJson());
	}

}
