/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.analytics;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

// import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
// import net.fabricmc.loader.api.FabricLoader;
// import net.fabricmc.loader.api.ModContainer;
// import net.fabricmc.loader.api.Version;
// import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.language.LanguageManager;


public final class PlausibleAnalytics
{
	/* private static final Gson GSON = new Gson();
	private static final Logger LOGGER = LoggerFactory.getLogger("Plausible");

	private static final String MOD_ID = "wurst";
	private static final URI API_ENDPOINT =
		URI.create("https://plausible.wurstclient.net/api/event");

	private final HttpClient httpClient =
		HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
	private final LinkedBlockingQueue<PlausibleEvent> eventQueue =
		new LinkedBlockingQueue<>();
	private final JsonObject sessionProps = new JsonObject();
	private final AnalyticsConfigFile configFile;
	private boolean enabled = true;

	public PlausibleAnalytics(Path configFile)
	{
		this.configFile = new AnalyticsConfigFile(configFile);
		this.configFile.load(this);
		
		sessionProp("version", getVersion("wurst"));
		sessionProp("short_version", getShortVersion("wurst"));
		sessionProp("mc_version", getVersion("minecraft"));
		sessionProp("fabric_api_version", getVersion("fabric-api"));
		sessionProp("fabric_loader_version", getVersion("fabricloader"));
		sessionProp("modmenu_version", getVersion("modmenu"));
		sessionProp("sodium_version", getVersion("sodium"));
		sessionProp("sinytra_connector_version", getVersion("connector"));
		
		Thread.ofPlatform().daemon().name("Plausible")
			.start(this::runBackgroundLoop);
		
		ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE
			.register(this::onWorldChange);
	}
	
	private String getVersion(String modId)
	{
		return FabricLoader.getInstance().getModContainer(modId)
			.map(ModContainer::getMetadata).map(ModMetadata::getVersion)
			.map(Version::toString).orElse(null);
	}
	
	private String getShortVersion(String modId)
	{
		String version = getVersion(modId);
		if(version != null && version.contains("-MC"))
			return version.substring(0, version.indexOf("-MC"));
		
		return version;
	}
	
	private void onWorldChange(Minecraft client, ClientLevel world)
	{
		sessionProp("language", getLanguage(client));
		sessionProp("game_type", getGameType(client));
		pageview("/in-game");
	}
	
	private String getLanguage(Minecraft client)
	{
		return Optional.ofNullable(client.getLanguageManager())
			.map(LanguageManager::getSelected).map(String::toLowerCase)
			.orElse(null);
	}
	
	private String getGameType(Minecraft client)
	{
		ServerData server = client.getCurrentServer();
		if(server == null)
			return "singleplayer";
		if(server.isLan())
			return "lan";
		if(server.isRealm())
			return "realms";
		return "multiplayer";
	}
	
	public boolean isEnabled()
	{
		return enabled;
	}
	
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
		configFile.save(this);
	}
	
	private boolean isDebugMode()
	{
		return FabricLoader.getInstance().isDevelopmentEnvironment()
			|| System.getProperty("fabric.client.gametest") != null;
	}
	
	private void runBackgroundLoop()
	{
		while(!Thread.currentThread().isInterrupted())
			try
			{
				sendEvent(eventQueue.take());
				Thread.sleep(50);
				
			}catch(InterruptedException e)
			{
				Thread.currentThread().interrupt();
				break;
				
			}catch(Exception e)
			{
				LOGGER.error("Plausible error", e);
			}
	}
	
	private void sendEvent(PlausibleEvent event)
	{
		String body = createRequestBody(event);
		if(isDebugMode())
		{
			LOGGER.info("Event ({} props): {}", event.props().size(), body);
			return;
		}
		
		HttpRequest request = HttpRequest.newBuilder().uri(API_ENDPOINT)
			.header("User-Agent", getUserAgent())
			.header("Content-Type", "application/json")
			.POST(HttpRequest.BodyPublishers.ofString(body))
			.timeout(Duration.ofSeconds(5)).build();
		
		httpClient.sendAsync(request, HttpResponse.BodyHandlers.discarding())
			.exceptionally(ex -> null);
	}
	
	private String getUserAgent()
	{
		// Same as the "Operating System" entry in Minecraft crash reports.
		return System.getProperty("os.name") + " ("
			+ System.getProperty("os.arch") + ") version "
			+ System.getProperty("os.version");
	}
	
	private String createRequestBody(PlausibleEvent event)
	{
		JsonObject body = new JsonObject();
		body.addProperty("name", event.name());
		body.addProperty("url", event.url());
		body.addProperty("domain", MOD_ID);
		if(event.props() != null && !event.props().isEmpty())
			body.add("props", event.props());
		
		return GSON.toJson(body);
	}
	

	public void pageview(String path)
	{
		event("pageview", path, null);
	}

	public void pageview(String path, Map<String, String> props)
	{
		event("pageview", path, props);
	}
	

	public void event(String name, String path)
	{
		event(name, path, null);
	}

	public void event(String name, String path, Map<String, String> props)
	{
		if(!isEnabled() || name == null || path == null)
			return;
		
		String url = buildURL(path);
		JsonObject jsonProps = buildJsonProps(props);
		eventQueue.offer(new PlausibleEvent(name, url, jsonProps));
	}
	
	private String buildURL(String path)
	{
		String adjustedPath = path.startsWith("/") ? path : "/" + path;
		return "mod://" + MOD_ID + adjustedPath;
	}
	
	private JsonObject buildJsonProps(Map<String, String> props)
	{
		JsonObject jsonProps = sessionProps.deepCopy();
		if(props == null || props.isEmpty())
			return jsonProps;
		
		for(Map.Entry<String, String> entry : props.entrySet())
		{
			String key = entry.getKey();
			if(isDebugMode() && key.length() > 300)
				LOGGER.warn("Property key is too long ({} characters): {}",
					key.length(), key);
			
			String value = entry.getValue();
			if(isDebugMode() && value.length() > 2000)
				LOGGER.warn("Property value is too long ({} characters): {}",
					value.length(), value);
			
			if(key != null && value != null)
				jsonProps.addProperty(key, value);
		}
		
		if(isDebugMode() && jsonProps.size() > 30)
			LOGGER.warn("Too many properties ({})", jsonProps.size());
		
		return jsonProps;
	}

	public void sessionProp(String name, String value)
	{
		if(name != null && value != null)
			sessionProps.addProperty(name, value);
	}

	public void removeSessionProp(String name)
	{
		if(name != null)
			sessionProps.remove(name);
	}
	
	private record PlausibleEvent(String name, String url, JsonObject props)
	{} */
}
