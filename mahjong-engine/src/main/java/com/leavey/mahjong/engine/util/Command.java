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

package com.leavey.mahjong.engine.util;

import com.leavey.mahjong.engine.bean.*;
import com.leavey.mahjong.engine.factory.GameFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Leavey
 */
public class Command {
    private Integer player;
    private final String action;
    private final List<Tile> tiles;

    public Command(Integer player, String action, List<Tile> tiles) {
        this.player = player;
        this.action = action;
        this.tiles = tiles;
    }

    public Game execute(long gameId) {
        Game game = GameFactory.get(gameId);
        if (player == null) {
            if (game.getFocus() == null) {
                throw new RuntimeException("当前无焦点，请指定玩家");
            }
            player = game.getFocus() + 1;
            if (player >= game.getPlayerAmount()) {
                player = 0;
            }
        }
        if ("md".equals(action)) {
            //摸打
            ActionRequest draw = new ActionRequest(player, Action.DRAW);
            GameFactory.play(game.getId(), draw);
            return GameFactory.play(game.getId(), new ActionRequest(player, Action.PLAY, List.of(tiles.get(0))));
        } else if (action.startsWith("cd")) {
            //吃打
            Tile eatTile;
            Tile playTile;
            if (tiles.size() == 1) {
                //上一手打出的牌
                eatTile = game.getPrevOperation().getKeyTiles().get(0);
                playTile = tiles.get(0);
            } else {
                eatTile = tiles.get(0);
                playTile = tiles.get(1);
            }
            ActionRequest eatAction = new ActionRequest(player, Action.EAT, List.of(eatTile));
            eatAction.setEatPosition(parseEatPosition(action.charAt(2)));
            GameFactory.play(game.getId(), eatAction);
            return GameFactory.play(game.getId(), new ActionRequest(player, Action.PLAY, List.of(playTile)));
        } else if (action.equals("pd")) {
            //碰打
            Tile penTile;
            Tile playTile;
            if (tiles.size() == 1) {
                //上一手打出的牌
                penTile = game.getPrevOperation().getKeyTiles().get(0);
                playTile = tiles.get(0);
            } else {
                penTile = tiles.get(0);
                playTile = tiles.get(1);
            }
            GameFactory.play(game.getId(), new ActionRequest(player, Action.PEN, List.of(penTile)));
            return GameFactory.play(game.getId(), new ActionRequest(player, Action.PLAY, List.of(playTile)));
        } else if ("m".equals(action)) {
            //摸
            return GameFactory.play(game.getId(), new ActionRequest(player, Action.DRAW));
        } else if ("c".startsWith(action)) {
            //吃
            ActionRequest eatAction = new ActionRequest(player, Action.EAT, List.of(tiles.get(0)));
            eatAction.setEatPosition(parseEatPosition(action.charAt(1)));
            return GameFactory.play(game.getId(), eatAction);
        } else if ("p".equals(action)) {
            //碰
            return GameFactory.play(game.getId(), new ActionRequest(player, Action.PEN, List.of(tiles.get(0))));
        } else {
            throw new RuntimeException("不支持的命令：" + action);
        }
    }

    private EatPosition parseEatPosition(char c) {
        if (c == 'l') {
            return EatPosition.LEFT;
        } else if (c == 'm') {
            return EatPosition.MIDDLE;
        } else if (c == 'r') {
            return EatPosition.RIGHT;
        }
        throw new RuntimeException("未知的吃牌位置");
    }

    public static Command parse(String command) {
        System.out.println("命令：" + command);
        Integer player = null;
        try {
            player = Integer.parseInt(command.substring(0, 1));
            command = command.substring(1);
        } catch (NumberFormatException ignored) {
        }
        List<String> list = Arrays.stream(command.split(" ")).collect(Collectors.toList());
        String action = list.remove(0);
        List<Tile> tiles = list.stream().map(Matrix::parse).collect(Collectors.toList());
        return new Command(player, action, tiles);
    }
}