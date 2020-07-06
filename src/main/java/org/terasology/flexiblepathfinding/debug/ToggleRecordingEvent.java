// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.flexiblepathfinding.debug;

import org.terasology.input.BindButtonEvent;
import org.terasology.input.RegisterBindButton;

@RegisterBindButton(id = "toggle_recording", description = "Toggle metric recording", category = "flexiblepathfinding")
public class ToggleRecordingEvent extends BindButtonEvent {
}
