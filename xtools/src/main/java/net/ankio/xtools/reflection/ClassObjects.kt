package net.ankio.xtools.reflection

/**
 * Class类反射
 */
class ClassObjects internal constructor(cls: Class<*>?) {
    private var cls: Class<*>? = null
    init {
        this.cls = cls
    }
    /**
     * 获取指定名称的字段
     */
    fun getField(name: String, obj: Any? = null): Any? {
        val field = name.let { cls!!.getField(it) }
        field.isAccessible = true
        return field.get(obj)
    }
}