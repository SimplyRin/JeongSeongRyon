package net.simplyrin.jeongseongryon;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.reflect.ClassPath;

import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.simplyrin.config.Config;
import net.simplyrin.config.Configuration;
import net.simplyrin.jeongseongryon.classes.BaseCommand;
import net.simplyrin.jeongseongryon.listeners.CommandExecutor;
import net.simplyrin.jeongseongryon.listeners.EventHandler;
import net.simplyrin.rinstream.RinStream;

/**
 * Created by SimplyRin on 2022/08/01.
 *
 * Copyright (c) 2022 SimplyRin
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
@Getter
public class JeongSeongRyon {
	
	public static void main(String[] args) {
		new JeongSeongRyon().run(args);
	}
	
	private Configuration config;
	private Configuration data;
	
	private JDA jda;
	
	private EventHandler eventHandler;
	private CommandExecutor commandRegister;
	
	public void run(String[] args) {
		RinStream rinStream = new RinStream();
		rinStream.setPrefix("yyyy/MM/dd (E) HH:mm:ss")
				.enableError()
				.setSaveLog(true)
				.setEnableColor(true)
				.setEnableTranslateColor(true);
		
		File file = new File("config.yml");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}

			Configuration config = Config.getConfig(file);
			config.set("Token", "BOT_TOKEN_HERE");
			config.set("Restart-Script", "start.sh");
			config.set("ReceiveChannel", 0L);
			config.set("BotOwnerList", Arrays.asList("111111111111111111"));

			Config.saveConfig(config, file);
		}
		
		File data = new File("data.yml");
		if (!data.exists()) {
			try {
				data.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		this.config = Config.getConfig(file);
		this.data = Config.getConfig(data);
		
		String token = this.config.getString("Token");
		if (token.equals("BOT_TOKEN_HERE")) {
			System.out.println("Discord Bot Token を config.yml に入力してください！");
			System.exit(0);
			return;
		}
		
		try {
			List<GatewayIntent> list = new ArrayList<>();
			for (GatewayIntent intent : GatewayIntent.values()) {
				list.add(intent);
			}
			JDABuilder jdaBuilder = JDABuilder.createDefault(token, list);
			jdaBuilder.setMemberCachePolicy(MemberCachePolicy.ALL);
			jdaBuilder.setChunkingFilter(ChunkingFilter.ALL);
			jdaBuilder.enableCache(CacheFlag.ACTIVITY);
			jdaBuilder.enableCache(CacheFlag.VOICE_STATE);
			this.jda = jdaBuilder.build().awaitReady();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.jda.addEventListener(this.eventHandler = new EventHandler(this));
		this.jda.addEventListener(this.commandRegister = new CommandExecutor(this));
		// 自動登録
		try {
			System.out.println("コマンドを登録しています...");
			CommandListUpdateAction commands = this.getJda().updateCommands();
			
			final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			for (final ClassPath.ClassInfo classInfo : ClassPath.from(classLoader).getTopLevelClasses()) {
				if (classInfo.getName().startsWith("net.simplyrin.jeongseongryon.command")) {
					BaseCommand baseCommand = (BaseCommand) Class.forName(classInfo.getName()).getDeclaredConstructor().newInstance();
					this.commandRegister.registerCommand(commands, baseCommand.getCommand(), baseCommand);
				}
			}
			
			List<Command> commandList = commands.complete();
			System.out.println("コマンドを登録しました。スラッシュコマンド: " + commandList.size());
		} catch (Exception e) {
			System.out.println("エラーが発生しました。");
			e.printStackTrace();
			return;
		}
		
		try {
			System.out.println(this.jda.getSelfUser().getName());
			for (var guild : this.jda.getGuilds()) {
				System.out.println(guild.getName() + "@" + guild.getId());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			this.shutdown();
		}));
	}
	
	public void saveData() {
		Config.saveConfig(this.config, new File("config.yml"));
		Config.saveConfig(this.data, new File("data.yml"));
	}
	
	public void shutdown() {
		this.saveData();
		System.out.println("データを保存しました。");
		
		System.out.println("シャットダウンしています...");
		this.jda.shutdown();
	}
	
	public boolean isBotOwner(User user) {
		boolean bool = false;
		
		List<String> botOwnerList = this.getConfig().getStringList("BotOwnerList");
		if (botOwnerList == null || botOwnerList.isEmpty()) {
			this.getConfig().set("BotOwnerList", Arrays.asList("999", "888"));
		} else {
			for (String id : botOwnerList) {
				if (user.getId().equals(id)) {
					bool = true;
				}
			}
		}
		
		return bool;
	}

}
