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
public class ShowExecutor implements Executor {
    @Override
    public Action supportAction() {
        return Action.SHOW;
    }

    @Override
    public boolean validate(Game game, int player) {
        return true;
    }

    @Override
    public boolean validate(Game game, ActionRequest actionRequest) {
        return actionRequest.getKeyTiles() != null && !actionRequest.getKeyTiles().isEmpty();
    }

    @Override
    public Operation execute(Game game, ActionRequest actionRequest) {
        game.show(actionRequest.getPlayer(), actionRequest.getKeyTiles());
        return new Operation(actionRequest.getPlayer(), Action.SHOW, actionRequest.getKeyTiles());
    }
}
