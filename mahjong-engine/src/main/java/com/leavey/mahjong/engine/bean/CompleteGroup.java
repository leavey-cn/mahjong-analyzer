/*
 * Copyright 2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.leavey.mahjong.engine.bean;

import com.leavey.mahjong.common.bean.Tile;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Leavey
 */
public class CompleteGroup implements DeepCopy<CompleteGroup> {
    private final List<Tile> tiles;
    private boolean dark;

    public CompleteGroup(List<Tile> tiles) {
        this.tiles = tiles;
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    @Override
    public CompleteGroup deepCopy() {
        return new CompleteGroup(new ArrayList<>(tiles));
    }


    public void setDark(boolean dark) {
        this.dark = dark;
    }

    public boolean isDark() {
        return dark;
    }
}
