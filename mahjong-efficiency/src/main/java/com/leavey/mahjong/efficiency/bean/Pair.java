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

import java.util.Arrays;
import java.util.Comparator;

/**
 * 搭子
 *
 * @author Leavey
 */
public class Pair {
    private final Tile[] tiles;

    public Pair(Tile t1, Tile t2) {
        this.tiles = new Tile[]{t1, t2};
        Arrays.sort(tiles, Comparator.comparingInt(Tile::getCode));
    }

    public Pair(Tile t1, Tile t2, Tile t3) {
        this.tiles = new Tile[]{t1, t2, t3};
        Arrays.sort(tiles, Comparator.comparingInt(Tile::getCode));
    }
}
