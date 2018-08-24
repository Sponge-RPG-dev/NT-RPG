package cz.neumimto.rpg;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"cz.neumimto.rpg.effects.Generate"})
public class GlobalEffectAnnotationProcessor extends AbstractProcessor {

    private Filer filerUtils;
    private Elements elementUtils;
    private TypeElement myAnnotationTypeElement;

    private String template =  //fuck it
            "\n" +
                    "import cz.neumimto.rpg.effects.IEffectConsumer;\n" +
                    "import cz.neumimto.rpg.effects.IGlobalEffect;\n" +
                    "import cz.neumimto.rpg.effects.model.EffectModelFactory;\n" +
                    "\n" +
                    "import java.util.Map;\n" +
                    "\n" +
                    "public class %effect%Global implements IGlobalEffect<%effect%> {\n" +
                    "\tpublic %effect%Global() {\n" +
                    "\t}\n" +
                    "\n" +
                    "\t@Override\n" +
                    "\tpublic %effect% construct(IEffectConsumer consumer, long duration, Map<String, String> value) {\n" +
                    "\t\treturn new %init%;\n" +
                    "\t}\n" +
                    "\n" +
                    "\t@Override\n" +
                    "\tpublic String getName() {\n" +
                    "\t\treturn %effect%.%effect.nameField%;\n" +
                    "\t}\n" +
                    "\n" +
                    "\t@Override\n" +
                    "\tpublic Class<%effect%> asEffectClass() {\n" +
                    "\t\treturn %effect%.class;\n" +
                    "\t}\n" +
                    "}\n";

    private String init2 = "%effect%(consumer, duration)";
    private String init1 = "%effect%(consumer)";
    private String init3 = "%effect%(consumer, duration, EffectModelFactory.create(%effect%.class, value, %model%.class))";
    private String init4 = "%effect%(consumer, EffectModelFactory.create(%effect%.class, value, %model%.class))";

    private String init3_void = "%effect%(consumer, duration, null)";

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filerUtils = processingEnv.getFiler();
        elementUtils = processingEnv.getElementUtils();
        myAnnotationTypeElement = elementUtils.getTypeElement("cz.neumimto.rpg.effects.Generate");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(myAnnotationTypeElement);

            for (Element element : elementsAnnotatedWith) {
                if (element.getKind() == ElementKind.CLASS) {
                    List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();
                    String fieldName = null;
                    for (AnnotationMirror annotationMirror : annotationMirrors) {
                        DeclaredType annotationType = annotationMirror.getAnnotationType();
                        if (annotationType.asElement().getSimpleName().toString().equals("Generate")) {
                            Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = annotationMirror.getElementValues();
                            for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> a : elementValues.entrySet()) {
                                if (a.getKey().getSimpleName().toString().equals("id")) {
                                    fieldName = a.getValue().getValue().toString();
                                }
                            }
                        }
                    }

                    List<ExecutableElement> methodTrees = new ArrayList<>();
                    for (Element enclosedElement : element.getEnclosedElements()) {
                        if (enclosedElement.getKind() == ElementKind.CONSTRUCTOR) {
                            ExecutableElement exec = (ExecutableElement) enclosedElement;

                            methodTrees.add(exec);
                        }
                    }
                    System.out.println(element.getSimpleName() + " found " + methodTrees.size() + " constructors");
                    ExecutableElement methodTree = null;
                    if (methodTrees.size() == 1) {
                        methodTree = methodTrees.get(0);

                    } else {
                        mt:
                        for (ExecutableElement mt : methodTrees) {
                            List<? extends AnnotationMirror> annotations1 = mt.getAnnotationMirrors();
                            for (AnnotationMirror annotationMirror : annotations1) {
                                String name = ((QualifiedNameable) annotationMirror.getAnnotationType().asElement()).getQualifiedName().toString();
                                if (name.contains("Generate.Constructor")) {
                                    methodTree = mt;
                                    break mt;
                                }
                            }
                        }
                        if (methodTree == null) {
                            throw new RuntimeException(" -'" + element.getSimpleName() + "' Found multiple constuctors, but none of them annotated via @Generate.Constructor");
                        }
                    }
                    System.out.println("Found valid constructor - " + methodTree);

                    List<? extends VariableElement> parameters = methodTree.getParameters();
                    String _template = template;
                    String model = null;
                    VariableElement varModel = null;
                    if (parameters.size() == 1) {
                        _template = _template.replaceAll("%init%", init1);
                    } else if (parameters.size() == 2) {
                        if (parameters.get(1).getKind() == ElementKind.LOCAL_VARIABLE && parameters.get(1).toString().equalsIgnoreCase("long")) {
                            _template = _template.replaceAll("%init%", init2);
                        } else {
                            _template = _template.replaceAll("%init%", init4);
                            model = parameters.get(1).asType().toString();
                            varModel = parameters.get(1);
                        }
                    } else {
                        if (parameters.get(2).toString().startsWith("Void")) {
                            _template = _template.replaceAll("%init%", init3_void);
                        } else {
                            _template = _template.replaceAll("%init%", init3);
                            VariableElement tree = parameters.get(2);
                            TypeMirror typeMirror = tree.asType();
                            varModel = parameters.get(2);
                            if (typeMirror instanceof PrimitiveType) {
                                model = tree.asType().toString().toLowerCase();
                            } else {
                                model = tree.asType().toString();
                            }
                        }
                    }
                    TypeMirror mirror = varModel.asType();
                    if (varModel.asType() instanceof DeclaredType) {


                        DeclaredType declaredType = (DeclaredType) varModel.asType();
                        TypeElement fieldTypeElement = (TypeElement) declaredType.asElement();
                        List<? extends Element> enclosedElements = fieldTypeElement.getEnclosedElements();
                        for (Element enclosedElement : enclosedElements) {
                            if (enclosedElement.getKind() == ElementKind.FIELD) {
                                //todo
                            }
                        }
                    }

                    generateModelMapper(varModel);

                    generateGlobalEffect(_template, (TypeElement) element, fieldName, model);

                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Cannot create globaleffect ", e);
        }


        return true;
    }

    private void generateGlobalEffect(String _template, TypeElement enclosingClass, String fieldName, String model) throws IOException {
        String classname = enclosingClass.getQualifiedName().toString() + "Global";

        JavaFileObject javaFileObject = filerUtils.createSourceFile(classname);
        try (BufferedWriter writer = new BufferedWriter(javaFileObject.openWriter())) {
            System.out.println("Generating source code for " + classname);
            if (elementUtils.getPackageOf(enclosingClass).getQualifiedName().length() > 0) {
                writer.write("package " + elementUtils.getPackageOf(enclosingClass).getQualifiedName() + ";");
                writer.newLine();
            }
            _template = _template
                    .replaceAll("%effect%", enclosingClass.getSimpleName().toString())
                    .replaceAll("%import\\.effect%", enclosingClass.getEnclosingElement().getSimpleName().toString())
                    .replaceAll("%effect\\.nameField%", fieldName);
            if (model != null) {
                _template = _template.replaceAll("%model%", model);
            }

            writer.write(_template);

            writer.flush();
        }
    }

    private void generateModelMapper(VariableElement model) {

    }


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton("cz.neumimto.rpg.effects.Generate");
    }
}
