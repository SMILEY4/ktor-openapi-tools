package io.github.smiley4.ktoropenapi.config

import io.swagger.v3.oas.models.media.Schema
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Describes and identifies types and schemas.
 */
sealed interface TypeDescriptor


/**
 * Describes a type from a swagger [Schema]
 */
class SwaggerTypeDescriptor(val schema: Schema<*>) : TypeDescriptor


/**
 * Describes a type from a kotlin [KType]
 */
class KTypeDescriptor(val type: KType) : TypeDescriptor

/**
 * Describes a type from a kotlinx-serialization [SerialDescriptor]
 */
class SerialTypeDescriptor(val descriptor: SerialDescriptor) : TypeDescriptor

/**
 * Describes an array of types.
 */
class ArrayTypeDescriptor(val type: TypeDescriptor) : TypeDescriptor

/**
 * Describes an object matching any of the given types.
 */
class AnyOfTypeDescriptor(val types: List<TypeDescriptor>) : TypeDescriptor

/**
 * Describes an empty type/schema.
 */
class EmptyTypeDescriptor : TypeDescriptor

/**
 * Describes a reference to a schema in the component section.
 */
class RefTypeDescriptor(val schemaId: String) : TypeDescriptor

inline fun <reified T> type() = KTypeDescriptor(typeOf<T>())

fun empty() = EmptyTypeDescriptor()

fun ref(schemaId: String) = RefTypeDescriptor(schemaId)

fun array(type: TypeDescriptor) = ArrayTypeDescriptor(type)
fun array(type: Schema<*>) = ArrayTypeDescriptor(SwaggerTypeDescriptor(type))
fun array(type: KType) = ArrayTypeDescriptor(KTypeDescriptor(type))
inline fun <reified T> array() = ArrayTypeDescriptor(KTypeDescriptor(typeOf<T>()))

fun anyOf(vararg types: TypeDescriptor) = AnyOfTypeDescriptor(types.toList())
fun anyOf(types: Collection<TypeDescriptor>) = AnyOfTypeDescriptor(types.toList())

fun anyOf(vararg types: Schema<*>) = AnyOfTypeDescriptor(types.map { SwaggerTypeDescriptor(it) })
@JvmName("anyOfSwagger")
fun anyOf(types: Collection<Schema<*>>) = AnyOfTypeDescriptor(types.map { SwaggerTypeDescriptor(it) })

fun anyOf(vararg types: KType) = AnyOfTypeDescriptor(types.map { KTypeDescriptor(it) })
@JvmName("anyOfKType")
fun anyOf(types: Collection<KType>) = AnyOfTypeDescriptor(types.map { KTypeDescriptor(it) })
