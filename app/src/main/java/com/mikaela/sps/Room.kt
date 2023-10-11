package com.mikaela.sps


class Room {
    var name: String = ""
    var idFirst: String = ""
    var idSecond: String = ""


    constructor()
    constructor(name: String, idFirst: String, idSecond: String) {
        this.name = name
        this.idFirst = idFirst
        this.idSecond = idSecond
    }

//    fun setMyChoice(newChoice: String, isMyGame: Boolean) {
//        if (isMyGame)
//            this.choiceFirst = newChoice
//        else
//            this.choiceSecond = newChoice
//    }
//    fun getMyChoice(isMyGame: Boolean): String{
//        return if (isMyGame)
//            choiceFirst
//        else
//            choiceSecond
//    }
//    fun setEnemyChoice(newChoice: String, isMyGame: Boolean) {
//        if (isMyGame)
//            this.choiceSecond = newChoice
//        else
//            this.choiceFirst = newChoice
//    }
//    fun getEnemyChoice(isMyGame: Boolean): String{
//        return if (isMyGame)
//            choiceSecond
//        else
//            choiceFirst
//    }

}

