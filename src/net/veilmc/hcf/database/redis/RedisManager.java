package net.veilmc.hcf.database.redis;

import lombok.Getter;
import net.veilmc.hcf.HCF;
import net.veilmc.hcf.database.redis.pubsub.Publisher;
import net.veilmc.hcf.database.redis.pubsub.Subscriber;


public class RedisManager{
	@Getter public static Publisher publisher;
	@Getter public static Subscriber subscriber;
	private HCF plugin;

	public RedisManager(HCF plugin){
		this.plugin = plugin;
		init();
	}

	private void init(){
		publisher = new Publisher();
		subscriber = new Subscriber(plugin);
		plugin.getLogger().info("Registered sub and pub");
	}


}