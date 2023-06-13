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


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Leavey
 */
public class Player implements DeepCopy<Player> {
// shuffle洗牌，cut切牌，deal发牌，sort理牌，draw摸牌，play打出，discard弃牌

    /**
     * 手牌
     */
    private final HandTiles hand;
    /**
     * 吃、碰、杠
     */
    private final List<CompleteGroup> completeGroups;
    /**
     * 打出的牌
     */
    private final List<Tile> playTiles;

    /**
     * 开杠摸上来的牌数量
     */
    private int gangDrawAmount;

    /**
     * @param darkAmount 手牌开局数量
     */
    public Player(int darkAmount) {
        this.hand = new HandTiles(darkAmount);
        this.completeGroups = new ArrayList<>();
        this.playTiles = new ArrayList<>();
        this.gangDrawAmount = 0;
    }


    private Player(HandTiles hand, List<CompleteGroup> completeGroups, List<Tile> playTiles, int gangDrawAmount) {
        this.hand = hand;
        this.completeGroups = completeGroups;
        this.playTiles = playTiles;
        this.gangDrawAmount = gangDrawAmount;
    }

    @Override
    public Player deepCopy() {
        return new Player(hand.deepCopy(), completeGroups.stream().map(CompleteGroup::deepCopy).collect(Collectors.toList()), new ArrayList<>(playTiles), gangDrawAmount);
    }

    public void draw() {
        hand.incrDarkAmount(1);
    }
    public void draw(Tile tile){
        hand.add(tile);
    }

    public void play(Tile tile) {
        //手牌中打出一张
        hand.play(tile);
        //牌堆中增加一张
        playTiles.add(tile);
    }

    /**
     * 玩家吃一张牌
     *
     * @param tile     吃的牌
     * @param position 吃的位置
     */
    public void eat(Tile tile, EatPosition position) {
        //手牌吃牌，完成一个组合
        CompleteGroup group = hand.eat(tile, position);
        //添加到组合展示区
        completeGroups.add(group);
    }

    /**
     * 玩家碰一张牌
     *
     * @param tile 碰的牌
     */
    public void pen(Tile tile) {
        //手牌碰牌，完成一个组合，添加到组合展示区
        completeGroups.add(hand.pen(tile));
    }

    /**
     * 开杠后摸上一定数量的暗牌
     *
     * @param amount 数量
     */
    public void gangDraw(int amount) {
        if (this.gangDrawAmount != 0) {
            throw new IllegalStateException("数据异常，当前手中的杠牌不为空");
        }
        this.gangDrawAmount = amount;
    }

    /**
     * 开杠后打出杠上来的牌
     *
     * @param tiles 打出的牌
     */
    public void gangPlay(List<Tile> tiles) {
        if (this.gangDrawAmount != tiles.size()) {
            throw new IllegalStateException("数据异常，当前手中的杠牌数量和要打出的不一致");
        }
        this.gangDrawAmount = 0;
        //打出的牌进去弃牌区
        playTiles.addAll(tiles);
    }

    /**
     * 杠牌
     *
     * @param tile    牌
     * @param dark    是否暗杠
     * @param outside 要杠的牌从外部获取还是内部的
     *                明杠的牌、暗杠自身上次杠出的牌 是外部获取
     *                其他暗杠的牌 都是内部获取
     */
    public void gang(Tile tile, boolean dark, boolean outside) {
        CompleteGroup group = hand.gang(tile, outside);
        group.setDark(dark);
        completeGroups.add(group);
    }

    /**
     * 胡牌
     *
     * @param tile    牌
     * @param outsize 牌是否外部的
     */
    public void win(Tile tile, boolean outsize) {
        if (outsize) hand.add(tile);
    }

    /**
     * 添加若干张明牌
     *
     * @param tiles 明牌
     */
    public void show(List<Tile> tiles) {
        hand.show(tiles);
    }

    public List<CompleteGroup> getCompleteGroups() {
        return completeGroups.stream().map(CompleteGroup::deepCopy).collect(Collectors.toList());
    }

    public List<Tile> getPlayTiles() {
        return new ArrayList<>(playTiles);
    }

    public HandTiles getHand() {
        return hand.deepCopy();
    }
}
