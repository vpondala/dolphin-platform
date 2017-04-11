package com.canoo.dolphin.impl.commands;

import org.opendolphin.RemotingConstants;
import org.opendolphin.core.comm.Command;

public final class StartLongPollCommand extends Command {

    public StartLongPollCommand() {
        super(RemotingConstants.START_LONG_POLL_COMMAND_NAME);
    }
}

