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

package com.leavey.mahjong.engine.executor;

import com.leavey.mahjong.engine.bean.Action;
import com.leavey.mahjong.engine.bean.ActionRequest;
import com.leavey.mahjong.engine.bean.Game;
import com.leavey.mahjong.engine.bean.Operation;

/**
 * @author Leavey
 */
public class PlayExecutor implements Executor {
    @Override
    public Action supportAction() {
        return Action.PLAY;
    }

    @Override
    public boolean validate(Game game, int player) {
        //自身摸牌、吃牌、碰牌后，允许打牌
        Action action = game.getPrevAction();
        return game.isFocus(player) && (action == Action.DRAW || action == Action.EAT || action == Action.PEN);
    }

    @Override
    public boolean validate(Game game, ActionRequest actionRequest) {
        //当前允许打出牌，且数量1张
        return validate(game, actionRequest.getPlayer()) && actionRequest.getKeyTiles().size() == 1;
    }

    @Override
    public Operation execute(Game game, ActionRequest actionRequest) {
        game.play(actionRequest.getPlayer(), actionRequest.getKeyTiles().get(0));
        return new Operation(actionRequest.getPlayer(), actionRequest.getAction(), actionRequest.getKeyTiles());
    }
}
