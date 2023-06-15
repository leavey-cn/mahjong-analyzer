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

package com.leavey.mahjong.api.vue.vo;

import com.leavey.mahjong.common.bean.Tile;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Leavey
 */
@Data
@NoArgsConstructor
public class TileVo {

    public static final String VIEW = "view";
    public static final String VIEW_AMOUNT = "view-amount";
    public static final String INCR_AMOUNT = "incr-amount";

    private int code;
    private String behavior;
    private int amount;

    public TileVo(int code, String behavior, int amount) {
        this.code = code;
        this.behavior = behavior;
        this.amount = amount;
    }

    public static TileVo view(Tile tile) {
        return new TileVo(tile.getCode(), VIEW, 0);
    }

    public static TileVo viewAmount(Tile tile, int amount) {
        return new TileVo(tile.getCode(), VIEW_AMOUNT, amount);
    }

    public static TileVo incrAmount(Tile tile) {
        return new TileVo(tile.getCode(), INCR_AMOUNT, 0);
    }
}
