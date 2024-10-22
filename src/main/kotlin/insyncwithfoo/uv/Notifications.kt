package insyncwithfoo.uv

import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction.createSimpleExpiring
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.notification.NotificationsManager
import com.intellij.openapi.project.Project
import insyncwithfoo.uv.icons.UVIcon


private const val ID = "uv notifications"
private val ICON = UVIcon.SMALL_SIMPLIFIED


internal val uvNotificationGroup: NotificationGroup
    get() {
        val groupManager = NotificationGroupManager.getInstance()
        return groupManager.getNotificationGroup(ID)
    }


internal fun Notification.prettify() = apply {
    isImportant = false
    icon = ICON
}


internal fun Notification.addSimpleExpiringAction(text: String, action: () -> Unit) =
    addAction(createSimpleExpiring(text, action))


internal fun Notification.runThenNotify(project: Project, action: Notification.() -> Unit) {
    run(action)
    notify(project)
}


internal fun NotificationGroup.createErrorNotification(title: String, content: String) =
    createNotification(title, content, NotificationType.ERROR)


internal val Project.openingUVNotifications: List<Notification>
    get() = NotificationsManager.getNotificationsManager()
        .getNotificationsOfType(Notification::class.java, this)
        .filter { it.groupId == ID }
