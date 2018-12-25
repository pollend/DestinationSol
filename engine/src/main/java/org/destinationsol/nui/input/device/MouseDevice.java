/*
 * Copyright 2018 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.destinationsol.nui.input.device;

import com.badlogic.gdx.math.Vector2;
import org.terasology.module.sandbox.API;

import java.util.Queue;

/**
 */
@API
public interface MouseDevice extends InputDevice {

    @Override
    Queue<MouseAction> getInputQueue();

    /**
     * @return The current position of the mouse in screen space
     */
    Vector2 getPosition();

    /**
     * @return The change in mouse position over the last update
     */
    Vector2 getDelta();

    /**
     * @param button
     * @return The current state of the given button
     */
    boolean isButtonDown(int button);

    /**
     * @return Whether the mouse cursor is visible
     */
    boolean isVisible();

    /**
     * Specifies if the mouse is grabbed and there is thus no mouse cursor that can get to a border.
     */
    void setGrabbed(boolean grabbed);
}
