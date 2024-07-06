package com.nexia.teams.commands;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.Event;

public class CommandLoader {
    public static void registerCommands() {
        Event<CommandRegistrationCallback> callbackEvent = CommandRegistrationCallback.EVENT;

        callbackEvent.register(KothCommand::register);
        callbackEvent.register(TeamCommand::register);
        callbackEvent.register(MeteorCommand::register);
        callbackEvent.register(CalendarCommand::register);
        callbackEvent.register(HardcoreCommand::register);
        callbackEvent.register(PvpCommand::register);
        callbackEvent.register(EndCommand::register);
        callbackEvent.register(NetherCommand::register);
        callbackEvent.register(TournamentCommand::register);
    }
}
