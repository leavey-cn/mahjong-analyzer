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

import lombok.Data;

import java.util.List;

/**
 * @author Leavey
 */
@Data
public class Operation {
    private final Action action;
    private final int player;
    private Operation prev;
    private List<Tile> keyTiles;
    /**
     * 操作涉及到的暗牌数量
     */
    private int darkTileAmount;

    private EatPosition eatPosition;

    //标识是否暗杠
    private Boolean dark;
    //是否自摸
    private Boolean self;

    public Operation(int player, Action action) {
        this.player = player;
        this.action = action;
    }

    public Operation(int player, Action action, List<Tile> keyTiles) {
        this.action = action;
        this.player = player;
        this.keyTiles = keyTiles;
    }

    public Action getAction() {
        return action;
    }

    public int getPlayer() {
        return player;
    }

    public void setPrev(Operation prev) {
        this.prev = prev;
    }
}
