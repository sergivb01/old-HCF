package com.sergivb01.hcf.timer;

import com.sergivb01.hcf.HCF;
import com.sergivb01.hcf.timer.event.TimerExpireEvent;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class TimerRunnable{
	private final UUID represented;
	private final Timer timer;
	private BukkitTask bukkitTask;
	private long expiryMillis;
	private long pauseMillis;

	public TimerRunnable(Timer timer, long duration){
		this.represented = null;
		this.timer = timer;
		this.setRemaining(duration);
	}

	public TimerRunnable(UUID playerUUID, Timer timer, long duration){
		this.represented = playerUUID;
		this.timer = timer;
		this.setRemaining(duration);
	}

	public Timer getTimer(){
		return this.timer;
	}

	public long getRemaining(){
		return this.getRemaining(false);
	}

	public void setRemaining(long remaining){
		this.setExpiryMillis(remaining);
	}

	public long getRemaining(boolean ignorePaused){
		if(!ignorePaused && this.pauseMillis != 0){
			return this.pauseMillis;
		}
		return this.expiryMillis - System.currentTimeMillis();
	}

	public long getExpiryMillis(){
		return this.expiryMillis;
	}

	private void setExpiryMillis(long remainingMillis){
		long expiryMillis = System.currentTimeMillis() + remainingMillis;
		if(expiryMillis == this.expiryMillis){
			return;
		}
		this.expiryMillis = expiryMillis;
		if(remainingMillis > 0){
			if(this.bukkitTask != null){
				this.bukkitTask.cancel();
			}
			this.bukkitTask = new BukkitRunnable(){

				public void run(){
					TimerExpireEvent expireEvent = new TimerExpireEvent(TimerRunnable.this.represented, TimerRunnable.this.timer);
					Bukkit.getPluginManager().callEvent(expireEvent);
				}
			}.runTaskLater(HCF.getPlugin(), remainingMillis / 50);
		}
	}

	public long getPauseMillis(){
		return this.pauseMillis;
	}

	public void setPauseMillis(long pauseMillis){
		this.pauseMillis = pauseMillis;
	}

	public boolean isPaused(){
		return this.pauseMillis != 0;
	}

	public void setPaused(boolean paused){
		if(paused == this.isPaused()){
			return;
		}
		if(paused){
			this.pauseMillis = this.getRemaining(true);
			this.cancel();
		}else{
			this.setExpiryMillis(this.pauseMillis);
			this.pauseMillis = 0;
		}
	}

	public void cancel(){
		if(this.bukkitTask != null){
			this.bukkitTask.cancel();
		}
	}

}

