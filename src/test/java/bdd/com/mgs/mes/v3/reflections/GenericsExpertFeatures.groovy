package com.mgs.mes.v3.reflections

import com.mgs.mes.v3.reflection.GenericsExpert
import com.mgs.reflection.ParametrizedType
import spock.lang.Specification

import static java.util.Optional.of


class GenericsExpertFeatures extends Specification{
    GenericsExpert generics

    def "setup" (){
        generics = new GenericsExpert()
    }

    def "should read simple generics" (){
        when:
        ParametrizedType parametrizedType = generics.parseMethodReturnType(Generics.getMethod("getListOfStrings"))

        then:
        parametrizedType.name == "java.util.List"
        parametrizedType.specificClass == of(List)
        parametrizedType.childrenParametrizedTypes.isPresent()
        parametrizedType.childrenParametrizedTypes.get().size() == 1
        parametrizedType.childrenParametrizedTypes.get().get(0).specificClass.isPresent()
        parametrizedType.childrenParametrizedTypes.get().get(0).specificClass.get() == String
        parametrizedType.childrenParametrizedTypes.get().get(0).name == "java.lang.String"
        ! parametrizedType.childrenParametrizedTypes.get().get(0).childrenParametrizedTypes.isPresent()
    }

    def "should read nested generics" (){
        when:
        ParametrizedType parametrizedType = generics.parseMethodReturnType(Generics.getMethod("getNestedStrings"))

        then:
        parametrizedType.name == "java.util.List"
        parametrizedType.specificClass == of(List)
        parametrizedType.childrenParametrizedTypes.isPresent()
        parametrizedType.childrenParametrizedTypes.get().size() == 1
        parametrizedType.childrenParametrizedTypes.get().get(0).specificClass.isPresent()
        parametrizedType.childrenParametrizedTypes.get().get(0).specificClass.get() == List
        parametrizedType.childrenParametrizedTypes.get().get(0).name == "java.util.List<java.lang.String>"
        parametrizedType.childrenParametrizedTypes.get().get(0).childrenParametrizedTypes.isPresent()
        parametrizedType.childrenParametrizedTypes.get().get(0).childrenParametrizedTypes.get().get(0).specificClass.isPresent()
        parametrizedType.childrenParametrizedTypes.get().get(0).childrenParametrizedTypes.get().get(0).specificClass.get() == String
        parametrizedType.childrenParametrizedTypes.get().get(0).childrenParametrizedTypes.get().get(0).name == "java.lang.String"
        ! parametrizedType.childrenParametrizedTypes.get().get(0).childrenParametrizedTypes.get().get(0).childrenParametrizedTypes.isPresent()
    }

    def "should read unespecified generics" () {
        when:
        ParametrizedType parametrizedType = generics.parseMethodReturnType(Generics.getMethod("getUnespecified"))

        then:
        parametrizedType.name == "java.util.List"
        parametrizedType.specificClass == of(List)
        parametrizedType.childrenParametrizedTypes.isPresent()
        parametrizedType.childrenParametrizedTypes.get().size() == 1
        ! parametrizedType.childrenParametrizedTypes.get().get(0).specificClass.isPresent()
        parametrizedType.childrenParametrizedTypes.get().get(0).name == "T"
        ! parametrizedType.childrenParametrizedTypes.get().get(0).childrenParametrizedTypes.isPresent()
    }

    def "should read nested unespecified generics" (){
        when:
        ParametrizedType parametrizedType = generics.parseMethodReturnType(Generics.getMethod("getNestedUnespecified"))

        then:
        parametrizedType.name == "java.util.List"
        parametrizedType.specificClass == of(List)
        parametrizedType.childrenParametrizedTypes.isPresent()
        parametrizedType.childrenParametrizedTypes.get().size() == 1
        parametrizedType.childrenParametrizedTypes.get().get(0).specificClass.isPresent()
        parametrizedType.childrenParametrizedTypes.get().get(0).specificClass.get() == List
        parametrizedType.childrenParametrizedTypes.get().get(0).name == "java.util.List<T>"
        parametrizedType.childrenParametrizedTypes.get().get(0).childrenParametrizedTypes.isPresent()
        ! parametrizedType.childrenParametrizedTypes.get().get(0).childrenParametrizedTypes.get().get(0).specificClass.isPresent()
        parametrizedType.childrenParametrizedTypes.get().get(0).childrenParametrizedTypes.get().get(0).name == "T"
        ! parametrizedType.childrenParametrizedTypes.get().get(0).childrenParametrizedTypes.get().get(0).childrenParametrizedTypes.isPresent()
    }

    def "should parse raw generics" (){
        when:
        ParametrizedType parametrizedType = generics.parseMethodReturnType(Generics.getMethod("getIt"))

        then:
        parametrizedType.name == "T"
        ! parametrizedType.specificClass.isPresent()
        ! parametrizedType.childrenParametrizedTypes.isPresent()
    }

    private static interface Generics<T> {
        List<String> getListOfStrings()

        List<List<String>> getNestedStrings()

        List<T> getUnespecified()

        List<List<T>> getNestedUnespecified()

        T getIt ()
    }
}
