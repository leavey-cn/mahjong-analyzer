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
public class ActionRequest {
    private final int player;
    private final Action action;
    /**
     * 操作涉及到的明牌
     */
    private List<Tile> keyTiles;
    /**
     * 吃牌的位置
     */
    private EatPosition eatPosition;

    public ActionRequest(int player, Action action) {
        this.player = player;
        this.action = action;
    }

    public ActionRequest(int player, Action action, List<Tile> keyTiles) {
        this.player = player;
        this.action = action;
        this.keyTiles = keyTiles;
    }


    public ActionRequest(int player, Action action, Tile keyTile, EatPosition eatPosition) {
        this.player = player;
        this.action = action;
        this.keyTiles = List.of(keyTile);
        this.eatPosition = eatPosition;
    }

}
