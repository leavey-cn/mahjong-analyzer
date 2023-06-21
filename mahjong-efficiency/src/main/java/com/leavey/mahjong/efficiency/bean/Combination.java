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

package com.leavey.mahjong.efficiency.bean;

import com.leavey.mahjong.common.bean.Tile;

import java.util.*;

/**
 * @author Leavey
 */
public class Combination {
    private final Tile[] tiles;

    public Combination(List<Tile> tiles) {
        this.tiles = tiles.toArray(Tile[]::new);
        Arrays.sort(this.tiles, Tile::compareTo);
    }

    public Combination(Tile t1, Tile t2) {
        this(List.of(t1, t2));
    }

    public Combination(Tile t1, Tile t2, Tile t3) {
        this(List.of(t1, t2, t3));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Combination that = (Combination) o;
        return Arrays.equals(tiles, that.tiles);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(tiles);
    }

    @Override
    public String toString() {
        return "Combination{" +
                "tiles=" + Arrays.toString(tiles) +
                '}';
    }
}
