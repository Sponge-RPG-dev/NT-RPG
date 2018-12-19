package cz.neumimto.rpg;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"cz.neumimto.rpg.bridges.itemizer.ItemizerFactoryMethod"})
public class ItemizerAnnotationProcessor extends AbstractProcessor {

    private Filer filerUtils;
    private Elements elementUtils;
    private TypeElement myAnnotationTypeElement;

    private String template =
            "\nimport ninja.leaping.configurate.ConfigurationNode;" +
            "\nimport com.onaple.itemizer.service.IItemBeanFactory;" +
            "\nimport org.spongepowered.api.data.key.Key;" +
            "\nimport org.spongepowered.api.data.manipulator.DataManipulator;" +
            "\nimport com.onaple.itemizer.data.beans.AbstractItemBeanConfiguration;" +
            "\nimport com.onaple.itemizer.data.beans.IItemBeanConfiguration;" +
            "\nimport com.google.common.reflect.TypeToken;" +
            "\nimport ninja.leaping.configurate.objectmapping.ObjectMappingException;" +

                    "" +
                    "\n\npublic class %KeyId%BeanFactory implements IItemBeanFactory {\n" +
                    "    @Override\n" +
                    "    public String getKeyId() {\n" +
                    "        return \"%KeyId%\";\n" +
                    "    }\n" +
                    "\n" +
                    "    @Override\n" +
                    "    public IItemBeanConfiguration build(final ConfigurationNode node) {\n" +
                    "        return new IItemBeanConfiguration() {\n" +
                    "            @Override\n" +
                    "            public Key getKey() {\n" +
                    "                return %Key%;\n" +
                    "            }\n" +
                    "\n" +
                    "            @Override\n" +
                    "            public DataManipulator<?, ?> constructDataManipulator() {\n" +
                    "                %mappingFunc%\n" +
                    "                return new %DataManiulator%%ctorcall%;\n" +
                    "            }\n" +
                    "        };\n" +
                    "    }\n" +
                    "}";//fuck it

        private String list =
                "java.util.List<%model%> o = null;\n" +
                        "try {\n" +
                " o = node.getList(TypeToken.of(%model%.class));\n" +
                "} catch (ObjectMappingException e) {\n" +
                "        throw new RuntimeException(e);\n" +
                "    }";
        private String set = "java.util.Set<%model%> o = new java.util.HashSet(node.getList(TypeToken.of(%model%.class)));";



        //private String set = "Map<?, ?> o = node.getMap(TypeToken.of(%model%.class));";


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filerUtils = processingEnv.getFiler();
        elementUtils = processingEnv.getElementUtils();
        myAnnotationTypeElement = elementUtils.getTypeElement("cz.neumimto.rpg.bridges.itemizer.ItemizerFactoryMethod");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(myAnnotationTypeElement);

            for (Element element : elementsAnnotatedWith) {
                Map<String, String> mappings = new HashMap<>();
                if (element.getKind() == ElementKind.CONSTRUCTOR) {
                    List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();
                    for (AnnotationMirror annotationMirror : annotationMirrors) {
                        DeclaredType annotationType = annotationMirror.getAnnotationType();
                        if (annotationType.asElement().getSimpleName().toString().equals("ItemizerFactoryMethod")) {
                            Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = annotationMirror.getElementValues();
                            for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> a : elementValues.entrySet()) {
                                if (a.getKey().getSimpleName().toString().equalsIgnoreCase("keyid")) {
                                    mappings.put("%KeyId%", a.getValue().getValue().toString());
                                } else if (a.getKey().getSimpleName().toString().equalsIgnoreCase("keyStaticPath")) {
                                    mappings.put("%Key%", a.getValue().getValue().toString());
                                }
                            }
                        }
                    }
                    Element enclosingElement = element.getEnclosingElement();
                    if (enclosingElement.getKind() == ElementKind.CLASS) {
                        mappings.put("%DataManiulator%", enclosingElement.asType().toString());
                    }
                    List<? extends VariableElement> parameters = ((ExecutableElement) element).getParameters();
                    if (parameters.size() == 0) {
                        mappings.put("%ctorcall%", "()");
                        mappings.put("%mappingFunc%", "");
                    } else if (parameters.size() == 1) {
                        mappings.put("%ctorcall%", "(o)");
                        VariableElement variableElement = parameters.get(0);
                        String s = variableElement.asType().toString();
                        if (s.contains("<")) {
                            String[] split = s.split("<");
                            split[1] = split[1].replaceAll(">", "");
                            String s1 = split[0];
                            if (s1.equalsIgnoreCase(List.class.getCanonicalName())) {
                                String s2 = list.replaceAll("%model%", split[1]);
                                mappings.put("%mappingFunc%", s2);
                            } else if (s1.equalsIgnoreCase(Set.class.getCanonicalName())) {

                            } else if (s1.equalsIgnoreCase(Map.class.getCanonicalName())) {

                            }
                        } else {

                        }
                    } else {
                        throw new IllegalStateException("@ItemizerFactoryMethod has to be put on a constructor having only one or zero parameters. " +
                                enclosingElement.asType().toString() + " had " + parameters.size());

                    }
                    String file = template;

                    for (Map.Entry<String, String> a : mappings.entrySet()) {
                        file = file.replaceAll(a.getKey(), a.getValue());
                    }

                    JavaFileObject javaFileObject = null;

                    try {
                        javaFileObject = filerUtils.createSourceFile(mappings.get("%KeyId%")+"BeanFactory");
                        try (BufferedWriter writer = new BufferedWriter(javaFileObject.openWriter())) {
                            if (elementUtils.getPackageOf(element).getQualifiedName().length() > 0) {
                                writer.write("package cz.neumimto.rpg.bridges.itemizer;");
                                writer.newLine();
                            }
                            writer.write(file);
                            writer.flush();
                        }
                    } catch (IOException ee) {
                        throw new RuntimeException(ee);
                    }
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
        return Collections.singleton("cz.neumimto.rpg.bridges.itemizer.ItemizerFactoryMethod");
    }

}
