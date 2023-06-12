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
public class DrawExecutor implements Executor {
    @Override
    public Action supportAction() {
        return Action.DRAW;
    }

    @Override
    public boolean validate(Game game, int player) {
        //牌局刚开始，任意一家都可作为庄家摸牌
        if (game.focusNotExist()) {
            return true;
        }
        //上家打出牌，作为下家可以开始摸牌
        return game.getPrevAction() == Action.PLAY && game.isFocusNext(player);
    }

    @Override
    public boolean validate(Game game, ActionRequest actionRequest) {
        //当前允许摸牌
        return validate(game, actionRequest.getPlayer());
    }

    @Override
    public Operation execute(Game game, ActionRequest actionRequest) {
        game.draw(actionRequest.getPlayer());
        return new Operation(actionRequest.getPlayer(), actionRequest.getAction());
    }
}
