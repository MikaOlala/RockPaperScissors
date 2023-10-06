package com.mikaela.sps


class Room {
    var name: String = ""
    var scoreFirst: Int = 0
    var scoreSecond: Int = 0
    var scoreDraw: Int = 0
    var choiceFirst: String = Ius.statusOffline
    var choiceSecond: String = Ius.statusOffline

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

    fun setMyChoice(newChoice: String, isMyGame: Boolean) {
        if (isMyGame)
            this.choiceFirst = newChoice
        else
            this.choiceSecond = newChoice
    }
    fun getMyChoice(isMyGame: Boolean): String{
        return if (isMyGame)
            choiceFirst
        else
            choiceSecond
    }
    fun setEnemyChoice(newChoice: String, isMyGame: Boolean) {
        if (isMyGame)
            this.choiceSecond = newChoice
        else
            this.choiceFirst = newChoice
    }
    fun getEnemyChoice(isMyGame: Boolean): String{
        return if (isMyGame)
            choiceSecond
        else
            choiceFirst
    }

}

