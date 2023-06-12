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
import com.leavey.mahjong.engine.util.PositionUtil;

/**
 * @author Leavey
 */
public class PenExecutor implements Executor {
    @Override
    public Action supportAction() {
        return Action.PEN;
    }

    @Override
    public boolean validate(Game game, int player) {
        //其他家打出牌|| 其他家开杠后打出的牌
        Action prevAction = game.getPrevAction();
        return (prevAction == Action.PLAY || prevAction == Action.GANG_PLAY) && !game.isFocus(player);
    }

    @Override
    public boolean validate(Game game, ActionRequest actionRequest) {
        //允许碰牌 && 要碰牌数量=1
        return validate(game, actionRequest.getPlayer()) && actionRequest.getKeyTiles().size() == 1;
    }

    @Override
    public Operation execute(Game game, ActionRequest actionRequest) {
        game.pen(actionRequest.getPlayer(), actionRequest.getKeyTiles().get(0));
        return new Operation(actionRequest.getPlayer(), actionRequest.getAction(), actionRequest.getKeyTiles());
    }
}
