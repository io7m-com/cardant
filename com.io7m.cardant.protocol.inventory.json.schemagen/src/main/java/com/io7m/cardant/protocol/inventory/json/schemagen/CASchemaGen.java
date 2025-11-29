/*
 * Copyright © 2025 Mark Raynsford <code@io7m.com> https://www.io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.cardant.protocol.inventory.json.schemagen;

import com.fasterxml.classmate.MemberResolver;
import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.ResolvedMethod;
import com.fasterxml.classmate.types.ResolvedArrayType;
import com.fasterxml.classmate.types.ResolvedInterfaceType;
import com.fasterxml.classmate.types.ResolvedObjectType;
import com.fasterxml.classmate.types.ResolvedPrimitiveType;
import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;
import com.io7m.cardant.error_codes.CAErrorCode;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1MessageType;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1TypePackageIdentifier;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1TypeRecordFieldIdentifier;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1TypeRecordIdentifier;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1UnsignedInt;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1UnsignedLong;
import com.io7m.jaffirm.core.Invariants;
import com.io7m.lanark.core.RDottedName;
import com.io7m.lanark.core.RDottedNamePatterns;
import org.joda.money.CurrencyUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

public final class CASchemaGen
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CASchemaGen.class);

  private static final TypeResolver RESOLVER =
    new TypeResolver();

  private static final JsonMapper MAPPER =
    JsonMapper.builder()
      .build();

  private final FullyResolvedType root;
  private final TreeMap<String, FullyResolvedType> types;
  private final TreeMap<String, DefinitionType> definitions;
  private ObjectNode defs;
  private ObjectNode schema;

  private CASchemaGen(
    final FullyResolvedType root)
  {
    this.root = root;
    this.types = new TreeMap<>();
    this.definitions = new TreeMap<>();
  }

  private static FullyResolvedType resolve(
    final Type type,
    final List<? extends Type> typeArguments)
  {
    final var typeArgumentsArray = new Type[typeArguments.size()];
    typeArguments.toArray(typeArgumentsArray);

    final var base =
      RESOLVER.resolve(type, typeArgumentsArray);

    final var methodResolver =
      new MemberResolver(RESOLVER);

    methodResolver.setMethodFilter(rawMethod -> {
      return Stream.of(rawMethod.getAnnotations())
        .anyMatch(annotation -> {
          return Objects.equals(
            annotation.annotationType(),
            JsonProperty.class
          );
        });
    });

    final var withMembers =
      methodResolver.resolve(
        base,
        null,
        null
      );

    final var methods =
      new TreeMap<String, ResolvedMethod>();
    final var methodsRequired =
      new TreeSet<String>();
    final var methodDescriptions =
      new TreeMap<String, String>();

    for (final var method : withMembers.getMemberMethods()) {
      final var rawMethod = method.getRawMember();
      final var annotation = rawMethod.getAnnotation(JsonProperty.class);
      Invariants.checkInvariantV(
        annotation != null,
        "Method '%s' on type '%s' must be annotated with JsonProperty",
        method.getName(),
        type
      );
      final var propertyName = annotation.value();
      if (annotation.required()) {
        methodsRequired.add(propertyName);
      }
      methods.put(propertyName, method);

      final var descAnnotation =
        rawMethod.getAnnotation(JsonPropertyDescription.class);
      if (descAnnotation != null) {
        methodDescriptions.put(propertyName, descAnnotation.value());
      }
    }

    final var subclasses = new ArrayList<FullyResolvedType>();
    final var rawSubclasses = rawSubclassesOf(type);
    for (final var subclass : rawSubclasses) {
      final var bindings =
        base.getTypeBindings().getTypeParameters();
      subclasses.add(resolve(subclass, bindings));
    }

    final var typeProperty =
      typePropertyValue(base);
    final var classDescription =
      classDescriptionOf(type);

    return new FullyResolvedType(
      base,
      methods,
      methodsRequired,
      methodDescriptions,
      subclasses,
      classDescription,
      typeProperty
    );
  }

  private static Optional<String> classDescriptionOf(
    final Type type)
  {
    return switch (type) {
      case final Class<?> clazz -> {
        final var classDescriptionAnnot =
          clazz.getAnnotation(JsonClassDescription.class);
        if (classDescriptionAnnot != null) {
          yield Optional.of(classDescriptionAnnot.value());
        }
        yield Optional.empty();
      }

      case final ResolvedInterfaceType clazz -> {
        yield classDescriptionOf(clazz.getErasedType());
      }

      case final ResolvedObjectType clazz -> {
        yield classDescriptionOf(clazz.getErasedType());
      }

      case final ResolvedArrayType clazz -> {
        yield classDescriptionOf(clazz.getErasedType());
      }

      case final ResolvedPrimitiveType clazz -> {
        yield classDescriptionOf(clazz.getErasedType());
      }

      default -> {
        throw new IllegalStateException(
          "Unable to extract description from type %s (%s)"
            .formatted(type, type.getClass())
        );
      }
    };
  }

  private static Optional<String> typePropertyValue(
    final ResolvedType type)
  {
    final var allInterfaces = new HashSet<Class<?>>();
    findAllAnnotatedInterfaces(allInterfaces, type);

    for (final var interfaceT : allInterfaces) {
      final var subtypes =
        interfaceT.getAnnotation(JsonSubTypes.class);

      for (final var subtype : subtypes.value()) {
        if (Objects.equals(subtype.value(), type.getErasedType())) {
          return Optional.of(subtype.name());
        }
      }
    }
    return Optional.empty();
  }

  private static void findAllAnnotatedInterfaces(
    final Set<Class<?>> allInterfaces,
    final ResolvedType type)
  {
    final var erased =
      type.getErasedType();
    final var subtypes =
      erased.getAnnotation(JsonSubTypes.class);

    if (subtypes != null) {
      allInterfaces.add(erased);
    }

    for (final var parent : type.getImplementedInterfaces()) {
      findAllAnnotatedInterfaces(allInterfaces, parent);
    }
  }

  private static List<Type> rawSubclassesOf(
    final Type type)
  {
    return switch (type) {
      case final Class<?> clazz -> {
        final var permitted = clazz.getPermittedSubclasses();
        if (permitted != null) {
          yield List.of(permitted);
        }
        yield List.of();
      }

      case final ResolvedInterfaceType clazz -> {
        final var rawClazz = clazz.getErasedType();
        final var permitted = rawClazz.getPermittedSubclasses();
        if (permitted != null) {
          yield List.of(permitted);
        }
        yield List.of();
      }

      case final ResolvedObjectType ignored -> {
        yield List.of();
      }

      case final ResolvedArrayType ignored -> {
        yield List.of();
      }

      case final ResolvedPrimitiveType ignored -> {
        yield List.of();
      }

      default -> {
        throw new IllegalStateException(
          "Unable to extract subclasses from type %s (%s)"
            .formatted(type, type.getClass())
        );
      }
    };
  }

  public static void main(
    final String[] args)
    throws Exception
  {
    final var output =
      Paths.get(args[0]);

    final var schema =
      new CASchemaGen(resolve(CJ1MessageType.class, List.of()))
        .run();

    try (var writer = Files.newBufferedWriter(
      output,
      StandardCharsets.UTF_8,
      CREATE,
      WRITE,
      TRUNCATE_EXISTING)) {
      writer.append(
        MAPPER.writerWithDefaultPrettyPrinter()
          .writeValueAsString(schema)
      );
      writer.flush();
    }
  }

  public ObjectNode run()
  {
    this.collectTypes(this.root);
    LOG.debug("Collected {} types.", this.types.size());
    this.createDefinitions();
    LOG.debug("Created {} definitions.", this.definitions.size());

    final var rootDef = MAPPER.createObjectNode();
    rootDef.put("$ref", this.root.refName());
    final var oneOf = MAPPER.createArrayNode();
    oneOf.add(rootDef);

    this.defs = MAPPER.createObjectNode();
    this.executeDefinitions();

    this.schema = MAPPER.createObjectNode();
    this.schema.put("$schema", "https://json-schema.org/draft/2020-12/schema");
    this.schema.put("$id", "urn:com.io7m.cardant.inventory:1.0");
    this.schema.put("title", "Cardant Inventory 1.0");
    this.schema.set("oneOf", oneOf);
    this.schema.set("$defs", this.defs);
    return this.schema;
  }

  private void executeDefinitions()
  {
    for (final var name : this.definitions.keySet()) {
      final var definition = this.definitions.get(name);
      this.defs.set(name, definition.execute());
    }
  }

  private void createDefinitions()
  {
    this.createDefinitionsCustom();

    for (final var name : this.types.keySet()) {
      if (this.definitions.containsKey(name)) {
        continue;
      }

      final var type = this.types.get(name);
      this.definitions.put(name, this.createDefinition(type));
    }
  }

  private void createDefinitionsCustom()
  {
    {
      final var definition =
        new DefinitionUUID(resolve(UUID.class, List.of()));
      this.definitions.put(definition.type.name(), definition);
    }

    {
      final var definition =
        new DefinitionByteArray(resolve(byte[].class, List.of()));
      this.definitions.put(definition.type.name(), definition);
    }

    {
      final var definition =
        new DefinitionString(resolve(String.class, List.of()));
      this.definitions.put(definition.type.name(), definition);
    }

    {
      final var definition =
        new DefinitionBoolean(resolve(boolean.class, List.of()));
      this.definitions.put(definition.type.name(), definition);
    }

    {
      final var definition =
        new DefinitionUnsignedLong(resolve(CJ1UnsignedLong.class, List.of()));
      this.definitions.put(definition.type.name(), definition);
    }

    {
      final var definition =
        new DefinitionUnsignedInt(resolve(CJ1UnsignedInt.class, List.of()));
      this.definitions.put(definition.type.name(), definition);
    }

    {
      final var definition =
        new DefinitionCurrencyUnit(resolve(CurrencyUnit.class, List.of()));
      this.definitions.put(definition.type.name(), definition);
    }

    {
      final var definition =
        new DefinitionBigDecimal(resolve(BigDecimal.class, List.of()));
      this.definitions.put(definition.type.name(), definition);
    }

    {
      final var definition =
        new DefinitionCAErrorCode(resolve(CAErrorCode.class, List.of()));
      this.definitions.put(definition.type.name(), definition);
    }

    {
      final var definition =
        new DefinitionDouble(resolve(double.class, List.of()));
      this.definitions.put(definition.type.name(), definition);
    }

    {
      final var definition =
        new DefinitionLong(resolve(long.class, List.of()));
      this.definitions.put(definition.type.name(), definition);
    }

    {
      final var definition =
        new DefinitionOffsetDateTime(resolve(OffsetDateTime.class, List.of()));
      this.definitions.put(definition.type.name(), definition);
    }

    {
      final var definition =
        new DefinitionRDottedName(resolve(RDottedName.class, List.of()));
      this.definitions.put(definition.type.name(), definition);
    }

    {
      final var definition =
        new DefinitionTypePackageIdentifier(
          resolve(CJ1TypePackageIdentifier.class, List.of()));
      this.definitions.put(definition.type.name(), definition);
    }

    {
      final var definition =
        new DefinitionTypeRecordIdentifier(
          resolve(CJ1TypeRecordIdentifier.class, List.of()));
      this.definitions.put(definition.type.name(), definition);
    }

    {
      final var definition =
        new DefinitionTypeRecordFieldIdentifier(
          resolve(CJ1TypeRecordFieldIdentifier.class, List.of()));
      this.definitions.put(definition.type.name(), definition);
    }
  }

  private DefinitionType createDefinition(
    final FullyResolvedType type)
  {
    if (type.isSealedInterface()) {
      return new DefinitionSealedInterface(type);
    }
    if (type.isEnum()) {
      return new DefinitionEnum(type);
    }
    if (type.isPrimitive()) {
      return new DefinitionSealedPrimitive(type);
    }

    if (type.isBaseType(Map.class)) {
      return new DefinitionMap(type);
    }
    if (type.isBaseType(Set.class)) {
      return new DefinitionSet(type);
    }
    if (type.isBaseType(List.class)) {
      return new DefinitionList(type);
    }
    if (type.isBaseType(Optional.class)) {
      return new DefinitionOptional(type);
    }

    return new DefinitionPlainObject(type);
  }

  private void collectTypes(
    final FullyResolvedType currentType)
  {
    if (this.types.containsKey(currentType.name())) {
      return;
    }

    LOG.debug("Collected type: {}", currentType.name());
    this.types.put(currentType.name(), currentType);

    for (final var parameter : currentType.type.getTypeParameters()) {
      this.collectTypes(resolve(parameter, List.of()));
    }
    for (final var method : currentType.methods.values()) {
      this.collectTypes(resolve(method.getReturnType(), List.of()));
    }
    for (final var subclass : currentType.subclasses) {
      this.collectTypes(subclass);
    }
  }

  private interface DefinitionType
  {
    ObjectNode execute();
  }

  private record FullyResolvedType(
    ResolvedType type,
    SortedMap<String, ResolvedMethod> methods,
    SortedSet<String> methodRequired,
    SortedMap<String, String> methodDescriptions,
    List<FullyResolvedType> subclasses,
    Optional<String> description,
    Optional<String> typeProperty)
  {
    static String refName(
      final ResolvedType type)
    {
      return String.format("#/$defs/%s", shortName(type));
    }

    static String shortName(
      final ResolvedType type)
    {
      final var simpleName = type.getErasedType().getSimpleName();
      final var parameters = type.getTypeParameters();
      if (parameters.isEmpty()) {
        return simpleName;
      }
      return String.format(
        "%s<%s>",
        simpleName,
        parameters.stream()
          .map(FullyResolvedType::shortName)
          .collect(Collectors.joining(","))
      );
    }

    String name()
    {
      return shortName(this.type);
    }

    String refName()
    {
      return refName(this.type);
    }

    public boolean isSealedInterface()
    {
      final var clazz = this.type.getErasedType();
      return clazz.isInterface() && clazz.isSealed();
    }

    public boolean isEnum()
    {
      final var clazz = this.type.getErasedType();
      return clazz.isEnum();
    }

    public boolean isPrimitive()
    {
      final var clazz = this.type.getErasedType();
      return clazz.isPrimitive();
    }

    public void putDescription(
      final ObjectNode object)
    {
      this.description.ifPresent(text -> {
        object.put("description", text);
      });
    }

    public boolean isBaseType(
      final Class<?> target)
    {
      final var clazz = this.type.getErasedType();
      return Objects.equals(clazz, target);
    }
  }

  private final class DefinitionSealedInterface
    implements DefinitionType
  {
    private final FullyResolvedType type;

    private DefinitionSealedInterface(
      final FullyResolvedType type)
    {
      this.type = type;
    }

    @Override
    public ObjectNode execute()
    {
      final var cases = MAPPER.createArrayNode();
      for (final var subclass : this.type.subclasses) {
        final var ref = MAPPER.createObjectNode();
        ref.put("$ref", subclass.refName());
        cases.add(ref);
      }

      final var object = MAPPER.createObjectNode();
      this.type.putDescription(object);
      object.set("oneOf", cases);
      return object;
    }
  }

  private final class DefinitionEnum
    implements DefinitionType
  {
    private final FullyResolvedType type;

    private DefinitionEnum(
      final FullyResolvedType type)
    {
      this.type = type;
    }

    @Override
    public ObjectNode execute()
    {
      final var constants =
        Arrays.stream(this.type.type.getErasedType()
                        .getEnumConstants())
          .map(Object::toString)
          .toList();

      final var enumArray = MAPPER.createArrayNode();
      for (final var ccase : constants) {
        enumArray.add(ccase);
      }

      final var object = MAPPER.createObjectNode();
      this.type.putDescription(object);
      object.put("type", "string");
      object.set("enum", enumArray);
      return object;
    }
  }

  private final class DefinitionSealedPrimitive
    implements DefinitionType
  {
    private final FullyResolvedType type;

    private DefinitionSealedPrimitive(
      final FullyResolvedType type)
    {
      this.type = type;
    }

    @Override
    public ObjectNode execute()
    {
      final var object = MAPPER.createObjectNode();
      this.type.putDescription(object);
      return object;
    }
  }

  private final class DefinitionPlainObject
    implements DefinitionType
  {
    private final FullyResolvedType type;

    private DefinitionPlainObject(
      final FullyResolvedType type)
    {
      this.type = type;
    }

    @Override
    public ObjectNode execute()
    {
      final var props =
        MAPPER.createObjectNode();
      final var required =
        MAPPER.createArrayNode();

      for (final var entry : this.type.methods.entrySet()) {
        final var name =
          entry.getKey();
        final var description =
          Optional.ofNullable(this.type.methodDescriptions.get(name));

        final var ref = MAPPER.createObjectNode();
        description.ifPresent(text -> {
          ref.put("description", text);
        });
        ref.put(
          "$ref",
          FullyResolvedType.refName(entry.getValue().getReturnType())
        );
        props.set(name, ref);
      }

      this.type.typeProperty.ifPresent(name -> {
        final var typeProp = MAPPER.createObjectNode();
        typeProp.put("type", "string");
        typeProp.put("pattern", name);
        props.set("@type", typeProp);
        required.add("@type");
      });

      for (final var name : this.type.methodRequired) {
        required.add(name);
      }

      final var object = MAPPER.createObjectNode();
      this.type.putDescription(object);
      object.put("type", "object");
      object.set("properties", props);
      object.set("required", required);
      return object;
    }
  }

  private final class DefinitionMap
    implements DefinitionType
  {
    private final FullyResolvedType type;

    private DefinitionMap(
      final FullyResolvedType type)
    {
      this.type = type;
    }

    @Override
    public ObjectNode execute()
    {
      final var param =
        this.type.type.getTypeBindings()
          .getBoundType(1);

      final var ref = MAPPER.createObjectNode();
      ref.put("$ref", FullyResolvedType.refName(param));

      final var object = MAPPER.createObjectNode();
      this.type.putDescription(object);
      object.put("type", "object");
      object.set("additionalProperties", ref);
      return object;
    }
  }

  private final class DefinitionSet
    implements DefinitionType
  {
    private final FullyResolvedType type;

    private DefinitionSet(
      final FullyResolvedType type)
    {
      this.type = type;
    }

    @Override
    public ObjectNode execute()
    {
      final var param =
        this.type.type.getTypeBindings()
          .getBoundType(0);

      final var ref = MAPPER.createObjectNode();
      ref.put("$ref", FullyResolvedType.refName(param));

      final var object = MAPPER.createObjectNode();
      this.type.putDescription(object);
      object.put("type", "array");
      object.set("items", ref);
      return object;
    }
  }

  private final class DefinitionList
    implements DefinitionType
  {
    private final FullyResolvedType type;

    private DefinitionList(
      final FullyResolvedType type)
    {
      this.type = type;
    }

    @Override
    public ObjectNode execute()
    {
      final var param =
        this.type.type.getTypeBindings()
          .getBoundType(0);

      final var ref = MAPPER.createObjectNode();
      ref.put("$ref", FullyResolvedType.refName(param));

      final var object = MAPPER.createObjectNode();
      this.type.putDescription(object);
      object.put("type", "array");
      object.set("items", ref);
      return object;
    }
  }

  private final class DefinitionOptional
    implements DefinitionType
  {
    private final FullyResolvedType type;

    private DefinitionOptional(
      final FullyResolvedType type)
    {
      this.type = type;
    }

    @Override
    public ObjectNode execute()
    {
      final var param =
        this.type.type.getTypeBindings()
          .getBoundType(0);

      final var object = MAPPER.createObjectNode();
      this.type.putDescription(object);
      object.put("$ref", FullyResolvedType.refName(param));
      return object;
    }
  }

  private final class DefinitionUUID
    implements DefinitionType
  {
    private final FullyResolvedType type;

    private DefinitionUUID(
      final FullyResolvedType type)
    {
      this.type = type;
    }

    @Override
    public ObjectNode execute()
    {
      final var object = MAPPER.createObjectNode();
      object.put("description", "An RFC 9562 UUID string.");
      object.put("type", "string");
      object.put("format", "uuid");
      return object;
    }
  }

  private final class DefinitionByteArray
    implements DefinitionType
  {
    private final FullyResolvedType type;

    private DefinitionByteArray(
      final FullyResolvedType type)
    {
      this.type = type;
    }

    @Override
    public ObjectNode execute()
    {
      final var object = MAPPER.createObjectNode();
      object.put(
        "description",
        "An RFC 4648 'standard' base64 encoded byte array.");
      object.put("type", "string");
      object.put("format", "base64");
      return object;
    }
  }

  private final class DefinitionString
    implements DefinitionType
  {
    private final FullyResolvedType type;

    private DefinitionString(
      final FullyResolvedType type)
    {
      this.type = type;
    }

    @Override
    public ObjectNode execute()
    {
      final var object = MAPPER.createObjectNode();
      object.put("description", "An arbitrary string.");
      object.put("type", "string");
      return object;
    }
  }

  private final class DefinitionBoolean
    implements DefinitionType
  {
    private final FullyResolvedType type;

    private DefinitionBoolean(
      final FullyResolvedType type)
    {
      this.type = type;
    }

    @Override
    public ObjectNode execute()
    {
      final var object = MAPPER.createObjectNode();
      object.put("type", "boolean");
      return object;
    }
  }

  private final class DefinitionUnsignedLong
    implements DefinitionType
  {
    private final FullyResolvedType type;

    private DefinitionUnsignedLong(
      final FullyResolvedType type)
    {
      this.type = type;
    }

    @Override
    public ObjectNode execute()
    {
      final var object = MAPPER.createObjectNode();
      object.put("description", "A 64-bit unsigned integer value.");
      object.put("type", "number");
      object.put("minimum", 0);
      object.put("exclusiveMaximum", new BigInteger("18446744073709551616"));
      return object;
    }
  }

  private final class DefinitionUnsignedInt
    implements DefinitionType
  {
    private final FullyResolvedType type;

    private DefinitionUnsignedInt(
      final FullyResolvedType type)
    {
      this.type = type;
    }

    @Override
    public ObjectNode execute()
    {
      final var object = MAPPER.createObjectNode();
      object.put("description", "A 32-bit unsigned integer value.");
      object.put("type", "number");
      object.put("minimum", 0);
      object.put("exclusiveMaximum", new BigInteger("4294967296"));
      return object;
    }
  }

  private final class DefinitionCurrencyUnit
    implements DefinitionType
  {
    private final FullyResolvedType type;

    private DefinitionCurrencyUnit(
      final FullyResolvedType type)
    {
      this.type = type;
    }

    @Override
    public ObjectNode execute()
    {
      final var object = MAPPER.createObjectNode();
      object.put("description", "An ISO 4217 currency code.");
      object.put("type", "string");
      object.put("pattern", "[A-Z][A-Z][A-Z]");
      return object;
    }
  }

  private final class DefinitionBigDecimal
    implements DefinitionType
  {
    private final FullyResolvedType type;

    private DefinitionBigDecimal(
      final FullyResolvedType type)
    {
      this.type = type;
    }

    @Override
    public ObjectNode execute()
    {
      final var object = MAPPER.createObjectNode();
      object.put("description", "An arbitrary real number.");
      object.put("type", "number");
      return object;
    }
  }

  private final class DefinitionDouble
    implements DefinitionType
  {
    private final FullyResolvedType type;

    private DefinitionDouble(
      final FullyResolvedType type)
    {
      this.type = type;
    }

    @Override
    public ObjectNode execute()
    {
      final var object = MAPPER.createObjectNode();
      object.put("description", "An arbitrary real number.");
      object.put("type", "number");
      return object;
    }
  }

  private final class DefinitionCAErrorCode
    implements DefinitionType
  {
    private final FullyResolvedType type;

    private DefinitionCAErrorCode(
      final FullyResolvedType type)
    {
      this.type = type;
    }

    @Override
    public ObjectNode execute()
    {
      final var object = MAPPER.createObjectNode();
      object.put("description", "An error code.");
      object.put("type", "string");
      object.put("pattern", "[a-z][a-z\\-]+");
      return object;
    }
  }

  private final class DefinitionLong
    implements DefinitionType
  {
    private final FullyResolvedType type;

    private DefinitionLong(
      final FullyResolvedType type)
    {
      this.type = type;
    }

    @Override
    public ObjectNode execute()
    {
      final var object = MAPPER.createObjectNode();
      object.put("description", "A 64-bit signed integer.");
      object.put("type", "number");
      object.put("minimum", Long.MIN_VALUE);
      object.put("maximum", Long.MAX_VALUE);
      return object;
    }
  }

  private final class DefinitionOffsetDateTime
    implements DefinitionType
  {
    private final FullyResolvedType type;

    private DefinitionOffsetDateTime(
      final FullyResolvedType type)
    {
      this.type = type;
    }

    @Override
    public ObjectNode execute()
    {
      final var object = MAPPER.createObjectNode();
      object.put("description", "An ISO 8601 timestamp.");
      object.put("type", "string");
      object.put("format", "date-time");
      return object;
    }
  }

  private final class DefinitionRDottedName
    implements DefinitionType
  {
    private final FullyResolvedType type;

    private DefinitionRDottedName(
      final FullyResolvedType type)
    {
      this.type = type;
    }

    @Override
    public ObjectNode execute()
    {
      final var object = MAPPER.createObjectNode();
      object.put("description", "A Lanark dotted name.");
      object.put("type", "string");
      object.put("pattern", RDottedNamePatterns.dottedName().pattern());
      return object;
    }
  }

  private final class DefinitionTypePackageIdentifier
    implements DefinitionType
  {
    private final FullyResolvedType type;

    private DefinitionTypePackageIdentifier(
      final FullyResolvedType type)
    {
      this.type = type;
    }

    @Override
    public ObjectNode execute()
    {
      final var object = MAPPER.createObjectNode();
      object.put("description", "A type package identifier.");
      object.put("type", "string");
      return object;
    }
  }

  private final class DefinitionTypeRecordFieldIdentifier
    implements DefinitionType
  {
    private final FullyResolvedType type;

    private DefinitionTypeRecordFieldIdentifier(
      final FullyResolvedType type)
    {
      this.type = type;
    }

    @Override
    public ObjectNode execute()
    {
      final var object = MAPPER.createObjectNode();
      object.put("description", "A type record field identifier.");
      object.put("type", "string");
      return object;
    }
  }

  private final class DefinitionTypeRecordIdentifier
    implements DefinitionType
  {
    private final FullyResolvedType type;

    private DefinitionTypeRecordIdentifier(
      final FullyResolvedType type)
    {
      this.type = type;
    }

    @Override
    public ObjectNode execute()
    {
      final var object = MAPPER.createObjectNode();
      object.put("description", "A type record identifier.");
      object.put("type", "string");
      return object;
    }
  }
}
