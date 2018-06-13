package net.veilmc.hcf.payloads.types;

import lombok.Getter;
import org.bson.Document;

import java.util.UUID;

 @Getter public class ServerSwitchPayload extends Payload{
	private UUID uuid;
	private String playerName;
	private UUID playerUUID;
	private String status;

	public ServerSwitchPayload(){
		super("serverswitch");
	}

	public ServerSwitchPayload(String playerName, UUID playerUUID, String status){
		super("serverswitch");
		this.uuid = UUID.randomUUID();
		this.playerName = playerName;
		this.playerUUID = playerUUID;
		this.status = status;
	}

	@Override
	public void fromDocument(Document document){
		this.playerName = document.getString("playername");
		this.playerUUID = (UUID) document.get("playerUUID");
		this.status = document.getString("status");
	}

	@Override
	public Document toDocument(){
		return new Document("playerName", playerName)
				.append("playerUUID", playerUUID)
				.append("status", status)
				.append("uuid", uuid);
	}

	 public void broadcast(){
		 //TODO: Broadcast
	 }

}
