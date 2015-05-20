package com.mgs.mes.v4

import spock.lang.Specification

class TypeParserSpecification extends Specification {
    TypeParser typeParser = new TypeParser()

    def "should parse simple type" (){
        when:
        ParsedType result= typeParser.parse(Integer)

        then:
        result.ownDeclaration.typeResolution.typeName == "java.lang.Integer"
        result.ownDeclaration.typeResolution.specificClass.get() == Integer
        result.ownDeclaration.parameters.size() == 0

        result.superDeclarations.size() == 0
    }

    def "should parse simple generic type" (){
        when:
        ParsedType result= typeParser.parse(DeclarationHolder.getMethod("simpleGenerics").genericReturnType)

        then:
        result.ownDeclaration.typeResolution.typeName == 'java.util.List<java.lang.String>'
        result.ownDeclaration.typeResolution.specificClass.get() == List
        result.ownDeclaration.parameters.size() == 1

        result.ownDeclaration.parameters.get("E").typeResolution.typeName == "java.lang.String"
        result.ownDeclaration.parameters.get("E").typeResolution.specificClass.get() == String
        result.ownDeclaration.parameters.get("E").parameters.size() == 0

        result.superDeclarations.size() == 0
    }

    private static interface DeclarationHolder{
        public List<String> simpleGenerics()
    }
}
