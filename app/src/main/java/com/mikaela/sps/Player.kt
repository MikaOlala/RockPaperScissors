package com.mikaela.sps

import java.util.*

class Player{
    var id: String = ""
    var score: Int = 0
    var choice: String = Ius.statusChoosing
    var isOnline: Boolean = false
    var gameConnectedTo: String = ""
    var gameOwner: Boolean = false
    var enemyId: String = ""
    var lastConnection: String = ""

    var imgRock: String = ""
    var imgPaper: String = ""
    var imgScissors: String = ""



    constructor()
    constructor(
        id: String,
        score: Int,
        choice: String,
        isOnline: Boolean,
        gameConnectedTo: String,
        gameOwner: Boolean,
        enemyId: String,
        lastConnection: String,
        imgRock: String,
        imgPaper: String,
        imgScissors: String
    ) {
        this.id = id
        this.score = score
        this.choice = choice
        this.isOnline = isOnline
        this.gameConnectedTo = gameConnectedTo
        this.gameOwner = gameOwner
        this.enemyId = enemyId
        this.lastConnection = lastConnection
        this.imgRock = imgRock
        this.imgPaper = imgPaper
        this.imgScissors = imgScissors
    }


}
