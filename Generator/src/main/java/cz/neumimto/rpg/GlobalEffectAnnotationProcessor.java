package cz.neumimto.rpg;

import com.sun.source.tree.*;
import com.sun.source.util.Trees;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;
import java.io.BufferedWriter;
import java.util.*;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"cz.neumimto.rpg.effects.Generate"})
public class GlobalEffectAnnotationProcessor extends AbstractProcessor {

    private Filer filerUtils;
    private Elements elementUtils;
    private TypeElement myAnnotationTypeElement;
    private Trees trees;

    private String template =  //fuck it
            "\n" +
            "import cz.neumimto.rpg.effects.IEffectConsumer;\n" +
            "import cz.neumimto.rpg.effects.IGlobalEffect;\n" +
     //       "import %import.effect%;\n" +
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
    private String init3_void = "%effect%(consumer, duration, null)";

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filerUtils = processingEnv.getFiler();
        elementUtils = processingEnv.getElementUtils();
        myAnnotationTypeElement = elementUtils.getTypeElement("cz.neumimto.rpg.effects.Generate");
        trees = Trees.instance(processingEnv);
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

                    List<MethodTree> methodTrees = new ArrayList<>();
                    for (Element enclosedElement : element.getEnclosedElements()) {
                        if (enclosedElement.getKind() == ElementKind.CONSTRUCTOR) {
                            ConstructorScanner methodScanner = new ConstructorScanner();
                            MethodTree methodTree = methodScanner.scan((ExecutableElement) enclosedElement, this.trees);
                            methodTrees.add(methodTree);
                        }
                    }
                    System.out.println(element.getSimpleName() + " found " + methodTrees.size() + " constructors");
                    MethodTree methodTree = null;
                    if (methodTrees.size() == 1) {
                        methodTree = methodTrees.get(0);

                    } else {
                        mt:
                        for (MethodTree mt : methodTrees) {
                            List<? extends AnnotationTree> annotations1 = mt.getModifiers().getAnnotations();
                            for (AnnotationTree annotationTree : annotations1) {
                                Tree annotationType = annotationTree.getAnnotationType();
                                if (annotationType.toString().equalsIgnoreCase("Generate.Constructor")) {
                                    methodTree = mt;
                                    break mt;
                                }
                            }
                        }
                        if (methodTree == null) {
                            System.out.println(" - Found multiple constuctors, but none of them annotated via @Generate.Constructor");
                            return true;
                        }
                    }
                    System.out.println("Found valid constructor - " + methodTree);

                    List<? extends VariableTree> parameters = methodTree.getParameters();
                    String _template = template;
                    String model = null;
                    if (parameters.size() == 1) {
                        _template = _template.replaceAll("%init%", init1);
                    } else if (parameters.size() == 2) {
                        _template = _template.replaceAll("%init%", init2);
                    } else {
                        System.out.println(parameters.get(2).toString());
                        if (parameters.get(2).toString().startsWith("Void")) {
                            _template = _template.replaceAll("%init%", init3_void);
                        } else {
                            _template = _template.replaceAll("%init%", init3);
                            VariableTree tree = parameters.get(2);
                            System.out.println("ASD");
                            if (tree.getType().getKind() == Tree.Kind.PRIMITIVE_TYPE) {
                                model = tree.getType().toString();
                            }
                        }
                    }


                    TypeElement enclosingClass = (TypeElement) element;
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
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return true;
    }


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton("cz.neumimto.rpg.effects.Generate");
    }
}
