package com.mgs.mes.meta.utils;

import com.mgs.mes.context.EntityDescriptor;
import com.mgs.mes.model.Entity;
import com.mgs.mes.model.EntityBuilder;
import com.mgs.mes.model.OneToMany;
import com.mgs.mes.model.OneToOne;
import com.mgs.reflection.FieldAccessor;
import com.mgs.reflection.FieldAccessorParser;
import com.mgs.reflection.FieldAccessorType;
import com.mgs.reflection.Reflections;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mgs.reflection.FieldAccessorType.BUILDER;
import static com.mgs.reflection.FieldAccessorType.GET;
import static java.lang.String.format;
import static java.util.Arrays.asList;

public class Validator {
	private final Reflections reflections;
	private final FieldAccessorParser fieldAccessorParser;

	public Validator(Reflections reflections, FieldAccessorParser fieldAccessorParser) {
		this.reflections = reflections;
		this.fieldAccessorParser = fieldAccessorParser;
	}

	public <T extends Entity, Z extends EntityBuilder<T>> void validate(EntityDescriptor<T, Z> descriptor) {
		try {
			tryToValidate(descriptor.getEntityType(), descriptor.getBuilderType());
		} catch (Exception e) {
			String errorMsg = format("Can't validate %s as valid interfaces to drive the getters and builders for Mongo Easy", descriptor.toString());
			throw new IllegalArgumentException(errorMsg, e);
		}
	}

	private <T extends Entity, Z extends EntityBuilder<T>> void tryToValidate(Class<T> entityType, Class<Z> builderType) {
		Stream<FieldAccessor> modelFieldAccessors = assertMethodsValidity(entityType, GET, asList("asDbo","dataEquals"));
		Stream<FieldAccessor> updaterFieldAccessors = assertMethodsValidity(builderType, BUILDER, asList("create", "withId","getEntityType","asDbo"));

		if (!accessorsMatch (modelFieldAccessors, updaterFieldAccessors)){
			String errorMsg = format("Can't match the updaters from %s into the getters from %s", builderType, entityType);
			throw new IllegalArgumentException(errorMsg);
		}
	}

	private Stream<FieldAccessor> assertMethodsValidity(Class<?> sourceType, FieldAccessorType expectedAccessorType, List<String> methodsToIgnore) {
		return 	extractMethodsAndFieldAccessors(sourceType).
				filter(ignoreMethods(methodsToIgnore)).
				filter(assertAllMethodsAreFieldAccessor(sourceType)).
				map(extractFieldAccessor()).
				filter(assertFieldAccessorIsOfType(sourceType, expectedAccessorType)).
				filter(assertFieldAccessorTypeIsValid(sourceType));
	}

	private Stream<Map.Entry<Method, Optional<FieldAccessor>>> extractMethodsAndFieldAccessors(Class<?> sourceType) {
		return fieldAccessorParser.parseAll(sourceType).entrySet().stream();
	}

	private Predicate<FieldAccessor> assertFieldAccessorTypeIsValid(Class<?> sourceType) {
		return (fieldAccessor) -> {
			if (!isCorrectDataType(fieldAccessor))
				throw new IllegalArgumentException("The field accessor for " + fieldAccessor.getMethodName() + " in " + sourceType + " return type is not dbo or a mongo entity");
			return true;
		};
	}

	private Predicate<FieldAccessor> assertFieldAccessorIsOfType(Class<?> sourceType, FieldAccessorType accessorType) {
		return (fieldAccessor) -> {
			if (fieldAccessor.getType() != accessorType) throw new IllegalArgumentException("The field accessor for " + fieldAccessor.getMethodName() + " is not of the expected type (" + accessorType + ") for the class " + sourceType.getName());
			return true;
		};
	}

	private Predicate<Map.Entry<Method, Optional<FieldAccessor>>> assertAllMethodsAreFieldAccessor(Class<?> sourceType) {
		return (fieldAccessorByMethod) -> {
			Optional<FieldAccessor> value = fieldAccessorByMethod.getValue();
			if (!value.isPresent()) {
				String methodName = fieldAccessorByMethod.getKey().getName();
				throw new IllegalArgumentException("The field accessor for " + methodName + " in " + sourceType + " is not a field accessor");
			}


			return true;
		};
	}

	private Function<Map.Entry<Method, Optional<FieldAccessor>>, FieldAccessor> extractFieldAccessor() {
		return (fieldAccessorByMethod) -> {
			Optional<FieldAccessor> fieldAccessorOptional = fieldAccessorByMethod.getValue();
			return fieldAccessorOptional.get();
		};
	}

	private Predicate<Map.Entry<Method, Optional<FieldAccessor>>> ignoreMethods(List<String> methodsToIgnore) {
		return (fieldAccessorByMethod) -> {
			String methodName = fieldAccessorByMethod.getKey().getName();
			return !methodsToIgnore.contains(methodName);
		};
	}

	private boolean isCorrectDataType(FieldAccessor fieldAccessor) {
		return
				fieldAccessor.getFieldName().equals("id") ||
				reflections.isSimpleOrAssignableTo(fieldAccessor.getReturnType().getOwnDeclaration().getTypeResolution().getSpecificClass().get(), Entity.class) ||
				reflections.isAssignableTo(fieldAccessor.getReturnType().getOwnDeclaration().getTypeResolution().getSpecificClass().get(), Collection.class);
	}

	@SuppressWarnings("CodeBlock2Expr")
	private boolean accessorsMatch(Stream<FieldAccessor> modelFieldAccessors, Stream<FieldAccessor> updaterFieldAccessors) {
		Map<String, FieldAccessor> modelFieldAccessorByName = modelFieldAccessors.collect(Collectors.toMap(
				FieldAccessor::getFieldName,
				fieldAccessor -> {
					return fieldAccessor;
				}
		));
		return updaterFieldAccessors.
				allMatch((updaterFieldAccessor)->{
					String updaterFieldName = updaterFieldAccessor.getFieldName();
					FieldAccessor getterFieldAccessor = modelFieldAccessorByName.get(updaterFieldName);
					if (getterFieldAccessor == null) {
						String errorMsg = format("Can't find the update accessor for the field %s in the list of valid getters", updaterFieldName);
						throw new IllegalArgumentException(errorMsg);
					}

					Class<?> getterType = getterFieldAccessor.getReturnType().getOwnDeclaration().getTypeResolution().getSpecificClass().get();
					Class<?> updaterType = updaterFieldAccessor.getReturnType().getOwnDeclaration().getTypeResolution().getSpecificClass().get();
					if (!isValidGetterAndSetter(getterType, updaterType)) {
						String errorMsg = format("The declared type for the field %s is of different types in the getter| [%s], and the updater [%s]",
								updaterFieldName,
								getterType,
								updaterType
						);
						throw new IllegalArgumentException(errorMsg);
					}

					return true;
				});
	}

	private boolean isValidGetterAndSetter(Class<?> getterType, Class<?> updaterType) {
		return
				isValidSimpleType(getterType, updaterType) ||
				isValidOneToOne(getterType, updaterType) ||
				isValidOneToMany(getterType, updaterType);
	}

	private boolean isValidOneToMany(Class<?> getterType, Class<?> updaterType) {
		return reflections.isAssignableTo(getterType, OneToMany.class) && reflections.isAssignableTo(updaterType, List.class);
	}

	private boolean isValidOneToOne(Class<?> getterType, Class<?> updaterType) {
		return reflections.isAssignableTo(getterType, OneToOne.class) && reflections.isAssignableTo(updaterType, Entity.class);
	}

	private boolean isValidSimpleType(Class<?> getterType, Class<?> updaterType) {
		return getterType.equals(updaterType);
	}
}