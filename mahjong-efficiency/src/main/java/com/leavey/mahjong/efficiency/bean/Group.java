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
import lombok.Getter;

/**
 * 一句话
 *
 * @author Leavey
 */
@Getter
public class Group {
    /**
     * 一句话中第一张牌
     */
    private final Tile tile;
    /**
     * 是否为1坎
     */
    private final boolean isSame;

    public Group(Tile tile, boolean isSame) {
        this.tile = tile;
        this.isSame = isSame;
    }
    public Tile getTile() {
        return tile;
    }
}
