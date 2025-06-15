package net.bxx2004.netzach.core.threads


class Task(val name: String, val action: (listener: TaskListener) -> Unit)
class TaskListener(
    var current: String = "未知",
    var isDone: Boolean = false,
    var progress: Float = 0.0f
) {
    fun setTask(name: String) {
        progress = 0.0f
        current = name
    }
}
fun runTask(task: List<Task>): TaskListener {
    val listener = TaskListener()
    submit(0,-1){
        task.forEach {
            if (listener.isDone) {
                return@forEach
            }
            listener.setTask(it.name)
            it.action(listener)
        }
        listener.isDone = true
    }
    return listener
}
fun runTask(task:Task):TaskListener{
    val listener = TaskListener()
    submit(0,-1){
        listener.setTask(task.name)
        task.action(listener)
        listener.isDone = true
    }
    return listener
}