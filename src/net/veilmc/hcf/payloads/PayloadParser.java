package net.veilmc.hcf.payloads;

import net.veilmc.hcf.HCF;
import net.veilmc.hcf.payloads.types.*;
import net.veilmc.hcf.utils.config.ConfigurationService;
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
		payload.broadcast();

		if(!server.equalsIgnoreCase(ConfigurationService.SERVER_NAME)){
			Cache.addPayload(payload);
		}
	}

	public static Payload getPayloadFromType(String type){
		switch(type.toLowerCase()){
			case "report":
				return new ReportPayload();

			case "request":
				return new RequestPayload();

			case "serverswitch":
				return new ServerSwitchPayload();

			case "staffchat":
				return new StaffChatPayload();
		}
		return null;
	}

}
