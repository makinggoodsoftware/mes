package com.mgs.mes.v3.reflections
import com.mgs.mes.v3.reflection.GenericsExpert
import com.mgs.mes.v3.reflection.ParsedTypeFactory
import com.mgs.reflection.ParsedType
import spock.lang.Specification

class GenericsExpertFeatures extends Specification{
    GenericsExpert generics

    def "setup" (){
        generics = new GenericsExpert(new ParsedTypeFactory())
    }

    def "should read simple generics" (){
        when:
        ParsedType parsedType = generics.parseMethodReturnType(Generics.getMethod("getListOfStrings"))

        then:
        parsedType.typeName == "java.util.List<java.lang.String>"
        parsedType.isParametrized()
        parsedType.isResolved()
        parsedType.actualType.isPresent()
        parsedType.actualType.get() == List
        parsedType.parameters.size() == 1

        parsedType.parameters.get(0).typeName == "java.lang.String"
        ! parsedType.parameters.get(0).isParametrized()
        parsedType.parameters.get(0).isResolved()
        parsedType.parameters.get(0).actualType.isPresent()
        parsedType.parameters.get(0).actualType.get() == String
        parsedType.parameters.get(0).parameters.size() == 0
    }

    def "should read nested generics" (){
        when:
        ParsedType parametrizedType = generics.parseMethodReturnType(Generics.getMethod("getNestedStrings"))

        then:
        parametrizedType.typeName == "java.util.List<java.util.List<java.lang.String>>"
        parametrizedType.isParametrized()
        parametrizedType.isResolved()
        parametrizedType.actualType.get() == List
        parametrizedType.parameters.size() == 1

        parametrizedType.parameters.get(0).typeName == "java.util.List<java.lang.String>"
        parametrizedType.parameters.get(0).isParametrized()
        parametrizedType.parameters.get(0).isResolved()
        parametrizedType.parameters.get(0).actualType.get() == List
        parametrizedType.parameters.get(0).parameters.size() == 1

        parametrizedType.parameters.get(0).parameters.get(0).typeName == "java.lang.String"
        ! parametrizedType.parameters.get(0).parameters.get(0).isParametrized()
        parametrizedType.parameters.get(0).parameters.get(0).isResolved()
        parametrizedType.parameters.get(0).parameters.get(0).actualType.get() == String
        parametrizedType.parameters.get(0).parameters.get(0).parameters.size() == 0
    }

    def "should read unespecified generics" () {
        when:
        ParsedType parametrizedType = generics.parseMethodReturnType(Generics.getMethod("getUnespecified"))

        then:
        parametrizedType.typeName == "java.util.List<T>"
        parametrizedType.isParametrized()
        parametrizedType.isResolved()
        parametrizedType.actualType.get() == List
        parametrizedType.parameters.size() == 1

        parametrizedType.parameters.get(0).typeName == "T"
        ! parametrizedType.parameters.get(0).isParametrized()
        ! parametrizedType.parameters.get(0).isResolved()
        ! parametrizedType.parameters.get(0).actualType.isPresent()
        parametrizedType.parameters.get(0).parameters.size() == 0
    }

    def "should read nested unespecified generics" (){
        when:
        ParsedType parametrizedType = generics.parseMethodReturnType(Generics.getMethod("getNestedUnespecified"))

        then:
        parametrizedType.typeName == "java.util.List<java.util.List<T>>"
        parametrizedType.isParametrized()
        parametrizedType.isResolved()
        parametrizedType.actualType.get() == List
        parametrizedType.parameters.size() == 1

        parametrizedType.parameters.get(0).typeName == "java.util.List<T>"
        parametrizedType.parameters.get(0).isParametrized()
        parametrizedType.parameters.get(0).isResolved()
        parametrizedType.parameters.get(0).actualType.get() == List
        parametrizedType.parameters.get(0).parameters.size() == 1

        parametrizedType.parameters.get(0).parameters.get(0).typeName == "T"
        ! parametrizedType.parameters.get(0).parameters.get(0).isParametrized()
        ! parametrizedType.parameters.get(0).parameters.get(0).isResolved()
        ! parametrizedType.parameters.get(0).parameters.get(0).actualType.isPresent()
        parametrizedType.parameters.get(0).parameters.get(0).parameters.size() == 0
    }

    def "should parse raw generics" (){
        when:
        ParsedType parametrizedType = generics.parseMethodReturnType(Generics.getMethod("getIt"))

        then:
        parametrizedType.typeName == "T"
        ! parametrizedType.isParametrized()
        ! parametrizedType.isResolved()
        ! parametrizedType.actualType.isPresent()
        parametrizedType.parameters.size() == 0
    }

    def "should parse simple datatypes" (){
        when:
        ParsedType parametrizedType = generics.parseMethodReturnType(Generics.getMethod("getInt"))

        then:
        parametrizedType.typeName == "java.lang.Integer"
        ! parametrizedType.isParametrized()
        parametrizedType.isResolved()
        parametrizedType.actualType.get() == Integer
        parametrizedType.parameters.size() == 0
    }

    def "should parse primitive datatypes" (){
        when:
        ParsedType parametrizedType = generics.parseMethodReturnType(Generics.getMethod("getIntPrimitive"))

        then:
        parametrizedType.typeName == "int"
        ! parametrizedType.isParametrized()
        parametrizedType.isResolved()
        parametrizedType.actualType.get() == int
        parametrizedType.parameters.size() == 0
    }

    private static interface Generics<T> {
        List<String> getListOfStrings()

        List<List<String>> getNestedStrings()

        List<T> getUnespecified()

        List<List<T>> getNestedUnespecified()

        T getIt ()

        Integer getInt ()

        int getIntPrimitive ()
    }
}
