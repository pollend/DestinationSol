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
package org.destinationsol.game.sound;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.common.Nullable;
import org.destinationsol.game.GameDrawer;
import org.destinationsol.game.SolCam;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DebugHintDrawer {
    private final Map<SolObject, DebugHint> myTracedNotes;
    private final Map<Vector2, DebugHint> myFreeNotes;

    private final SolCam solCam;

    public DebugHintDrawer(SolCam solCam) {
        this.solCam = solCam;
        myTracedNotes = new HashMap<>();
        myFreeNotes = new HashMap<>();
    }

    public void add(@Nullable SolObject owner, Vector2 position, String value) {
        DebugHint dh;
        if (owner == null) {
            dh = myFreeNotes.computeIfAbsent(position, p -> new DebugHint(solCam,null, p));
        } else {
            dh = myTracedNotes.computeIfAbsent(owner, o -> new DebugHint(solCam,o, o.getPosition()));
        }
        dh.add(value);
    }

    public void update() {
        updateEach(myTracedNotes.values().iterator());
        updateEach( myFreeNotes.values().iterator());
    }

    private void updateEach( Iterator<DebugHint> it) {
        while (it.hasNext()) {
            DebugHint n = it.next();
            n.update();
            if (n.shouldRemove()) {
                it.remove();
            }
        }
    }

    public void draw(GameDrawer drawer) {
        for (DebugHint n : myTracedNotes.values()) {
            n.draw(drawer);
        }
        for (DebugHint n : myFreeNotes.values()) {
            n.draw(drawer);
        }
    }

}
