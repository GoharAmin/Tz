package com.gohar_amin.tz.model

import com.google.firebase.firestore.Exclude


class Chat {
    constructor(senderId: String?, receiverId: String?, time: String?, messageTypeId: Int) {
        this.senderId = senderId
        this.receiverId = receiverId
        this.time = time
        this.messageTypeId = messageTypeId
    }

    constructor() {}

    var messageTypeId = 0

    @Exclude
    var isReceiver = 0
    var userName: String? = null
    var imageUrl: String? = null
    var profile: String? = null
    var time: String? = null
    var text: String? = null
    var senderId: String? = null
    var receiverId: String? = null
}
