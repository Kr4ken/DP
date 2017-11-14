package com.kr4ken.dp.models.hooks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.julienvey.trello.domain.Board;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.TList;


/**
 * Данные о действиях которые приходят в хуке трелло
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrelloActionDataHook {
    private String id;
    private Board board;
    private TList list;
    private Card card;

    public TrelloActionDataHook() {
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public TList getList() {
        return list;
    }

    public void setList(TList list) {
        this.list = list;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }
}
