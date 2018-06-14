package net.veilmc.hcf.payloads;

import net.veilmc.hcf.HCF;
import net.veilmc.hcf.payloads.types.*;
import org.bson.Document;

public class PayloadParser{

	public static void parse(Document document){
		String server = document.getString("server");
		String type = document.getString("type");


		Payload payload = getPayloadFromType(type);
		if(payload == null){
			HCF.getInstance().getLogger().severe("Failed to parse payload - " + document.toJson());
			return;
		}

		payload.fromDocument(document);
		if(payload instanceof StatusPayload){ //Do not log or either broadcast server status.
			Cache.setStatus((StatusPayload) payload);
			return;
		}

		payload.broadcast();

		Cache.addPayload(payload);
	}

	public static Payload getPayloadFromType(String type){
		switch(type.toLowerCase()){
			case "status":
				return new StatusPayload();
			case "staffchat":
				return new StaffChatPayload();
			case "report":
				return new ReportPayload();

			case "request":
				return new RequestPayload();

			case "serverswitch":
				return new ServerSwitchPayload();

		}
		return null;
	}

}
