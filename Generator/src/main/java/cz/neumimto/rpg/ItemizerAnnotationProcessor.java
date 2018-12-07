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
@SupportedAnnotationTypes({"cz.neumimto.rpg.bridges.itemizer.ItemizerBean"})
public class ItemizerAnnotationProcessor extends AbstractProcessor {

    private Filer filerUtils;
    private Elements elementUtils;
    private TypeElement myAnnotationTypeElement;

    private String template =
            "\"public class EffectBeanFactory implements IItemBeanFactory {\n" +
                    "    @Override\n" +
                    "    public String getKeyId() {\n" +
                    "        return \"effect\";\n" +
                    "    }\n" +
                    "\n" +
                    "    @Override\n" +
                    "    public IItemBeanConfiguration build(ConfigurationNode node) {\n" +
                    "        return new IItemBeanConfiguration() {\n" +
                    "            @Override\n" +
                    "            public Key getKey() {\n" +
                    "                return NKeys.ITEM_EFFECTS;\n" +
                    "            }\n" +
                    "\n" +
                    "            @Override\n" +
                    "            public DataManipulator<?, ?> constructDataManipulator() {\n" +
                    "                List<?> list = node.getList(TypeToken.of(ItemEffectBean.class));\n" +
                    "                return new EffectsData(new HashMap<>());\n" +
                    "            }\n" +
                    "        };\n" +
                    "    }\n" +
                    "}";//fuck it
            

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filerUtils = processingEnv.getFiler();
        elementUtils = processingEnv.getElementUtils();
        myAnnotationTypeElement = elementUtils.getTypeElement("cz.neumimto.rpg.bridges.itemizer.ItemizerBean");
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
                            throw new RuntimeException(" -'" + element.getSimpleName()
                                    + "' Found multiple constuctors, but none of them annotated via @Generate.Constructor");
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
                        if (!javaTypes.containsKey(mirror.toString())) {
                            DeclaredType declaredType = (DeclaredType) varModel.asType();
                            TypeElement fieldTypeElement = (TypeElement) declaredType.asElement();
                            List<? extends Element> enclosedElements = fieldTypeElement.getEnclosedElements();
                            Map<String, String> elements = new HashMap<>();

                            String modelMapperFor = mirror.toString();
                            String modelSimpleName = ((DeclaredType) mirror).asElement().getSimpleName().toString();
                            for (Element enclosedElement : enclosedElements) {
                                if (enclosedElement.getKind() == ElementKind.FIELD) {
                                    TypeMirror typeMirror = enclosedElement.asType();
                                    String className = typeMirror.toString();
                                    String modelfieldName = enclosedElement.toString();
                                    elements.put(className, modelfieldName);
                                }
                            }
                            generateModelMapper(modelSimpleName, modelMapperFor, elements, element);
                        }
                    }

                    generateGlobalEffect(_template, (TypeElement) element, fieldName, model);

                }
            }

        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw new RuntimeException("Cannot create globaleffect ", e);
        }


        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton("cz.neumimto.rpg.bridges.itemizer.ItemizerBean");
    }

}
