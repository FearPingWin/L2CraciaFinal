/* 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package net.sf.l2j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class contains global server configuration.<br>
 * It has static final fields initialized from configuration files.<br>
 * It's initialized at the very begin of startup, and later JIT will optimize
 * away debug/unused code.
 * 
 * @author mkizub
 */
public final class Config
{
    private final static Log _log = LogFactory.getLog(Config.class.getName());
    
    //*******************************************************************************************	
    public static final String  CONFIGURATION_FILE          = "./config/server.properties";
    //*******************************************************************************************
    public static int           GAME_SERVER_LOGIN_PORT;
    public static String        GAME_SERVER_LOGIN_HOST;
    public static String        INTERNAL_HOSTNAME;
    public static String        INTERNAL_NETWORKS;
    public static String        EXTERNAL_HOSTNAME;
    public static String        OPTIONAL_NETWORKS;
	public static int 			REQUEST_ID; 					// ID for request to the server
	public static boolean 		RESERVE_HOST_ON_LOGIN = false;
	public static boolean 		ACCEPT_ALTERNATE_ID; 			// Accept alternate ID for server ?
    public static Pattern 		CNAME_PATTERN;	 				// Character name template
    public static Pattern 		PET_NAME_PATTERN;  				// Pet name template
    public static Pattern 		CLAN_ALLY_NAME_PATTERN;			// Clan and ally name template      
    public static Pattern 		TITLE_PATTERN;    				// Clan title template
    public static int 			MAX_CHARACTERS_NUMBER_PER_ACCOUNT; // Maximum number of characters per account
    public static int 			PORT_GAME;						// Game Server ports
    public static String 		GAMESERVER_HOSTNAME;			// Hostname of the Game Server
    public static String 		DATABASE_DRIVER;				// Driver to access to database
    public static String 		DATABASE_URL;					// Path to access to database
    public static String 		DATABASE_LOGIN;					// Database login
    public static String 		DATABASE_PASSWORD;				// Database password
    public static int 			DATABASE_MAX_CONNECTIONS;		// Maximum number of connections to the database
    public static int   		MAXIMUM_ONLINE_USERS;			// Maximum number of players allowed to play simultaneously on server
    public static boolean    	SAFE_REBOOT = false;			// Safe mode will disable some feature during restart/shutdown to prevent exploit
    public static int        	SAFE_REBOOT_TIME = 10;
    public static boolean    	SAFE_REBOOT_DISABLE_ENCHANT = false;
    public static boolean    	SAFE_REBOOT_DISABLE_TELEPORT = false;
    public static boolean    	SAFE_REBOOT_DISABLE_CREATEITEM = false;
    public static boolean    	SAFE_REBOOT_DISABLE_TRANSACTION = false;
    public static boolean    	SAFE_REBOOT_DISABLE_PC_ITERACTION = false;
    public static boolean    	SAFE_REBOOT_DISABLE_NPC_ITERACTION = false;
    public static boolean 		NETWORK_TRAFFIC_OPTIMIZATION;
    public static int     		NETWORK_TRAFFIC_OPTIMIZATION_MS;
    public static int           MIN_PROTOCOL_REVISION;			// protocol revision
    public static int           MAX_PROTOCOL_REVISION;
	public static boolean 		FLOOD_PROTECTION = false;
	public static int 			PACKET_LIMIT;
	public static int 			PACKET_TIME_LIMIT;
    public static File    		DATAPACK_ROOT;					// Datapack root directory
    public static int           NEW_NODE_ID;
    public static int           SELECTED_NODE_ID;
    public static int           LINKED_NODE_ID;
    public static String        NEW_NODE_TYPE;
    /** JMX Admin */ 
    /** Properties administration port*/
    public static  int JMX_TCP_PORT ;
    /** Properties port adaptor Http*/
    public static  int JMX_HTTP_PORT ;
    /** Property Keystore */
    public static  String JMX_KEYSTORE ;
    /** Property Keystore Password */
    public static  String JMX_KEYSTORE_PASSWORD ;
    
    //*******************************************************************************************    
    public static void loadConfiguration()
    {
        _log.info("loading " + CONFIGURATION_FILE);
	    try {
	        Properties serverSettings    = new Properties();
			InputStream is               = new FileInputStream(new File(CONFIGURATION_FILE));
			serverSettings.load(is);
			is.close();
			
			GAME_SERVER_LOGIN_HOST  = serverSettings.getProperty("LoginHost","127.0.0.1");
			GAME_SERVER_LOGIN_PORT  = Integer.parseInt(serverSettings.getProperty("LoginPort","9013"));
			REQUEST_ID              = Integer.parseInt(serverSettings.getProperty("RequestServerID","0"));
			ACCEPT_ALTERNATE_ID     = Boolean.parseBoolean(serverSettings.getProperty("AcceptAlternateID","True"));
            PORT_GAME               = Integer.parseInt(serverSettings.getProperty("GameserverPort", "7777"));
            try
            {
                CNAME_PATTERN       = Pattern.compile(serverSettings.getProperty("CnameTemplate", "[A-Za-z0-9\\-]{3,16}"));
            }
            catch (PatternSyntaxException e)
            {
                _log.warn("Character name pattern is wrong!",e);
                CNAME_PATTERN  = Pattern.compile("[A-Za-z0-9\\-]{3,16}");
            }
            
            try
            {
                PET_NAME_PATTERN    = Pattern.compile(serverSettings.getProperty("PetNameTemplate", "[A-Za-z0-9\\-]{3,16}"));
            }
            catch (PatternSyntaxException e)
            {
                _log.warn("Pet name pattern is wrong!",e);
                PET_NAME_PATTERN  = Pattern.compile("[A-Za-z0-9\\-]{3,16}");
            }
            
            try
            {
                CLAN_ALLY_NAME_PATTERN  = Pattern.compile(serverSettings.getProperty("ClanAllyNameTemplate", "[A-Za-z0-9 \\-]{3,16}"));
            }
            catch (PatternSyntaxException e)
            {
                _log.warn("Clan and ally name pattern is wrong!",e);
                CLAN_ALLY_NAME_PATTERN  = Pattern.compile("[A-Za-z0-9 \\-]{3,16}");
            }
            
            try
            {
                TITLE_PATTERN           = Pattern.compile(serverSettings.getProperty("TitleTemplate", "[A-Za-z0-9 \\\\[\\\\]\\(\\)\\<\\>\\|\\!]{3,16}"));
            }
            catch (PatternSyntaxException e)
            {
                _log.warn("Character title pattern is wrong!",e);
                TITLE_PATTERN  = Pattern.compile("[A-Za-z0-9 \\\\[\\\\]\\(\\)\\<\\>\\|\\!]{3,16}");
            }
            MAX_CHARACTERS_NUMBER_PER_ACCOUNT = Integer.parseInt(serverSettings.getProperty("CharMaxNumber", "0"));
            GAMESERVER_HOSTNAME     = serverSettings.getProperty("GameserverHostname");
            DATAPACK_ROOT    = new File(serverSettings.getProperty("DatapackRoot", ".")).getCanonicalFile();
            MIN_PROTOCOL_REVISION   = Integer.parseInt(serverSettings.getProperty("MinProtocolRevision", "694"));
            MAX_PROTOCOL_REVISION   = Integer.parseInt(serverSettings.getProperty("MaxProtocolRevision", "709"));
            if (MIN_PROTOCOL_REVISION > MAX_PROTOCOL_REVISION)
            {
                throw new Error("MinProtocolRevision is bigger than MaxProtocolRevision in server configuration file.");
            }
            INTERNAL_HOSTNAME   = serverSettings.getProperty("InternalHostname", "127.0.0.1");
            INTERNAL_NETWORKS   = serverSettings.getProperty("InternalNetworks", "");
            EXTERNAL_HOSTNAME   = serverSettings.getProperty("ExternalHostname", "127.0.0.1");
            OPTIONAL_NETWORKS   = serverSettings.getProperty("OptionalNetworks", "");
            MAXIMUM_ONLINE_USERS        = Integer.parseInt(serverSettings.getProperty("MaximumOnlineUsers", "100"));
            DATABASE_DRIVER             = serverSettings.getProperty("Driver", "com.mysql.jdbc.Driver");
            DATABASE_URL                = serverSettings.getProperty("URL", "jdbc:mysql://localhost/l2jdb");
            DATABASE_LOGIN              = serverSettings.getProperty("Login", "root");
            DATABASE_PASSWORD           = serverSettings.getProperty("Password", "");
            DATABASE_MAX_CONNECTIONS    = Integer.parseInt(serverSettings.getProperty("MaximumDbConnections", "10"));
            
            SAFE_REBOOT  = Boolean.parseBoolean(serverSettings.getProperty("SafeReboot", "False"));
            SAFE_REBOOT_TIME    = Integer.parseInt(serverSettings.getProperty("SafeRebootTime", "10"));
            SAFE_REBOOT_DISABLE_ENCHANT = Boolean.parseBoolean(serverSettings.getProperty("SafeRebootDisableEnchant", "False"));
            SAFE_REBOOT_DISABLE_TELEPORT = Boolean.parseBoolean(serverSettings.getProperty("SafeRebootDisableTeleport", "False"));
            SAFE_REBOOT_DISABLE_CREATEITEM = Boolean.parseBoolean(serverSettings.getProperty("SafeRebootDisableCreateItem", "False"));
            SAFE_REBOOT_DISABLE_TRANSACTION = Boolean.parseBoolean(serverSettings.getProperty("SafeRebootDisableTransaction", "False"));
            SAFE_REBOOT_DISABLE_PC_ITERACTION = Boolean.parseBoolean(serverSettings.getProperty("SafeRebootDisablePcIteraction", "False"));
            SAFE_REBOOT_DISABLE_NPC_ITERACTION = Boolean.parseBoolean(serverSettings.getProperty("SafeRebootDisableNpcIteraction", "False"));

            NETWORK_TRAFFIC_OPTIMIZATION     = Boolean.parseBoolean(serverSettings.getProperty("NetworkTrafficOptimization", "False"));
            NETWORK_TRAFFIC_OPTIMIZATION_MS  = Integer.parseInt(serverSettings.getProperty("NetworkTrafficOptimizationMs", "1100"));
            FLOOD_PROTECTION     = Boolean.parseBoolean(serverSettings.getProperty("FloodProtection", "False"));
            PACKET_LIMIT         = Integer.parseInt(serverSettings.getProperty("PacketLimit", "500"));
            PACKET_TIME_LIMIT    = Integer.parseInt(serverSettings.getProperty("PacketTimeLimit", "1100"));
            
            JMX_TCP_PORT = Integer.parseInt(serverSettings.getProperty("admin_portJMX","-1"));
            JMX_HTTP_PORT = Integer.parseInt(serverSettings.getProperty("admin_portHTTP","-1"));
            JMX_KEYSTORE = serverSettings.getProperty("keystore","keystore.jks");
            JMX_KEYSTORE_PASSWORD = serverSettings.getProperty("keystore_password","");
        }
        catch (Exception e)
        {
            _log.error(e.getMessage(),e);
            throw new Error("Failed to Load "+CONFIGURATION_FILE+" File.");
        }
    }

    
    //*******************************************************************************************    
    public static final String  CLANS_FILE                	= "./config/clans.properties";
    //*******************************************************************************************
    public static int 		ALT_CLAN_MEMBERS_FOR_WAR;				// Number of members needed to request a clan war
    public static int 		ALT_CLAN_JOIN_DAYS;						// Number of days before joining a new clan
    public static int 		ALT_CLAN_CREATE_DAYS;					// Number of days before creating a new clan
    public static int 		ALT_CLAN_DISSOLVE_DAYS;					// Number of days it takes to dissolve a clan
    public static int 		ALT_ALLY_JOIN_DAYS_WHEN_LEAVED;			// Number of days before joining a new alliance when clan voluntarily leave an alliance
    public static int 		ALT_ALLY_JOIN_DAYS_WHEN_DISMISSED;		// Number of days before joining a new alliance when clan was dismissed from an alliance
    public static int 		ALT_ACCEPT_CLAN_DAYS_WHEN_DISMISSED; 	// Number of days before accepting a new clan for alliance when clan was dismissed from an alliance
    public static int 		ALT_CREATE_ALLY_DAYS_WHEN_DISSOLVED;	// Number of days before creating a new alliance when dissolved an alliance
    public static int 		ALT_MAX_NUM_OF_CLANS_IN_ALLY;			// Maximum number of clans in ally
    public static enum ClanLeaderColored{name,title}				// Clan leader name color
    public static boolean   CLAN_LEADER_COLOR_ENABLED;
    public static ClanLeaderColored       CLAN_LEADER_COLORED;
    public static int       CLAN_LEADER_COLOR;
    public static int       CLAN_LEADER_COLOR_CLAN_LEVEL;
    public static int       MEMBER_FOR_LEVEL_SIX;                   // Number of members to level up a clan to lvl 6
    public static int       MEMBER_FOR_LEVEL_SEVEN;                 // Number of members to level up a clan to lvl 7
    public static int       MEMBER_FOR_LEVEL_EIGHT;                 // Number of members to level up a clan to lvl 8
    //*******************************************************************************************    
    public static void loadClansConfig()
    {
        _log.info("loading " + CLANS_FILE);
	    try
	    {
	        Properties clansSettings	= new Properties();
	        InputStream is              = new FileInputStream(new File(CLANS_FILE));
	        clansSettings.load(is);
	        is.close();

	        ALT_CLAN_MEMBERS_FOR_WAR                            = Integer.parseInt(clansSettings.getProperty("AltClanMembersForWar", "15"));
	        ALT_CLAN_JOIN_DAYS                                  = Integer.parseInt(clansSettings.getProperty("DaysBeforeJoinAClan", "5"));
	        ALT_CLAN_CREATE_DAYS                                = Integer.parseInt(clansSettings.getProperty("DaysBeforeCreateAClan", "10"));                
	        ALT_CLAN_DISSOLVE_DAYS      						= Integer.parseInt(clansSettings.getProperty("DaysToPassToDissolveAClan", "7"));
	        ALT_ALLY_JOIN_DAYS_WHEN_LEAVED       				= Integer.parseInt(clansSettings.getProperty("DaysBeforeJoinAllyWhenLeaved", "1"));
	        ALT_ALLY_JOIN_DAYS_WHEN_DISMISSED    				= Integer.parseInt(clansSettings.getProperty("DaysBeforeJoinAllyWhenDismissed", "1"));
	        ALT_ACCEPT_CLAN_DAYS_WHEN_DISMISSED  				= Integer.parseInt(clansSettings.getProperty("DaysBeforeAcceptNewClanWhenDismissed", "1"));
	        ALT_CREATE_ALLY_DAYS_WHEN_DISSOLVED  				= Integer.parseInt(clansSettings.getProperty("DaysBeforeCreateNewAllyWhenDissolved", "10"));
	        ALT_MAX_NUM_OF_CLANS_IN_ALLY                        = Integer.parseInt(clansSettings.getProperty("AltMaxNumOfClansInAlly", "3"));
	        CLAN_LEADER_COLOR_ENABLED     			       		= Boolean.parseBoolean(clansSettings.getProperty("ClanLeaderNameColorEnabled", "True"));
	        CLAN_LEADER_COLORED                  				= ClanLeaderColored.valueOf(clansSettings.getProperty("ClanLeaderColored", "name"));
	        CLAN_LEADER_COLOR                                   = Integer.decode("0x" + clansSettings.getProperty("ClanLeaderColor", "00FFFF"));
	        CLAN_LEADER_COLOR_CLAN_LEVEL                        = Integer.parseInt(clansSettings.getProperty("ClanLeaderColorAtClanLevel", "1"));
	        MEMBER_FOR_LEVEL_SIX                                = Integer.parseInt(clansSettings.getProperty("MemberForLevel6", "30"));
	        MEMBER_FOR_LEVEL_SEVEN                              = Integer.parseInt(clansSettings.getProperty("MemberForLevel7", "80"));
	        MEMBER_FOR_LEVEL_EIGHT                              = Integer.parseInt(clansSettings.getProperty("MemberForLevel8", "120"));
	    }
	    catch (Exception e)
	    {
	        _log.error(e.getMessage(),e);
	        throw new Error("Failed to Load "+CLANS_FILE+" File.");
	    }
    }

    
    
    //*******************************************************************************************    
    public static final String  CHAMPIONS_FILE             	= "./config/champions.properties";
    //*******************************************************************************************
    public static int       CHAMPION_FREQUENCY;   // Frequency of spawn
    public static int       CHAMPION_HP;          // Hp multiplier
    public static float     CHAMPION_HP_REGEN;    // Hp.reg multiplier
    public static float     CHAMPION_ATK;         // P.Atk & M.Atk multiplier
    public static float     CHAMPION_SPD_ATK;     // Attack speed multiplier
    public static int       CHAMPION_ADENA;       // Adena/Sealstone reward multiplier
    public static int       CHAMPION_REWARDS;     // Drop/Spoil reward multiplier
    public static int       CHAMPION_EXP_SP;      // Exp/Sp reward multiplier
    public static boolean   CHAMPION_BOSS;        // Bosses can be champions
    public static int       CHAMPION_MIN_LEVEL;   // Champion Minimum Level
    public static int       CHAMPION_MAX_LEVEL;   // Champion Maximum Level
    public static boolean   CHAMPION_MINIONS;     // set Minions to champions when leader champion
    public static int       CHAMPION_SPCL_CHANCE; // Chance in % to drop an special reward item.
    public static int       CHAMPION_SPCL_ITEM;   // Item ID that drops from Champs.
    public static int       CHAMPION_SPCL_QTY;    // Amount of special champ drop items.
    //*******************************************************************************************    
    public static void loadChampionsConfig()
    {
        _log.info("loading " + CHAMPIONS_FILE);    	
        try
        {
            Properties championsSettings = new Properties();
            InputStream is               = new FileInputStream(new File(CHAMPIONS_FILE));
            championsSettings.load(is);
            is.close();
            
            CHAMPION_FREQUENCY      = Integer.parseInt(championsSettings.getProperty("ChampionFrequency", "0"));
            CHAMPION_HP             = Integer.parseInt(championsSettings.getProperty("ChampionHp", "7"));
            CHAMPION_HP_REGEN       = Float.parseFloat(championsSettings.getProperty("ChampionRegenHp","1."));    
            CHAMPION_REWARDS        = Integer.parseInt(championsSettings.getProperty("ChampionRewards", "8"));
            CHAMPION_ADENA          = Integer.parseInt(championsSettings.getProperty("ChampionAdenasRewards", "1"));
            CHAMPION_ATK            = Float.parseFloat(championsSettings.getProperty("ChampionAtk", "1."));
            CHAMPION_SPD_ATK        = Float.parseFloat(championsSettings.getProperty("ChampionSpdAtk", "1."));
            CHAMPION_EXP_SP         = Integer.parseInt(championsSettings.getProperty("ChampionExpSp", "8"));
            CHAMPION_BOSS           = Boolean.parseBoolean(championsSettings.getProperty("ChampionBoss", "false"));
            CHAMPION_MIN_LEVEL      = Integer.parseInt(championsSettings.getProperty("ChampionMinLevel", "20"));
            CHAMPION_MAX_LEVEL      = Integer.parseInt(championsSettings.getProperty("ChampionMaxLevel", "60"));
            CHAMPION_MINIONS        = Boolean.parseBoolean(championsSettings.getProperty("ChampionMinions", "false"));
            CHAMPION_SPCL_CHANCE    = Integer.parseInt(championsSettings.getProperty("ChampionSpecialItemChance", "0"));
            CHAMPION_SPCL_ITEM      = Integer.parseInt(championsSettings.getProperty("ChampionSpecialItemID", "6393"));
            CHAMPION_SPCL_QTY       = Integer.parseInt(championsSettings.getProperty("ChampionSpecialItemAmount", "1"));
        }
        catch (Exception e)
        {
            _log.error(e.getMessage(),e);
            throw new Error("Failed to Load "+CHAMPIONS_FILE+" File.");
        }
    }

    
    
    //  *******************************************************************************************    
    public static final String  LOTTERY_FILE             	= "./config/lottery.properties";
    //  *******************************************************************************************
    public static int 	ALT_LOTTERY_PRIZE;				// Initial Lottery prize
    public static int 	ALT_LOTTERY_TICKET_PRICE;		// Lottery Ticket Price
    public static float ALT_LOTTERY_5_NUMBER_RATE;		// What part of jackpot amount should receive characters who pick 5 wining numbers
    public static float ALT_LOTTERY_4_NUMBER_RATE;		// What part of jackpot amount should receive characters who pick 4 wining numbers
    public static float ALT_LOTTERY_3_NUMBER_RATE;		// What part of jackpot amount should receive characters who pick 3 wining numbers
    public static int ALT_LOTTERY_2_AND_1_NUMBER_PRIZE;	// How much adena receive characters who pick two or less of the winning number
    //  *******************************************************************************************
    public static void loadLotteryConfig()
    {
        _log.info("loading " + LOTTERY_FILE);
        try
        {
            Properties lotterySettings = new Properties();
            InputStream is               = new FileInputStream(new File(LOTTERY_FILE));
            lotterySettings.load(is);
            is.close();
            
            ALT_LOTTERY_PRIZE                                   = Integer.parseInt(lotterySettings.getProperty("AltLotteryPrize","50000"));
            ALT_LOTTERY_TICKET_PRICE                            = Integer.parseInt(lotterySettings.getProperty("AltLotteryTicketPrice","2000"));
            ALT_LOTTERY_5_NUMBER_RATE                           = Float.parseFloat(lotterySettings.getProperty("AltLottery5NumberRate","0.6"));
            ALT_LOTTERY_4_NUMBER_RATE                           = Float.parseFloat(lotterySettings.getProperty("AltLottery4NumberRate","0.2"));
            ALT_LOTTERY_3_NUMBER_RATE                           = Float.parseFloat(lotterySettings.getProperty("AltLottery3NumberRate","0.2"));
            ALT_LOTTERY_2_AND_1_NUMBER_PRIZE                    = Integer.parseInt(lotterySettings.getProperty("AltLottery2and1NumberPrize","200"));
        }
        catch (Exception e)
        {
            _log.error(e.getMessage(),e);
            throw new Error("Failed to Load "+LOTTERY_FILE+" File.");
        }
    }

    
    
    //  *******************************************************************************************
    public static final String  WEDDING_FILE             	= "./config/wedding.properties";
    //  *******************************************************************************************
    public static int 		WEDDING_PRICE;
    public static boolean 	WEDDING_PUNISH_INFIDELITY;
    public static boolean 	WEDDING_TELEPORT;
    public static int 		WEDDING_TELEPORT_PRICE;
    public static int 		WEDDING_TELEPORT_INTERVAL;
    public static boolean 	WEDDING_SAMESEX;
    public static boolean 	WEDDING_FORMALWEAR;
    public static int 		WEDDING_DIVORCE_COSTS;
    //  *******************************************************************************************
    public static void loadWeddingConfig()
    {
        _log.info("loading " + WEDDING_FILE);
        try
        {
            Properties weddingSettings = new Properties();
            InputStream is               = new FileInputStream(new File(WEDDING_FILE));
            weddingSettings.load(is);
            is.close();
            
            WEDDING_PRICE                                       = Integer.parseInt(weddingSettings.getProperty("WeddingPrice", "500000"));
            WEDDING_PUNISH_INFIDELITY                           = Boolean.parseBoolean(weddingSettings.getProperty("WeddingPunishInfidelity", "true"));                
            WEDDING_TELEPORT                                    = Boolean.parseBoolean(weddingSettings.getProperty("WeddingTeleport", "true"));
            WEDDING_TELEPORT_PRICE                              = Integer.parseInt(weddingSettings.getProperty("WeddingTeleportPrice", "500000"));
            WEDDING_TELEPORT_INTERVAL                           = Integer.parseInt(weddingSettings.getProperty("WeddingTeleportInterval", "120"));
            WEDDING_SAMESEX                                     = Boolean.parseBoolean(weddingSettings.getProperty("WeddingAllowSameSex", "true"));
            WEDDING_FORMALWEAR                                  = Boolean.parseBoolean(weddingSettings.getProperty("WeddingFormalWear", "true"));
            WEDDING_DIVORCE_COSTS                               = Integer.parseInt(weddingSettings.getProperty("WeddingDivorceCosts", "20"));
        }
        catch (Exception e)
        {
            _log.error(e.getMessage(),e);
            throw new Error("Failed to Load "+WEDDING_FILE+" File.");
        }
    }

    
    
    //  *******************************************************************************************
    public static final String  SEPULCHURS_FILE            	= "./config/sepulchurs.properties";
    //  *******************************************************************************************
	public static int FS_TIME_ATTACK;
	public static int FS_TIME_COOLDOWN;
	public static int FS_TIME_ENTRY;
	public static int FS_TIME_WARMUP;
	public static int FS_PARTY_MEMBER_COUNT;
    //  *******************************************************************************************
    public static void loadSepulchursConfig()
    {
        _log.info("loading " + SEPULCHURS_FILE);
        try
        {
            Properties sepulchursSettings = new Properties();
            InputStream is               = new FileInputStream(new File(SEPULCHURS_FILE));
            sepulchursSettings.load(is);
            is.close();
            
            FS_TIME_ATTACK 										= Integer.parseInt(sepulchursSettings.getProperty("TimeOfAttack", "50"));
            FS_TIME_COOLDOWN 									= Integer.parseInt(sepulchursSettings.getProperty("TimeOfCoolDown", "5"));
            FS_TIME_ENTRY 										= Integer.parseInt(sepulchursSettings.getProperty("TimeOfEntry", "3"));
            FS_TIME_WARMUP 										= Integer.parseInt(sepulchursSettings.getProperty("TimeOfWarmUp", "2"));
            FS_PARTY_MEMBER_COUNT 								= Integer.parseInt(sepulchursSettings.getProperty("NumberOfNecessaryPartyMembers", "4"));
            if(FS_TIME_ATTACK <= 0) FS_TIME_ATTACK = 50;
            if(FS_TIME_COOLDOWN <= 0) FS_TIME_COOLDOWN = 5;
            if(FS_TIME_ENTRY <= 0) FS_TIME_ENTRY = 3;
            if(FS_TIME_ENTRY <= 0) FS_TIME_ENTRY = 3;
            if(FS_TIME_ENTRY <= 0) FS_TIME_ENTRY = 3;
            
        }
        catch (Exception e)
        {
            _log.error(e.getMessage(),e);
            throw new Error("Failed to Load "+SEPULCHURS_FILE+" File.");
        }
    }

    
    
    //  *******************************************************************************************
    public static final String  RATES_CONFIG_FILE           = "./config/rates.properties";    
    //  *******************************************************************************************
    public static float     RATE_XP;
    public static float     RATE_SP;
    public static float     RATE_PARTY_XP;
    public static float     RATE_PARTY_SP;
    public static float     RATE_QUESTS_REWARD;
    public static float     RATE_DROP_ADENA;
    public static float     RATE_CONSUMABLE_COST;
    public static float     RATE_CRAFT_COST;
    public static float     RATE_DROP_ITEMS;
    public static float     RATE_DROP_SPOIL;
    public static float     RATE_DROP_QUEST;
    public static float     RATE_KARMA_EXP_LOST;
    public static float     RATE_SIEGE_GUARDS_PRICE;
    public static float     RATE_RUN_SPEED;
    public static float     ALT_GAME_EXPONENT_XP;     // Alternative Xp/Sp rewards, if not 0, then calculated as 2^((mob.level-player.level) / coef
    public static float     ALT_GAME_EXPONENT_SP;
    public static int       PLAYER_DROP_LIMIT;
    public static int       PLAYER_RATE_DROP;
    public static int       PLAYER_RATE_DROP_ITEM;
    public static int       PLAYER_RATE_DROP_EQUIP;    
    public static int       PLAYER_RATE_DROP_EQUIP_WEAPON;    
    public static float     PET_XP_RATE; 
    public static float     PET_FOOD_RATE;
    public static float     SINEATER_XP_RATE;
    public static float     RATE_DROP_COMMON_HERBS;
    public static float     RATE_DROP_MP_HP_HERBS;
    public static float     RATE_DROP_GREATER_HERBS;
    public static float     RATE_DROP_SUPERIOR_HERBS;
    public static float     RATE_DROP_SPECIAL_HERBS;
    public static int       KARMA_DROP_LIMIT;
    public static int       KARMA_RATE_DROP;
    public static int       KARMA_RATE_DROP_ITEM;
    public static int       KARMA_RATE_DROP_EQUIP;    
    public static int       KARMA_RATE_DROP_EQUIP_WEAPON;    
    //  *******************************************************************************************
    public static void loadRatesConfig()
    {
        _log.info("loading " + RATES_CONFIG_FILE);
        try
        {
            Properties ratesSettings    = new Properties();
            InputStream is              = new FileInputStream(new File(RATES_CONFIG_FILE));
            ratesSettings.load(is);
            is.close();
            
            RATE_XP                         = Float.parseFloat(ratesSettings.getProperty("RateXp", "1."));
            RATE_SP                         = Float.parseFloat(ratesSettings.getProperty("RateSp", "1."));
            RATE_PARTY_XP                   = Float.parseFloat(ratesSettings.getProperty("RatePartyXp", "1."));
            RATE_PARTY_SP                   = Float.parseFloat(ratesSettings.getProperty("RatePartySp", "1."));
            RATE_QUESTS_REWARD              = Float.parseFloat(ratesSettings.getProperty("RateQuestsReward", "1."));
            RATE_DROP_ADENA                 = Float.parseFloat(ratesSettings.getProperty("RateDropAdena", "1."));
            RATE_CONSUMABLE_COST            = Float.parseFloat(ratesSettings.getProperty("RateConsumableCost", "1."));
            RATE_CRAFT_COST                 = Float.parseFloat(ratesSettings.getProperty("RateCraftCost","1."));
            RATE_DROP_ITEMS                 = Float.parseFloat(ratesSettings.getProperty("RateDropItems", "1."));
            RATE_DROP_SPOIL                 = Float.parseFloat(ratesSettings.getProperty("RateDropSpoil", "1."));
            RATE_DROP_QUEST                 = Float.parseFloat(ratesSettings.getProperty("RateDropQuest", "1."));
            RATE_RUN_SPEED                  = Float.parseFloat(ratesSettings.getProperty("RateRunSpeed", "1."));
            RATE_KARMA_EXP_LOST             = Float.parseFloat(ratesSettings.getProperty("RateKarmaExpLost", "1."));    
            RATE_SIEGE_GUARDS_PRICE         = Float.parseFloat(ratesSettings.getProperty("RateSiegeGuardsPrice", "1."));

            RATE_DROP_COMMON_HERBS          = Float.parseFloat(ratesSettings.getProperty("RateCommonHerbs", "15."));
            RATE_DROP_MP_HP_HERBS           = Float.parseFloat(ratesSettings.getProperty("RateHpMpHerbs", "10."));                
            RATE_DROP_GREATER_HERBS         = Float.parseFloat(ratesSettings.getProperty("RateGreaterHerbs", "4."));
            RATE_DROP_SUPERIOR_HERBS        = Float.parseFloat(ratesSettings.getProperty("RateSuperiorHerbs", "0.8"))*10;                
            RATE_DROP_SPECIAL_HERBS         = Float.parseFloat(ratesSettings.getProperty("RateSpecialHerbs", "0.2"))*10;
            
            PLAYER_DROP_LIMIT               = Integer.parseInt(ratesSettings.getProperty("PlayerDropLimit", "3"));
            PLAYER_RATE_DROP                = Integer.parseInt(ratesSettings.getProperty("PlayerRateDrop", "5"));
            PLAYER_RATE_DROP_ITEM           = Integer.parseInt(ratesSettings.getProperty("PlayerRateDropItem", "70"));
            PLAYER_RATE_DROP_EQUIP          = Integer.parseInt(ratesSettings.getProperty("PlayerRateDropEquip", "25"));
            PLAYER_RATE_DROP_EQUIP_WEAPON   = Integer.parseInt(ratesSettings.getProperty("PlayerRateDropEquipWeapon", "5"));

            PET_XP_RATE                     = Float.parseFloat(ratesSettings.getProperty("PetXpRate", "1."));
            PET_FOOD_RATE                   = Float.parseFloat(ratesSettings.getProperty("PetFoodRate", "1")); 
            SINEATER_XP_RATE                = Float.parseFloat(ratesSettings.getProperty("SinEaterXpRate", "1."));

            KARMA_DROP_LIMIT                = Integer.parseInt(ratesSettings.getProperty("KarmaDropLimit", "10"));
            KARMA_RATE_DROP                 = Integer.parseInt(ratesSettings.getProperty("KarmaRateDrop", "70"));
            KARMA_RATE_DROP_ITEM            = Integer.parseInt(ratesSettings.getProperty("KarmaRateDropItem", "50"));
            KARMA_RATE_DROP_EQUIP           = Integer.parseInt(ratesSettings.getProperty("KarmaRateDropEquip", "40"));
            KARMA_RATE_DROP_EQUIP_WEAPON    = Integer.parseInt(ratesSettings.getProperty("KarmaRateDropEquipWeapon", "10"));
        }
        catch (Exception e) {
            _log.error(e);
            throw new Error("Failed to Load "+RATES_CONFIG_FILE+" File.");
        }
    }

    
    
    //  *******************************************************************************************
    public static final String  ENCHANT_CONFIG_FILE         = "./config/enchant.properties";    
    //  *******************************************************************************************
    public static int 		ENCHANT_CHANCE_WEAPON;
    public static int 		ENCHANT_CHANCE_ARMOR;
    public static int 		ENCHANT_CHANCE_JEWELRY;
    public static int 		ENCHANT_CHANCE_WEAPON_CRYSTAL;
    public static int 		ENCHANT_CHANCE_ARMOR_CRYSTAL;
    public static int 		ENCHANT_CHANCE_JEWELRY_CRYSTAL;
    public static int 		ENCHANT_CHANCE_WEAPON_BLESSED;
    public static int 		ENCHANT_CHANCE_ARMOR_BLESSED;
    public static int 		ENCHANT_CHANCE_JEWELRY_BLESSED;
    public static boolean 	ENCHANT_BREAK_WEAPON;			// If an enchant fails - will the item break or only reset to 0?
    public static boolean 	ENCHANT_BREAK_ARMOR;
    public static boolean 	ENCHANT_BREAK_JEWELRY;
    public static boolean 	ENCHANT_BREAK_WEAPON_CRYSTAL;
    public static boolean 	ENCHANT_BREAK_ARMOR_CRYSTAL;
    public static boolean 	ENCHANT_BREAK_JEWELRY_CRYSTAL;
    public static boolean 	ENCHANT_BREAK_WEAPON_BLESSED;
    public static boolean 	ENCHANT_BREAK_ARMOR_BLESSED;
    public static boolean 	ENCHANT_BREAK_JEWELRY_BLESSED;
    public static boolean 	ENCHANT_HERO_WEAPONS;			// Enchant hero weapons?
    public static boolean 	ENCHANT_DWARF_SYSTEM; 			// Dwarf enchant System?
    public static int 		ENCHANT_MAX_WEAPON;				// Maximum level of enchantment
    public static int 		ENCHANT_MAX_ARMOR;
    public static int 		ENCHANT_MAX_JEWELRY;
    public static int 		ENCHANT_SAFE_MAX;				// maximum level of safe enchantment
    public static int 		ENCHANT_SAFE_MAX_FULL;
    public static int 		ENCHANT_DWARF_1_ENCHANTLEVEL; // Dwarf enchant System Dwarf 1 Enchantlevel?
    public static int 		ENCHANT_DWARF_2_ENCHANTLEVEL; // Dwarf enchant System Dwarf 2 Enchantlevel?
    public static int 		ENCHANT_DWARF_3_ENCHANTLEVEL; // Dwarf enchant System Dwarf 3 Enchantlevel?
    public static int 		ENCHANT_DWARF_1_CHANCE; // Dwarf enchant System Dwarf 1 chance?
    public static int 		ENCHANT_DWARF_2_CHANCE; // Dwarf enchant System Dwarf 2 chance?
    public static int 		ENCHANT_DWARF_3_CHANCE; // Dwarf enchant System Dwarf 3 chance?
    //  *******************************************************************************************    
    public static void loadEnchantConfig()
    {
        _log.info("loading " + ENCHANT_CONFIG_FILE);
        try
        {
            Properties enchantSettings  = new Properties();
            InputStream is              = new FileInputStream(new File(ENCHANT_CONFIG_FILE));
            enchantSettings.load(is);
            is.close();
            
            /* chance to enchant an item normal scroll*/
            ENCHANT_CHANCE_WEAPON  = Integer.parseInt(enchantSettings.getProperty("EnchantChanceWeapon", "65"));
            ENCHANT_CHANCE_ARMOR  = Integer.parseInt(enchantSettings.getProperty("EnchantChanceArmor", "65"));
            ENCHANT_CHANCE_JEWELRY  = Integer.parseInt(enchantSettings.getProperty("EnchantChanceJewelry", "65"));
            /* item may break normal scroll*/
            ENCHANT_BREAK_WEAPON  = Boolean.parseBoolean(enchantSettings.getProperty("EnchantBreakWeapon", "True"));
            ENCHANT_BREAK_ARMOR  = Boolean.parseBoolean(enchantSettings.getProperty("EnchantBreakArmor", "True"));
            ENCHANT_BREAK_JEWELRY  = Boolean.parseBoolean(enchantSettings.getProperty("EnchantBreakJewelry", "True"));
            /* chance to enchant an item crystal scroll */
            ENCHANT_CHANCE_WEAPON_CRYSTAL  = Integer.parseInt(enchantSettings.getProperty("EnchantChanceWeaponCrystal", "75"));
            ENCHANT_CHANCE_ARMOR_CRYSTAL  = Integer.parseInt(enchantSettings.getProperty("EnchantChanceArmorCrystal", "75"));
            ENCHANT_CHANCE_JEWELRY_CRYSTAL  = Integer.parseInt(enchantSettings.getProperty("EnchantChanceJewelryCrystal", "75"));
            /* item may break crystal scroll */
            ENCHANT_BREAK_WEAPON_CRYSTAL  = Boolean.parseBoolean(enchantSettings.getProperty("EnchantBreakWeaponCrystal", "True"));
            ENCHANT_BREAK_ARMOR_CRYSTAL  = Boolean.parseBoolean(enchantSettings.getProperty("EnchantBreakArmorCrystal", "True"));
            ENCHANT_BREAK_JEWELRY_CRYSTAL  = Boolean.parseBoolean(enchantSettings.getProperty("EnchantBreakJewelryCrystal", "True"));
            /* chance to enchant an item blessed scroll */
            ENCHANT_CHANCE_WEAPON_BLESSED  = Integer.parseInt(enchantSettings.getProperty("EnchantChanceWeaponBlessed", "65"));
            ENCHANT_CHANCE_ARMOR_BLESSED  = Integer.parseInt(enchantSettings.getProperty("EnchantChanceArmorBlessed", "65"));
            ENCHANT_CHANCE_JEWELRY_BLESSED  = Integer.parseInt(enchantSettings.getProperty("EnchantChanceJewelryBlessed", "65"));
            /* item may break blessed scroll */
            ENCHANT_BREAK_WEAPON_BLESSED  = Boolean.parseBoolean(enchantSettings.getProperty("EnchantBreakWeaponBlessed", "False"));
            ENCHANT_BREAK_ARMOR_BLESSED  = Boolean.parseBoolean(enchantSettings.getProperty("EnchantBreakArmorBlessed", "False"));
            ENCHANT_BREAK_JEWELRY_BLESSED  = Boolean.parseBoolean(enchantSettings.getProperty("EnchantBreakJewelryBlessed", "True"));
            /* enchat hero weapons? */
            ENCHANT_HERO_WEAPONS  = Boolean.parseBoolean(enchantSettings.getProperty("EnchantHeroWeapons", "False"));
            /* enchant dwarf system */
            ENCHANT_DWARF_SYSTEM  = Boolean.parseBoolean(enchantSettings.getProperty("EnchantDwarfSystem", "False"));
            /* limit on enchant */
            ENCHANT_MAX_WEAPON = Integer.parseInt(enchantSettings.getProperty("EnchantMaxWeapon", "255"));
            ENCHANT_MAX_ARMOR = Integer.parseInt(enchantSettings.getProperty("EnchantMaxArmor", "255"));
            ENCHANT_MAX_JEWELRY = Integer.parseInt(enchantSettings.getProperty("EnchantMaxJewelry", "255"));
            /* limit of safe enchant */
            ENCHANT_SAFE_MAX = Integer.parseInt(enchantSettings.getProperty("EnchantSafeMax", "3"));
            ENCHANT_SAFE_MAX_FULL = Integer.parseInt(enchantSettings.getProperty("EnchantSafeMaxFull", "4"));
            ENCHANT_DWARF_1_ENCHANTLEVEL = Integer.parseInt(enchantSettings.getProperty("EnchantDwarf1Enchantlevel", "8"));
            ENCHANT_DWARF_2_ENCHANTLEVEL = Integer.parseInt(enchantSettings.getProperty("EnchantDwarf2Enchantlevel", "10"));
            ENCHANT_DWARF_3_ENCHANTLEVEL = Integer.parseInt(enchantSettings.getProperty("EnchantDwarf3Enchantlevel", "12"));
            ENCHANT_DWARF_1_CHANCE = Integer.parseInt(enchantSettings.getProperty("EnchantDwarf1Chance", "15"));
            ENCHANT_DWARF_2_CHANCE = Integer.parseInt(enchantSettings.getProperty("EnchantDwarf2Chance", "15"));
            ENCHANT_DWARF_3_CHANCE = Integer.parseInt(enchantSettings.getProperty("EnchantDwarf3Chance", "15"));
        }
        catch (Exception e) {
            _log.error(e.getMessage(),e);
            throw new Error("Failed to Load "+ENCHANT_CONFIG_FILE+" File.");
        }
    }

    
    
    //  *******************************************************************************************    
    public static final String  PVP_CONFIG_FILE             = "./config/pvp.properties";
    //  *******************************************************************************************    
    public static int     			KARMA_MIN_KARMA;
    public static int     			KARMA_MAX_KARMA;
    public static int     			KARMA_XP_DIVIDER;
    public static int     			KARMA_LOST_BASE;
    public static boolean 			KARMA_DROP_GM;
    public static boolean 			KARMA_AWARD_PK_KILL;
    public static int     			KARMA_PK_LIMIT;
    public static String  			KARMA_NONDROPPABLE_PET_ITEMS;
    public static String  			KARMA_NONDROPPABLE_ITEMS;
    public static FastList<Integer> KARMA_LIST_NONDROPPABLE_PET_ITEMS   = new FastList<Integer>();
    public static FastList<Integer> KARMA_LIST_NONDROPPABLE_ITEMS       = new FastList<Integer>();
    public static int 				PVP_TIME;    
    public static boolean 			ALT_PLAYER_CAN_DROP_ADENA;		// Player can drop adena ?
    public static boolean 			ALT_ANNOUNCE_PK;				// Announce Pks ?
    public static boolean 			ALT_ANNOUNCE_PK_NORMAL_MESSAGE;
    public static int  				PLAYER_RATE_DROP_ADENA;
    public static int 				PVP_NORMAL_TIME; 				// Duration (in ms) while a player stay in PVP mode after hitting an innocent    
    public static int 				PVP_PVP_TIME;					// Duration (in ms) while a player stay in PVP mode after hitting a purple player       
    //  *******************************************************************************************    
    public static void loadPvpConfig()
    {
        _log.info("loading " + PVP_CONFIG_FILE);
        try
        {
            Properties pvpSettings      = new Properties();
            InputStream is              = new FileInputStream(new File(PVP_CONFIG_FILE));  
            pvpSettings.load(is);
            is.close();
            
            /* KARMA SYSTEM */
            KARMA_MIN_KARMA     = Integer.parseInt(pvpSettings.getProperty("MinKarma", "240"));
            KARMA_MAX_KARMA     = Integer.parseInt(pvpSettings.getProperty("MaxKarma", "10000"));
            KARMA_XP_DIVIDER    = Integer.parseInt(pvpSettings.getProperty("XPDivider", "260"));
            KARMA_LOST_BASE     = Integer.parseInt(pvpSettings.getProperty("BaseKarmaLost", "0"));
            
            KARMA_DROP_GM               = Boolean.parseBoolean(pvpSettings.getProperty("CanGMDropEquipment", "false"));
            KARMA_AWARD_PK_KILL         = Boolean.parseBoolean(pvpSettings.getProperty("AwardPKKillPVPPoint", "true"));
            
            KARMA_PK_LIMIT                      = Integer.parseInt(pvpSettings.getProperty("MinimumPKRequiredToDrop", "5"));
            
            KARMA_NONDROPPABLE_PET_ITEMS    = pvpSettings.getProperty("ListOfPetItems", "2375,3500,3501,3502,4422,4423,4424,4425,6648,6649,6650");          
            KARMA_NONDROPPABLE_ITEMS        = pvpSettings.getProperty("ListOfNonDroppableItems", "57,1147,425,1146,461,10,2368,7,6,2370,2369");
            
            KARMA_LIST_NONDROPPABLE_PET_ITEMS = new FastList<Integer>();
            for (String id : KARMA_NONDROPPABLE_PET_ITEMS.trim().split(",")) {
                KARMA_LIST_NONDROPPABLE_PET_ITEMS.add(Integer.parseInt(id));
            }
            
            KARMA_LIST_NONDROPPABLE_ITEMS = new FastList<Integer>();
            for (String id : KARMA_NONDROPPABLE_ITEMS.trim().split(",")) {
                KARMA_LIST_NONDROPPABLE_ITEMS.add(Integer.parseInt(id));
            }
            ALT_PLAYER_CAN_DROP_ADENA= Boolean.parseBoolean(pvpSettings.getProperty("PlayerCanDropAdena", "false"));
            PLAYER_RATE_DROP_ADENA  = Integer.parseInt(pvpSettings.getProperty("PlayerRateDropAdena", "1"));
            ALT_ANNOUNCE_PK = Boolean.parseBoolean(pvpSettings.getProperty("AnnouncePk", "false"));
            ALT_ANNOUNCE_PK_NORMAL_MESSAGE = Boolean.parseBoolean(pvpSettings.getProperty("AnnouncePkNormalMessage", "false"));
            PVP_NORMAL_TIME = Integer.parseInt(pvpSettings.getProperty("PvPVsNormalTime", "15000"));
            PVP_PVP_TIME = Integer.parseInt(pvpSettings.getProperty("PvPVsPvPTime", "30000"));
            PVP_TIME = PVP_NORMAL_TIME;
        }
        catch (Exception e)
        {
            _log.error(e);
            throw new Error("Failed to Load "+PVP_CONFIG_FILE+" File.");
        }
    }

    
    //  *******************************************************************************************
    public static final String  ID_CONFIG_FILE				= "./config/idfactory.properties";
    //  *******************************************************************************************
    public static ObjectMapType   	MAP_TYPE;			// Type of map object
    public static ObjectSetType   	SET_TYPE;			// Type of set object
	public static enum IdFactoryType{Compaction, BitSet, Stack, Increment}
    public static IdFactoryType 	IDFACTORY_TYPE;		// ID Factory type
    public static boolean 			BAD_ID_CHECKING;	// Check for bad ID ?
    //  *******************************************************************************************
    public static void loadIdFactoryConfig()
    {
        _log.info("loading " + ID_CONFIG_FILE);
        try
        {
            Properties idSettings   = new Properties();
            InputStream is          = new FileInputStream(new File(ID_CONFIG_FILE));
            idSettings.load(is);
            is.close();
            
            MAP_TYPE        = ObjectMapType.valueOf(idSettings.getProperty("L2Map", "WorldObjectMap"));
            SET_TYPE        = ObjectSetType.valueOf(idSettings.getProperty("L2Set", "WorldObjectSet"));
            IDFACTORY_TYPE  = IdFactoryType.valueOf(idSettings.getProperty("IDFactory", "Compaction"));
            BAD_ID_CHECKING = Boolean.parseBoolean(idSettings.getProperty("BadIdChecking", "True"));
        }
        catch (Exception e)
        {
            _log.error(e);
            throw new Error("Failed to Load "+ID_CONFIG_FILE+" File.");
        }
    }

    
    
    //  *******************************************************************************************
    public static final String  OTHER_CONFIG_FILE			= "./config/other.properties";
    //  *******************************************************************************************
    public static boolean 			JAIL_IS_PVP;						// Jail config	
    public static boolean 			JAIL_DISABLE_CHAT;					// Jail config
    public static String  			ALLOWED_NPC_TYPES;
    public static FastList<String> 	LIST_ALLOWED_NPC_TYPES = new FastList<String>();// List of NPC types that won't allow casting
    public static int           	WYVERN_SPEED;
    public static int           	STRIDER_SPEED;
    public static boolean       	ALLOW_WYVERN_UPGRADER;
    public static boolean 			PETITIONING_ALLOWED;
    public static int 				MAX_PETITIONS_PER_PLAYER;
    public static int 				MAX_PETITIONS_PENDING;
    public static boolean 			STORE_SKILL_COOLTIME;				// Store skills cooltime on char exit/relogin
    public static boolean 			ANNOUNCE_MAMMON_SPAWN;
    public static double  			RESPAWN_RESTORE_CP;					// Percent CP is restore on respawn
    public static double  			RESPAWN_RESTORE_HP;					// Percent HP is restore on respawn
    public static double  			RESPAWN_RESTORE_MP;					// Percent MP is restore on respawn
 	public static boolean 			RESPAWN_RANDOM_ENABLED;				// Allow randomizing of the respawn point in towns. 
 	public static int 				RESPAWN_RANDOM_MAX_OFFSET;			// The maximum offset from the base respawn point to allow.
    public static int  				MAX_PVTSTORE_SLOTS_DWARF;			// Maximum number of available slots for pvt stores (sell/buy) - Dwarves
    public static int  				MAX_PVTSTORE_SLOTS_OTHER;			// Maximum number of available slots for pvt stores (sell/buy) - Others
    public static String  			PARTY_XP_CUTOFF_METHOD;				// Define Party XP cutoff point method - Possible values: level and percentage
    public static int 				PARTY_XP_CUTOFF_LEVEL;				// Define the cutoff point value for the "level" method
    public static double  			PARTY_XP_CUTOFF_PERCENT;			// Define the cutoff point value for the "percentage" method
    public static int 				PARTY_RANGE;						// Range of Members to get exp/drops 
    public static int 				MAX_PARTY_LEVEL_DIFFERENCE;			// Maximum level difference between party members in levels  
    public static double   			RAID_HP_REGEN_MULTIPLIER;			// Multiplier for Raid boss HP regeneration
    public static double   			RAID_MP_REGEN_MULTIPLIER;			// Mulitplier for Raid boss MP regeneration
    public static double   			RAID_DEFENCE_MULTIPLIER;			// Multiplier for Raid boss defense multiplier
    public static double   			RAID_MINION_RESPAWN_TIMER;			// Raid Boss Minin Spawn Timer
    public static float   			RAID_MIN_RESPAWN_MULTIPLIER;		// Mulitplier for Raid boss minimum time respawn
    public static float   			RAID_MAX_RESPAWN_MULTIPLIER;		// Mulitplier for Raid boss maximum time respawn  
    public static int 				STARTING_ADENA;						// Amount of adenas when starting a new character
    public static boolean 			DEEPBLUE_DROP_RULES;				// Deep Blue Mobs' Drop Rules Enabled
    public static int     			UNSTUCK_INTERVAL;
    public static int   			PLAYER_SPAWN_PROTECTION;			// Player Protection control
    public static int   			PLAYER_FAKEDEATH_UP_PROTECTION;
    public static double  			NPC_HP_REGEN_MULTIPLIER;			// NPC regen multipliers
    public static double  			NPC_MP_REGEN_MULTIPLIER;			// NPC regen multipliers
    public static double  			PLAYER_HP_REGEN_MULTIPLIER;			// Player regen multipliers
    public static double  			PLAYER_MP_REGEN_MULTIPLIER;			// Player regen multipliers
    public static double  			PLAYER_CP_REGEN_MULTIPLIER;			// Player regen multipliers
    public static int               MAX_ITEM_IN_PACKET;
    
    /**
     * Allow lesser effects to be canceled if stronger effects are used when effects of the same stack group are used.<br> 
     * New effects that are added will be canceled if they are of lesser priority to the old one.
     */
    public static boolean 			EFFECT_CANCELING;
    public static String  			NONDROPPABLE_ITEMS;
    public static FastList<Integer> LIST_NONDROPPABLE_ITEMS = new FastList<Integer>();
    public static String  			PET_RENT_NPC;
    public static FastList<Integer> LIST_PET_RENT_NPC = new FastList<Integer>();
    public static int       		ADD_MAX_LOAD;						// Weight limit
    public static boolean   		LEVEL_ADD_LOAD;
    public static int           	WAREHOUSE_SLOTS_NO_DWARF;			// Warehouse slots limits
    public static int           	WAREHOUSE_SLOTS_DWARF;				// Warehouse slots limits
    public static int           	WAREHOUSE_SLOTS_CLAN;				// Warehouse slots limits
    public static int           	FREIGHT_SLOTS;						// Freight slots limits
    public static int           	INVENTORY_MAXIMUM_NO_DWARF;			// Inventory slots limits
    public static int           	INVENTORY_MAXIMUM_DWARF;			// Inventory slots limits
    public static int           	INVENTORY_MAXIMUM_GM;				// Inventory slots limits
    public static String			FORBIDDEN_RAID_SKILLS;
    public static FastList<Integer> FORBIDDEN_RAID_SKILLS_LIST  = new FastList<Integer>();
    public static int           	DEATH_PENALTY_CHANCE;				// Death Penalty chance
    //  *******************************************************************************************    
    //  *******************************************************************************************
    public static void loadOtherConfig()
    {
        _log.info("loading " + OTHER_CONFIG_FILE);
        try
        {
            Properties otherSettings    = new Properties();
            InputStream is              = new FileInputStream(new File(OTHER_CONFIG_FILE));
            otherSettings.load(is);
            is.close();
            
            DEEPBLUE_DROP_RULES = Boolean.parseBoolean(otherSettings.getProperty("UseDeepBlueDropRules", "True"));
            EFFECT_CANCELING    = Boolean.parseBoolean(otherSettings.getProperty("CancelLesserEffect", "True"));
            WYVERN_SPEED        = Integer.parseInt(otherSettings.getProperty("WyvernSpeed", "100"));         
            STRIDER_SPEED       = Integer.parseInt(otherSettings.getProperty("StriderSpeed", "80"));
            ALLOW_WYVERN_UPGRADER     = Boolean.parseBoolean(otherSettings.getProperty("AllowWyvernUpgrader", "False"));
            
            /* Inventory slots limits */
            INVENTORY_MAXIMUM_NO_DWARF  = Integer.parseInt(otherSettings.getProperty("MaximumSlotsForNoDwarf", "80"));
            INVENTORY_MAXIMUM_DWARF  = Integer.parseInt(otherSettings.getProperty("MaximumSlotsForDwarf", "100"));
            INVENTORY_MAXIMUM_GM    = Integer.parseInt(otherSettings.getProperty("MaximumSlotsForGMPlayer", "250"));
          
            /* Config weight limit */
            ADD_MAX_LOAD = Integer.parseInt(otherSettings.getProperty("AddWeightLimit", "0"));
            LEVEL_ADD_LOAD = Boolean.parseBoolean(otherSettings.getProperty("IncreaseWeightLimitByLevel", "false"));
            
            /* Inventory slots limits */
            WAREHOUSE_SLOTS_NO_DWARF = Integer.parseInt(otherSettings.getProperty("MaximumWarehouseSlotsForNoDwarf", "100"));
            WAREHOUSE_SLOTS_DWARF = Integer.parseInt(otherSettings.getProperty("MaximumWarehouseSlotsForDwarf", "120"));
            WAREHOUSE_SLOTS_CLAN = Integer.parseInt(otherSettings.getProperty("MaximumWarehouseSlotsForClan", "150"));
            FREIGHT_SLOTS       = Integer.parseInt(otherSettings.getProperty("MaximumFreightSlots", "20"));
            MAX_ITEM_IN_PACKET = Math.max(INVENTORY_MAXIMUM_NO_DWARF, Math.max(INVENTORY_MAXIMUM_DWARF, INVENTORY_MAXIMUM_GM));
            
            /* if different from 100 (ie 100%) heal rate is modified acordingly */
            NPC_HP_REGEN_MULTIPLIER = Double.parseDouble(otherSettings.getProperty("NPCHpRegenMultiplier", "100")) /100;
            NPC_MP_REGEN_MULTIPLIER = Double.parseDouble(otherSettings.getProperty("NPCMpRegenMultiplier", "100")) /100;

            PLAYER_HP_REGEN_MULTIPLIER = Double.parseDouble(otherSettings.getProperty("PlayerHpRegenMultiplier", "100")) /100;
            PLAYER_MP_REGEN_MULTIPLIER = Double.parseDouble(otherSettings.getProperty("PlayerMpRegenMultiplier", "100")) /100;
            PLAYER_CP_REGEN_MULTIPLIER = Double.parseDouble(otherSettings.getProperty("PlayerCpRegenMultiplier", "100")) /100;
            
            RAID_HP_REGEN_MULTIPLIER  = Double.parseDouble(otherSettings.getProperty("RaidHpRegenMultiplier", "100")) /100;
            RAID_MP_REGEN_MULTIPLIER  = Double.parseDouble(otherSettings.getProperty("RaidMpRegenMultiplier", "100")) /100; 
            RAID_DEFENCE_MULTIPLIER  = Double.parseDouble(otherSettings.getProperty("RaidDefenceMultiplier", "100")) /100;    
            RAID_MINION_RESPAWN_TIMER  = Integer.parseInt(otherSettings.getProperty("RaidMinionRespawnTime", "300000"));                

            RAID_MIN_RESPAWN_MULTIPLIER = Float.parseFloat(otherSettings.getProperty("RaidMinRespawnMultiplier", "1.0"));
            RAID_MAX_RESPAWN_MULTIPLIER = Float.parseFloat(otherSettings.getProperty("RaidMaxRespawnMultiplier", "1.0"));

            STARTING_ADENA      = Integer.parseInt(otherSettings.getProperty("StartingAdena", "100"));
            UNSTUCK_INTERVAL    = Integer.parseInt(otherSettings.getProperty("UnstuckInterval", "300"));

            /* Player protection after teleport or login */
            PLAYER_SPAWN_PROTECTION = Integer.parseInt(otherSettings.getProperty("PlayerSpawnProtection", "0"));
            
            /* Player protection after recovering from fake death (works against mobs only) */
            PLAYER_FAKEDEATH_UP_PROTECTION = Integer.parseInt(otherSettings.getProperty("PlayerFakeDeathUpProtection", "0"));	            
            
            /* Defines some Party XP related values */
            PARTY_XP_CUTOFF_METHOD  = otherSettings.getProperty("PartyXpCutoffMethod", "percentage");
            PARTY_XP_CUTOFF_PERCENT = Double.parseDouble(otherSettings.getProperty("PartyXpCutoffPercent", "3."));
            PARTY_RANGE             = Integer.parseInt(otherSettings.getProperty("PartyRange", "1000"));
            PARTY_XP_CUTOFF_LEVEL   = Integer.parseInt(otherSettings.getProperty("PartyXpCutoffLevel", "30"));
            MAX_PARTY_LEVEL_DIFFERENCE = Integer.parseInt(otherSettings.getProperty("PartyMaxLevelDifference", "20"));
            
            /* Amount of HP, MP, and CP is restored */
            RESPAWN_RESTORE_CP = Double.parseDouble(otherSettings.getProperty("RespawnRestoreCP", "0")) / 100;
            RESPAWN_RESTORE_HP = Double.parseDouble(otherSettings.getProperty("RespawnRestoreHP", "70")) / 100;
            RESPAWN_RESTORE_MP = Double.parseDouble(otherSettings.getProperty("RespawnRestoreMP", "70")) / 100;
            
            RESPAWN_RANDOM_ENABLED = Boolean.parseBoolean(otherSettings.getProperty("RespawnRandomInTown", "False")); 
            RESPAWN_RANDOM_MAX_OFFSET = Integer.parseInt(otherSettings.getProperty("RespawnRandomMaxOffset", "50")); 
            
            /* Maximum number of available slots for pvt stores */
            MAX_PVTSTORE_SLOTS_DWARF = Integer.parseInt(otherSettings.getProperty("MaxPvtStoreSlotsDwarf", "5"));
            MAX_PVTSTORE_SLOTS_OTHER = Integer.parseInt(otherSettings.getProperty("MaxPvtStoreSlotsOther", "4"));
            
            STORE_SKILL_COOLTIME = Boolean.parseBoolean(otherSettings.getProperty("StoreSkillCooltime", "true"));
            
            PET_RENT_NPC =  otherSettings.getProperty("ListPetRentNpc", "30827"); 
            LIST_PET_RENT_NPC = new FastList<Integer>(); 
            for (String id : PET_RENT_NPC.split(","))
            { 
                LIST_PET_RENT_NPC.add(Integer.parseInt(id)); 
            }

            NONDROPPABLE_ITEMS        = otherSettings.getProperty("ListOfNonDroppableItems", "1147,425,1146,461,10,2368,7,6,2370,2369,5598");
            
            LIST_NONDROPPABLE_ITEMS = new FastList<Integer>();
            for (String id : NONDROPPABLE_ITEMS.trim().split(","))
            {
                LIST_NONDROPPABLE_ITEMS.add(Integer.parseInt(id.trim()));
            }
            
            ANNOUNCE_MAMMON_SPAWN = Boolean.parseBoolean(otherSettings.getProperty("AnnounceMammonSpawn", "True"));

            PETITIONING_ALLOWED = Boolean.parseBoolean(otherSettings.getProperty("PetitioningAllowed", "True"));
            MAX_PETITIONS_PER_PLAYER = Integer.parseInt(otherSettings.getProperty("MaxPetitionsPerPlayer", "5"));
            MAX_PETITIONS_PENDING = Integer.parseInt(otherSettings.getProperty("MaxPetitionsPending", "25"));                
            
            JAIL_IS_PVP       = Boolean.parseBoolean(otherSettings.getProperty("JailIsPvp", "True"));
            JAIL_DISABLE_CHAT = Boolean.parseBoolean(otherSettings.getProperty("JailDisableChat", "True"));
            
            ALLOWED_NPC_TYPES  = otherSettings.getProperty("AllowedNPCTypes");
            LIST_ALLOWED_NPC_TYPES = new FastList<String>();
            for (String npc_type : ALLOWED_NPC_TYPES.trim().split(","))
            {
                LIST_ALLOWED_NPC_TYPES.add(npc_type.trim());
            }
            
            FORBIDDEN_RAID_SKILLS = otherSettings.getProperty("ForbiddenRaidSkills", "1064,100");
            FORBIDDEN_RAID_SKILLS_LIST = new FastList<Integer>();
            for (String id : FORBIDDEN_RAID_SKILLS.trim().split(","))
            {
                FORBIDDEN_RAID_SKILLS_LIST.add(Integer.parseInt(id.trim()));
            }

            DEATH_PENALTY_CHANCE = Integer.parseInt(otherSettings.getProperty("DeathPenaltyChance", "20"));
        }
        catch (Exception e)
        {
            _log.error(e);
            throw new Error("Failed to Load "+OTHER_CONFIG_FILE+" File.");
        }
    }
    
    
    
    //  *******************************************************************************************    
    public static final String  OPTIONS_FILE                = "./config/options.properties";
    //  *******************************************************************************************
    public static boolean 				ASSERT;							// Enable/disable assertions
    public static boolean 				DEVELOPER;						// Enable/disable DEVELOPER TREATMENT	
    public static boolean       		TEST_KNOWNLIST = false;			// Internal properties for developers tests only
    public static boolean 				ALLOW_WEDDING;
    public static boolean 				SERVER_LIST_BRACKET;			// Displays [] in front of server name ?
    public static boolean 				SERVER_LIST_CLOCK;				// Displays a clock next to the server name ?
    public static boolean 				SERVER_LIST_TESTSERVER;			// Display test server in the list of servers ?
    public static boolean 				SERVER_GMONLY;					// Set the server as gm only at startup ?
    public static int 					THREAD_P_EFFECTS;				// Thread pool size effect
    public static int 					THREAD_P_GENERAL;				// Thread pool size general
    public static int 					GENERAL_PACKET_THREAD_CORE_SIZE;// Packet max thread
    public static int 					IO_PACKET_THREAD_CORE_SIZE;
    public static int 					GENERAL_THREAD_CORE_SIZE;		// General max thread
    public static int 					AI_MAX_THREAD;					// AI max thread
    public static boolean 				AUTODELETE_INVALID_QUEST_DATA;	// Auto-delete invalid quest data ?
    public static boolean       		FORCE_INVENTORY_UPDATE;
    public static boolean 				SHOW_L2J_LICENSE;				// Show License at login
    public static boolean 				SHOW_HTML_WELCOME;				// Show html window at login
    public static boolean				SHOW_HTML_NEWBIE;
    public static int					LEVEL_HTML_NEWBIE;				// Show newbie html when player's level is < to define level
    public static boolean 				USE_SAY_FILTER;					// Config for use chat filter
    public static ArrayList<String> 	FILTER_LIST = new ArrayList<String>();
    public static int     				AUTODESTROY_ITEM_AFTER;			// Time after which item will auto-destroy
    public static int     				HERB_AUTO_DESTROY_TIME;			// Auto destroy herb time
    public static String  				PROTECTED_ITEMS;
    public static FastList<Integer> 	LIST_PROTECTED_ITEMS = new FastList<Integer>();	// List of items that will not be destroyed
    public static boolean   			DESTROY_DROPPED_PLAYER_ITEM;	// Auto destroy nonequipable items dropped by players
    public static boolean   			DESTROY_PLAYER_INVENTORY_DROP;	// Auto destroy items dropped by players from inventory    
    public static boolean   			DESTROY_EQUIPABLE_PLAYER_ITEM;	// Auto destroy equipable items dropped by players
    public static boolean   			SAVE_DROPPED_ITEM;				// Save items on ground for restoration on server restart
    public static boolean   			EMPTY_DROPPED_ITEM_TABLE_AFTER_LOAD;// Empty table ItemsOnGround after load all items
    public static int       			SAVE_DROPPED_ITEM_INTERVAL;		// Time interval to save into db items on ground
    public static boolean   			CLEAR_DROPPED_ITEM_TABLE;		// Clear all items stored in ItemsOnGround table
    public static boolean 				PRECISE_DROP_CALCULATION;		// Accept precise drop calculation ?
    public static boolean 				MULTIPLE_ITEM_DROP;				// Accept multi-items drop ?
    /** This is setting of experimental Client <--> Server Player coordinates synchronization<br>
     * <b><u>Valeurs :</u></b>
     * <li>0 - no synchronization at all</li>
     * <li>1 - parcial synchronization Client --> Server only * using this option it is difficult for players 
     *         to bypass obstacles</li>
     * <li>2 - parcial synchronization Server --> Client only</li>
     * <li>3 - full synchronization Client <--> Server</li>
     * <li>-1 - Old system: will synchronize Z only</li>
     */
    public static int					COORD_SYNCHRONIZE;
    public static int					DELETE_DAYS;
    public static int					FLOODPROTECTOR_INITIALSIZE;		// FloodProtector initial capacity
    public static int					MAX_DRIFT_RANGE;				// Maximum range mobs can randomly go from spawn point
    public static boolean				ALLOW_FISHING;
    public static boolean				ALLOW_MANOR;					// Allow Manor system
    public static boolean				ALLOW_GUARDS;					// Allow guards against aggressive monsters

    public static boolean				GEODATA;						// Load geodata files
    public static boolean				GEO_CHECK_LOS;					// Enable Line Of Sight check for skills and aggro
    public static boolean				GEO_MOVE_PC;					// Movement check for playable instances
    public static boolean				GEO_MOVE_NPC;					// Movement check for NPCs
    public static boolean				GEO_PATH_FINDING;				// Enable Path Finding [ EXPERIMENTAL]
    public static enum   CorrectSpawnsZ { TOWN, MONSTER, ALL, NONE }
    public static        CorrectSpawnsZ GEO_CORRECT_Z;					// Enable spawns' z-correction
    public static boolean				FORCE_GEODATA;					// Force loading GeoData to psychical memory

    public static boolean       		ALLOW_DISCARDITEM;
    public static boolean       		ALLOW_FREIGHT;
    public static boolean       		ALLOW_WAREHOUSE;
    public static boolean				ENABLE_WAREHOUSESORTING_CLAN;    //Warehouse Sorting Clan
    public static boolean				ENABLE_WAREHOUSESORTING_PRIVATE; //Warehouse Sorting Privat
    public static boolean				ENABLE_WAREHOUSESORTING_FREIGHT; //Warehouse Sorting freight
    public static boolean         		WAREHOUSE_CACHE;				// Allow warehouse cache?
    public static int             		WAREHOUSE_CACHE_TIME;			// How long store WH datas
    public static boolean 	    		ALLOW_WEAR;
    public static int           		WEAR_DELAY;
    public static int           		WEAR_PRICE;    
    public static boolean 	    		ALLOW_LOTTERY;
    //public static boolean 	    		ALLOW_RACE;
    public static boolean 	    		ALLOW_WATER;
    public static boolean       		ALLOW_RENTPET;
    public static boolean 	    		ALLOW_BOAT;
    public static boolean        		ALLOW_CURSED_WEAPONS;			// Allow cursed weapons ?
    public static String  				DEFAULT_GLOBAL_CHAT;			// Global chat state
    public static int					GLOBAL_CHAT_TIME;
    public static String  				DEFAULT_TRADE_CHAT;				// Trade chat state
    public static int					TRADE_CHAT_TIME;
    public static boolean 				LOG_CHAT;						// Logging Chat Window
    public static boolean 				LOG_ITEMS;
    public static int 					DEFAULT_PUNISH;					// Default punishment for illegal actions
    public static int 					DEFAULT_PUNISH_PARAM;			// Parameter for default punishment    
    public static enum                  CacheType {ehcache,mapcache,none};
    public static CacheType             TYPE_CACHE;
    public static int                   CACHE_TIMETOLIVESECONDS;
    public static int                   CACHE_TIMETOIDLESECONDS;
    public static int                   CACHE_MAX_ELEM_IN_MEMORY;
    public static boolean 				GM_AUDIT;
    public static String        		COMMUNITY_TYPE;					// Community Board
    public static String        		BBS_DEFAULT;    
    public static boolean       		SHOW_LEVEL_COMMUNITYBOARD;
    public static boolean       		SHOW_STATUS_COMMUNITYBOARD;
    public static int           		NAME_PAGE_SIZE_COMMUNITYBOARD;
    public static int           		NAME_PER_ROW_COMMUNITYBOARD;
    public static boolean 				SHOW_CURSED_WEAPON_OWNER;		// Show Owner(s) of Cursed Weapons in CB ?
    public static int           		ZONE_TOWN;						// Zone Setting
    public static int           		MIN_NPC_ANIMATION;				// random animation interval
    public static int           		MAX_NPC_ANIMATION;
    public static int           		MIN_MONSTER_ANIMATION;
    public static int           		MAX_MONSTER_ANIMATION;
    public static boolean       		SHOW_NPC_LVL;					// Show L2Monster level and aggro ?
    public static int           		PACKET_LIFETIME;
    public static long          		PACKET_EXECUTIONTIME;
    public static boolean				BYPASS_VALIDATION;
    public static boolean 				GAMEGUARD_ENFORCE;
    public static boolean 				GAMEGUARD_PROHIBITACTION;    
    public static boolean 				ONLINE_PLAYERS_AT_STARTUP;		// Show Online Players announce
    public static int  					ONLINE_PLAYERS_ANNOUNCE_INTERVAL; 
    public static boolean 				GRIDS_ALWAYS_ON;				// Grid Options
    public static int 					GRID_NEIGHBOR_TURNON_TIME;		// Grid Options
    public static int 					GRID_NEIGHBOR_TURNOFF_TIME;  	// Grid Options
    public static boolean 				CHECK_SKILLS_ON_ENTER;			// Skill Tree check on EnterWorld
    public static boolean   			CHAR_VIP_SKIP_SKILLS_CHECK;		// VIP Characters configuration
    public static boolean   			CHAR_VIP_COLOR_ENABLED;			// VIP Characters configuration
    public static int       			CHAR_VIP_COLOR;					// VIP Characters configuration
    public static boolean 				ALT_DEV_NO_QUESTS;				// Alt Settings for devs
    public static boolean 				ALT_DEV_NO_SPAWNS;				// Alt Settings for devs
    public static boolean 				ONLY_GM_ITEMS_FREE;				// Only GM buy items for free
    //  *******************************************************************************************
    public static void loadOptionsConfig()
    {
        _log.info("loading " + OPTIONS_FILE);
        try 
        {
            Properties optionsSettings    = new Properties();
            InputStream is               = new FileInputStream(new File(OPTIONS_FILE));
            optionsSettings.load(is);
            is.close();
            
            ASSERT                          = Boolean.parseBoolean(optionsSettings.getProperty("Assert", "false"));
            DEVELOPER                       = Boolean.parseBoolean(optionsSettings.getProperty("Developer", "false"));
            SERVER_LIST_TESTSERVER          = Boolean.parseBoolean(optionsSettings.getProperty("TestServer", "false"));
                         
            SERVER_LIST_BRACKET             = Boolean.parseBoolean(optionsSettings.getProperty("ServerListBrackets", "false"));
            SERVER_LIST_CLOCK               = Boolean.parseBoolean(optionsSettings.getProperty("ServerListClock", "false"));
            SERVER_GMONLY                   = Boolean.parseBoolean(optionsSettings.getProperty("ServerGMOnly", "false"));
            
            AUTODESTROY_ITEM_AFTER          = Integer.parseInt(optionsSettings.getProperty("AutoDestroyDroppedItemAfter", "0"));
            HERB_AUTO_DESTROY_TIME          = Integer.parseInt(optionsSettings.getProperty("AutoDestroyHerbTime","15"))*1000;
            PROTECTED_ITEMS                 = optionsSettings.getProperty("ListOfProtectedItems");
            LIST_PROTECTED_ITEMS = new FastList<Integer>();
            for (String id : PROTECTED_ITEMS.trim().split(",")) {
                LIST_PROTECTED_ITEMS.add(Integer.parseInt(id.trim()));
                 }
            DESTROY_DROPPED_PLAYER_ITEM     = Boolean.parseBoolean(optionsSettings.getProperty("DestroyPlayerDroppedItem", "false"));
            DESTROY_PLAYER_INVENTORY_DROP   = Boolean.parseBoolean(optionsSettings.getProperty("DestroyPlayerInventoryDrop", "false"));            
            DESTROY_EQUIPABLE_PLAYER_ITEM   = Boolean.parseBoolean(optionsSettings.getProperty("DestroyEquipableItem", "false"));
            SAVE_DROPPED_ITEM               = Boolean.parseBoolean(optionsSettings.getProperty("SaveDroppedItem", "false"));
            EMPTY_DROPPED_ITEM_TABLE_AFTER_LOAD = Boolean.parseBoolean(optionsSettings.getProperty("EmptyDroppedItemTableAfterLoad", "false"));
            SAVE_DROPPED_ITEM_INTERVAL      = Integer.parseInt(optionsSettings.getProperty("SaveDroppedItemInterval", "0"))*60000;
            CLEAR_DROPPED_ITEM_TABLE        = Boolean.parseBoolean(optionsSettings.getProperty("ClearDroppedItemTable", "false"));

            PRECISE_DROP_CALCULATION        = Boolean.parseBoolean(optionsSettings.getProperty("PreciseDropCalculation", "True"));
            MULTIPLE_ITEM_DROP              = Boolean.parseBoolean(optionsSettings.getProperty("MultipleItemDrop", "True"));

            COORD_SYNCHRONIZE               = Integer.parseInt(optionsSettings.getProperty("CoordSynchronize", "-1"));

            ALLOW_WAREHOUSE                 = Boolean.parseBoolean(optionsSettings.getProperty("AllowWarehouse", "True"));
            ENABLE_WAREHOUSESORTING_CLAN    = Boolean.valueOf(optionsSettings.getProperty("EnableWarehouseSortingClan", "False"));
            ENABLE_WAREHOUSESORTING_PRIVATE = Boolean.valueOf(optionsSettings.getProperty("EnableWarehouseSortingPrivate", "False"));
            ENABLE_WAREHOUSESORTING_FREIGHT = Boolean.valueOf(optionsSettings.getProperty("EnableWarehouseSortingFreight", "False"));
            WAREHOUSE_CACHE                 = Boolean.parseBoolean(optionsSettings.getProperty("WarehouseCache", "False"));
            WAREHOUSE_CACHE_TIME            = Integer.parseInt(optionsSettings.getProperty("WarehouseCacheTime", "15"));
            ALLOW_FREIGHT                   = Boolean.parseBoolean(optionsSettings.getProperty("AllowFreight", "True"));
            ALLOW_WEAR                      = Boolean.parseBoolean(optionsSettings.getProperty("AllowWear", "False"));
            WEAR_DELAY                      = Integer.parseInt(optionsSettings.getProperty("WearDelay", "5"));
            WEAR_PRICE                      = Integer.parseInt(optionsSettings.getProperty("WearPrice", "10"));
            ALLOW_LOTTERY                   = Boolean.parseBoolean(optionsSettings.getProperty("AllowLottery", "False"));
            //ALLOW_RACE                    = Boolean.parseBoolean(optionsSettings.getProperty("AllowRace", "False"));
            ALLOW_WATER                     = Boolean.parseBoolean(optionsSettings.getProperty("AllowWater", "True"));
            ALLOW_RENTPET                   = Boolean.parseBoolean(optionsSettings.getProperty("AllowRentPet", "False"));
            ALLOW_DISCARDITEM               = Boolean.parseBoolean(optionsSettings.getProperty("AllowDiscardItem", "True"));
            ALLOW_FISHING                   = Boolean.parseBoolean(optionsSettings.getProperty("AllowFishing", "True"));
            ALLOW_MANOR                     = Boolean.parseBoolean(optionsSettings.getProperty("AllowManor", "False"));
            ALLOW_BOAT                      = Boolean.parseBoolean(optionsSettings.getProperty("AllowBoat", "False"));
            ALLOW_CURSED_WEAPONS            = Boolean.parseBoolean(optionsSettings.getProperty("AllowCursedWeapons", "False"));
            ALLOW_WEDDING                   = Boolean.parseBoolean(optionsSettings.getProperty("AllowWedding", "False"));
            ALLOW_GUARDS                    = Boolean.parseBoolean(optionsSettings.getProperty("AllowGuards", "False"));

            DEFAULT_GLOBAL_CHAT             = optionsSettings.getProperty("GlobalChat", "ON");
            GLOBAL_CHAT_TIME				= Integer.parseInt(optionsSettings.getProperty("GlobalChatTime", "1"));
            DEFAULT_TRADE_CHAT              = optionsSettings.getProperty("TradeChat", "ON");
            TRADE_CHAT_TIME					= Integer.parseInt(optionsSettings.getProperty("TradeChatTime", "1"));            

            LOG_CHAT                        = Boolean.parseBoolean(optionsSettings.getProperty("LogChat", "false"));
            LOG_ITEMS                       = Boolean.parseBoolean(optionsSettings.getProperty("LogItems", "false"));

            GM_AUDIT                        = Boolean.parseBoolean(optionsSettings.getProperty("GMAudit", "False"));

            COMMUNITY_TYPE                  = optionsSettings.getProperty("CommunityType", "old").toLowerCase();
            BBS_DEFAULT                     = optionsSettings.getProperty("BBSDefault", "_bbshome");
            SHOW_LEVEL_COMMUNITYBOARD       = Boolean.parseBoolean(optionsSettings.getProperty("ShowLevelOnCommunityBoard", "False"));
            SHOW_STATUS_COMMUNITYBOARD      = Boolean.parseBoolean(optionsSettings.getProperty("ShowStatusOnCommunityBoard", "True"));
            NAME_PAGE_SIZE_COMMUNITYBOARD   = Integer.parseInt(optionsSettings.getProperty("NamePageSizeOnCommunityBoard", "50"));
            if (NAME_PAGE_SIZE_COMMUNITYBOARD > 25) NAME_PAGE_SIZE_COMMUNITYBOARD = 25;
            NAME_PER_ROW_COMMUNITYBOARD     = Integer.parseInt(optionsSettings.getProperty("NamePerRowOnCommunityBoard", "5"));
            if (NAME_PER_ROW_COMMUNITYBOARD > 5) NAME_PER_ROW_COMMUNITYBOARD = 5;
            SHOW_CURSED_WEAPON_OWNER        = Boolean.parseBoolean(optionsSettings.getProperty("ShowCursedWeaponOwner", "False"));

            ZONE_TOWN                       = Integer.parseInt(optionsSettings.getProperty("ZoneTown", "0"));

            MAX_DRIFT_RANGE                 = Integer.parseInt(optionsSettings.getProperty("MaxDriftRange", "300"));

            MIN_NPC_ANIMATION               = Integer.parseInt(optionsSettings.getProperty("MinNPCAnimation", "10"));
            MAX_NPC_ANIMATION               = Integer.parseInt(optionsSettings.getProperty("MaxNPCAnimation", "20"));
            MIN_MONSTER_ANIMATION           = Integer.parseInt(optionsSettings.getProperty("MinMonsterAnimation", "5"));
            MAX_MONSTER_ANIMATION           = Integer.parseInt(optionsSettings.getProperty("MaxMonsterAnimation", "20"));

            SHOW_NPC_LVL                    = Boolean.parseBoolean(optionsSettings.getProperty("ShowNpcLevel", "False"));

            FORCE_INVENTORY_UPDATE          = Boolean.parseBoolean(optionsSettings.getProperty("ForceInventoryUpdate", "False"));

            AUTODELETE_INVALID_QUEST_DATA   = Boolean.parseBoolean(optionsSettings.getProperty("AutoDeleteInvalidQuestData", "False"));

            THREAD_P_EFFECTS                = Integer.parseInt(optionsSettings.getProperty("ThreadPoolSizeEffects", "6"));
            THREAD_P_GENERAL                = Integer.parseInt(optionsSettings.getProperty("ThreadPoolSizeGeneral", "15"));
            GENERAL_PACKET_THREAD_CORE_SIZE = Integer.parseInt(optionsSettings.getProperty("GeneralPacketThreadCoreSize", "4"));
            IO_PACKET_THREAD_CORE_SIZE      = Integer.parseInt(optionsSettings.getProperty("UrgentPacketThreadCoreSize", "2"));
            GENERAL_THREAD_CORE_SIZE        = Integer.parseInt(optionsSettings.getProperty("GeneralThreadCoreSize", "4"));
            AI_MAX_THREAD                   = Integer.parseInt(optionsSettings.getProperty("AiMaxThread", "10"));

            DELETE_DAYS                     = Integer.parseInt(optionsSettings.getProperty("DeleteCharAfterDays", "7"));

            FLOODPROTECTOR_INITIALSIZE		= Integer.parseInt(optionsSettings.getProperty("FloodProtectorInitialSize", "50"));

            DEFAULT_PUNISH                  = Integer.parseInt(optionsSettings.getProperty("DefaultPunish", "2"));
            DEFAULT_PUNISH_PARAM            = Integer.parseInt(optionsSettings.getProperty("DefaultPunishParam", "0"));

            TYPE_CACHE                      = CacheType.valueOf(optionsSettings.getProperty("CacheType", "ehcache").toLowerCase());
            CACHE_MAX_ELEM_IN_MEMORY        = Integer.parseInt(optionsSettings.getProperty("MaxElemInMemory", "9700"));
            CACHE_TIMETOLIVESECONDS         = Integer.parseInt(optionsSettings.getProperty("TimeToLiveSeconds", "7200"));
            CACHE_TIMETOIDLESECONDS         = Integer.parseInt(optionsSettings.getProperty("TimeToIdleSeconds", "3600"));

            PACKET_LIFETIME                 = Integer.parseInt(optionsSettings.getProperty("PacketLifeTime", "0"));
            PACKET_EXECUTIONTIME            = Long.parseLong(optionsSettings.getProperty("PacketExecutionTime", "0"));

            BYPASS_VALIDATION               = Boolean.parseBoolean(optionsSettings.getProperty("BypassValidation", "False"));

            GAMEGUARD_ENFORCE               = Boolean.parseBoolean(optionsSettings.getProperty("GameGuardEnforce", "False"));
            GAMEGUARD_PROHIBITACTION        = Boolean.parseBoolean(optionsSettings.getProperty("GameGuardProhibitAction", "False"));
            GRIDS_ALWAYS_ON                 = Boolean.parseBoolean(optionsSettings.getProperty("GridsAlwaysOn", "False"));
            GRID_NEIGHBOR_TURNON_TIME       = Integer.parseInt(optionsSettings.getProperty("GridNeighborTurnOnTime", "30"));
            GRID_NEIGHBOR_TURNOFF_TIME      = Integer.parseInt(optionsSettings.getProperty("GridNeighborTurnOffTime", "300"));    

            GEODATA                         = Boolean.parseBoolean(optionsSettings.getProperty("GeoData", "False"));
            GEO_CHECK_LOS                   = Boolean.parseBoolean(optionsSettings.getProperty("GeoCheckLoS", "False")) && GEODATA;
            GEO_MOVE_PC                     = Boolean.parseBoolean(optionsSettings.getProperty("GeoCheckMovePlayable", "False")) && GEODATA;
            GEO_MOVE_NPC                    = Boolean.parseBoolean(optionsSettings.getProperty("GeoCheckMoveNpc", "False")) && GEODATA;
            GEO_PATH_FINDING                = Boolean.parseBoolean(optionsSettings.getProperty("GeoPathFinding", "False")) && GEODATA;
            FORCE_GEODATA                   = Boolean.parseBoolean(optionsSettings.getProperty("ForceGeoData", "True")) && GEODATA;
            String correctZ                 = GEODATA ? optionsSettings.getProperty("GeoCorrectZ", "ALL") : "NONE";
            GEO_CORRECT_Z                   = CorrectSpawnsZ.valueOf(correctZ.toUpperCase());

            SHOW_L2J_LICENSE                = Boolean.parseBoolean(optionsSettings.getProperty("ShowL2JLicense", "false"));
            SHOW_HTML_WELCOME               = Boolean.parseBoolean(optionsSettings.getProperty("ShowHTMLWelcome", "false"));
            SHOW_HTML_NEWBIE                = Boolean.parseBoolean(optionsSettings.getProperty("ShowHTMLNewbie", "False"));
            LEVEL_HTML_NEWBIE               = Integer.parseInt(optionsSettings.getProperty("LevelShowHTMLNewbie", "10"));
            USE_SAY_FILTER                  = Boolean.parseBoolean(optionsSettings.getProperty("UseSayFilter", "false"));

            CHAR_VIP_SKIP_SKILLS_CHECK		= Boolean.parseBoolean(optionsSettings.getProperty("CharViPSkipSkillsCheck", "false"));
            CHAR_VIP_COLOR_ENABLED			= Boolean.parseBoolean(optionsSettings.getProperty("CharViPAllowColor", "false"));
            CHAR_VIP_COLOR                  = Integer.decode("0x" + optionsSettings.getProperty("CharViPNameColor", "00CCFF"));

            ONLINE_PLAYERS_AT_STARTUP = Boolean.parseBoolean(optionsSettings.getProperty("ShowOnlinePlayersAtStartup","True"));
            ONLINE_PLAYERS_ANNOUNCE_INTERVAL = Integer.parseInt(optionsSettings.getProperty("OnlinePlayersAnnounceInterval","900000"));

            ONLY_GM_ITEMS_FREE             = Boolean.parseBoolean(optionsSettings.getProperty("OnlyGMItemsFree", "True"));

            // ---------------------------------------------------
            // Configuration values not found in config files
            // ---------------------------------------------------

            CHECK_SKILLS_ON_ENTER          = Boolean.parseBoolean(optionsSettings.getProperty("CheckSkillsOnEnter","false"));

            ALT_DEV_NO_QUESTS               = Boolean.parseBoolean(optionsSettings.getProperty("AltDevNoQuests", "False"));
            ALT_DEV_NO_SPAWNS               = Boolean.parseBoolean(optionsSettings.getProperty("AltDevNoSpawns", "False"));
        }
        catch (Exception e)
        {
            _log.error(e.getMessage(),e);
            throw new Error("Failed to Load "+OPTIONS_FILE+" File.");
        }
    }
    
    
    
    //  *******************************************************************************************    
    public static final String  ALT_SETTINGS_FILE			= "./config/altsettings.properties";
    //  *******************************************************************************************
    public static boolean 			ALT_GAME_CREATION;					// Alternative game crafting
    public static double 			ALT_GAME_CREATION_SPEED;			// Alternative game crafting speed mutiplier - default 0 (fastest but still not instant)
    public static double 			ALT_GAME_CREATION_XP_RATE;			// Alternative game crafting XP rate multiplier - default 1
    public static double 			ALT_GAME_CREATION_SP_RATE;			// Alternative game crafting SP rate multiplier - default 1
    public static boolean 			ALT_BLACKSMITH_USE_RECIPES;			// Alternative setting to blacksmith use of recipes to craft - default true
    public static int 				ALT_GAME_NUMBER_OF_CUMULATED_BUFF;	// Alternative number of cumulated buff
    public static boolean 			ALT_DISABLE_RAIDBOSS_PETRIFICATION;	// Disable Raidboss Petrification
    public static boolean 			ALT_STATPOINT_DISTRIBUTION;			// Own Stat Point Distribution
    public static int 				ALT_CRITICAL_CAP;					// Critical Cap
    public static boolean 			ALT_GAME_SKILL_LEARN;				// Alternative game skill learning
    public static boolean 			ALT_GAME_CANCEL_BOW;				// Cancel attack bow by hit
    public static boolean 			ALT_GAME_CANCEL_CAST;				// Cancel cast by hit
    public static boolean 			ALT_GAME_TIREDNESS;					// Alternative game - use tiredness, instead of CP
    public static boolean 			ALT_GAME_SHIELD_BLOCKS;				// Alternative shield defence
    public static int 				ALT_PERFECT_SHLD_BLOCK;				// Alternative Perfect shield defence rate
    public static boolean 			ALT_GAME_MOB_ATTACK_AI;				// Alternative game mob ATTACK AI
    public static boolean			ALT_MOB_AGRO_IN_PEACEZONE;			// -
    public static float 			ALT_INSTANT_KILL_EFFECT_2;			// Rate of Instant kill effect 2(CP no change ,HP =1,no kill
    public static float 			ALT_DAGGER_DMG_VS_HEAVY;			// Alternative damage for dagger skills VS heavy
    public static float 			ALT_DAGGER_DMG_VS_ROBE;				// Alternative damage for dagger skills VS robe
    public static float 			ALT_DAGGER_DMG_VS_LIGHT;			// Alternative damage for dagger skills VS light
    public static boolean 			ALT_DAGGER_FORMULA;					// Alternative success rate formulas for skills such dagger/critical skills and blows
    public static float 			ALT_ATTACK_DELAY;					// Alternative config for next hit delay
    public static int 				ALT_DAGGER_RATE;					// Alternative success rate for dagger blow,MAX value 100 (100% rate)
    public static int 				ALT_DAGGER_FAIL_RATE;				// Alternative fail rate for dagger blow,MAX value 100 (100% rate)
    public static int 				ALT_DAGGER_RATE_BEHIND;				// Alternative increasement to success rate for dagger/critical skills if activeChar is Behind the target
    public static int 				ALT_DAGGER_RATE_FRONT;				// Alternative increasement to success rate for dagger/critical skills if activeChar is in Front of target
    public static boolean 			ALT_GAME_FREIGHTS;					// Alternative freight modes - Freights can be withdrawed from any village
    public static int 				ALT_GAME_FREIGHT_PRICE;				// Alternative freight modes - Sets the price value for each freightened item
    public static float 			ALT_GAME_SKILL_HIT_RATE;			// Fast or slow multiply coefficient for skill hit time
    public static boolean 			ALT_GAME_DELEVEL;					// Alternative gameing - loss of XP on death
    public static boolean 			ALT_GAME_MAGICFAILURES;				// Alternative gameing - magic dmg failures
    public static boolean 			ALT_GAME_FREE_TELEPORT;				// Alternative gaming - allow free teleporting around the world.
    public static boolean 			ALT_RECOMMEND;						// Disallow recommend character twice or more a day ?
    public static boolean 			ALT_GAME_SUBCLASS_WITHOUT_QUESTS;	// Alternative gaming - allow sub-class addition without quest completion.
    public static int 				MAX_SUBCLASS;						// Allow to change max number of subclasses
    public static boolean 			ALT_GAME_VIEWNPC;					// View npc stats/drop by shift-cliking it for nongm-players    
    public static boolean 			ALT_GAME_NEW_CHAR_ALWAYS_IS_NEWBIE;	// Alternative gaming - all new characters always are newbies.
    public static boolean 			ALT_STRICT_HERO_SYSTEM;				// Strict Hero Mode
    public static boolean 			ALT_STRICT_SEVENSIGNS;				// Strict Seven Signs
    public static boolean 			SP_BOOK_NEEDED;						// Spell Book needed to learn skill
    public static boolean 			CLAN_ITEM_NEEDED;					// Clan Item needed to learn clan skills
    public static int 				ALT_BUFF_TIME;
    public static int 				ALT_DANCE_TIME;
    public static int 				ALT_SEED_TIME;
    public static int 				MAX_PATK_SPEED;						// Config for limit physical attack speed
    public static int 				MAX_MATK_SPEED;						// Config for limit magical attack speed
    public static float 			ALT_MAGES_PHYSICAL_DAMAGE_MULTI;	// Config for damage multiplies
    public static float 			ALT_MAGES_MAGICAL_DAMAGE_MULTI;		// Config for damage multiplies
    public static float 			ALT_FIGHTERS_PHYSICAL_DAMAGE_MULTI;	// Config for damage multiplies
    public static float 			ALT_FIGHTERS_MAGICAL_DAMAGE_MULTI;	// Config for damage multiplies
    public static float 			ALT_PETS_PHYSICAL_DAMAGE_MULTI;		// Config for damage multiplies
    public static float 			ALT_PETS_MAGICAL_DAMAGE_MULTI;		// Config for damage multiplies
    public static float 			ALT_NPC_PHYSICAL_DAMAGE_MULTI;		// Config for damage multiplies
    public static float 			ALT_NPC_MAGICAL_DAMAGE_MULTI;		// Config for damage multiplies
    public static int 				ALT_URN_TEMP_FAIL;					// Config for URN temp fail
    public static int 				ALT_BUFFER_HATE;					// Buffer Hate
    public static int 				ALT_DIFF_CUTOFF;					// No exp cutoff
    public static boolean       	SPAWN_WYVERN_MANAGER;
    public static boolean       	SPAWN_CLASS_MASTER;
    public static boolean			CLASS_MASTER_STRIDER_UPDATE;
    public static String        	CLASS_MASTER_SETTINGS_LINE;
    public static ClassMasterSettings  CLASS_MASTER_SETTINGS;
    public static double 			ALT_CRAFT_PRICE;                    // reference price multiplier
    public static int 				ALT_CRAFT_DEFAULT_PRICE;       	    // default price, in case reference is 0
    public static boolean 			ALT_CRAFT_ALLOW_CRAFT;             	// allow to craft dwarven recipes
    public static boolean 			ALT_CRAFT_ALLOW_CRYSTALLIZE;      	// allow to break items
    public static boolean 			ALT_CRAFT_ALLOW_COMMON;            	// allow to craft common craft recipes
    public static boolean 			AUTO_LOOT;							// Accept auto-loot ?
    public static boolean 			AUTO_LOOT_HERBS;
    public static boolean 			SPAWN_SIEGE_GUARD;					// Config for spawn siege guards 
	public static int 				TIME_IN_A_DAY_OF_OPEN_A_DOOR;		
	public static int 				TIME_OF_OPENING_A_DOOR;
	public static int 				NURSEANT_RESPAWN_DELAY;
    public static int 				DWARF_RECIPE_LIMIT;					// Recipebook limits
    public static int 				COMMON_RECIPE_LIMIT;  
    public static int 				CHANCE_BREAK;						// Chance For Soul Crystal to Break
    public static int 				CHANCE_LEVEL;						// Chance For Soul Crystal to Level
    public static boolean 			ALT_GAME_KARMA_PLAYER_CAN_BE_KILLED_IN_PEACEZONE;// Karma Punishment
    public static boolean 			ALT_GAME_KARMA_PLAYER_CAN_SHOP;
    public static boolean 			ALT_GAME_KARMA_PLAYER_CAN_USE_GK;	// Allow player with karma to use GK ?    
    public static boolean 			ALT_GAME_KARMA_PLAYER_CAN_TELEPORT;
    public static boolean 			ALT_GAME_KARMA_PLAYER_CAN_TRADE;
    public static boolean 			ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE;
    public static int 				ALT_PLAYER_PROTECTION_LEVEL;		// Player Protection Level    
    public static boolean 			AUTO_LEARN_SKILLS;					// Config for Auto Learn Skills
    public static boolean 			GRADE_PENALTY;						// Disable Grade penalty
    public static boolean       	IS_CRAFTING_ENABLED;				// Crafting Enabled?
    public static boolean   		FAIL_FAKEDEATH;						// Config for Fake Death Fail Feature
    public static boolean 			ALT_FLYING_WYVERN_IN_SIEGE;			// Config for Wyvern enable flying in siege **/
    public static int 				ALT_OLY_START_TIME;			// Olympiad Compitition Starting time
    public static int 				ALT_OLY_MIN;				// Olympiad Compition Min
    public static int 				ALT_OLY_CPERIOD;			// Olympaid Comptetition Period
    public static int 				ALT_OLY_BATTLE;				// Olympiad Battle Period
    public static int 				ALT_OLY_BWAIT;				// Olympiad Battle Wait
    public static int 				ALT_OLY_IWAIT;				// Olympiad Inital Wait
    public static int 				ALT_OLY_WPERIOD;			// Olympaid Weekly Period
    public static int 				ALT_OLY_VPERIOD;			// Olympaid Validation Period
    public static float ALT_GAME_SUMMON_PENALTY_RATE;			// Alternative game summon penalty    

    // Dimensional Rift
    public static int 				RIFT_MIN_PARTY_SIZE;		// Minimum siz e of a party that may enter dimensional rift
    public static int 				RIFT_SPAWN_DELAY;			// Time in ms the party has to wait until the mobs spawn when entering a room
    public static int 				RIFT_MAX_JUMPS;				// Amount of random rift jumps before party is ported back
    public static int 				RIFT_AUTO_JUMPS_TIME_MIN;	// Random time between two jumps in dimensional rift - in seconds
    public static int 				RIFT_AUTO_JUMPS_TIME_MAX;
    public static int 				RIFT_ENTER_COST_RECRUIT;	// Dimensional Fragment cost for entering rift
    public static int 				RIFT_ENTER_COST_SOLDIER;
    public static int 				RIFT_ENTER_COST_OFFICER;
    public static int 				RIFT_ENTER_COST_CAPTAIN;
    public static int 				RIFT_ENTER_COST_COMMANDER;
    public static int 				RIFT_ENTER_COST_HERO;
    public static float 			RIFT_BOSS_ROOM_TIME_MUTIPLY;// Time multiplier for boss room

    //  *******************************************************************************************    
    //  *******************************************************************************************    
    //  *******************************************************************************************
    public static void loadAltConfig()
    {
        _log.info("loading " + ALT_SETTINGS_FILE);
        try
        {
            Properties altSettings  = new Properties();
            InputStream is          = new FileInputStream(new File(ALT_SETTINGS_FILE));  
            altSettings.load(is);
            is.close();
            
            ALT_GAME_TIREDNESS      = Boolean.parseBoolean(altSettings.getProperty("AltGameTiredness", "false"));
            ALT_GAME_CREATION       = Boolean.parseBoolean(altSettings.getProperty("AltGameCreation", "false"));
            ALT_GAME_CREATION_SPEED = Double.parseDouble(altSettings.getProperty("AltGameCreationSpeed", "1"));
            ALT_GAME_CREATION_XP_RATE=Double.parseDouble(altSettings.getProperty("AltGameCreationRateXp", "1"));
            ALT_GAME_CREATION_SP_RATE=Double.parseDouble(altSettings.getProperty("AltGameCreationRateSp", "1"));
            ALT_BLACKSMITH_USE_RECIPES=Boolean.parseBoolean(altSettings.getProperty("AltBlacksmithUseRecipes", "true"));
            ALT_GAME_NUMBER_OF_CUMULATED_BUFF= Integer.parseInt(altSettings.getProperty("AltNbCumulatedBuff", "24"));
            ALT_GAME_SKILL_LEARN    = Boolean.parseBoolean(altSettings.getProperty("AltGameSkillLearn", "false"));
            ALT_GAME_CANCEL_BOW     = altSettings.getProperty("AltGameCancelByHit", "Cast").trim().equalsIgnoreCase("bow") || altSettings.getProperty("AltGameCancelByHit", "Cast").trim().equalsIgnoreCase("all");
            ALT_GAME_CANCEL_CAST    = altSettings.getProperty("AltGameCancelByHit", "Cast").trim().equalsIgnoreCase("cast") || altSettings.getProperty("AltGameCancelByHit", "Cast").trim().equalsIgnoreCase("all");
            ALT_GAME_SHIELD_BLOCKS  = Boolean.parseBoolean(altSettings.getProperty("AltShieldBlocks", "false"));
            ALT_PERFECT_SHLD_BLOCK  = Integer.parseInt(altSettings.getProperty("AltPerfectShieldBlockRate", "10"));
            ALT_GAME_DELEVEL        = Boolean.parseBoolean(altSettings.getProperty("Delevel", "true"));
            ALT_GAME_MAGICFAILURES  = Boolean.parseBoolean(altSettings.getProperty("MagicFailures", "false"));
            ALT_GAME_MOB_ATTACK_AI  = Boolean.parseBoolean(altSettings.getProperty("AltGameMobAttackAI", "false"));
            ALT_MOB_AGRO_IN_PEACEZONE = Boolean.parseBoolean(altSettings.getProperty("AltMobAgroInPeaceZone", "true"));
            ALT_INSTANT_KILL_EFFECT_2 = Float.parseFloat(altSettings.getProperty("InstantKillEffect2", "2"));
            ALT_DAGGER_DMG_VS_HEAVY = Float.parseFloat(altSettings.getProperty("DaggerVSHeavy", "2.50"));
            ALT_DAGGER_DMG_VS_ROBE  = Float.parseFloat(altSettings.getProperty("DaggerVSRobe", "2.00"));
            ALT_DAGGER_DMG_VS_LIGHT = Float.parseFloat(altSettings.getProperty("DaggerVSLight", "1.80"));
            ALT_DAGGER_FORMULA      = Boolean.parseBoolean(altSettings.getProperty("AltGameDaggerFormula", "false"));
            ALT_DAGGER_RATE         = Integer.parseInt(altSettings.getProperty("AltCancelRate", "85"));
            ALT_DAGGER_FAIL_RATE    = Integer.parseInt(altSettings.getProperty("AltFailRate", "15"));
            ALT_DAGGER_RATE_BEHIND  = Integer.parseInt(altSettings.getProperty("AltSuccessRateBehind", "20"));
            ALT_DAGGER_RATE_FRONT   = Integer.parseInt(altSettings.getProperty("AltSuccessRateFront", "5"));
            ALT_ATTACK_DELAY        = Float.parseFloat(altSettings.getProperty("AltAttackDelay", "1.00"));
            ALT_GAME_EXPONENT_XP    = Float.parseFloat(altSettings.getProperty("AltGameExponentXp", "0."));
            ALT_GAME_EXPONENT_SP    = Float.parseFloat(altSettings.getProperty("AltGameExponentSp", "0."));
            
            SPAWN_WYVERN_MANAGER      = Boolean.parseBoolean(altSettings.getProperty("SpawnWyvernManager", "True"));
            SPAWN_CLASS_MASTER      = Boolean.parseBoolean(altSettings.getProperty("SpawnClassMaster", "False"));
            CLASS_MASTER_STRIDER_UPDATE = Boolean.parseBoolean(altSettings.getProperty("ClassMasterUpdateStrider", "False")); 
            if (!altSettings.getProperty("ConfigClassMaster").trim().equalsIgnoreCase("False"))
            	CLASS_MASTER_SETTINGS_LINE = altSettings.getProperty("ConfigClassMaster");
            
            CLASS_MASTER_SETTINGS = new ClassMasterSettings(CLASS_MASTER_SETTINGS_LINE);
            
            ALT_GAME_FREIGHTS       = Boolean.parseBoolean(altSettings.getProperty("AltGameFreights", "false"));
            ALT_GAME_FREIGHT_PRICE  = Integer.parseInt(altSettings.getProperty("AltGameFreightPrice", "1000"));
            ALT_GAME_SKILL_HIT_RATE = Float.parseFloat(altSettings.getProperty("AltGameSkillHitRate", "1."));
            CHANCE_BREAK            = Integer.parseInt(altSettings.getProperty("ChanceToBreak", "10"));
            CHANCE_LEVEL            = Integer.parseInt(altSettings.getProperty("ChanceToLevel", "32"));
            IS_CRAFTING_ENABLED     							= Boolean.parseBoolean(altSettings.getProperty("CraftingEnabled", "true"));
            FAIL_FAKEDEATH 										= Boolean.parseBoolean(altSettings.getProperty("FailFakeDeath", "true"));
            ALT_FLYING_WYVERN_IN_SIEGE 							= Boolean.parseBoolean(altSettings.getProperty("AltFlyingWyvernInSiege", "false"));
            SP_BOOK_NEEDED          							= Boolean.parseBoolean(altSettings.getProperty("SpBookNeeded", "true"));
            CLAN_ITEM_NEEDED									= Boolean.parseBoolean(altSettings.getProperty("ClanItemNeeded", "True"));
            AUTO_LOOT               							= altSettings.getProperty("AutoLoot").trim().equalsIgnoreCase("True");
            AUTO_LOOT_HERBS         							= altSettings.getProperty("AutoLootHerbs").trim().equalsIgnoreCase("True");
            ALT_GAME_KARMA_PLAYER_CAN_BE_KILLED_IN_PEACEZONE    = Boolean.parseBoolean(altSettings.getProperty("AltKarmaPlayerCanBeKilledInPeaceZone", "false"));
            ALT_GAME_KARMA_PLAYER_CAN_SHOP                      = Boolean.parseBoolean(altSettings.getProperty("AltKarmaPlayerCanShop", "true"));
            ALT_GAME_KARMA_PLAYER_CAN_USE_GK                    = Boolean.parseBoolean(altSettings.getProperty("AltKarmaPlayerCanUseGK", "false"));
            ALT_GAME_KARMA_PLAYER_CAN_TELEPORT                  = Boolean.parseBoolean(altSettings.getProperty("AltKarmaPlayerCanTeleport", "true"));
            ALT_GAME_KARMA_PLAYER_CAN_TRADE                     = Boolean.parseBoolean(altSettings.getProperty("AltKarmaPlayerCanTrade", "true"));
            ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE             = Boolean.parseBoolean(altSettings.getProperty("AltKarmaPlayerCanUseWareHouse", "true"));
            ALT_PLAYER_PROTECTION_LEVEL                         = Integer.parseInt(altSettings.getProperty("AltPlayerProtectionLevel","0"));
            ALT_GAME_FREE_TELEPORT                              = Boolean.parseBoolean(altSettings.getProperty("AltFreeTeleporting", "False"));
            ALT_RECOMMEND                                       = Boolean.parseBoolean(altSettings.getProperty("AltRecommend", "False"));
            ALT_GAME_SUBCLASS_WITHOUT_QUESTS                    = Boolean.parseBoolean(altSettings.getProperty("AltSubClassWithoutQuests", "False"));
            MAX_SUBCLASS                                        = Integer.parseInt(altSettings.getProperty("MaxSubclass","3"));
            ALT_GAME_VIEWNPC                    				= Boolean.parseBoolean(altSettings.getProperty("AltGameViewNpc", "False"));
            ALT_GAME_NEW_CHAR_ALWAYS_IS_NEWBIE                  = Boolean.parseBoolean(altSettings.getProperty("AltNewCharAlwaysIsNewbie", "False"));
            DWARF_RECIPE_LIMIT                                  = Integer.parseInt(altSettings.getProperty("DwarfRecipeLimit","50"));
            COMMON_RECIPE_LIMIT                                 = Integer.parseInt(altSettings.getProperty("CommonRecipeLimit","50"));
            
            ALT_STRICT_HERO_SYSTEM                              = Boolean.parseBoolean(altSettings.getProperty("StrictHeroSystem", "True"));
            ALT_STRICT_SEVENSIGNS                               = Boolean.parseBoolean(altSettings.getProperty("StrictSevenSigns", "True"));
            
            ALT_BUFF_TIME                                       = Integer.parseInt(altSettings.getProperty("AltBuffTime", "1"));
            ALT_DANCE_TIME                                      = Integer.parseInt(altSettings.getProperty("AltDanceTime", "1"));
            ALT_SEED_TIME                                       = Integer.parseInt(altSettings.getProperty("AltSeedTime", "1"));
            SPAWN_SIEGE_GUARD 									= Boolean.parseBoolean(altSettings.getProperty("SpawnSiegeGuard", "true"));
            AUTO_LEARN_SKILLS 									= Boolean.parseBoolean(altSettings.getProperty("AutoLearnSkills", "false"));
            MAX_PATK_SPEED 										= Integer.parseInt(altSettings.getProperty("MaxPAtkSpeed", "0"));
            MAX_MATK_SPEED                                      = Integer.parseInt(altSettings.getProperty("MaxMAtkSpeed", "0"));
            ALT_MAGES_PHYSICAL_DAMAGE_MULTI                     = Float.parseFloat(altSettings.getProperty("AltPDamageMages", "1.00"));
            ALT_MAGES_MAGICAL_DAMAGE_MULTI                      = Float.parseFloat(altSettings.getProperty("AltMDamageMages", "1.00"));
            ALT_FIGHTERS_PHYSICAL_DAMAGE_MULTI                  = Float.parseFloat(altSettings.getProperty("AltPDamageFighters", "1.00"));
            ALT_FIGHTERS_MAGICAL_DAMAGE_MULTI                   = Float.parseFloat(altSettings.getProperty("AltMDamageFighters", "1.00"));
            ALT_PETS_PHYSICAL_DAMAGE_MULTI                      = Float.parseFloat(altSettings.getProperty("AltPDamagePets", "1.00"));
            ALT_PETS_MAGICAL_DAMAGE_MULTI                       = Float.parseFloat(altSettings.getProperty("AltMDamagePets", "1.00"));
            ALT_NPC_PHYSICAL_DAMAGE_MULTI                       = Float.parseFloat(altSettings.getProperty("AltPDamageNpc", "1.00"));
            ALT_NPC_MAGICAL_DAMAGE_MULTI                        = Float.parseFloat(altSettings.getProperty("AltMDamageNpc", "1.00"));
            ALT_BUFFER_HATE                                     = Integer.parseInt(altSettings.getProperty("BufferHate", "4"));                
            GRADE_PENALTY										= Boolean.parseBoolean(altSettings.getProperty("GradePenalty", "true"));
            ALT_URN_TEMP_FAIL                                   = Integer.parseInt(altSettings.getProperty("UrnTempFail", "10"));
            ALT_DISABLE_RAIDBOSS_PETRIFICATION                  = Boolean.parseBoolean(altSettings.getProperty("DisableRaidBossPetrification", "false"));
            ALT_STATPOINT_DISTRIBUTION                          = Boolean.parseBoolean(altSettings.getProperty("AltStatPointDistribution", "false"));
            ALT_CRITICAL_CAP								 	= Integer.parseInt(altSettings.getProperty("AltCriticalCap", "500"));
            
            ALT_OLY_START_TIME                                  = Integer.parseInt(altSettings.getProperty("AltOlyStartTime", "20"));
	        ALT_OLY_MIN                                         = Integer.parseInt(altSettings.getProperty("AltOlyMin","00"));
	        ALT_OLY_CPERIOD                                     = Integer.parseInt(altSettings.getProperty("AltOlyPeriod","14100000"));
	        ALT_OLY_BATTLE                                      = Integer.parseInt(altSettings.getProperty("AltOlyBattle","180000"));
	        ALT_OLY_BWAIT                                       = Integer.parseInt(altSettings.getProperty("AltOlyBWait","600000"));
	        ALT_OLY_IWAIT                                       = Integer.parseInt(altSettings.getProperty("AltOlyPwait","300000"));
	        ALT_OLY_WPERIOD                                     = Integer.parseInt(altSettings.getProperty("AltOlyWperiod","604800000"));
	        ALT_OLY_VPERIOD                                     = Integer.parseInt(altSettings.getProperty("AltOlyVperiod","86400000"));
	    	
            ALT_CRAFT_ALLOW_CRAFT                               = Boolean.parseBoolean(altSettings.getProperty("CraftManagerDwarvenCraft", "True"));
            ALT_CRAFT_ALLOW_COMMON                              = Boolean.parseBoolean(altSettings.getProperty("CraftManagerCommonCraft", "False"));
            ALT_CRAFT_ALLOW_CRYSTALLIZE                         = Boolean.parseBoolean(altSettings.getProperty("CraftManagerCrystallize", "True"));
            ALT_CRAFT_PRICE                                     = Float.parseFloat(altSettings.getProperty("CraftManagerPriceMultiplier", "0.1"));
            ALT_CRAFT_DEFAULT_PRICE                             = Integer.parseInt(altSettings.getProperty("CraftManagerDefaultPrice", "50000"));
            
            TIME_IN_A_DAY_OF_OPEN_A_DOOR 						= Integer.parseInt(altSettings.getProperty("TimeInADayOfOpenADoor", "0"));
            TIME_OF_OPENING_A_DOOR 								= Integer.parseInt(altSettings.getProperty("TimeOfOpeningADoor", "5"));
            NURSEANT_RESPAWN_DELAY 								= Integer.parseInt(altSettings.getProperty("NurseAntRespawnDelay", "15"));
            if (NURSEANT_RESPAWN_DELAY < 15) NURSEANT_RESPAWN_DELAY = 15;
            else if (NURSEANT_RESPAWN_DELAY > 120) NURSEANT_RESPAWN_DELAY = 120;
            NURSEANT_RESPAWN_DELAY = NURSEANT_RESPAWN_DELAY * 1000;
            
            ALT_GAME_SUMMON_PENALTY_RATE = Float.parseFloat(altSettings.getProperty("AltSummonPenaltyRate", "1"));

            // Dimensional Rift Config
            RIFT_MIN_PARTY_SIZE              = Integer.parseInt(altSettings.getProperty("RiftMinPartySize", "5")); 
            RIFT_MAX_JUMPS                   = Integer.parseInt(altSettings.getProperty("MaxRiftJumps", "4")); 
            RIFT_SPAWN_DELAY                 = Integer.parseInt(altSettings.getProperty("RiftSpawnDelay", "10000"));
            RIFT_AUTO_JUMPS_TIME_MIN         = Integer.parseInt(altSettings.getProperty("AutoJumpsDelayMin", "480")); 
            RIFT_AUTO_JUMPS_TIME_MAX         = Integer.parseInt(altSettings.getProperty("AutoJumpsDelayMax", "600")); 
            RIFT_ENTER_COST_RECRUIT          = Integer.parseInt(altSettings.getProperty("RecruitCost", "18")); 
            RIFT_ENTER_COST_SOLDIER          = Integer.parseInt(altSettings.getProperty("SoldierCost", "21")); 
            RIFT_ENTER_COST_OFFICER          = Integer.parseInt(altSettings.getProperty("OfficerCost", "24")); 
            RIFT_ENTER_COST_CAPTAIN          = Integer.parseInt(altSettings.getProperty("CaptainCost", "27")); 
            RIFT_ENTER_COST_COMMANDER        = Integer.parseInt(altSettings.getProperty("CommanderCost", "30")); 
            RIFT_ENTER_COST_HERO             = Integer.parseInt(altSettings.getProperty("HeroCost", "33")); 
            RIFT_BOSS_ROOM_TIME_MUTIPLY      = Float.parseFloat(altSettings.getProperty("BossRoomTimeMultiply", "1.5"));
        }
        catch (Exception e)
        {
            _log.error(e);
            throw new Error("Failed to Load "+ALT_SETTINGS_FILE+" File.");
        }
    }
    
    
    
    //  *******************************************************************************************    
    public static final String  GM_ACCESS_FILE				= "./config/GMAccess.properties";
    //  *******************************************************************************************
    public static boolean 	ALT_PRIVILEGES_ADMIN;
    public static boolean 	EVERYBODY_HAS_ADMIN_RIGHTS;		// For test servers - everybody has admin rights
    public static int     	GM_ACCESSLEVEL;
    public static int     	GM_MIN;					// General GM Minimal AccessLevel
    public static int     	GM_ALTG_MIN_LEVEL;		// Minimum privileges level for a GM to do Alt+G
    public static int     	GM_ANNOUNCE;			// General GM AccessLevel to change announcements
    public static int     	GM_BAN;					// General GM AccessLevel can /ban /unban
    public static int     	GM_BAN_CHAT;			// General GM AccessLevel can /ban /unban for chat
    public static int     	GM_CREATE_ITEM;			// General GM AccessLevel can /create_item and /gmshop
    public static int       GM_FREE_SHOP;           // General GM AccessLevel can shop for free
    public static int     	GM_ENCHANT;				// General GM AccessLevel can enchant armor
    public static int     	GM_DELETE;				// General GM AccessLevel can /delete
    public static int     	GM_KICK;				// General GM AccessLevel can /kick /disconnect
    public static int     	GM_MENU;				// General GM AccessLevel for access to GMMenu
    public static int     	GM_GODMODE;				// General GM AccessLevel to use god mode command
    public static int     	GM_CHAR_EDIT;			// General GM AccessLevel with character edit rights
    public static int     	GM_CHAR_EDIT_OTHER;		// General GM AccessLevel with edit rights for other characters
    public static int     	GM_CHAR_VIEW;			// General GM AccessLevel with character view rights
    public static int     	GM_CHAR_VIEW_INFO;			// General GM AccessLevel with character view rights ALT+G
    public static int     	GM_CHAR_INVENTORY;			// General GM AccessLevel with character view inventory rights ALT+G
    public static int     	GM_CHAR_CLAN_VIEW;			// General GM AccessLevel with character view clan info rights ALT+G
    public static int     	GM_CHAR_VIEW_QUEST;			// General GM AccessLevel with character view quest rights ALT+G
    public static int     	GM_CHAR_VIEW_SKILL;			// General GM AccessLevel with character view skill rights ALT+G
    public static int     	GM_CHAR_VIEW_WAREHOUSE;			// General GM AccessLevel with character warehouse view rights ALT+G    
    public static int     	GM_NPC_EDIT;			// General GM AccessLevel with NPC edit rights
    public static int     	GM_NPC_VIEW;
    public static int     	GM_TELEPORT;			// General GM AccessLevel to teleport to any location
    public static int     	GM_TELEPORT_OTHER;		// General GM AccessLevel to teleport character to any location
    public static int     	GM_RESTART;				// General GM AccessLevel to restart server
    public static int     	GM_MONSTERRACE;			// General GM AccessLevel for MonsterRace
    public static int     	GM_RIDER;				// General GM AccessLevel to ride Wyvern
    public static int     	GM_ESCAPE;				// General GM AccessLevel to unstuck without 5min delay
    public static int     	GM_FIXED;				// General GM AccessLevel to resurect fixed after death
    public static int     	GM_CREATE_NODES;		// General GM AccessLevel to create Path Nodes
    public static int     	GM_DOOR;				// General GM AccessLevel to close/open Doors
    public static int     	GM_RES;					// General GM AccessLevel with Resurrection rights
    public static int     	GM_PEACEATTACK;			// General GM AccessLevel to attack in the peace zone   
    public static int     	GM_HEAL;				// General GM AccessLevel to heal
    public static int     	GM_IRC;					// General GM AccessLevel to IRC commands
    public static int     	GM_UNBLOCK;				// General GM AccessLevel to unblock IPs detected as hack IPs
    public static int 		GM_CACHE;				// General GM AccessLevel to use Cache commands				
    public static int 		GM_TALK_BLOCK;			// General GM AccessLevel to use test&st commands
    public static int 		GM_TEST;
    public static boolean 	GM_DISABLE_TRANSACTION;	// Disable transaction on AccessLevel
    public static int 		GM_TRANSACTION_MIN;
    public static int 		GM_TRANSACTION_MAX;
    public static int     	GM_CAN_GIVE_DAMAGE;		// Minimum level to allow a GM giving damage
    public static int     	GM_DONT_TAKE_EXPSP;		// Minimum level to don't give Exp/Sp in party
    public static int     	GM_DONT_TAKE_AGGRO;		// Minimum level to don't take aggro    
    public static boolean   GM_NAME_COLOR_ENABLED;	// GM name color
    public static boolean   GM_TITLE_COLOR_ENABLED;
    public static int       GM_NAME_COLOR;
    public static int       GM_TITLE_COLOR;
    public static int       ADMIN_NAME_COLOR;
    public static int       ADMIN_TITLE_COLOR;
    public static boolean 	SHOW_GM_LOGIN;			// GM Announce at login
    public static boolean 	HIDE_GM_STATUS;
    public static boolean 	GM_STARTUP_INVISIBLE;
    public static boolean 	GM_STARTUP_SILENCE;
    public static boolean 	GM_STARTUP_AUTO_LIST;
    public static String  	GM_ADMIN_MENU_STYLE;
    public static int 		STANDARD_RESPAWN_DELAY;	// Standard Respawn Delay
    public static boolean 	GM_HERO_AURA;			// Place an aura around the GM ?
    public static boolean 	GM_STARTUP_INVULNERABLE;	// Set the GM invulnerable at startup ?
    //  *******************************************************************************************
    public static void loadGmAccess()
    {
        _log.info("loading " + GM_ACCESS_FILE);
        try
        {
            Properties gmSettings   = new Properties();
            InputStream is          = new FileInputStream(new File(GM_ACCESS_FILE));  
            gmSettings.load(is);
            is.close();               
            ALT_PRIVILEGES_ADMIN = Boolean.parseBoolean(gmSettings.getProperty("AltPrivilegesAdmin", "False"));
            EVERYBODY_HAS_ADMIN_RIGHTS      = Boolean.parseBoolean(gmSettings.getProperty("EverybodyHasAdminRights", "false"));
            
            GM_ACCESSLEVEL  = Integer.parseInt(gmSettings.getProperty("GMAccessLevel", "100"));
            GM_MIN          = Integer.parseInt(gmSettings.getProperty("GMMinLevel", "100"));
            GM_ALTG_MIN_LEVEL = Integer.parseInt(gmSettings.getProperty("GMCanAltG", "100"));
            GM_ANNOUNCE     = Integer.parseInt(gmSettings.getProperty("GMCanAnnounce", "100"));
            GM_BAN          = Integer.parseInt(gmSettings.getProperty("GMCanBan", "100"));
            GM_BAN_CHAT     = Integer.parseInt(gmSettings.getProperty("GMCanBanChat", "100"));
            GM_CREATE_ITEM  = Integer.parseInt(gmSettings.getProperty("GMCanShop", "100"));
            GM_FREE_SHOP    = Integer.parseInt(gmSettings.getProperty("GMCanBuyFree", "100"));
            GM_DELETE       = Integer.parseInt(gmSettings.getProperty("GMCanDelete", "100"));
            GM_KICK         = Integer.parseInt(gmSettings.getProperty("GMCanKick", "100"));
            GM_MENU         = Integer.parseInt(gmSettings.getProperty("GMMenu", "100"));
            GM_GODMODE      = Integer.parseInt(gmSettings.getProperty("GMGodMode", "100"));
            GM_CHAR_EDIT    = Integer.parseInt(gmSettings.getProperty("GMCanEditChar", "100"));
            GM_CHAR_EDIT_OTHER    = Integer.parseInt(gmSettings.getProperty("GMCanEditCharOther", "100"));
            GM_CHAR_VIEW    = Integer.parseInt(gmSettings.getProperty("GMCanViewChar", "100"));
            GM_CHAR_VIEW_INFO    = Integer.parseInt(gmSettings.getProperty("GMViewCharacterInfo", "100"));
            GM_CHAR_INVENTORY    = Integer.parseInt(gmSettings.getProperty("GMViewItemList", "100"));
            GM_CHAR_CLAN_VIEW    = Integer.parseInt(gmSettings.getProperty("GMViewClanInfo", "100"));
            GM_CHAR_VIEW_QUEST    = Integer.parseInt(gmSettings.getProperty("GMViewQuestList", "100"));
            GM_CHAR_VIEW_SKILL    = Integer.parseInt(gmSettings.getProperty("GMViewSkillInfo", "100"));
            GM_CHAR_VIEW_WAREHOUSE    = Integer.parseInt(gmSettings.getProperty("GMViewWarehouseWithdrawList", "100"));
            GM_NPC_EDIT     = Integer.parseInt(gmSettings.getProperty("GMCanEditNPC", "100"));
            GM_NPC_VIEW     = Integer.parseInt(gmSettings.getProperty("GMCanViewNPC", "100"));
            GM_TELEPORT     = Integer.parseInt(gmSettings.getProperty("GMCanTeleport", "100"));
            GM_TELEPORT_OTHER     = Integer.parseInt(gmSettings.getProperty("GMCanTeleportOther", "100"));
            GM_RESTART      = Integer.parseInt(gmSettings.getProperty("GMCanRestart", "100"));
            GM_MONSTERRACE  = Integer.parseInt(gmSettings.getProperty("GMMonsterRace", "100"));
            GM_RIDER        = Integer.parseInt(gmSettings.getProperty("GMRider", "100"));
            GM_ESCAPE       = Integer.parseInt(gmSettings.getProperty("GMFastUnstuck", "100"));
            GM_FIXED        = Integer.parseInt(gmSettings.getProperty("GMResurectFixed", "100"));
            GM_CREATE_NODES = Integer.parseInt(gmSettings.getProperty("GMCreateNodes", "100"));
            GM_DOOR         = Integer.parseInt(gmSettings.getProperty("GMDoor", "100"));
            GM_RES          = Integer.parseInt(gmSettings.getProperty("GMRes", "100"));
            GM_PEACEATTACK  = Integer.parseInt(gmSettings.getProperty("GMPeaceAttack", "100"));
            GM_HEAL         = Integer.parseInt(gmSettings.getProperty("GMHeal", "100"));
            GM_IRC          = Integer.parseInt(gmSettings.getProperty("GMIRC", "100"));
            GM_ENCHANT      = Integer.parseInt(gmSettings.getProperty("GMEnchant", "100"));
            GM_UNBLOCK      = Integer.parseInt(gmSettings.getProperty("GMUnblock", "100"));
            GM_CACHE        = Integer.parseInt(gmSettings.getProperty("GMCache", "100"));
            GM_TALK_BLOCK   = Integer.parseInt(gmSettings.getProperty("GMTalkBlock", "100"));
            GM_TEST         = Integer.parseInt(gmSettings.getProperty("GMTest", "100"));
            GM_STARTUP_AUTO_LIST = Boolean.parseBoolean(gmSettings.getProperty("GMStartupAutoList", "True"));
            GM_ADMIN_MENU_STYLE = gmSettings.getProperty("GMAdminMenuStyle", "modern");
            GM_HERO_AURA    = Boolean.parseBoolean(gmSettings.getProperty("GMHeroAura", "True"));
            GM_STARTUP_INVULNERABLE = Boolean.parseBoolean(gmSettings.getProperty("GMStartupInvulnerable", "True"));
            STANDARD_RESPAWN_DELAY = Integer.parseInt(gmSettings.getProperty("StandardRespawnDelay", "0"));
            
            String gmTrans = gmSettings.getProperty("GMDisableTransaction", "False");
            
            if (!gmTrans.trim().equalsIgnoreCase("false"))
            {
                String[] params = gmTrans.trim().split(",");
                GM_DISABLE_TRANSACTION = true;
                GM_TRANSACTION_MIN = Integer.parseInt(params[0].trim());
                GM_TRANSACTION_MAX = Integer.parseInt(params[1].trim());
            }
            else
            {
                GM_DISABLE_TRANSACTION = false; 
            }
            GM_CAN_GIVE_DAMAGE = Integer.parseInt(gmSettings.getProperty("GMCanGiveDamage", "90"));
            GM_DONT_TAKE_AGGRO = Integer.parseInt(gmSettings.getProperty("GMDontTakeAggro", "90"));
            GM_DONT_TAKE_EXPSP = Integer.parseInt(gmSettings.getProperty("GMDontGiveExpSp", "90"));
            
            GM_NAME_COLOR_ENABLED  = Boolean.parseBoolean(gmSettings.getProperty("GMNameColorEnabled", "True"));
            GM_TITLE_COLOR_ENABLED  = Boolean.parseBoolean(gmSettings.getProperty("GMTitleColorEnabled", "True"));
            GM_NAME_COLOR          = Integer.decode("0x" + gmSettings.getProperty("GMNameColor", "00FF00"));
            GM_TITLE_COLOR         = Integer.decode("0x" + gmSettings.getProperty("GMTitleColor", "00FF00"));
            ADMIN_NAME_COLOR       = Integer.decode("0x" + gmSettings.getProperty("AdminNameColor", "00FF00"));
            ADMIN_TITLE_COLOR      = Integer.decode("0x" + gmSettings.getProperty("AdminTitleColor", "00FF00"));
            SHOW_GM_LOGIN 	       = Boolean.parseBoolean(gmSettings.getProperty("ShowGMLogin", "false"));
            HIDE_GM_STATUS	       = Boolean.parseBoolean(gmSettings.getProperty("HideGMStatus", "false")); 
            GM_STARTUP_INVISIBLE   = Boolean.parseBoolean(gmSettings.getProperty("GMStartupInvisible", "True"));
            GM_STARTUP_SILENCE     = Boolean.parseBoolean(gmSettings.getProperty("GMStartupSilence", "True"));
            
        }
        catch (Exception e)
        {
            _log.error(e);
            throw new Error("Failed to Load "+GM_ACCESS_FILE+" File.");
        }
    }
    
    
    
    //  *******************************************************************************************    
    public static final String  TELNET_FILE					= "./config/telnet.properties";
    //  *******************************************************************************************
    public static boolean IS_TELNET_ENABLED;	// Is telnet enabled ?
    //  *******************************************************************************************
    public static void loadTelnetConfig()
    {
        _log.info("loading " + TELNET_FILE);
        try
        {
            Properties telnetSettings   = new Properties();
            InputStream is              = new FileInputStream(new File(TELNET_FILE));  
            telnetSettings.load(is);
            is.close();
            
            IS_TELNET_ENABLED   = Boolean.parseBoolean(telnetSettings.getProperty("EnableTelnet", "false"));
        }
        catch (Exception e)
        {
            _log.error(e);
            throw new Error("Failed to Load "+TELNET_FILE+" File.");
        }
    }
    
    //  *******************************************************************************************    
    public static final String  SIEGE_CONFIGURATION_FILE	= "./config/siege.properties";
    //  *******************************************************************************************
    
    public static int SIEGE_MAX_ATTACKER;
    public static int SIEGE_MAX_DEFENDER;
    public static int SIEGE_RESPAWN_DELAY_ATTACKER;
    public static int SIEGE_RESPAWN_DELAY_DEFENDER;

    public static int SIEGE_CT_LOSS_PENALTY;
    public static int SIEGE_FLAG_MAX_COUNT;
    public static int SIEGE_CLAN_MIN_LEVEL;
    public static int SIEGE_LENGTH_MINUTES;
    
    public static void loadSiegeConfig()
    {
        _log.info("loading " + SIEGE_CONFIGURATION_FILE);
        try
        {
            Properties siegeSettings = new Properties();
            InputStream is           = new FileInputStream(SIEGE_CONFIGURATION_FILE);
            siegeSettings.load(is);
            is.close();

            SIEGE_MAX_ATTACKER = Integer.parseInt(siegeSettings.getProperty("AttackerMaxClans", "500"));
            SIEGE_MAX_DEFENDER = Integer.parseInt(siegeSettings.getProperty("DefenderMaxClans", "500"));
            SIEGE_RESPAWN_DELAY_ATTACKER = Integer.parseInt(siegeSettings.getProperty("AttackerRespawn", "30000"));
            SIEGE_RESPAWN_DELAY_DEFENDER = Integer.parseInt(siegeSettings.getProperty("DefenderRespawn", "30000"));

            SIEGE_CT_LOSS_PENALTY = Integer.parseInt(siegeSettings.getProperty("CTLossPenalty", "20000"));
            SIEGE_FLAG_MAX_COUNT  = Integer.parseInt(siegeSettings.getProperty("MaxFlags", "1"));
            SIEGE_CLAN_MIN_LEVEL  = Integer.parseInt(siegeSettings.getProperty("SiegeClanMinLevel", "4"));
            SIEGE_LENGTH_MINUTES  = Integer.parseInt(siegeSettings.getProperty("SiegeLength", "120"));
        }
        catch (Exception e)
        {
            _log.error(e);
            throw new Error("Failed to Load "+SIEGE_CONFIGURATION_FILE+" File.");
        }
    }
    
    //  *******************************************************************************************
    public static final String  HEXID_FILE					= "./config/hexid.txt";
    //  *******************************************************************************************
    public static byte[] HEX_ID;	// Hexadecimal ID of the game server
    /** Server ID used with the HexID */
    public static int SERVER_ID;
    //  *******************************************************************************************
    public static void loadHexId()
    {
        _log.info("loading " + HEXID_FILE);
        try
        {
            Properties Settings   = new Properties();
            InputStream is          = new FileInputStream(HEXID_FILE);  
            Settings.load(is);
            is.close();  
            SERVER_ID = Integer.parseInt(Settings.getProperty("ServerID"));
            HEX_ID = new BigInteger(Settings.getProperty("HexID"), 16).toByteArray();
        }
        catch (Exception e)
        {
            _log.warn("Could not load HexID file ("+HEXID_FILE+"). Hopefully login will give us one.");
        }
    }
    
    
    
    //  *******************************************************************************************    
    public static final String  COMMAND_PRIVILEGES_FILE     = "./config/command-privileges.properties";
    //  *******************************************************************************************
    public static final Map<String, Integer> GM_COMMAND_PRIVILEGES = new FastMap<String, Integer>();
    //  *******************************************************************************************
    public static void loadPrivilegesConfig()
    {
        _log.info("loading " + COMMAND_PRIVILEGES_FILE);
        try
        {
            Properties CommandPrivileges  = new Properties();
            InputStream is            = new FileInputStream(new File(COMMAND_PRIVILEGES_FILE));  
            CommandPrivileges.load(is);
            is.close();
            
            for(Map.Entry<Object, Object> _command : CommandPrivileges.entrySet())
            {
            	String command = String.valueOf(_command.getKey());
            	String commandLevel = String.valueOf(_command.getValue()).trim();
            	
            	int accessLevel = GM_ACCESSLEVEL;
            	
            	try
            	{
            		accessLevel = Integer.parseInt(commandLevel);
            	} catch (Exception e)
            	{
            		_log.warn("Failed to parse command \""+command+"\"!",e );
            	}
            	
            	GM_COMMAND_PRIVILEGES.put(command, accessLevel);
            }
        }
        catch (Exception e)
        {
            _log.error(e);
            throw new Error("Failed to Load "+COMMAND_PRIVILEGES_FILE+" File.");
        }
    }
    
    
    //  *******************************************************************************************    
    public static final String  SEVENSIGNS_FILE             = "./config/sevensigns.properties";
    //  *******************************************************************************************
    public static boolean 	ALT_GAME_REQUIRE_CASTLE_DAWN;	// Alternative gaming - player must be in a castle-owning clan or ally to sign up for Dawn.
    public static boolean 	ALT_GAME_REQUIRE_CLAN_CASTLE;	// Alternative gaming - allow clan-based castle ownage check rather than ally-based.
    public static int 		ALT_FESTIVAL_MIN_PLAYER;		// Minimum number of player to participate in SevenSigns Festival
    public static int 		ALT_MAXIMUM_PLAYER_CONTRIB;		// Maximum of player contrib during Festival    
    public static long 		ALT_FESTIVAL_MANAGER_START;		// Festival Manager start time.
    public static long 		ALT_FESTIVAL_LENGTH;			// Festival Length
    public static long 		ALT_FESTIVAL_CYCLE_LENGTH;		// Festival Cycle Length
    public static long 		ALT_FESTIVAL_FIRST_SPAWN;		// Festival First Spawn
    public static long 		ALT_FESTIVAL_FIRST_SWARM;		// Festival First Swarm
    public static long 		ALT_FESTIVAL_SECOND_SPAWN;		// Festival Second Spawn
    public static long 		ALT_FESTIVAL_SECOND_SWARM;		// Festival Second Swarm
    public static long 		ALT_FESTIVAL_CHEST_SPAWN;		// Festival Chest Spawn
    //  *******************************************************************************************
    public static void loadSevenSignsConfig()
    {
        _log.info("loading " + SEVENSIGNS_FILE);
        try
        {
            Properties SevenSettings  = new Properties();
            InputStream is            = new FileInputStream(new File(SEVENSIGNS_FILE));  
            SevenSettings.load(is);
            is.close();
            
            ALT_GAME_REQUIRE_CASTLE_DAWN    = Boolean.parseBoolean(SevenSettings.getProperty("AltRequireCastleForDawn", "False"));
            ALT_GAME_REQUIRE_CLAN_CASTLE    = Boolean.parseBoolean(SevenSettings.getProperty("AltRequireClanCastle", "False"));
            ALT_FESTIVAL_MIN_PLAYER         = Integer.parseInt(SevenSettings.getProperty("AltFestivalMinPlayer", "5"));
            ALT_MAXIMUM_PLAYER_CONTRIB      = Integer.parseInt(SevenSettings.getProperty("AltMaxPlayerContrib", "1000000"));
            ALT_FESTIVAL_MANAGER_START      = Long.parseLong(SevenSettings.getProperty("AltFestivalManagerStart", "120000"));
            ALT_FESTIVAL_LENGTH             = Long.parseLong(SevenSettings.getProperty("AltFestivalLength", "1080000"));
            ALT_FESTIVAL_CYCLE_LENGTH       = Long.parseLong(SevenSettings.getProperty("AltFestivalCycleLength", "2280000"));
            ALT_FESTIVAL_FIRST_SPAWN        = Long.parseLong(SevenSettings.getProperty("AltFestivalFirstSpawn", "120000"));
            ALT_FESTIVAL_FIRST_SWARM        = Long.parseLong(SevenSettings.getProperty("AltFestivalFirstSwarm", "300000"));
            ALT_FESTIVAL_SECOND_SPAWN       = Long.parseLong(SevenSettings.getProperty("AltFestivalSecondSpawn", "540000"));
            ALT_FESTIVAL_SECOND_SWARM       = Long.parseLong(SevenSettings.getProperty("AltFestivalSecondSwarm", "720000"));
            ALT_FESTIVAL_CHEST_SPAWN        = Long.parseLong(SevenSettings.getProperty("AltFestivalChestSpawn", "900000"));
        }
        catch (Exception e)
        {
            _log.error(e);
            throw new Error("Failed to Load "+SEVENSIGNS_FILE+" File.");
        }
    }
    
    
    
    //  *******************************************************************************************    
    public static final String  CLANHALL_CONFIG_FILE        = "./config/clanhall.properties";
    //  *******************************************************************************************
    /** Clan Hall function related configs*/
    public static long 	CH_TELE_FEE_RATIO;
    public static int 	CH_TELE1_FEE;
    public static int 	CH_TELE2_FEE;
    public static long 	CH_ITEM_FEE_RATIO;
    public static int 	CH_ITEM1_FEE;
    public static int 	CH_ITEM2_FEE;
    public static int 	CH_ITEM3_FEE;
    public static long 	CH_MPREG_FEE_RATIO;
    public static int 	CH_MPREG1_FEE;
    public static int 	CH_MPREG2_FEE;
    public static int 	CH_MPREG3_FEE;
    public static int 	CH_MPREG4_FEE;
    public static int 	CH_MPREG5_FEE;    
    public static long 	CH_HPREG_FEE_RATIO;
    public static int 	CH_HPREG1_FEE;
    public static int 	CH_HPREG2_FEE;
    public static int 	CH_HPREG3_FEE;
    public static int 	CH_HPREG4_FEE;
    public static int 	CH_HPREG5_FEE;
    public static int 	CH_HPREG6_FEE;
    public static int 	CH_HPREG7_FEE;
    public static int 	CH_HPREG8_FEE;
    public static int 	CH_HPREG9_FEE;
    public static int 	CH_HPREG10_FEE;
    public static int 	CH_HPREG11_FEE;
    public static int 	CH_HPREG12_FEE;
    public static int 	CH_HPREG13_FEE;
    public static long 	CH_EXPREG_FEE_RATIO;
    public static int 	CH_EXPREG1_FEE;
    public static int 	CH_EXPREG2_FEE;
    public static int 	CH_EXPREG3_FEE;
    public static int 	CH_EXPREG4_FEE;
    public static int 	CH_EXPREG5_FEE;
    public static int 	CH_EXPREG6_FEE;
    public static int 	CH_EXPREG7_FEE;
    public static long 	CH_SUPPORT_FEE_RATIO;
    public static int 	CH_SUPPORT1_FEE;
    public static int 	CH_SUPPORT2_FEE;
    public static int 	CH_SUPPORT3_FEE;
    public static int 	CH_SUPPORT4_FEE;
    public static int 	CH_SUPPORT5_FEE;
    public static int 	CH_SUPPORT6_FEE;
    public static int 	CH_SUPPORT7_FEE;
    public static int 	CH_SUPPORT8_FEE;
    public static long 	CH_CURTAIN_FEE_RATIO;
    public static int 	CH_CURTAIN1_FEE;
    public static int 	CH_CURTAIN2_FEE;
    public static long 	CH_FRONT_FEE_RATIO;
    public static int 	CH_FRONT1_FEE;
    public static int 	CH_FRONT2_FEE;    
    //  *******************************************************************************************
    public static void loadClanHallConfig()
    {
        _log.info("loading " + CLANHALL_CONFIG_FILE);
        try
        {
            Properties clanhallSettings  = new Properties();
            InputStream is          = new FileInputStream(new File(CLANHALL_CONFIG_FILE));
            clanhallSettings.load(is);
            is.close();
             
            CH_TELE_FEE_RATIO                                   = Long.parseLong(clanhallSettings.getProperty("ClanHallTeleportFunctionFeeRation", "86400000"));
            CH_TELE1_FEE                                        = Integer.parseInt(clanhallSettings.getProperty("ClanHallTeleportFunctionFeeLvl1", "86400000"));
            CH_TELE2_FEE                                        = Integer.parseInt(clanhallSettings.getProperty("ClanHallTeleportFunctionFeeLvl2", "86400000"));
            CH_SUPPORT_FEE_RATIO                                = Long.parseLong(clanhallSettings.getProperty("ClanHallSupportFunctionFeeRation", "86400000"));
            CH_SUPPORT1_FEE                                     = Integer.parseInt(clanhallSettings.getProperty("ClanHallSupportFeeLvl1", "86400000"));
            CH_SUPPORT2_FEE                                     = Integer.parseInt(clanhallSettings.getProperty("ClanHallSupportFeeLvl2", "86400000"));
            CH_SUPPORT3_FEE                                     = Integer.parseInt(clanhallSettings.getProperty("ClanHallSupportFeeLvl3", "86400000"));
            CH_SUPPORT4_FEE                                     = Integer.parseInt(clanhallSettings.getProperty("ClanHallSupportFeeLvl4", "86400000"));
            CH_SUPPORT5_FEE                                     = Integer.parseInt(clanhallSettings.getProperty("ClanHallSupportFeeLvl5", "86400000"));
            CH_SUPPORT6_FEE                                     = Integer.parseInt(clanhallSettings.getProperty("ClanHallSupportFeeLvl6", "86400000"));
            CH_SUPPORT7_FEE                                     = Integer.parseInt(clanhallSettings.getProperty("ClanHallSupportFeeLvl7", "86400000"));
            CH_SUPPORT8_FEE                                     = Integer.parseInt(clanhallSettings.getProperty("ClanHallSupportFeeLvl8", "86400000"));
            CH_MPREG_FEE_RATIO                                  = Long.parseLong(clanhallSettings.getProperty("ClanHallMpRegenerationFunctionFeeRation", "86400000"));
            CH_MPREG1_FEE                                       = Integer.parseInt(clanhallSettings.getProperty("ClanHallMpRegenerationFeeLvl1", "86400000"));
            CH_MPREG2_FEE                                       = Integer.parseInt(clanhallSettings.getProperty("ClanHallMpRegenerationFeeLvl2", "86400000"));
            CH_MPREG3_FEE                                       = Integer.parseInt(clanhallSettings.getProperty("ClanHallMpRegenerationFeeLvl3", "86400000"));
            CH_MPREG4_FEE                                       = Integer.parseInt(clanhallSettings.getProperty("ClanHallMpRegenerationFeeLvl4", "86400000"));
            CH_MPREG5_FEE                                       = Integer.parseInt(clanhallSettings.getProperty("ClanHallMpRegenerationFeeLvl5", "86400000"));
            CH_HPREG_FEE_RATIO                                  = Long.parseLong(clanhallSettings.getProperty("ClanHallHpRegenerationFunctionFeeRation", "86400000"));
            CH_HPREG1_FEE                                       = Integer.parseInt(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl1", "86400000"));
            CH_HPREG2_FEE                                       = Integer.parseInt(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl2", "86400000"));
            CH_HPREG3_FEE                                       = Integer.parseInt(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl3", "86400000"));
            CH_HPREG4_FEE                                       = Integer.parseInt(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl4", "86400000"));
            CH_HPREG5_FEE                                       = Integer.parseInt(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl5", "86400000"));
            CH_HPREG6_FEE                                       = Integer.parseInt(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl6", "86400000"));
            CH_HPREG7_FEE                                       = Integer.parseInt(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl7", "86400000"));
            CH_HPREG8_FEE                                       = Integer.parseInt(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl8", "86400000"));
            CH_HPREG9_FEE                                       = Integer.parseInt(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl9", "86400000"));
            CH_HPREG10_FEE                                      = Integer.parseInt(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl10", "86400000"));
            CH_HPREG11_FEE                                      = Integer.parseInt(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl11", "86400000"));
            CH_HPREG12_FEE                                      = Integer.parseInt(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl12", "86400000"));
            CH_HPREG13_FEE                                      = Integer.parseInt(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl13", "86400000"));
            CH_EXPREG_FEE_RATIO                                 = Long.parseLong(clanhallSettings.getProperty("ClanHallExpRegenerationFunctionFeeRation", "86400000"));
            CH_EXPREG1_FEE                                      = Integer.parseInt(clanhallSettings.getProperty("ClanHallExpRegenerationFeeLvl1", "86400000"));
            CH_EXPREG2_FEE                                      = Integer.parseInt(clanhallSettings.getProperty("ClanHallExpRegenerationFeeLvl2", "86400000"));
            CH_EXPREG3_FEE                                      = Integer.parseInt(clanhallSettings.getProperty("ClanHallExpRegenerationFeeLvl3", "86400000"));
            CH_EXPREG4_FEE                                      = Integer.parseInt(clanhallSettings.getProperty("ClanHallExpRegenerationFeeLvl4", "86400000"));
            CH_EXPREG5_FEE                                      = Integer.parseInt(clanhallSettings.getProperty("ClanHallExpRegenerationFeeLvl5", "86400000"));
            CH_EXPREG6_FEE                                      = Integer.parseInt(clanhallSettings.getProperty("ClanHallExpRegenerationFeeLvl6", "86400000"));
            CH_EXPREG7_FEE                                      = Integer.parseInt(clanhallSettings.getProperty("ClanHallExpRegenerationFeeLvl7", "86400000"));
            CH_ITEM_FEE_RATIO                                   = Long.parseLong(clanhallSettings.getProperty("ClanHallItemCreationFunctionFeeRation", "86400000"));
            CH_ITEM1_FEE                                        = Integer.parseInt(clanhallSettings.getProperty("ClanHallItemCreationFunctionFeeLvl1", "86400000"));
            CH_ITEM2_FEE                                        = Integer.parseInt(clanhallSettings.getProperty("ClanHallItemCreationFunctionFeeLvl2", "86400000"));
            CH_ITEM3_FEE                                        = Integer.parseInt(clanhallSettings.getProperty("ClanHallItemCreationFunctionFeeLvl3", "86400000"));
            CH_CURTAIN_FEE_RATIO                                = Long.parseLong(clanhallSettings.getProperty("ClanHallCurtainFunctionFeeRation", "86400000"));
            CH_CURTAIN1_FEE                                     = Integer.parseInt(clanhallSettings.getProperty("ClanHallCurtainFunctionFeeLvl1", "86400000"));
            CH_CURTAIN2_FEE                                     = Integer.parseInt(clanhallSettings.getProperty("ClanHallCurtainFunctionFeeLvl2", "86400000"));
            CH_FRONT_FEE_RATIO                                  = Long.parseLong(clanhallSettings.getProperty("ClanHallFrontPlatformFunctionFeeRation", "86400000"));
            CH_FRONT1_FEE                                       = Integer.parseInt(clanhallSettings.getProperty("ClanHallFrontPlatformFunctionFeeLvl1", "86400000"));
            CH_FRONT2_FEE                                       = Integer.parseInt(clanhallSettings.getProperty("ClanHallFrontPlatformFunctionFeeLvl2", "86400000"));
        }
        catch (Exception e)
        {
            _log.error(e.getMessage(),e);
            throw new Error("Failed to Load "+CLANHALL_CONFIG_FILE+" File.");
        }
    }
    
    
    
    //  *******************************************************************************************
    
    
    //  *******************************************************************************************    
    public static final String  FUN_ENGINES_FILE	= "./config/fun_engines.properties";
    //  *******************************************************************************************
    public static String TVT_EVEN_TEAMS;
    public static String CTF_EVEN_TEAMS;
    public static boolean CTF_ALLOW_INTERFERENCE;
    public static boolean CTF_ALLOW_POTIONS;
    public static boolean CTF_ALLOW_SUMMON;
    public static boolean CTF_ON_START_REMOVE_ALL_EFFECTS;
    public static boolean CTF_ON_START_UNSUMMON_PET;
    public static boolean TVT_ALLOW_INTERFERENCE;
    public static boolean TVT_ALLOW_POTIONS;
    public static boolean TVT_ALLOW_SUMMON;
    public static boolean TVT_ON_START_REMOVE_ALL_EFFECTS;
    public static boolean TVT_ON_START_UNSUMMON_PET;
    public static boolean TVT_REVIVE_RECOVERY;
    public static boolean TVT_ANNOUNCE_TEAM_STATS;
    public static boolean DM_ALLOW_INTERFERENCE;
    public static boolean DM_ALLOW_POTIONS;
    public static boolean DM_ALLOW_SUMMON;
    public static boolean DM_ON_START_REMOVE_ALL_EFFECTS;
    public static boolean DM_ON_START_UNSUMMON_PET;
    //  *******************************************************************************************    
    //  *******************************************************************************************
    public static void loadFunEnginesConfig()
    {
        _log.info("loading " + FUN_ENGINES_FILE);
        try
        {
           Properties funEnginesSettings = new Properties();
           InputStream is = new FileInputStream(new File(FUN_ENGINES_FILE));  
           funEnginesSettings.load(is);
           is.close();
           
           CTF_EVEN_TEAMS = funEnginesSettings.getProperty("CTFEvenTeams", "BALANCE");
           CTF_ALLOW_INTERFERENCE = Boolean.parseBoolean(funEnginesSettings.getProperty("CTFAllowInterference", "false"));
           CTF_ALLOW_POTIONS = Boolean.parseBoolean(funEnginesSettings.getProperty("CTFAllowPotions", "false"));
           CTF_ALLOW_SUMMON = Boolean.parseBoolean(funEnginesSettings.getProperty("CTFAllowSummon", "false"));
           CTF_ON_START_REMOVE_ALL_EFFECTS = Boolean.parseBoolean(funEnginesSettings.getProperty("CTFOnStartRemoveAllEffects", "true"));
           CTF_ON_START_UNSUMMON_PET = Boolean.parseBoolean(funEnginesSettings.getProperty("CTFOnStartUnsummonPet", "true"));
           
           TVT_EVEN_TEAMS = funEnginesSettings.getProperty("TvTEvenTeams", "BALANCE");
           TVT_ALLOW_INTERFERENCE = Boolean.parseBoolean(funEnginesSettings.getProperty("TvTAllowInterference", "false"));
           TVT_ALLOW_POTIONS = Boolean.parseBoolean(funEnginesSettings.getProperty("TvTAllowPotions", "false"));
           TVT_ALLOW_SUMMON = Boolean.parseBoolean(funEnginesSettings.getProperty("TvTAllowSummon", "false"));
           TVT_ON_START_REMOVE_ALL_EFFECTS = Boolean.parseBoolean(funEnginesSettings.getProperty("TvTOnStartRemoveAllEffects", "true"));
           TVT_ON_START_UNSUMMON_PET = Boolean.parseBoolean(funEnginesSettings.getProperty("TvTOnStartUnsummonPet", "true"));
           TVT_REVIVE_RECOVERY = Boolean.parseBoolean(funEnginesSettings.getProperty("TvTReviveRecovery", "false"));
           TVT_ANNOUNCE_TEAM_STATS = Boolean.parseBoolean(funEnginesSettings.getProperty("TvtAnnounceTeamStats", "false"));
        
           DM_ALLOW_INTERFERENCE = Boolean.parseBoolean(funEnginesSettings.getProperty("DMAllowInterference", "false"));
           DM_ALLOW_POTIONS = Boolean.parseBoolean(funEnginesSettings.getProperty("DMAllowPotions", "false"));
           DM_ALLOW_SUMMON = Boolean.parseBoolean(funEnginesSettings.getProperty("DMAllowSummon", "false"));
           DM_ON_START_REMOVE_ALL_EFFECTS = Boolean.parseBoolean(funEnginesSettings.getProperty("DMOnStartRemoveAllEffects", "true"));
           DM_ON_START_UNSUMMON_PET = Boolean.parseBoolean(funEnginesSettings.getProperty("DMOnStartUnsummonPet", "true"));
        }
        catch (Exception e)
        {
            _log.error(e.getMessage(),e);
            throw new Error("Failed to Load " + FUN_ENGINES_FILE + " File.");
        }
    }

    //  *******************************************************************************************
    public static final String  IRC_FILE             	= "./config/irc.properties";
    //  *******************************************************************************************
    public static boolean	IRC_ENABLED;
    public static boolean	IRC_LOG_CHAT;
    public static boolean 	IRC_SSL;
    public static String	IRC_SERVER;
    public static int 		IRC_PORT;
    public static String	IRC_PASS;
    public static String	IRC_NICK;
    public static String	IRC_USER;
    public static String	IRC_NAME;
    public static boolean 	IRC_NICKSERV;
    public static String	IRC_NICKSERV_NAME;
    public static String	IRC_NICKSERV_COMMAND;
    public static String	IRC_LOGIN_COMMAND;
    public static String	IRC_CHANNEL;
    public static String	IRC_FROM_GAME_TYPE;
    public static String	IRC_TO_GAME_TYPE;
    public static String	IRC_TO_GAME_SPECIAL_CHAR;
    public static String	IRC_TO_GAME_DISPLAY;
    public static boolean	IRC_ANNOUNCE;
    //  *******************************************************************************************
    public static void loadIrcConfig()
    {
        _log.info("loading " + IRC_FILE);
        try
        {
            Properties ircSettings = new Properties();
            InputStream is               = new FileInputStream(new File(IRC_FILE));
            ircSettings.load(is);
            is.close();

            IRC_ENABLED                           				= Boolean.parseBoolean(ircSettings.getProperty("Enable", "false"));                
            IRC_LOG_CHAT                          				= Boolean.parseBoolean(ircSettings.getProperty("LogChat", "false"));
            IRC_SSL                                    			= Boolean.parseBoolean(ircSettings.getProperty("SSL", "false"));
            IRC_SERVER											= ircSettings.getProperty("Server", "localhost");
            IRC_PORT                                       		= Integer.parseInt(ircSettings.getProperty("Port", "6667"));
            IRC_PASS											= ircSettings.getProperty("Pass", "localhost");
            IRC_NICK											= ircSettings.getProperty("Nick", "l2jfbot");
            IRC_USER											= ircSettings.getProperty("User", "l2jfree");
            IRC_NAME											= ircSettings.getProperty("Name", "l2jfree");
            IRC_NICKSERV                               			= Boolean.parseBoolean(ircSettings.getProperty("NickServ", "false"));
            IRC_NICKSERV_NAME									= ircSettings.getProperty("NickservName", "nickserv");
            IRC_NICKSERV_COMMAND								= ircSettings.getProperty("NickservCommand", "");
            IRC_LOGIN_COMMAND									= ircSettings.getProperty("LoginCommand", "");
            IRC_CHANNEL											= ircSettings.getProperty("Channel", "#mychan");
            IRC_ANNOUNCE                           				= Boolean.parseBoolean(ircSettings.getProperty("IrcAnnounces", "false"));
            IRC_FROM_GAME_TYPE									= ircSettings.getProperty("GameToIrcType", "off");            
            IRC_TO_GAME_TYPE									= ircSettings.getProperty("IrcToGameType", "off");
            IRC_TO_GAME_SPECIAL_CHAR							= ircSettings.getProperty("IrcToGameSpecialChar", "#");
            IRC_TO_GAME_DISPLAY									= ircSettings.getProperty("IrcToGameDisplay", "tade");
        }
        catch (Exception e)
        {
            _log.error(e.getMessage(),e);
            throw new Error("Failed to Load "+IRC_FILE+" File.");
        }
    }
    //  *******************************************************************************************
    public static final String  BOSS_FILE              = "./config/boss.properties";
    //  *******************************************************************************************
    public static int 		FWA_INTERVALOFANTHARAS;
    public static int 		FWA_APPTIMEOFANTHARAS;
    public static int 		FWA_ACTIVITYTIMEOFANTHARAS;
    public static boolean 	FWA_OLDANTHARAS;
    public static int 		FWA_LIMITOFWEAK;
    public static int 		FWA_LIMITOFNORMAL;
    public static int 		FWA_INTERVALOFBEHEMOTHONWEAK;
    public static int 		FWA_INTERVALOFBEHEMOTHONNORMAL;
    public static int 		FWA_INTERVALOFBEHEMOTHONSTRONG;
    public static int 		FWA_INTERVALOFBOMBERONWEAK;
    public static int 		FWA_INTERVALOFBOMBERONNORMAL;
    public static int 		FWA_INTERVALOFBOMBERONSTRONG;
    public static boolean 	FWA_MOVEATRANDOM;
    public static int 		FWB_INTERVALOFBAIUM;
    public static int 		FWB_ACTIVITYTIMEOFBAIUM;
    public static boolean 	FWB_MOVEATRANDOM;
    public static int 		FWV_INTERVALOFVALAKAS;
    public static int 		FWV_APPTIMEOFVALAKAS;
    public static int 		FWV_ACTIVITYTIMEOFVALAKAS;
    public static int 		FWV_CAPACITYOFLAIR;
    public static boolean 	FWV_MOVEATRANDOM;
    public static boolean 	FWS_ENABLESINGLEPLAYER;
    public static int 		FWS_INTERVALOFSAILRENSPAWN;
    public static int 		FWS_INTERVALOFNEXTMONSTER;
    public static int 		FWS_ACTIVITYTIMEOFMOBS;    
    
    //  *******************************************************************************************
    public static void loadBossConfig()
    {
        _log.info("loading " + BOSS_FILE);
        try
        {
            Properties bossSettings = new Properties();
            InputStream is               = new FileInputStream(new File(BOSS_FILE));
            bossSettings.load(is);
            is.close();

            FWA_INTERVALOFANTHARAS = Integer.parseInt(bossSettings.getProperty("IntervalOfAntharas", "1440"));
            if(FWA_INTERVALOFANTHARAS < 5 || FWA_INTERVALOFANTHARAS > 1440) FWA_INTERVALOFANTHARAS = 1440;
            FWA_INTERVALOFANTHARAS = FWA_INTERVALOFANTHARAS * 60000;
            FWA_APPTIMEOFANTHARAS = Integer.parseInt(bossSettings.getProperty("AppTimeOfAntharas", "10"));
            if(FWA_APPTIMEOFANTHARAS < 5 || FWA_APPTIMEOFANTHARAS > 60) FWA_APPTIMEOFANTHARAS = 10;
            FWA_APPTIMEOFANTHARAS = FWA_APPTIMEOFANTHARAS * 60000;
            FWA_ACTIVITYTIMEOFANTHARAS = Integer.parseInt(bossSettings.getProperty("ActivityTimeOfAntharas", "120"));
            if(FWA_ACTIVITYTIMEOFANTHARAS < 120 || FWA_ACTIVITYTIMEOFANTHARAS > 720) FWA_ACTIVITYTIMEOFANTHARAS = 120;
            FWA_ACTIVITYTIMEOFANTHARAS = FWA_ACTIVITYTIMEOFANTHARAS * 60000;
            FWA_OLDANTHARAS = Boolean.parseBoolean(bossSettings.getProperty("OldAntharas", "False"));
            FWA_LIMITOFWEAK = Integer.parseInt(bossSettings.getProperty("LimitOfWeak", "299"));
            FWA_LIMITOFNORMAL = Integer.parseInt(bossSettings.getProperty("LimitOfNormal", "399"));
            FWA_INTERVALOFBEHEMOTHONWEAK = Integer.parseInt(bossSettings.getProperty("IntervalOfBehemothOnWeak", "8"));
            if(FWA_INTERVALOFBEHEMOTHONWEAK < 1 || FWA_INTERVALOFBEHEMOTHONWEAK > 10) FWA_INTERVALOFBEHEMOTHONWEAK = 8;
            FWA_INTERVALOFBEHEMOTHONWEAK = FWA_INTERVALOFBEHEMOTHONWEAK * 60000;
            FWA_INTERVALOFBEHEMOTHONNORMAL = Integer.parseInt(bossSettings.getProperty("IntervalOfBehemothOnNormal", "5"));
            if(FWA_INTERVALOFBEHEMOTHONNORMAL < 1 || FWA_INTERVALOFBEHEMOTHONNORMAL > 10) FWA_INTERVALOFBEHEMOTHONNORMAL = 5;
            FWA_INTERVALOFBEHEMOTHONNORMAL = FWA_INTERVALOFBEHEMOTHONNORMAL * 60000;
            FWA_INTERVALOFBEHEMOTHONSTRONG = Integer.parseInt(bossSettings.getProperty("IntervalOfBehemothOnStrong", "3"));
            if(FWA_INTERVALOFBEHEMOTHONSTRONG < 1 || FWA_INTERVALOFBEHEMOTHONSTRONG > 10) FWA_INTERVALOFBEHEMOTHONSTRONG = 3;
            FWA_INTERVALOFBEHEMOTHONSTRONG = FWA_INTERVALOFBEHEMOTHONSTRONG * 60000;
            FWA_INTERVALOFBOMBERONWEAK = Integer.parseInt(bossSettings.getProperty("IntervalOfBomberOnWeak", "6"));
            if(FWA_INTERVALOFBOMBERONWEAK < 1 || FWA_INTERVALOFBOMBERONWEAK > 10) FWA_INTERVALOFBOMBERONWEAK = 6;
            FWA_INTERVALOFBOMBERONWEAK = FWA_INTERVALOFBOMBERONWEAK * 60000;
            FWA_INTERVALOFBOMBERONNORMAL = Integer.parseInt(bossSettings.getProperty("IntervalOfBomberOnNormal", "4"));
            if(FWA_INTERVALOFBOMBERONNORMAL < 1 || FWA_INTERVALOFBOMBERONNORMAL > 10) FWA_INTERVALOFBOMBERONNORMAL = 4;
            FWA_INTERVALOFBOMBERONNORMAL = FWA_INTERVALOFBOMBERONNORMAL * 60000;
            FWA_INTERVALOFBOMBERONSTRONG = Integer.parseInt(bossSettings.getProperty("IntervalOfBomberOnStrong", "3"));
            if(FWA_INTERVALOFBOMBERONSTRONG < 1 || FWA_INTERVALOFBOMBERONSTRONG > 10) FWA_INTERVALOFBOMBERONSTRONG = 3;
            FWA_INTERVALOFBOMBERONSTRONG = FWA_INTERVALOFBOMBERONSTRONG * 60000;
            FWA_MOVEATRANDOM = Boolean.parseBoolean(bossSettings.getProperty("MoveAtRandom", "True"));
            FWB_INTERVALOFBAIUM = Integer.parseInt(bossSettings.getProperty("IntervalOfBaium", "1440"));
            if(FWB_INTERVALOFBAIUM < 5 || FWB_INTERVALOFBAIUM > 1440) FWB_INTERVALOFBAIUM = 1440;
            FWB_INTERVALOFBAIUM = FWB_INTERVALOFBAIUM * 60000;
            FWB_ACTIVITYTIMEOFBAIUM = Integer.parseInt(bossSettings.getProperty("ActivityTimeOfBaium", "120"));
            if(FWB_ACTIVITYTIMEOFBAIUM < 120 || FWB_ACTIVITYTIMEOFBAIUM > 720) FWB_ACTIVITYTIMEOFBAIUM = 120;
            FWB_ACTIVITYTIMEOFBAIUM = FWB_ACTIVITYTIMEOFBAIUM * 60000;
            FWB_MOVEATRANDOM = Boolean.parseBoolean(bossSettings.getProperty("MoveAtRandom", "True"));
            FWS_ENABLESINGLEPLAYER = Boolean.parseBoolean(bossSettings.getProperty("EnableSinglePlayer", "False"));
            FWS_INTERVALOFSAILRENSPAWN = Integer.parseInt(bossSettings.getProperty("IntervalOfSailrenSpawn", "1440"));
            if(FWS_INTERVALOFSAILRENSPAWN < 5 || FWS_INTERVALOFSAILRENSPAWN > 1440) FWS_INTERVALOFSAILRENSPAWN = 1440;
            FWS_INTERVALOFSAILRENSPAWN = FWS_INTERVALOFSAILRENSPAWN * 60000;
            FWS_INTERVALOFNEXTMONSTER = Integer.parseInt(bossSettings.getProperty("IntervalOfNextMonster", "1"));
            if(FWS_INTERVALOFNEXTMONSTER < 1 || FWS_INTERVALOFNEXTMONSTER > 10) FWS_INTERVALOFNEXTMONSTER = 1;
            FWS_INTERVALOFNEXTMONSTER = FWS_INTERVALOFNEXTMONSTER * 60000;
            FWS_ACTIVITYTIMEOFMOBS = Integer.parseInt(bossSettings.getProperty("ActivityTimeOfMobs", "120"));
            if(FWS_ACTIVITYTIMEOFMOBS < 1 || FWS_ACTIVITYTIMEOFMOBS > 120) FWS_ACTIVITYTIMEOFMOBS = 120;
            FWS_ACTIVITYTIMEOFMOBS = FWS_ACTIVITYTIMEOFMOBS * 60000;        
            FWV_INTERVALOFVALAKAS = Integer.parseInt(bossSettings.getProperty("IntervalOfValakas", "1440"));
            if(FWV_INTERVALOFVALAKAS < 5 || FWV_INTERVALOFVALAKAS > 1440) FWV_INTERVALOFVALAKAS = 1440;
            FWV_INTERVALOFVALAKAS = FWV_INTERVALOFVALAKAS * 60000;
            FWV_APPTIMEOFVALAKAS = Integer.parseInt(bossSettings.getProperty("AppTimeOfValakas", "20"));
            if(FWV_APPTIMEOFVALAKAS < 5 || FWV_APPTIMEOFVALAKAS > 60) FWV_APPTIMEOFVALAKAS = 10;
            FWV_APPTIMEOFVALAKAS = FWV_APPTIMEOFVALAKAS * 60000;
            FWV_ACTIVITYTIMEOFVALAKAS = Integer.parseInt(bossSettings.getProperty("ActivityTimeOfValakas", "120"));
            if(FWV_ACTIVITYTIMEOFVALAKAS < 120 || FWV_ACTIVITYTIMEOFVALAKAS > 720) FWV_ACTIVITYTIMEOFVALAKAS = 120;
            FWV_ACTIVITYTIMEOFVALAKAS = FWV_ACTIVITYTIMEOFVALAKAS * 60000;
            FWV_CAPACITYOFLAIR = Integer.parseInt(bossSettings.getProperty("CapacityOfLairOfValakas", "200"));
            FWV_MOVEATRANDOM = Boolean.parseBoolean(bossSettings.getProperty("MoveAtRandom", "True"));
        }
        catch (Exception e)
        {
            _log.error(e.getMessage(),e);
            throw new Error("Failed to Load "+BOSS_FILE+" File.");
        }
    }

    //  *******************************************************************************************
    public static final String	SAY_FILTER_FILE				= "./config/sayfilter.txt";
    //  *******************************************************************************************
    public static void loadSayFilter()
    {
        _log.info("loading " + SAY_FILTER_FILE);
        if(USE_SAY_FILTER){
            try{
            LineNumberReader lnr = null;
            File say_filter = new File(SAY_FILTER_FILE);
            lnr = new LineNumberReader(new BufferedReader(new FileReader(say_filter)));
            String line = null;
            while ((line = lnr.readLine()) != null){
                if (line.trim().length() == 0 || line.startsWith("#"))
                {
                    continue;
                }
                FILTER_LIST.add(line);
            }
            _log.info("Say Filter: Loaded " + FILTER_LIST.size() + " words");
            }catch (FileNotFoundException e)
            {
                _log.warn("sayfilter.txt is missing in config folder");
            }
            catch (Exception e)
            {
                _log.warn("error loading say filter: " + e);
            }
        }
    }
    //  *******************************************************************************************    
    
    public static class  ClassMasterSettings
    {
    	private FastMap<Integer,FastMap<Integer,Integer>> _claimItems;
    	private FastMap<Integer,FastMap<Integer,Integer>> _rewardItems;
    	private FastMap<Integer,Boolean> _allowedClassChange;
    	
    	public ClassMasterSettings(String _configLine)
    	{
    		_claimItems = new FastMap<Integer,FastMap<Integer,Integer>>();
    		_rewardItems = new FastMap<Integer,FastMap<Integer,Integer>>();
    		_allowedClassChange = new FastMap<Integer,Boolean>();
    		if (_configLine != null)
    			parseConfigLine(_configLine.trim());
    	}
    	
    	private void parseConfigLine(String _configLine)
    	{
    		StringTokenizer st = new StringTokenizer(_configLine, ";");
    		
    		while (st.hasMoreTokens())
    		{
    		// get allowed class change
   			int job = Integer.parseInt(st.nextToken());
    		
    		_allowedClassChange.put(job, true);
    		
    		FastMap<Integer,Integer> _items = new FastMap<Integer,Integer>();
            // parse items needed for class change
    		if (st.hasMoreTokens())
			{
    			StringTokenizer st2 = new StringTokenizer(st.nextToken(), "[],");
				
    			while (st2.hasMoreTokens())
				{
					StringTokenizer st3 = new StringTokenizer(st2.nextToken(), "()");
					int _itemId = Integer.parseInt(st3.nextToken());
					int _quantity = Integer.parseInt(st3.nextToken());
					_items.put(_itemId, _quantity);
				}
			}
    		
			_claimItems.put(job, _items);
			
			_items = new FastMap<Integer,Integer>();
    		// parse gifts after class change
			if (st.hasMoreTokens())
			{
				StringTokenizer st2 = new StringTokenizer(st.nextToken(), "[],");
				
				while (st2.hasMoreTokens())
				{
					StringTokenizer st3 = new StringTokenizer(st2.nextToken(), "()");
					int _itemId = Integer.parseInt(st3.nextToken());
					int _quantity = Integer.parseInt(st3.nextToken());
					_items.put(_itemId, _quantity);
				}
			}
			
			_rewardItems.put(job, _items);
    		}
    	}
    	
    	public boolean isAllowed(int job)
    	{
    		if (_allowedClassChange == null)
    			return false;
    		if (_allowedClassChange.containsKey(job))
    			return _allowedClassChange.get(job);
    		else 
    			return false;
    	}
    	
    	public FastMap<Integer,Integer> getRewardItems(int job)
    	{
    		if (_rewardItems.containsKey(job))
    			return _rewardItems.get(job);
    		else 
    			return null;
    	}
    	
    	public FastMap<Integer,Integer> getRequireItems(int job)
    	{
    		if (_claimItems.containsKey(job))
    			return _claimItems.get(job);
    		else 
    			return null;
    	}
    	
    }
    
    /** Enumeration for type of maps object */
    public static enum ObjectMapType
    {
        L2ObjectHashMap,
        WorldObjectMap
    }

    /** Enumeration for type of set object */
    public static enum ObjectSetType
    {
        L2ObjectHashSet,
        WorldObjectSet
    }
    
    public static boolean FACTION_ENABLED = false;
    public static boolean FACTION_KILL_REWARD = false;
    public static int FACTION_KILL_RATE = 1000;
    public static int FACTION_QUEST_RATE = 1;
	
	public static void load()
	{
			_log.info("loading gameserver config");
			loadConfiguration();
			loadHexId();
			loadRatesConfig();
			loadEnchantConfig();
			loadPvpConfig();
			loadTelnetConfig();
			loadOptionsConfig();
			loadOtherConfig();
			loadAltConfig();
			loadClansConfig();
			loadChampionsConfig();
			loadLotteryConfig();
			loadWeddingConfig();
			loadSiegeConfig();
			loadSepulchursConfig();
			loadClanHallConfig();
			loadIdFactoryConfig();
			loadFunEnginesConfig();
			loadSevenSignsConfig();
			loadGmAccess();
			loadPrivilegesConfig();
			loadIrcConfig();
			loadBossConfig();
			loadSayFilter();
			
			initDBProperties();
	}
	
    /**
     * Set a new value to a game parameter from the admin console.
     * @param pName (String) : name of the parameter to change
     * @param pValue (String) : new value of the parameter
     * @return boolean : true if modification has been made
     * @link useAdminCommand
     */
    public static boolean setParameterValue(String pName, String pValue)
    {
        // Server settings
        if (pName.equalsIgnoreCase("RateXp")) RATE_XP = Float.parseFloat(pValue);
        else if (pName.equalsIgnoreCase("RateSp")) RATE_SP = Float.parseFloat(pValue);
        else if (pName.equalsIgnoreCase("RatePartyXp")) RATE_PARTY_XP = Float.parseFloat(pValue);
        else if (pName.equalsIgnoreCase("RatePartySp")) RATE_PARTY_SP = Float.parseFloat(pValue);
        else if (pName.equalsIgnoreCase("RateQuestsReward")) RATE_QUESTS_REWARD = Float.parseFloat(pValue);
        else if (pName.equalsIgnoreCase("RateDropAdena")) RATE_DROP_ADENA = Float.parseFloat(pValue);
        else if (pName.equalsIgnoreCase("RateConsumableCost")) RATE_CONSUMABLE_COST = Float.parseFloat(pValue);
        else if (pName.equalsIgnoreCase("RateDropItems")) RATE_DROP_ITEMS = Float.parseFloat(pValue);
        else if (pName.equalsIgnoreCase("RateDropSpoil")) RATE_DROP_SPOIL = Float.parseFloat(pValue);
        else if (pName.equalsIgnoreCase("RateDropQuest")) RATE_DROP_QUEST = Float.parseFloat(pValue);
        else if (pName.equalsIgnoreCase("RateKarmaExpLost")) RATE_KARMA_EXP_LOST = Float.parseFloat(pValue);
        else if (pName.equalsIgnoreCase("RateSiegeGuardsPrice")) RATE_SIEGE_GUARDS_PRICE = Float.parseFloat(pValue);

        else if (pName.equalsIgnoreCase("PlayerDropLimit")) PLAYER_DROP_LIMIT = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("PlayerRateDrop")) PLAYER_RATE_DROP = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("PlayerRateDropItem")) PLAYER_RATE_DROP_ITEM = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("PlayerRateDropEquip")) PLAYER_RATE_DROP_EQUIP = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("PlayerRateDropEquipWeapon")) PLAYER_RATE_DROP_EQUIP_WEAPON = Integer.parseInt(pValue);

        else if (pName.equalsIgnoreCase("KarmaDropLimit")) KARMA_DROP_LIMIT = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("KarmaRateDrop")) KARMA_RATE_DROP = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("KarmaRateDropItem")) KARMA_RATE_DROP_ITEM = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("KarmaRateDropEquip")) KARMA_RATE_DROP_EQUIP = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("KarmaRateDropEquipWeapon")) KARMA_RATE_DROP_EQUIP_WEAPON = Integer.parseInt(pValue);

        else if (pName.equalsIgnoreCase("AutoDestroyDroppedItemAfter")) AUTODESTROY_ITEM_AFTER = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("SaveDroppedItem")) SAVE_DROPPED_ITEM = Boolean.parseBoolean(pValue);
        //else if (pName.equalsIgnoreCase("CategoryDropSystem")) CATEGORY_DROP_SYSTEM = CategoryDropSystem.valueOf(pValue);
        else if (pName.equalsIgnoreCase("CoordSynchronize")) COORD_SYNCHRONIZE = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("DeleteCharAfterDays")) DELETE_DAYS = Integer.parseInt(pValue);

        else if (pName.equalsIgnoreCase("ChanceToBreak")) CHANCE_BREAK = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("ChanceToLevel")) CHANCE_LEVEL = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("AllowDiscardItem")) ALLOW_DISCARDITEM = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("ChampionFrequency")) CHAMPION_FREQUENCY = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("ChampionHp")) CHAMPION_HP = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("ChampionHpRegen")) CHAMPION_HP_REGEN = Float.parseFloat(pValue);
        else if (pName.equalsIgnoreCase("ChampionAtk")) CHAMPION_ATK = Float.parseFloat(pValue);
        else if (pName.equalsIgnoreCase("ChampionSpdAtk")) CHAMPION_SPD_ATK = Float.parseFloat(pValue);
        else if (pName.equalsIgnoreCase("ChampionRewards")) CHAMPION_REWARDS = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("ChampionAdenasRewards")) CHAMPION_ADENA = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("ChampionExpSp")) CHAMPION_EXP_SP = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("ChampionBoss")) CHAMPION_BOSS = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("ChampionMinLevel")) CHAMPION_MIN_LEVEL = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("ChampionMaxLevel")) CHAMPION_MAX_LEVEL = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("ChampionMinions")) CHAMPION_MINIONS = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("ChampionSpecialItemChance")) CHAMPION_SPCL_CHANCE = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("ChampionSpecialItemID")) CHAMPION_SPCL_ITEM = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("ChampionSpecialItemAmount")) CHAMPION_SPCL_QTY = Integer.parseInt(pValue);

        else if (pName.equalsIgnoreCase("AllowFreight")) ALLOW_FREIGHT = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("AllowWarehouse")) ALLOW_WAREHOUSE = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("AllowWear")) ALLOW_WEAR = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("WearDelay")) WEAR_DELAY = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("WearPrice")) WEAR_PRICE = Integer.parseInt(pValue);        
        else if (pName.equalsIgnoreCase("AllowWater")) ALLOW_WATER = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("AllowRentPet")) ALLOW_RENTPET = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("CommunityType")) COMMUNITY_TYPE = pValue.toLowerCase();
        else if (pName.equalsIgnoreCase("BBSDefault")) BBS_DEFAULT = pValue;        
        else if (pName.equalsIgnoreCase("AllowBoat")) ALLOW_BOAT = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("AllowCursedWeapons")) ALLOW_CURSED_WEAPONS = Boolean.parseBoolean(pValue);

        else if (pName.equalsIgnoreCase("ShowLevelOnCommunityBoard")) SHOW_LEVEL_COMMUNITYBOARD = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("ShowStatusOnCommunityBoard")) SHOW_STATUS_COMMUNITYBOARD = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("NamePageSizeOnCommunityBoard")) NAME_PAGE_SIZE_COMMUNITYBOARD = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("NamePerRowOnCommunityBoard")) NAME_PER_ROW_COMMUNITYBOARD = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("ShowCursedWeaponOwner")) SHOW_CURSED_WEAPON_OWNER = Boolean.parseBoolean(pValue);

        else if (pName.equalsIgnoreCase("ShowNpcLevel")) SHOW_NPC_LVL = Boolean.parseBoolean(pValue);
        
        else if (pName.equalsIgnoreCase("ForceInventoryUpdate")) FORCE_INVENTORY_UPDATE = Boolean.parseBoolean(pValue);

        else if (pName.equalsIgnoreCase("AutoDeleteInvalidQuestData")) AUTODELETE_INVALID_QUEST_DATA = Boolean.parseBoolean(pValue);

        else if (pName.equalsIgnoreCase("MaximumOnlineUsers")) MAXIMUM_ONLINE_USERS = Integer.parseInt(pValue);

        else if (pName.equalsIgnoreCase("ZoneTown")) ZONE_TOWN = Integer.parseInt(pValue);

        else if (pName.equalsIgnoreCase("ShowGMLogin")) SHOW_GM_LOGIN = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("HideGMStatus")) HIDE_GM_STATUS = Boolean.parseBoolean(pValue);
        
        // Other settings
        else if (pName.equalsIgnoreCase("UseDeepBlueDropRules")) DEEPBLUE_DROP_RULES = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("CancelLesserEffect")) EFFECT_CANCELING = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("WyvernSpeed")) WYVERN_SPEED = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("StriderSpeed")) STRIDER_SPEED = Integer.parseInt(pValue);

        else if (pName.equalsIgnoreCase("MaximumSlotsForNoDwarf")) INVENTORY_MAXIMUM_NO_DWARF = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("MaximumSlotsForDwarf")) INVENTORY_MAXIMUM_DWARF = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("MaximumSlotsForGMPlayer")) INVENTORY_MAXIMUM_GM = Integer.parseInt(pValue);

        else if (pName.equalsIgnoreCase("MaximumWarehouseSlotsForNoDwarf")) WAREHOUSE_SLOTS_NO_DWARF = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("MaximumWarehouseSlotsForDwarf")) WAREHOUSE_SLOTS_DWARF = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("MaximumWarehouseSlotsForClan")) WAREHOUSE_SLOTS_CLAN = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("MaximumFreightSlots")) FREIGHT_SLOTS = Integer.parseInt(pValue);

        else if (pName.equalsIgnoreCase("EnchantChanceWeapon")) ENCHANT_CHANCE_WEAPON = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("EnchantChanceArmor")) ENCHANT_CHANCE_ARMOR = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("EnchantBreakWeapon")) ENCHANT_BREAK_WEAPON = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("EnchantBreakArmor")) ENCHANT_BREAK_ARMOR = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("EnchantChanceWeaponCrystal")) ENCHANT_CHANCE_WEAPON_CRYSTAL = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("EnchantChanceArmorCrystal")) ENCHANT_CHANCE_ARMOR_CRYSTAL = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("EnchantBreakWeaponCrystal")) ENCHANT_BREAK_WEAPON_CRYSTAL = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("EnchantBreakArmorCrystal")) ENCHANT_BREAK_ARMOR_CRYSTAL = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("EnchantChanceWeaponBlessed")) ENCHANT_CHANCE_WEAPON_BLESSED = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("EnchantChanceArmorBlessed")) ENCHANT_CHANCE_ARMOR_BLESSED = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("EnchantBreakWeaponBlessed")) ENCHANT_BREAK_WEAPON_BLESSED = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("EnchantBreakArmorBlessed")) ENCHANT_BREAK_ARMOR_BLESSED = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("EnchantMaxWeapon")) ENCHANT_MAX_WEAPON = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("EnchantMaxArmor")) ENCHANT_MAX_ARMOR = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("EnchantSafeMax")) ENCHANT_SAFE_MAX = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("EnchantSafeMaxFull")) ENCHANT_SAFE_MAX_FULL = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("EnchantDwarf1Enchantlevel")) ENCHANT_DWARF_1_ENCHANTLEVEL = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("EnchantDwarf2Enchantlevel")) ENCHANT_DWARF_2_ENCHANTLEVEL = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("EnchantDwarf3Enchantlevel")) ENCHANT_DWARF_3_ENCHANTLEVEL = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("EnchantDwarf1Chance")) ENCHANT_DWARF_1_CHANCE = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("EnchantDwarf2Chance")) ENCHANT_DWARF_2_CHANCE = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("EnchantDwarf3Chance")) ENCHANT_DWARF_3_CHANCE = Integer.parseInt(pValue);

        else if (pName.equalsIgnoreCase("NPCHpRegenMultiplier")) NPC_HP_REGEN_MULTIPLIER = Double.parseDouble(pValue);
        else if (pName.equalsIgnoreCase("NPCMpRegenMultiplier")) NPC_MP_REGEN_MULTIPLIER = Double.parseDouble(pValue);
        else if (pName.equalsIgnoreCase("PlayerHpRegenMultiplier")) PLAYER_HP_REGEN_MULTIPLIER = Double.parseDouble(pValue);
        else if (pName.equalsIgnoreCase("PlayerMpRegenMultiplier")) PLAYER_MP_REGEN_MULTIPLIER = Double.parseDouble(pValue);
        else if (pName.equalsIgnoreCase("PlayerCpRegenMultiplier")) PLAYER_CP_REGEN_MULTIPLIER = Double.parseDouble(pValue);

        else if (pName.equalsIgnoreCase("RaidHpRegenMultiplier")) RAID_HP_REGEN_MULTIPLIER = Double.parseDouble(pValue);
        else if (pName.equalsIgnoreCase("RaidMpRegenMultiplier")) RAID_MP_REGEN_MULTIPLIER = Double.parseDouble(pValue);
        else if (pName.equalsIgnoreCase("RaidDefenceMultiplier")) RAID_DEFENCE_MULTIPLIER = Double.parseDouble(pValue) /100;
        else if (pName.equalsIgnoreCase("RaidMinionRespawnTime")) RAID_MINION_RESPAWN_TIMER =Integer.parseInt(pValue);

        else if (pName.equalsIgnoreCase("StartingAdena")) STARTING_ADENA = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("UnstuckInterval")) UNSTUCK_INTERVAL = Integer.parseInt(pValue);

        else if (pName.equalsIgnoreCase("PlayerSpawnProtection")) PLAYER_SPAWN_PROTECTION = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("PlayerFakeDeathUpProtection")) PLAYER_FAKEDEATH_UP_PROTECTION = Integer.parseInt(pValue);

        else if (pName.equalsIgnoreCase("PartyXpCutoffMethod")) PARTY_XP_CUTOFF_METHOD = pValue;
        else if (pName.equalsIgnoreCase("PartyXpCutoffPercent")) PARTY_XP_CUTOFF_PERCENT = Double.parseDouble(pValue);
        else if (pName.equalsIgnoreCase("PartyXpCutoffLevel")) PARTY_XP_CUTOFF_LEVEL = Integer.parseInt(pValue);

        else if (pName.equalsIgnoreCase("RespawnRestoreCP")) RESPAWN_RESTORE_CP = Double.parseDouble(pValue) / 100;
        else if (pName.equalsIgnoreCase("RespawnRestoreHP")) RESPAWN_RESTORE_HP = Double.parseDouble(pValue) / 100;
        else if (pName.equalsIgnoreCase("RespawnRestoreMP")) RESPAWN_RESTORE_MP = Double.parseDouble(pValue) / 100;

        else if (pName.equalsIgnoreCase("MaxPvtStoreSlotsDwarf")) MAX_PVTSTORE_SLOTS_DWARF = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("MaxPvtStoreSlotsOther")) MAX_PVTSTORE_SLOTS_OTHER = Integer.parseInt(pValue);

        else if (pName.equalsIgnoreCase("StoreSkillCooltime")) STORE_SKILL_COOLTIME = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("AnnounceMammonSpawn")) ANNOUNCE_MAMMON_SPAWN = Boolean.parseBoolean(pValue);
        
        // Alternative settings
        else if (pName.equalsIgnoreCase("AltGameTiredness")) ALT_GAME_TIREDNESS = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("AltGameCreation")) ALT_GAME_CREATION = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("AltGameCreationSpeed")) ALT_GAME_CREATION_SPEED = Double.parseDouble(pValue);
        else if (pName.equalsIgnoreCase("AltGameCreationXpRate")) ALT_GAME_CREATION_XP_RATE = Double.parseDouble(pValue);
        else if (pName.equalsIgnoreCase("AltGameCreationSpRate")) ALT_GAME_CREATION_SP_RATE = Double.parseDouble(pValue); 
        else if (pName.equalsIgnoreCase("AltBlacksmithUseRecipes")) ALT_BLACKSMITH_USE_RECIPES = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("AltGameSkillLearn")) ALT_GAME_SKILL_LEARN = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("AltNbCumulatedBuff")) ALT_GAME_NUMBER_OF_CUMULATED_BUFF = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("AltBuffTime")) ALT_BUFF_TIME = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("AltSuccessRate")) ALT_DAGGER_RATE = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("DaggerVSRobe")) ALT_DAGGER_DMG_VS_ROBE = Float.parseFloat(pValue);
        else if (pName.equalsIgnoreCase("DaggerVSLight")) ALT_DAGGER_DMG_VS_LIGHT = Float.parseFloat(pValue);
        else if (pName.equalsIgnoreCase("InstantKillEffect2")) ALT_INSTANT_KILL_EFFECT_2 = Float.parseFloat(pValue);
        else if (pName.equalsIgnoreCase("DaggerVSHeavy")) ALT_DAGGER_DMG_VS_HEAVY = Float.parseFloat(pValue);
        else if (pName.equalsIgnoreCase("AltAttackDelay")) ALT_ATTACK_DELAY = Float.parseFloat(pValue);
        else if (pName.equalsIgnoreCase("AltFailRate")) ALT_DAGGER_FAIL_RATE = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("AltBehindRate")) ALT_DAGGER_RATE_BEHIND = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("AltFrontRate")) ALT_DAGGER_RATE_FRONT = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("AltDanceTime")) ALT_DANCE_TIME = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("MaxPAtkSpeed")) MAX_PATK_SPEED = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("MaxMAtkSpeed")) MAX_MATK_SPEED = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("GradePenalty")) GRADE_PENALTY = Boolean.parseBoolean(pValue);

        else if (pName.equalsIgnoreCase("AltGameCancelByHit"))
        {
            ALT_GAME_CANCEL_BOW     = pValue.equalsIgnoreCase("bow") || pValue.equalsIgnoreCase("all");
            ALT_GAME_CANCEL_CAST    = pValue.equalsIgnoreCase("cast") || pValue.equalsIgnoreCase("all");
        }

        else if (pName.equalsIgnoreCase("AltShieldBlocks")) ALT_GAME_SHIELD_BLOCKS = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("AltPerfectShieldBlockRate")) ALT_PERFECT_SHLD_BLOCK = Integer.parseInt(pValue); 
        else if (pName.equalsIgnoreCase("Delevel")) ALT_GAME_DELEVEL = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("MagicFailures")) ALT_GAME_MAGICFAILURES = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("AltGameMobAttackAI")) ALT_GAME_MOB_ATTACK_AI = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("AltMobAgroInPeaceZone")) ALT_MOB_AGRO_IN_PEACEZONE = Boolean.parseBoolean(pValue);

        else if (pName.equalsIgnoreCase("AltGameExponentXp")) ALT_GAME_EXPONENT_XP = Float.parseFloat(pValue);
        else if (pName.equalsIgnoreCase("AltGameExponentSp")) ALT_GAME_EXPONENT_SP = Float.parseFloat(pValue);

        else if (pName.equalsIgnoreCase("AltGameFreights")) ALT_GAME_FREIGHTS = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("AltGameFreightPrice")) ALT_GAME_FREIGHT_PRICE = Integer.parseInt(pValue);

        else if (pName.equalsIgnoreCase("AltGameSkillHitRate")) ALT_GAME_SKILL_HIT_RATE = Float.parseFloat(pValue);

        else if (pName.equalsIgnoreCase("CraftingEnabled")) IS_CRAFTING_ENABLED = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("SpBookNeeded")) SP_BOOK_NEEDED = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("ClanItemNeeded")) CLAN_ITEM_NEEDED = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("AutoLoot")) AUTO_LOOT = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("AutoLootHerbs")) AUTO_LOOT_HERBS = Boolean.parseBoolean(pValue);

        else if (pName.equalsIgnoreCase("AltKarmaPlayerCanBeKilledInPeaceZone")) ALT_GAME_KARMA_PLAYER_CAN_BE_KILLED_IN_PEACEZONE = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("AltKarmaPlayerCanShop")) ALT_GAME_KARMA_PLAYER_CAN_SHOP = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("AltKarmaPlayerCanUseGK")) ALT_GAME_KARMA_PLAYER_CAN_USE_GK = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("AltKarmaPlayerCanTeleport")) ALT_GAME_KARMA_PLAYER_CAN_TELEPORT = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("AltKarmaPlayerCanTrade")) ALT_GAME_KARMA_PLAYER_CAN_TRADE = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("AltKarmaPlayerCanUseWareHouse")) ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("AltRequireCastleForDawn")) ALT_GAME_REQUIRE_CASTLE_DAWN = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("AltRequireClanCastle")) ALT_GAME_REQUIRE_CLAN_CASTLE = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("AltFreeTeleporting")) ALT_GAME_FREE_TELEPORT = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("AltSubClassWithoutQuests")) ALT_GAME_SUBCLASS_WITHOUT_QUESTS = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("MaxSubclass")) MAX_SUBCLASS = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("AltNewCharAlwaysIsNewbie")) ALT_GAME_NEW_CHAR_ALWAYS_IS_NEWBIE = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("DwarfRecipeLimit")) DWARF_RECIPE_LIMIT = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("CommonRecipeLimit")) COMMON_RECIPE_LIMIT = Integer.parseInt(pValue);        

        // PvP settings
        else if (pName.equalsIgnoreCase("MinKarma")) KARMA_MIN_KARMA = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("MaxKarma")) KARMA_MAX_KARMA = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("XPDivider")) KARMA_XP_DIVIDER = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("BaseKarmaLost")) KARMA_LOST_BASE = Integer.parseInt(pValue);

        else if (pName.equalsIgnoreCase("CanGMDropEquipment")) KARMA_DROP_GM = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("AwardPKKillPVPPoint")) KARMA_AWARD_PK_KILL = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("MinimumPKRequiredToDrop")) KARMA_PK_LIMIT = Integer.parseInt(pValue);
        
        else if (pName.equalsIgnoreCase("PvPTime")) PVP_TIME = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("GlobalChat")) DEFAULT_GLOBAL_CHAT = pValue;
        else if (pName.equalsIgnoreCase("TradeChat")) DEFAULT_TRADE_CHAT = pValue;
		else if (pName.equalsIgnoreCase("MenuStyle")) GM_ADMIN_MENU_STYLE = pValue;
        
        else if (pName.equalsIgnoreCase("CTFEvenTeams"))  CTF_EVEN_TEAMS = pValue;
        else if (pName.equalsIgnoreCase("CTFAllowInterference")) CTF_ALLOW_INTERFERENCE = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("CTFAllowPotions")) CTF_ALLOW_POTIONS = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("CTFAllowSummon")) CTF_ALLOW_SUMMON = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("CTFOnStartRemoveAllEffects")) CTF_ON_START_REMOVE_ALL_EFFECTS = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("CTFOnStartUnsummonPet")) CTF_ON_START_UNSUMMON_PET = Boolean.parseBoolean(pValue);
        
        else if (pName.equalsIgnoreCase("TvTEvenTeams"))  TVT_EVEN_TEAMS = pValue;
        else if (pName.equalsIgnoreCase("TvTAllowInterference")) TVT_ALLOW_INTERFERENCE = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("TvTAllowPotions")) TVT_ALLOW_POTIONS = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("TvTAllowSummon")) TVT_ALLOW_SUMMON = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("TvTOnStartRemoveAllEffects")) TVT_ON_START_REMOVE_ALL_EFFECTS = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("TvTOnStartUnsummonPet")) TVT_ON_START_UNSUMMON_PET = Boolean.parseBoolean(pValue);
        
        else if (pName.equalsIgnoreCase("DMAllowInterference")) DM_ALLOW_INTERFERENCE = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("DMAllowPotions")) DM_ALLOW_POTIONS = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("DMAllowSummon")) DM_ALLOW_SUMMON = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("DMOnStartRemoveAllEffects")) DM_ON_START_REMOVE_ALL_EFFECTS = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("DMOnStartUnsummonPet")) DM_ON_START_UNSUMMON_PET = Boolean.parseBoolean(pValue);
        
        else if (pName.equalsIgnoreCase("FailFakeDeath")) FAIL_FAKEDEATH = Boolean.parseBoolean(pValue);

        // JP fight with Antharas Custom Setting
        else if (pName.equalsIgnoreCase("IntervalOfAntharas")) FWA_INTERVALOFANTHARAS = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("AppTimeOfAntharas")) FWA_APPTIMEOFANTHARAS = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("ActivityTimeOfAntharas")) FWA_ACTIVITYTIMEOFANTHARAS = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("OldAntharas")) FWA_OLDANTHARAS = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("LimitOfWeak")) FWA_LIMITOFWEAK = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("LimitOfNormal")) FWA_LIMITOFNORMAL = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("IntervalOfBehemothOnWeak")) FWA_INTERVALOFBEHEMOTHONWEAK = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("IntervalOfBehemothOnNormal")) FWA_INTERVALOFBEHEMOTHONNORMAL = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("IntervalOfBehemothOnStrong")) FWA_INTERVALOFBEHEMOTHONSTRONG = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("IntervalOfBomberOnWeak")) FWA_INTERVALOFBOMBERONWEAK = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("IntervalOfBomberOnNormal")) FWA_INTERVALOFBOMBERONNORMAL = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("IntervalOfBomberOnStrong")) FWA_INTERVALOFBOMBERONSTRONG = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("MoveAtRandom")) FWA_MOVEATRANDOM = Boolean.parseBoolean(pValue);

        // JP fight with Baium Custom Setting
        else if (pName.equalsIgnoreCase("IntervalOfBaium")) FWB_INTERVALOFBAIUM = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("ActivityTimeOfBaium")) FWB_ACTIVITYTIMEOFBAIUM = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("MoveAtRandom")) FWB_MOVEATRANDOM = Boolean.parseBoolean(pValue);

        // JP fight with Valakas Custom Setting
        else if (pName.equalsIgnoreCase("IntervalOfValakas")) FWV_INTERVALOFVALAKAS = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("AppTimeOfValakas")) FWV_APPTIMEOFVALAKAS = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("ActivityTimeOfValakas")) FWV_ACTIVITYTIMEOFVALAKAS = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("CapacityOfLairOfValakas")) FWV_CAPACITYOFLAIR = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("MoveAtRandom")) FWV_MOVEATRANDOM = Boolean.parseBoolean(pValue);

        // JP fight with sailren Custom Setting
        else if (pName.equalsIgnoreCase("EnableSinglePlayer")) FWS_ENABLESINGLEPLAYER = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("IntervalOfSailrenSpawn")) FWS_INTERVALOFSAILRENSPAWN = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("IntervalOfNextMonster")) FWS_INTERVALOFNEXTMONSTER = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("ActivityTimeOfMobs")) FWS_ACTIVITYTIMEOFMOBS = Integer.parseInt(pValue);

        // Siege settings
        else if (pName.equalsIgnoreCase("AttackerMaxClans")) SIEGE_MAX_ATTACKER = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("DefenderMaxClans")) SIEGE_MAX_DEFENDER = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("AttackerRespawn")) SIEGE_RESPAWN_DELAY_ATTACKER = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("DefenderRespawn")) SIEGE_RESPAWN_DELAY_DEFENDER = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("CTLossPenalty")) SIEGE_CT_LOSS_PENALTY = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("MaxFlags")) SIEGE_FLAG_MAX_COUNT = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("SiegeClanMinLevel")) SIEGE_CLAN_MIN_LEVEL = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("SiegeLength")) SIEGE_LENGTH_MINUTES = Integer.parseInt(pValue);

        else return false;
        return true;
    }
    
    // it has no instancies
    private Config() {}

    /**
     * Save hexadecimal ID of the server in the properties file.
     * @param string (String) : hexadecimal ID of the server to store
     * @see HEXID_FILE
     * @see saveHexid(String string, String fileName)
     * @link LoginServerThread
     */
    public static void saveHexid(int serverId, String string)
    {
        Config.saveHexid(serverId, string, HEXID_FILE);
    }

    /**
     * Save hexadecimal ID of the server in the properties file.
     * @param hexId (String) : hexadecimal ID of the server to store
     * @param fileName (String) : name of the properties file
     */
    public static void saveHexid(int serverId, String hexId, String fileName)
    {
        try
        {
            Properties hexSetting = new Properties();
            File file = new File(fileName);
            //Create a new empty file only if it doesn't exist
            file.createNewFile();
            OutputStream out = new FileOutputStream(file);
            hexSetting.setProperty("ServerID",String.valueOf(serverId));
            hexSetting.setProperty("HexID",hexId);
            hexSetting.store(out,"the hexID to auth into login");
            out.close();
        }
        catch (Exception e)
        {
            _log.warn("Failed to save hex id to "+fileName+" File.");
        }
    }    
    
    /**
     * To keep compatibility with old loginserver.properties, add db properties into system properties
     * Spring will use those values later
     */
    public static void initDBProperties() 
    {
        System.setProperty("net.sf.l2j.db.driverclass", DATABASE_DRIVER );
        System.setProperty("net.sf.l2j.db.urldb", DATABASE_URL );
        System.setProperty("net.sf.l2j.db.user", DATABASE_LOGIN );
        System.setProperty("net.sf.l2j.db.password", DATABASE_PASSWORD );       
        System.setProperty("net.sf.l2j.db.maximum.db.connection", Integer.toString(DATABASE_MAX_CONNECTIONS) );
    }    
}
