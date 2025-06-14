package isel.leic.group25.notifications.interfaces

import isel.leic.group25.notifications.model.ExpoNotification


interface Notifiable {
    fun toNotification(): ExpoNotification
}