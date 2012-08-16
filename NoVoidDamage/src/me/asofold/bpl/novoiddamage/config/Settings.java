package me.asofold.bpl.novoiddamage.config;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import me.asofold.bpl.novoiddamage.config.compatlayer.CompatConfig;
import me.asofold.bpl.novoiddamage.config.compatlayer.CompatConfigFactory;
import me.asofold.bpl.novoiddamage.config.compatlayer.ConfigUtil;

public class Settings {
	/**Exclude worlds matching this name exactly.*/
	public final Set<String> excludeExact = new HashSet<String>();
	/**Exclude worlds whose name ends on one of these suffixes.*/
	public final Set<String> excludeSuffix = new HashSet<String>();
	/**Log actions taken.*/
	public boolean log = true;
	
	public Settings(){
		excludeSuffix.add("_the_end");
	}
	
	public void fromConfig(CompatConfig cfg){
		Settings ref = new Settings();
		ConfigUtil.readStringSetFromList(cfg, "exclude-worlds.exact", excludeExact, true, true, false);
		ConfigUtil.readStringSetFromList(cfg, "exclude-worlds.suffix", excludeSuffix, true, true, false);
		log = cfg.getBoolean("log", ref.log);
	}
	
	public static CompatConfig getDefaultConfig(){
		CompatConfig cfg = CompatConfigFactory.getConfig(null);
		Settings ref = new Settings();
		cfg.set("exclude-worlds.exact", new LinkedList<String>(ref.excludeExact));
		cfg.set("exclude-worlds.suffix", new LinkedList<String>(ref.excludeSuffix));
		cfg.set("log", ref.log);
		return cfg;
	}
}
