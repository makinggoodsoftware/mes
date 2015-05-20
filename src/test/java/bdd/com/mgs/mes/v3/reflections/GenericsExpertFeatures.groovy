package com.mgs.mes.v3.reflections

import com.mgs.mes.v3.reflection.GenericsExpert
import com.mgs.mes.v3.reflection.ParsedTypeFactory
import com.mgs.mes.v3.reflections.domain.ExtendedGenerics
import com.mgs.mes.v3.reflections.domain.Generics
import com.mgs.mes.v3.reflections.domain.HiddenGenerics
import com.mgs.reflection.GenericType
import spock.lang.Specification

class GenericsExpertFeatures extends Specification{
    GenericsExpert generics

    def "setup" (){
        generics = new GenericsExpert(new ParsedTypeFactory())
    }

    def "should read simple generics" (){
        when:
        GenericType parsedType = generics.parseMethodReturnType(Generics, "getListOfStrings")

        then:
        parsedType.typeName == "java.util.List<java.lang.String>"
        parsedType.isParametrized()
        parsedType.isResolved()
        parsedType.actualType.isPresent()
        parsedType.actualType.get() == List
        parsedType.parameters.entrySet().size() == 1

        parsedType.parameters.get(List).get("E").typeName == "java.lang.String"
        ! parsedType.parameters.get(List).get("E").isParametrized()
        parsedType.parameters.get(List).get("E").isResolved()
        parsedType.parameters.get(List).get("E").actualType.isPresent()
        parsedType.parameters.get(List).get("E").actualType.get() == String
        parsedType.parameters.get(List).get("E").parameters.size() == 0
    }

    def "should read nested generics" (){
        when:
        GenericType parsedType = generics.parseMethodReturnType(Generics, "getNestedStrings")

        then:
        parsedType.typeName == "java.util.List<java.util.List<java.lang.String>>"
        parsedType.isParametrized()
        parsedType.isResolved()
        parsedType.actualType.get() == List
        parsedType.parameters.entrySet().size() == 1

        parsedType.parameters.get(List).get("E").typeName == "java.util.List<java.lang.String>"
        parsedType.parameters.get(List).get("E").isParametrized()
        parsedType.parameters.get(List).get("E").isResolved()
        parsedType.parameters.get(List).get("E").actualType.get() == List
        parsedType.parameters.get(List).get("E").parameters.size() == 1

        parsedType.parameters.get(List).get("E").parameters.get(List).get("E").typeName == "java.lang.String"
        ! parsedType.parameters.get(List).get("E").parameters.get(List).get("E").isParametrized()
        parsedType.parameters.get(List).get("E").parameters.get(List).get("E").isResolved()
        parsedType.parameters.get(List).get("E").parameters.get(List).get("E").actualType.get() == String
        parsedType.parameters.get(List).get("E").parameters.get(List).get("E").parameters.size() == 0
    }

    def "should read unespecified generics" () {
        when:
        GenericType parsedType = generics.parseMethodReturnType(Generics, "getUnespecified")

        then:
        parsedType.typeName == "java.util.List<T>"
        parsedType.isParametrized()
        parsedType.isResolved()
        parsedType.actualType.get() == List
        parsedType.parameters.entrySet().size() == 1

        parsedType.parameters.get(List).get("E").typeName == "T"
        ! parsedType.parameters.get(List).get("E").isParametrized()
        ! parsedType.parameters.get(List).get("E").isResolved()
        ! parsedType.parameters.get(List).get("E").actualType.isPresent()
        parsedType.parameters.get(List).get("E").parameters.size() == 0
    }

    def "should read nested unespecified generics" (){
        when:
        GenericType parsedType = generics.parseMethodReturnType(Generics, "getNestedUnespecified")

        then:
        parsedType.typeName == "java.util.List<java.util.List<T>>"
        parsedType.isParametrized()
        parsedType.isResolved()
        parsedType.actualType.get() == List
        parsedType.parameters.entrySet().size() == 1

        parsedType.parameters.get(List).get("E").typeName == "java.util.List<T>"
        parsedType.parameters.get(List).get("E").isParametrized()
        parsedType.parameters.get(List).get("E").isResolved()
        parsedType.parameters.get(List).get("E").actualType.get() == List
        parsedType.parameters.get(List).get("E").parameters.size() == 1

        parsedType.parameters.get(List).get("E").parameters.get(List).get("E").typeName == "T"
        ! parsedType.parameters.get(List).get("E").parameters.get(List).get("E").isParametrized()
        ! parsedType.parameters.get(List).get("E").parameters.get(List).get("E").isResolved()
        ! parsedType.parameters.get(List).get("E").parameters.get(List).get("E").actualType.isPresent()
        parsedType.parameters.get(List).get("E").parameters.get(List).get("E").parameters.size() == 0
    }

    def "should parse raw generics" (){
        when:
        GenericType parsedType = generics.parseMethodReturnType(Generics, "getIt")

        then:
        parsedType.typeName == "T"
        ! parsedType.isParametrized()
        ! parsedType.isResolved()
        ! parsedType.actualType.isPresent()
        parsedType.parameters.entrySet().size() == 0
    }

    def "should parse simple datatypes" (){
        when:
        GenericType parsedType = generics.parseMethodReturnType(Generics, "getInt")

        then:
        parsedType.typeName == "java.lang.Integer"
        ! parsedType.isParametrized()
        parsedType.isResolved()
        parsedType.actualType.get() == Integer
        parsedType.parameters.entrySet().size() == 0
    }

    def "should parse primitive datatypes" (){
        when:
        GenericType parsedType = generics.parseMethodReturnType(Generics, "getIntPrimitive")

        then:
        parsedType.typeName == "int"
        ! parsedType.isParametrized()
        parsedType.isResolved()
        parsedType.actualType.get() == int
        parsedType.parameters.entrySet().size() == 0
    }

    def "should parse void" (){
        when:
        GenericType parsedType = generics.parseMethodReturnType(Generics, "getVoid")

        then:
        parsedType.typeName == "void"
        ! parsedType.isParametrized()
        parsedType.isResolved()
        parsedType.actualType.get() == void
        parsedType.parameters.entrySet().size() == 0
    }

    def "should parse extended generics" (){
        when:
        GenericType parsedType = generics.parseMethodReturnType(ExtendedGenerics, "getSimpleGenerics")

        then:
        parsedType.typeName == "T"
        ! parsedType.isParametrized()
        ! parsedType.isResolved()
        ! parsedType.actualType.isPresent()
        parsedType.parameters.entrySet().size() == 0
    }

    def "should parse hidden generics" (){
        when:
        GenericType parsedType = generics.parseMethodReturnType(HiddenGenerics, "getSimpleGenerics")

        then:
        parsedType.typeName == "java.lang.String"
        ! parsedType.isParametrized()
        parsedType.isResolved()
        parsedType.actualType.get() == String
        parsedType.parameters.entrySet().size() == 0
    }

//    def "should parse map generics" (){
//        when:
//        GenericType parsedType = generics.parseMethodReturnType(Generics.getMethod("getMap"))
//
//        then:
//        parsedType.typeName == "java.util.Map<java.lang.String, com.mgs.mes.v3.reflections.domain.ExtendedGenerics<java.lang.Integer>>"
//        parsedType.isParametrized()
//        parsedType.isResolved()
//        parsedType.actualType.get() == Map
//        parsedType.parameters.entrySet().size() == 2
//
//        parsedType.parameters.get("K").typeName == "java.lang.String"
//        ! parsedType.parameters.get("K").isParametrized()
//        parsedType.parameters.get("K").isResolved()
//        parsedType.parameters.get("K").actualType.get() == String
//        parsedType.parameters.get("K").parameters.entrySet().size() == 0
//
//        parsedType.parameters.get("V").typeName == "com.mgs.mes.v3.reflections.domain.ExtendedGenerics<java.lang.Integer>"
//        parsedType.parameters.get("V").isParametrized()
//        parsedType.parameters.get("V").isResolved()
//        parsedType.parameters.get("V").actualType.get() == ExtendedGenerics
//        parsedType.parameters.get("V").parameters.entrySet().size() == 1
//
//        parsedType.parameters.get("V").parameters.get("T").typeName == "java.lang.Integer"
//        ! parsedType.parameters.get("V").parameters.get("T").isParametrized()
//        parsedType.parameters.get("V").parameters.get("T").isResolved()
//        parsedType.parameters.get("V").parameters.get("T").actualType.get() == Integer
//        parsedType.parameters.get("V").parameters.get("T").parameters.entrySet().size() == 0
//    }
//
//    def "should parse methods" (){
//        when:
//        GenericMethods parsedMethods = generics.parseMethods(generics.parseMethodReturnType(Generics.getMethod("getSimpleExtendedGenerics")))
//
//        then:
//        parsedMethods.parsedMethodsAsMap["getSimpleGenerics"].returnType.actualType.get() == Integer
//    }

}
