package net.veilmc.hcf.database.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import net.veilmc.hcf.HCF;
import net.veilmc.hcf.user.FactionUser;
import net.veilmc.hcf.utils.config.ConfigurationService;
import org.bson.Document;

@Getter
public class MongoManager{
	private HCF plugin;
	private MongoClient mongoClient;
	private static MongoDatabase mongoDatabase;
	private static MongoCollection<Document> payloads;
	private static MongoCollection<Document> factionProfiles;

	public MongoManager(HCF plugin){
		this.plugin = plugin;
		MongoClientURI uri;
		if(ConfigurationService.MONGO_AUTH){
			uri = new MongoClientURI("mongodb://" + ConfigurationService.MONGO_USERNAME + ":" + ConfigurationService.MONGO_PASSWORD + "@" + ConfigurationService.MONGO_HOST + ":" + ConfigurationService.MONGO_PORT + "/?authSource=" + ConfigurationService.MONGO_DATABASE);
		}else{
			uri = new MongoClientURI("mongodb://" + ConfigurationService.MONGO_HOST + ":" + ConfigurationService.MONGO_PORT + "/?authSource=" + ConfigurationService.MONGO_DATABASE);
		}

		mongoClient = new MongoClient(uri);
		mongoDatabase = mongoClient.getDatabase(ConfigurationService.MONGO_DATABASE);

		payloads = mongoDatabase.getCollection("payloads");
		factionProfiles = mongoDatabase.getCollection("factionprofiles");
	}

	public static void addPayload(Document document){
		payloads.insertOne(document);
	}

	public static void savePlayer(FactionUser factionUser){
		Document found = factionProfiles.find(
				new Document("uuid", factionUser.getUserUUID())
		).first();

		if(found != null){
			factionProfiles.updateOne(found, new Document(factionUser.serialize()));
		}else{
			factionProfiles.insertOne(new Document(factionUser.serialize()));
		}
	}

	public static MongoDatabase getMongoDatabase(){
		return mongoDatabase;
	}

}
