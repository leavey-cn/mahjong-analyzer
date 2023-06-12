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

package com.leavey.mahjong.engine.factory;

import com.leavey.mahjong.engine.bean.Action;
import com.leavey.mahjong.engine.bean.ActionRequest;
import com.leavey.mahjong.engine.bean.Game;
import com.leavey.mahjong.engine.executor.*;
import com.leavey.mahjong.engine.rule.Rule;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Leavey
 */
public class GameFactory {

    private static Map<Long, LinkedList<Game>> REPOSITORY = new HashMap<>();

    public static Game newGame(Rule rule) {
        long id = System.currentTimeMillis();
        Game game = new Game(id, rule);
        LinkedList<Game> games = new LinkedList<>();
        REPOSITORY.put(id, games);
        games.addLast(game);
        return game;
    }

    public static Game get(long id) {
        return REPOSITORY.get(id).peekLast();
    }

    public static Game play(long id, ActionRequest actionRequest) {
        Game game = REPOSITORY.get(id).peekLast();
        Game nextGame = game.deepCopy();
        nextGame.play(actionRequest);
        REPOSITORY.get(id).addLast(nextGame);
        return nextGame;
    }

    public static Game backoff(long id) {
        LinkedList<Game> games = REPOSITORY.get(id);
        Game game = games.pollLast();
        Game prevGame = games.peekLast();
        if (prevGame == null) {
            games.addLast(game);
            return game;
        } else {
            return prevGame;
        }
    }

    public static Map<Action, Executor> defaultExecutorMap() {
        List<Executor> executors = new ArrayList<>();
        executors.add(new DrawExecutor());
        executors.add(new EatExecutor());
        executors.add(new GangDrawExecutor());
        executors.add(new GangExecutor());
        executors.add(new GangPlayExecutor());
        executors.add(new PenExecutor());
        executors.add(new PlayExecutor());
        executors.add(new WinExecutor());
        executors.add(new ShowExecutor());
        return executors.stream().collect(Collectors.toMap(Executor::supportAction, Function.identity()));
    }

}
