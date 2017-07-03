/*
 * Copyright 2015-2017 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.canoo.dolphin.impl.codec;

import com.canoo.dolphin.impl.codec.encoders.AbstractCommandEncoder;
import com.canoo.dolphin.impl.codec.encoders.AttributeMetadataChangedCommandEncoder;
import com.canoo.dolphin.impl.codec.encoders.CallActionCommandEncoder;
import com.canoo.dolphin.impl.codec.encoders.ChangeAttributeMetadataCommandEncoder;
import com.canoo.dolphin.impl.codec.encoders.CommandEncoder;
import com.canoo.dolphin.impl.codec.encoders.CreateContextCommandEncoder;
import com.canoo.dolphin.impl.codec.encoders.CreateControllerCommandEncoder;
import com.canoo.dolphin.impl.codec.encoders.CreatePresentationModelCommandEncoder;
import com.canoo.dolphin.impl.codec.encoders.DeletePresentationModelCommandEncoder;
import com.canoo.dolphin.impl.codec.encoders.DestroyContextCommandEncoder;
import com.canoo.dolphin.impl.codec.encoders.DestroyControllerCommandEncoder;
import com.canoo.dolphin.impl.codec.encoders.EmptyCommandEncoder;
import com.canoo.dolphin.impl.codec.encoders.InterruptLongPollCommandEncoder;
import com.canoo.dolphin.impl.codec.encoders.PresentationModelDeletedCommandEncoder;
import com.canoo.dolphin.impl.codec.encoders.StartLongPollCommandEncoder;
import com.canoo.dolphin.impl.codec.encoders.ValueChangedCommandEncoder;
import com.canoo.dolphin.impl.commands.CallActionCommand;
import com.canoo.dolphin.impl.commands.CreateContextCommand;
import com.canoo.dolphin.impl.commands.CreateControllerCommand;
import com.canoo.dolphin.impl.commands.DestroyContextCommand;
import com.canoo.dolphin.impl.commands.DestroyControllerCommand;
import com.canoo.dolphin.impl.commands.InterruptLongPollCommand;
import com.canoo.dolphin.impl.commands.StartLongPollCommand;
import com.canoo.impl.platform.core.Assert;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import org.opendolphin.core.comm.AttributeMetadataChangedCommand;
import org.opendolphin.core.comm.ChangeAttributeMetadataCommand;
import org.opendolphin.core.comm.Codec;
import org.opendolphin.core.comm.Command;
import org.opendolphin.core.comm.CreatePresentationModelCommand;
import org.opendolphin.core.comm.DeletePresentationModelCommand;
import org.opendolphin.core.comm.EmptyCommand;
import org.opendolphin.core.comm.PresentationModelDeletedCommand;
import org.opendolphin.core.comm.ValueChangedCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.opendolphin.core.comm.CommandConstants.*;

public class OptimizedJsonCodec implements Codec {

    private static final Logger LOG = LoggerFactory.getLogger(OptimizedJsonCodec.class);

    private static final OptimizedJsonCodec INSTANCE = new OptimizedJsonCodec();

    private final Gson GSON;

    private final Map<Class<? extends Command>, CommandEncoder<?>> ENCODERS = new HashMap<>();

    private final Map<String, CommandEncoder<?>> DECODERS = new HashMap<>();

    private OptimizedJsonCodec() {
        GSON = new GsonBuilder().serializeNulls().create();

        addEncoder(new StartLongPollCommandEncoder(), StartLongPollCommand.class, START_LONG_POLL_COMMAND_ID);
        addEncoder(new InterruptLongPollCommandEncoder(), InterruptLongPollCommand.class, INTERRUPT_LONG_POLL_COMMAND_ID);

        addEncoder(new CreatePresentationModelCommandEncoder(), CreatePresentationModelCommand.class, CREATE_PRESENTATION_MODEL_COMMAND_ID);
        addEncoder(new DeletePresentationModelCommandEncoder(), DeletePresentationModelCommand.class, DELETE_PRESENTATION_MODEL_COMMAND_ID);
        addEncoder(new PresentationModelDeletedCommandEncoder(), PresentationModelDeletedCommand.class, PRESENTATION_MODEL_DELETED_COMMAND_ID);
        addEncoder(new ValueChangedCommandEncoder(), ValueChangedCommand.class, VALUE_CHANGED_COMMAND_ID);
        addEncoder(new ChangeAttributeMetadataCommandEncoder(), ChangeAttributeMetadataCommand.class, CHANGE_ATTRIBUTE_METADATA_COMMAND_ID);
        addEncoder(new AttributeMetadataChangedCommandEncoder(), AttributeMetadataChangedCommand.class, ATTRIBUTE_METADATA_CHANGED_COMMAND_ID);

        addEncoder(new EmptyCommandEncoder(), EmptyCommand.class, EMPTY_COMMAND_ID);

        addEncoder(new CreateContextCommandEncoder(), CreateContextCommand.class, CREATE_CONTEXT_COMMAND_ID);
        addEncoder(new DestroyContextCommandEncoder(), DestroyContextCommand.class, DESTROY_CONTEXT_COMMAND_ID);

        addEncoder(new CreateControllerCommandEncoder(), CreateControllerCommand.class, CREATE_CONTROLLER_COMMAND_ID);
        addEncoder(new DestroyControllerCommandEncoder(), DestroyControllerCommand.class, DESTROY_CONTROLLER_COMMAND_ID);
        addEncoder(new CallActionCommandEncoder(), CallActionCommand.class, CALL_ACTION_COMMAND_ID);
    }

    private <C extends Command> void addEncoder(final AbstractCommandEncoder<C> encoder, final Class<C> commandClass, final String commandId) {
        Assert.requireNonNull(encoder, "encoder");
        Assert.requireNonNull(commandClass, "commandClass");
        Assert.requireNonNull(commandId, "commandId");
        ENCODERS.put(commandClass, encoder);
        DECODERS.put(commandId, encoder);
    }

    @Override
    @SuppressWarnings("unchecked")
    public String encode(final List<? extends Command> commands) {
        Assert.requireNonNull(commands, "commands");
        LOG.trace("Encoding command list with {} commands", commands.size());
        final StringBuilder builder = new StringBuilder("[");
        for (final Command command : commands) {
            if (command == null) {
                throw new IllegalArgumentException("Command list contains a null command: " + command);
            } else {
                LOG.trace("Encoding command of type {}", command.getClass());
                final CommandEncoder encoder = ENCODERS.get(command.getClass());
                if (encoder == null) {
                    throw new RuntimeException("No encoder for command type " + command.getClass() + " found");
                }
                final JsonObject jsonObject = encoder.encode(command);
                GSON.toJson(jsonObject, builder);
                builder.append(",");
            }
        }
        if (!commands.isEmpty()) {
            final int length = builder.length();
            builder.delete(length - 1, length);
        }
        builder.append("]");
        if (LOG.isTraceEnabled()) {
            LOG.trace("Encoded message: {}", builder.toString());
        }
        return builder.toString();
    }

    @Override
    public List<Command> decode(final String transmitted) {
        Assert.requireNonNull(transmitted, "transmitted");
        LOG.trace("Decoding message: {}", transmitted);
        try {
            final List<Command> commands = new ArrayList<>();
            final JsonArray array = (JsonArray) new JsonParser().parse(transmitted);
            for (final JsonElement jsonElement : array) {
                final JsonObject command = (JsonObject) jsonElement;
                final JsonPrimitive idElement = command.getAsJsonPrimitive("id");
                if (idElement == null) {
                    throw new RuntimeException("Can not encode command without id!");
                }
                String id = idElement.getAsString();
                LOG.trace("Decoding command: {}", id);
                final CommandEncoder<?> encoder = DECODERS.get(id);
                if (encoder == null) {
                    throw new RuntimeException("Can not encode command of type " + id + ". No matching encoder found!");
                }
                commands.add(encoder.decode(command));
            }
            LOG.trace("Decoded command list with {} commands", commands.size());
            return commands;
        } catch (Exception ex) {
            throw new JsonParseException("Illegal JSON detected", ex);
        }
    }

    public static OptimizedJsonCodec getInstance() {
        return INSTANCE;
    }
}
