package com.canoo.dp.impl.remoting.codec.encoders;

import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.dp.impl.remoting.commands.DestroyContextCommand;
import com.google.gson.JsonObject;

import static org.opendolphin.core.comm.CommandConstants.DESTROY_CONTEXT_COMMAND_ID;
import static org.opendolphin.core.comm.CommandConstants.ID;

public class DestroyContextCommandEncoder extends AbstractCommandEncoder<DestroyContextCommand> {

    @Override
    public JsonObject encode(DestroyContextCommand command) {
        Assert.requireNonNull(command, "command");
        final JsonObject jsonCommand = new JsonObject();
        jsonCommand.addProperty(ID, DESTROY_CONTEXT_COMMAND_ID);
        return jsonCommand;
    }

    @Override
    public DestroyContextCommand decode(JsonObject jsonObject) {
        return new DestroyContextCommand();
    }
}
