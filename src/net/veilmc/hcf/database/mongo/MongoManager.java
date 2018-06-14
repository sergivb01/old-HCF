package net.veilmc.hcf.database.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import lombok.Getter;
import net.veilmc.hcf.HCF;
import net.veilmc.hcf.faction.FactionMember;
import net.veilmc.hcf.faction.type.PlayerFaction;
import net.veilmc.hcf.user.FactionUser;
import net.veilmc.hcf.utils.config.ConfigurationService;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;

@Getter
public class MongoManager{
	private HCF plugin;
	private MongoClient mongoClient;
	private static MongoDatabase mongoDatabase;
	private static MongoCollection<Document> payloads;
	private static MongoCollection<Document> playerProfiles;
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
		playerProfiles = mongoDatabase.getCollection("playerProfiles");
		factionProfiles = mongoDatabase.getCollection("factionProfiles");

		payloads.createIndex(Indexes.descending("timestamp"));
		playerProfiles.createIndex(Indexes.descending("kills", "deaths", "spawnTokens"));
	}

	public static void addPayload(Document document){
		payloads.insertOne(document);
	}

	public static void savePlayer(FactionUser factionUser){
		Document found = playerProfiles.find(
				new Document("userUUID", factionUser.getUserUUID().toString())
		).first();

		Player player = factionUser.getPlayer();

		Map<String, Object> map = factionUser.serialize();
		map.remove("faction-chat-spying");
		map.remove("shownScoreboardScores");
		map.put("online", player != null && player.isOnline());

		Document document = new Document(map);

		if(found != null){
			Bson updateOperation = new Document("$set", document);
			playerProfiles.updateOne(found, updateOperation);
		}else{
			playerProfiles.insertOne(new Document(map));
		}
	}

	public static void saveFaction(PlayerFaction playerFaction){
		Document found = factionProfiles.find(
				new Document("uniqueID", playerFaction.getUniqueID().toString())
		).first();

		Map<String, Object> map = playerFaction.serialize();
		map.remove("members");
		map.remove("claims");
		map.remove("home");
		map.put("online", playerFaction.getOnlineMembers().size());

		Player player = Bukkit.getPlayer(playerFaction.getUniqueID());

		Document members = new Document();
		for(FactionMember factionMember : playerFaction.getMembers().values()){
			members.append(factionMember.getName(),
					new Document(
							"role", factionMember.getRole().toString()
					).append("uuid", factionMember.getUniqueId())
					.append("online", player != null && player.isOnline())
			);
		}

		if(playerFaction.getHome() != null){
			Location loc = playerFaction.getHome();
			map.put("home", (int) loc.getX() + ";" + (int) loc.getY() + ";" + (int) loc.getZ());
		}

		if(found != null){
			Bson updateOperation = new Document("$set", new Document(map).append("members", members));
			factionProfiles.updateOne(found, updateOperation);
		}else{
			factionProfiles.insertOne(new Document(map));
		}
	}

	public static MongoDatabase getMongoDatabase(){
		return mongoDatabase;
	}

}
