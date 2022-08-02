package net.simplyrin.jeongseongryon.commands.botowner;

import net.simplyrin.jeongseongryon.JeongSeongRyon;
import net.simplyrin.jeongseongryon.classes.BaseCommand;
import net.simplyrin.jeongseongryon.classes.CommandPermission;
import net.simplyrin.jeongseongryon.classes.CommandType;
import net.simplyrin.jeongseongryon.classes.PandaMessageEvent;

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
public class ShutdownCommand extends BaseCommand {
	
	@Override
	public String getCommand() {
		return "!shutdown";
	}

	@Override
	public String getDescription() {
		return "Bot をシャットダウンします。";
	}

	@Override
	public boolean sendTyping() {
		return false;
	}

	@Override
	public CommandType getType() {
		return CommandType.EqualsIgnoreCase;
	}

	@Override
	public CommandPermission getPermission() {
		return CommandPermission.BotOwner;
	}

	@Override
	public void execute(JeongSeongRyon instance, PandaMessageEvent event, String[] args) {
		instance.shutdown();
	}

}
