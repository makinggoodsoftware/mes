package com.mgs.mes.meta

import com.mgs.config.mes.meta.MetaConfig
import com.mgs.config.reflection.ReflectionConfig
import com.mgs.mes.context.EntityDescriptor
import com.mgs.mes.meta.utils.Validator
import com.mgs.mes.model.Entity
import com.mgs.mes.model.EntityBuilder
import com.mgs.mes.model.OneToMany
import com.mgs.mes.model.OneToOne
import spock.lang.Specification
import spock.lang.Unroll

class ValidatorSpecification extends Specification {
    Validator testObj
    MetaConfig metaConfig = new MetaConfig(new ReflectionConfig())

    def "setup" (){
        testObj = metaConfig.validator()
    }


    def "should validate entity interfaces" (){
        when:
        testObj.validate(new EntityDescriptor<>(Getter1, Getter1Builder))

        then:
        notThrown Exception
    }

    def "should validate one to one interfaces" (){
        when:
        testObj.validate(new EntityDescriptor<>(OneToOneGetter, OneToOneGetterBuilder))

        then:
        notThrown Exception
    }

    def "should validate one to many interfaces" (){
        when:
        testObj.validate(new EntityDescriptor<>(OneToManyGetter, OneToOneManyBuilder))

        then:
        notThrown Exception
    }


    @Unroll
    def "should flag interfaces as not valid when the combination of methods is not correct" (){
        when:
        testObj.validate(new EntityDescriptor<>(getter, setter))

        then:
        thrown IllegalArgumentException

        where:
        getter          | setter
        Getter1         | Builder2
        InvalidGetter   | Builder2
        Getter1         | Builder1Wrong1
        Getter1         | Builder1Wrong2
    }

    public static interface InvalidGetter extends Entity{}

    public static interface Getter1 extends Entity{
        String getField1();
        Getter2 getChild();
    }

    public static interface Getter1Builder extends EntityBuilder<Getter1>{
        Getter1Builder withField1 (String field1);
        Getter1Builder withChild (Getter2 child);
    }

    public static interface Getter2 extends Entity{}

    public static interface Builder1Wrong1 extends EntityBuilder{
        Builder1Wrong1 withField1 (int field1);
        Builder1Wrong1 withChild (Getter2 child);
    }

    public static interface Builder1Wrong2 extends EntityBuilder{
        Builder1Wrong1 withField1 (String field1);
        Builder1Wrong1 withField2 (String field2);
        Builder1Wrong1 withChild (Getter2 child);
    }

    public static interface Builder2 extends EntityBuilder{
        Builder2 withField1 (String field1);
        Builder2 withField2 (String field2);
    }


    public static interface OneToOneGetter extends Entity{
        OneToOne<Getter1> getReference ()
    }

    public static interface OneToOneGetterBuilder extends EntityBuilder<OneToOneGetter> {
        OneToOneGetterBuilder withReference (Getter1 getter1)
    }

    public static interface OneToManyGetter extends Entity{
        OneToMany<Getter1> getReferences ()
    }

    public static interface OneToOneManyBuilder extends EntityBuilder<OneToOneGetter> {
        OneToOneManyBuilder withReferences (List<Getter1> getter1)
    }

}
