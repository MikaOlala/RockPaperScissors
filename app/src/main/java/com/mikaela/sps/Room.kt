package com.mikaela.sps


class Room {
    var name: String = ""
    var scoreFirst: Int = 0
    var scoreSecond: Int = 0
    var scoreDraw: Int = 0
    var choiceFirst: String = Ius.choiceWaiting
    var choiceSecond: String = Ius.choiceWaiting

    constructor(
        name: String,
        scoreFirst: Int,
        scoreSecond: Int,
        scoreDraw: Int,
        choiceFirst: String,
        choiceSecond: String
    ) {
        this.name = name
        this.scoreFirst = scoreFirst
        this.scoreSecond = scoreSecond
        this.scoreDraw = scoreDraw
        this.choiceFirst = choiceFirst
        this.choiceSecond = choiceSecond
    }

    constructor()

}

