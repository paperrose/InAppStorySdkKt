package com.inappstory.sdk.utils.json


import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.createType
import kotlin.reflect.full.declaredMembers
import kotlin.reflect.full.hasAnnotation

class JsonParser {


    private fun <T : Any> jsonArrayToPOJO(array: JSONArray?, ctype: KClass<T>): ArrayList<T> {
        val res = ArrayList<T>()
        if (array == null) return res
        val lst = arrayListOf<T>()
        when (ctype) {
            Int::class.createType(nullable = true).classifier,
            Int::class.createType().classifier,
            Long::class.createType(nullable = true).classifier,
            Long::class.createType().classifier,
            String::class.createType(nullable = true).classifier,
            String::class.createType().classifier,
            Double::class.createType(nullable = true).classifier,
            Double::class.createType().classifier,
            Float::class.createType(nullable = true).classifier,
            Float::class.createType().classifier,
            Boolean::class.createType(nullable = true).classifier,
            Boolean::class.createType().classifier -> {
                for (i in 0 until array.length()) {
                    lst.add(array[i] as T)
                }
                return lst
            }
            else -> {
                for (i in 0 until array.length()) {
                    if (array[i] is JSONObject) {
                        val obj = jsonObjectToPOJO(array[i] as JSONObject, ctype as KClass<*>);
                        if (obj != null)
                            lst.add(obj as T)
                    }
                }
                return lst
            }
        }
    }

    private fun <T : Any> jsonObjectToPOJO(jsonObject: JSONObject?, typeOfT: KClass<T>): T? {
        val res = typeOfT.createInstance()
        if (jsonObject == null) return null
        var properties: ArrayList<KProperty<*>> = ArrayList()
        typeOfT.declaredMembers.forEach {
            if (it is KProperty) {
                properties.add(it)
            }
        }
        typeOfT.supertypes.forEach {
            (it.classifier as KClass<*>).declaredMembers.forEach { subIt ->
                if (subIt is KProperty) {
                    properties.add(subIt)
                }
            }
        }
        properties.forEach {
            if (it.hasAnnotation<Ignore>()) return@forEach
            var fieldName: String = it.name
            if (it.hasAnnotation<SerializedName>()) {
                for (annotate in it.annotations) {
                    if (annotate is SerializedName)
                        fieldName = annotate.value
                }
            }
            if (it is KMutableProperty<*>) {
                if (it.returnType.isMarkedNullable && jsonObject.isNull(fieldName)) {
                    it.setter.call(res, null)
                } else {
                    when (it.returnType) {
                        Int::class.createType(nullable = true),
                        Int::class.createType() -> {
                            val c0 = jsonObject.optInt(fieldName)
                            it.setter.call(res, c0)
                        }
                        Long::class.createType(nullable = true),
                        Long::class.createType() -> {
                            val c0 = jsonObject.optLong(fieldName)
                            it.setter.call(res, c0)
                        }
                        String::class.createType(nullable = true),
                        String::class.createType() -> {
                            val c0 = jsonObject.optString(fieldName)
                            if (!c0.equals("null"))
                                it.setter.call(res, c0)
                        }
                        Double::class.createType(nullable = true),
                        Double::class.createType() -> {
                            val c0 = jsonObject.optDouble(fieldName)
                            it.setter.call(res, c0)
                        }
                        Float::class.createType(nullable = true),
                        Float::class.createType() -> {
                            val c0 = jsonObject.optDouble(fieldName)
                            it.setter.call(res, c0.toFloat())
                        }
                        Boolean::class.createType(nullable = true),
                        Boolean::class.createType() -> {
                            val c0 = jsonObject.optBoolean(fieldName)
                            it.setter.call(res, c0)
                        }
                        else -> {
                            if (it.returnType.classifier == ArrayList::class) {
                                val ctype = it.returnType.arguments[0].type?.classifier
                                val array: JSONArray? = jsonObject.optJSONArray(fieldName)
                                it.setter.call(res, jsonArrayToPOJO(array, ctype as KClass<*>))
                            } else {
                                it.setter.call(
                                    res, jsonObjectToPOJO(
                                        jsonObject.optJSONObject(fieldName),
                                        it.returnType.classifier
                                                as KClass<*>
                                    )
                                )
                            }
                        }
                    }
                }
            }

        }
        return res
    }

    private fun getJsonObject(instance: Any?): Any? {
        if (instance == null) return null
        val type = instance::class
        when (type) {
            Int::class,
            Long::class,
            Float::class,
            Boolean::class,
            String::class,
            Double::class -> {
                return instance
            }
            List::class,
            ArrayList::class -> {
                val jsonArray = JSONArray()
                (instance as List<*>).forEach {
                    jsonArray.put(getJsonObject(it))
                }
                return jsonArray
            }
            else -> {
                val jsonObject = JSONObject()
                val properties: ArrayList<KMutableProperty<*>> = ArrayList()
                instance::class.declaredMembers.forEach {
                    if (it is KMutableProperty<*>) {
                        properties.add(it)
                    }
                }
                instance::class.supertypes.forEach {
                    (it.classifier as KClass<*>).declaredMembers.forEach { subIt ->
                        if (subIt is KMutableProperty<*>) {
                            properties.add(subIt)
                        }
                    }
                }
                properties.forEach {
                    var fieldName: String = it.name
                    if (it.hasAnnotation<SerializedName>()) {
                        for (annotate in it.annotations) {
                            if (annotate is SerializedName)
                                fieldName = annotate.value
                        }
                    }
                    jsonObject.put(fieldName, getJsonObject(it.getter.call(instance)))
                }
                return jsonObject
            }
        }
    }

    fun jsonToPOJO(jsonString: String?, ctype: KClass<*>, isList: Boolean): Any? {
        if (jsonString == null) return null
        val jsonT = JSONTokener(jsonString)
        val obj = jsonT.nextValue()
        if (isList) {
            if (obj != null && obj is JSONArray) {
                return jsonArrayToPOJO(obj, ctype)
            }
        } else {
            if (obj != null) {
                return if (obj is JSONObject)
                    jsonObjectToPOJO(obj, ctype)
                else
                    obj
            }
        }
        return null
    }

    fun getJson(instance: Any?): String? {
        return if (instance is List<*> || instance is ArrayList<*>) {
            val arr = JSONArray()
            (instance as ArrayList<*>).forEach {
                arr.put(getJsonObject(it))
            }
            arr.toString()
        } else {
            (getJsonObject(instance) as JSONObject).toString()
        }
    }


}