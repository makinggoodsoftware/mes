package com.mgs.mes.v4;

import com.google.common.collect.ImmutableList;
import com.mgs.mes.v3.mapper.EntityMapBuilder;
import com.mgs.mes.v3.mapper.MapEntity;
import com.mgs.mes.v4.typeParser.Declaration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class EntityMapListBuilder<T extends MapEntity> {
	private final Declaration typeDeclaration;
	private final BiFunction<Declaration, EntityMapBuilder, Object> creator;
	private final List<T> list = new ArrayList<>();

	public EntityMapListBuilder(Declaration typeDeclaration, BiFunction<Declaration, EntityMapBuilder, Object> creator) {
		this.typeDeclaration = typeDeclaration;
		this.creator = creator;
	}

	public EntityMapListBuilder<T> with (EntityMapBuilder<T> builder){
		//noinspection unchecked
		list.add((T) creator.apply(typeDeclaration, builder));
		return this;
	}

	public List<T> build (){
		return ImmutableList.copyOf(list);
	}
}
