package net.veilmc.hcf.database.redis.pubsub;

import lombok.Getter;
import net.veilmc.hcf.HCF;
import net.veilmc.hcf.payloads.PayloadParser;
import net.veilmc.hcf.utils.config.ConfigurationService;
import org.bson.Document;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.Arrays;

public class Subscriber{
	private HCF instance;
	@Getter private JedisPubSub jedisPubSub;
	private Jedis jedis;

	public Subscriber(HCF instance){
		this.instance = instance;
		this.jedis = new Jedis(ConfigurationService.REDIS_HOST, ConfigurationService.REDIS_PORT, 2000);
		if(ConfigurationService.REDIS_AUTH){
			this.jedis.auth(ConfigurationService.REDIS_PASSWORD);
		}
		this.init();
	}

	private void init(){
		jedisPubSub = this.get();
		new Thread(() -> {
			jedis.subscribe(jedisPubSub, ConfigurationService.REDIS_CHANNEL);
			instance.getLogger().info("Redis subscriber has subscribed to " + ConfigurationService.REDIS_CHANNEL);
		}).start(); //Create subscriber in new Thread to avoid blocking main one
	}

	private JedisPubSub get(){
		return new JedisPubSub(){
			@Override
			public void onMessage(final String channel, final String message){
				final String[] args = message.split(";");
				final String command = args[0].toLowerCase();
				//TODO: Implement encryption (?)

				if(command.equalsIgnoreCase("payload")){
					Document document = Document.parse(args[1]);
					if(document != null){
						PayloadParser.parse(document);
					}
					return;
				}

				instance.getLogger().warning("Recived unknown redis message! " + Arrays.toString(args));
			}

			@Override
			public void onPMessage(String s, String s1, String s2){

			}

			@Override
			public void onSubscribe(String s, int i){

			}

			@Override
			public void onUnsubscribe(String s, int i){

			}

			@Override
			public void onPUnsubscribe(String s, int i){

			}

			@Override
			public void onPSubscribe(String s, int i){

			}

		};
	}


}