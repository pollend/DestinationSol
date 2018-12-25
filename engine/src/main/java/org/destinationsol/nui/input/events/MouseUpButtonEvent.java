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
package org.destinationsol.nui.input.events;


import com.badlogic.gdx.math.Vector2;
import org.destinationsol.nui.input.ButtonState;
import org.destinationsol.nui.input.MouseInput;

public class MouseUpButtonEvent extends MouseButtonEvent {

    private static MouseUpButtonEvent event = new MouseUpButtonEvent(MouseInput.NONE, 0);

    protected MouseUpButtonEvent(MouseInput button, float delta) {
        super(button, ButtonState.UP, delta);
    }

    public static MouseUpButtonEvent create(MouseInput button, Vector2 position, float delta) {
        event.reset(delta);
        event.setButton(button);
        event.setMousePosition(position);
        return event;
    }


}
