package com.sys1yagi.fragmentcreator.writer;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.sys1yagi.fragmentcreator.FragmentCreator;
import com.sys1yagi.fragmentcreator.model.FragmentCreatorModel;
import com.sys1yagi.fragmentcreator.annotation.Args;
import com.sys1yagi.fragmentcreator.exception.UnsupportedTypeException;
import com.sys1yagi.fragmentcreator.util.ArrayListCreator;
import com.sys1yagi.fragmentcreator.util.MirrorUtils;
import com.sys1yagi.fragmentcreator.util.SerializerHolder;
import com.sys1yagi.fragmentcreator.util.StringUtils;

import android.os.Bundle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleTypeVisitor6;

public class FragmentCreatorWriter {

    FragmentCreatorModel model;

    ProcessingEnvironment environment;

    public FragmentCreatorWriter(ProcessingEnvironment environment, FragmentCreatorModel model) {
        this.environment = environment;
        this.model = model;
    }

    public void write(Filer filer) throws IOException {
        FragmentCreatorBuilderGenerator fragmentCreatorBuilderGenerator =
                new FragmentCreatorBuilderGenerator(environment);
        FragmentCreatorReadGenerator fragmentCreatorReadGenerator = new FragmentCreatorReadGenerator(environment);

        TypeSpec.Builder classBuilder = createClassBuilder();

        classBuilder.addType(fragmentCreatorBuilderGenerator.create(model));
        classBuilder.addMethod(fragmentCreatorReadGenerator.createReadMethod(model));
        classBuilder.addMethod(fragmentCreatorBuilderGenerator.createBuilderNewBuilder(model, model.getArgsList()));

        TypeSpec outClass = classBuilder.build();
        JavaFile.builder(model.getPackageName(), outClass)
                .addFileComment("This file was generated by fragment-creator. Do not modify!")
                .build()
                .writeTo(filer);
    }

    private TypeSpec.Builder createClassBuilder(){
        TypeSpec.Builder classBuilder =TypeSpec.classBuilder(model.getCreatorClassName());
        classBuilder.addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        ClassName superClassName = ClassName.get(FragmentCreator.class);
        classBuilder.superclass(superClassName);
        return classBuilder;
    }
}
