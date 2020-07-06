// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.flexiblepathfinding.debug;

import org.terasology.input.BindButtonEvent;
import org.terasology.input.DefaultBinding;
import org.terasology.input.InputType;
import org.terasology.input.Keyboard;
import org.terasology.input.RegisterBindButton;

@RegisterBindButton(id = "toggle_debug_ui", description = "Toggle Debug UI", category = "flexiblepathfinding")
@DefaultBinding(type = InputType.KEY, id = Keyboard.KeyId.F7)
public class ToggleDebugScreenEvent extends BindButtonEvent {
}
