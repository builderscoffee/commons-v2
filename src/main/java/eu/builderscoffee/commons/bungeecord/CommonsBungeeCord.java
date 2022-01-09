package eu.builderscoffee.commons.bungeecord;

import eu.builderscoffee.api.common.data.DataManager;
import eu.builderscoffee.api.common.data.tables.Profil;
import eu.builderscoffee.api.common.redisson.Redis;
import eu.builderscoffee.api.common.redisson.RedisCredentials;
import eu.builderscoffee.api.common.redisson.RedisTopic;
import eu.builderscoffee.api.common.redisson.infos.Server;
import eu.builderscoffee.commons.bungeecord.commands.MoveCommand;
import eu.builderscoffee.commons.bungeecord.commands.PBanCommand;
import eu.builderscoffee.commons.bungeecord.commands.PPardonCommand;
import eu.builderscoffee.commons.bungeecord.configuration.MessageConfiguration;
import eu.builderscoffee.commons.bungeecord.configuration.PermissionConfiguration;
import eu.builderscoffee.commons.bungeecord.listeners.bukkit.ConnexionListener;
import eu.builderscoffee.commons.bungeecord.listeners.bukkit.PlayerListener;
import eu.builderscoffee.commons.bungeecord.listeners.redisson.HeartBeatListener;
import eu.builderscoffee.commons.bungeecord.listeners.redisson.ServersListListener;
import eu.builderscoffee.commons.common.configuration.SettingsConfig;
import eu.builderscoffee.commons.common.utils.LuckPermsUtils;
import eu.builderscoffee.commons.common.utils.ProfilCache;
import lombok.Getter;
import lombok.val;
import net.luckperms.api.LuckPermsProvider;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import org.redisson.api.RSortedSet;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Objects;

import static eu.builderscoffee.api.common.configuration.Configuration.readOrCreateConfiguration;

/**
 * This class is the main class of the Commons Proxy plugin
 */
@Getter
public class CommonsBungeeCord extends Plugin {

    @Getter
    private static CommonsBungeeCord instance;

    //Configuration
    private Map<Profil.Languages, MessageConfiguration> messages;
    private PermissionConfiguration permissions;

    private SettingsConfig settings;

    // Cache des profils
    private ProfilCache profilCache = new ProfilCache();

    @Override
    public void onEnable() {

        // Instance
        instance = this;

        // Configuration
        messages = readOrCreateConfiguration(this.getDescription().getName(), MessageConfiguration.class, Profil.Languages.class);
        permissions = readOrCreateConfiguration(this.getDescription().getName(), PermissionConfiguration.class);
        settings = readOrCreateConfiguration(this.getDescription().getName(), SettingsConfig.class);

        // Initialize Redisson
        val redisCredentials = new RedisCredentials()
                .setClientName(settings.getRedis().getClientName())
                .setIp(settings.getRedis().getIp())
                .setPassword(settings.getRedis().getPassword())
                .setPort(settings.getRedis().getPort());

        Redis.Initialize(ProxyServer.getInstance().getName(), redisCredentials, 0, 0);

        // Redisson Listeners
        Redis.subscribe(RedisTopic.HEARTBEATS, new HeartBeatListener());
        Redis.subscribe(RedisTopic.BUNGEECORD, new ServersListListener());

        // Database
        getLogger().info("Connexion à la base de donnée...");
        DataManager.init(settings.getMySQL().toHikari());

        // Service Provider
        LuckPermsUtils.init(LuckPermsProvider.get());

        // Add started servers
        final RSortedSet<Server> servers = Redis.getRedissonClient().getSortedSet("servers");
        servers.stream()
                .filter(s -> Objects.isNull(ProxyServer.getInstance().getServerInfo(s.getHostName())))
                .filter(s -> s.getServerType().equals(Server.ServerType.SPIGOT))
                .forEach(s -> {
                    val si = ProxyServer.getInstance().constructServerInfo(s.getHostName(), new InetSocketAddress(s.getHostAddress(), s.getHostPort()), "", false);
                    ProxyServer.getInstance().getServers().put(si.getName(), si);
                });

        // Commands
        getProxy().getPluginManager().registerCommand(this, new PBanCommand());
        getProxy().getPluginManager().registerCommand(this, new PPardonCommand());
        getProxy().getPluginManager().registerCommand(this, new MoveCommand());

        // Listeners
        getProxy().getPluginManager().registerListener(this, new ConnexionListener());
        getProxy().getPluginManager().registerListener(this, new PlayerListener());
    }

    @Override
    public void onDisable() {
        DataManager.getHikari().close();
        Redis.close();
    }
}
