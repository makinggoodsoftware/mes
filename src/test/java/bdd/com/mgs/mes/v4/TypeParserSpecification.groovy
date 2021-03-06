package com.mgs.mes.v4

import com.mgs.mes.v4.typeParser.ParsedType
import com.mgs.mes.v4.typeParser.TypeParser
import spock.lang.Specification

class TypeParserSpecification extends Specification {
    TypeParser typeParser = new TypeParser()

    def "should parse simple type" (){
        when:
        ParsedType result= typeParser.parse(Integer)

        then:
        result.ownDeclaration.typeResolution.specificClass.get() == Integer
        ! result.ownDeclaration.typeResolution.parameterizedType.isPresent()
        result.ownDeclaration.parameters.size() == 0

        result.superDeclarations.size() == 1
        result.superDeclarations.get(Comparable).ownDeclaration.typeResolution.specificClass.get() == Comparable
        result.superDeclarations.get(Comparable).ownDeclaration.typeResolution.parameterizedType.isPresent()
        result.superDeclarations.get(Comparable).ownDeclaration.parameters.size() == 1
        result.superDeclarations.get(Comparable).ownDeclaration.parameters.get("T").typeResolution.specificClass.get() == Integer
        result.superDeclarations.get(Comparable).ownDeclaration.parameters.get("T").parameters.size() == 0
    }

    def "should parse simple generic type" (){
        when:
        ParsedType result= typeParser.parse(DeclarationHolder.getMethod("simpleGenerics").genericReturnType)

        then:
        result.ownDeclaration.typeResolution.specificClass.get() == List
        result.ownDeclaration.typeResolution.parameterizedType.isPresent()
        result.ownDeclaration.parameters.size() == 1

        result.ownDeclaration.parameters.get("E").typeResolution.specificClass.get() == String
        ! result.ownDeclaration.parameters.get("E").typeResolution.parameterizedType.isPresent()
        result.ownDeclaration.parameters.get("E").parameters.size() == 0

        result.superDeclarations.size() == 2

        result.superDeclarations.get(Collection).ownDeclaration.typeResolution.specificClass.get() == Collection
        result.superDeclarations.get(Collection).ownDeclaration.typeResolution.parameterizedType.isPresent()
        result.superDeclarations.get(Collection).ownDeclaration.parameters.size() == 1
        result.superDeclarations.get(Collection).ownDeclaration.parameters.get("E").typeResolution.specificClass.get() == String
        ! result.superDeclarations.get(Collection).ownDeclaration.parameters.get("E").typeResolution.parameterizedType.isPresent()
        result.superDeclarations.get(Collection).ownDeclaration.parameters.get("E").parameters.size() == 0

        result.superDeclarations.get(Iterable).ownDeclaration.typeResolution.specificClass.get() == Iterable
        result.superDeclarations.get(Iterable).ownDeclaration.typeResolution.parameterizedType.isPresent()
        result.superDeclarations.get(Iterable).ownDeclaration.parameters.size() == 1
        result.superDeclarations.get(Iterable).ownDeclaration.parameters.get("T").typeResolution.specificClass.get() == String
        ! result.superDeclarations.get(Iterable).ownDeclaration.parameters.get("T").typeResolution.parameterizedType.isPresent()
        result.superDeclarations.get(Iterable).ownDeclaration.parameters.get("T").parameters.size() == 0
    }

    def "should parse unresolved type" (){
        when:
        ParsedType result= typeParser.parse(GenericDeclarationHolder.getMethod("unresolved").genericReturnType)

        then:
        ! result.ownDeclaration.typeResolution.specificClass.isPresent()
        result.ownDeclaration.parameters.size() == 0
    }

    def "should parse indirect generic type" (){
        when:
        ParsedType result= typeParser.parse(DeclarationHolder.getMethod("indirectDeclarationHolder").genericReturnType)

        then:
        result.ownDeclaration.typeResolution.specificClass.get() == IndirectDeclarationHolder
        result.ownDeclaration.parameters.size() == 0

        result.superDeclarations.size() == 1
    }

    private static interface DeclarationHolder{
        public List<String> simpleGenerics()
        public IndirectDeclarationHolder indirectDeclarationHolder()
    }

    private static interface GenericDeclarationHolder<T>{
        T unresolved()
    }

    private static interface IndirectDeclarationHolder extends GenericDeclarationHolder<String>{
    }
}
