package com.canoo.dolphin.impl.commands;

import org.opendolphin.RemotingConstants;
import org.opendolphin.core.comm.SignalCommand;

public final class InterruptLongPollCommand extends SignalCommand {

    public InterruptLongPollCommand() {
        super(RemotingConstants.INTERRUPT_LONG_POLL_COMMAND_NAME);
    }
}


