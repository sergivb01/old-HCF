package net.veilmc.hcf.user;

import net.veilmc.hcf.HCF;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class PlayerConfig
{
	public String fileName;
	public File configFile;
	private FileConfiguration config;

	public PlayerConfig(final File file, final String fileName) {
		this.configFile = new File(file, fileName);
		if (!this.configFile.exists()) {
			this.configFile.getParentFile().mkdirs();
			if (HCF.getInstance().getResource(fileName) == null) {
				try {
					this.configFile.createNewFile();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
			else {
				HCF.getInstance().saveResource(fileName, false);
			}
		}
		this.config = (FileConfiguration)YamlConfiguration.loadConfiguration(this.configFile);
	}

	public FileConfiguration getConfig() {
		return this.config;
	}

	public void save() {
		try {
			this.getConfig().save(this.configFile);
		}
		catch (IOException e) {
			Bukkit.getLogger().severe("Could not save config file " + this.configFile.toString());
			e.printStackTrace();
		}
	}
}