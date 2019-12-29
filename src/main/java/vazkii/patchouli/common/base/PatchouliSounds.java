package vazkii.patchouli.common.base;

import net.minecraft.util.Identifier;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class PatchouliSounds {
	
	public static SoundEvent book_open;
	public static SoundEvent book_flip;

	public static void preInit() {
		book_open = register("book_open");
		book_flip = register("book_flip");
		
		MinecraftForge.EVENT_BUS.register(PatchouliSounds.class);
	}
	
	public static SoundEvent register(String name) {
		Identifier loc = new Identifier(Patchouli.MOD_ID, name);
		SoundEvent e = new SoundEvent(loc).setRegistryName(loc);
		
		return e;
	}
	
	@SubscribeEvent
	public static void register(RegistryEvent.Register<SoundEvent> event) {
		event.getRegistry().register(book_open);
		event.getRegistry().register(book_flip);
	}
	
	public static SoundEvent getSound(String sound, SoundEvent fallback) {
		Identifier key = new Identifier(sound);
		return ForgeRegistries.SOUND_EVENTS.containsKey(key)
				? ForgeRegistries.SOUND_EVENTS.getValue(key)
				: fallback;
	}
	
}
