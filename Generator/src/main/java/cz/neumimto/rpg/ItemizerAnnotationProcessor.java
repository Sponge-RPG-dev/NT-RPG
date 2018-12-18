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
            "\"public class %KeyId%BeanFactory implements IItemBeanFactory {\n" +
                    "    @Override\n" +
                    "    public String getKeyId() {\n" +
                    "        return \"%KeyId&%\";\n" +
                    "    }\n" +
                    "\n" +
                    "    @Override\n" +
                    "    public IItemBeanConfiguration build(ConfigurationNode node) {\n" +
                    "        return new IItemBeanConfiguration() {\n" +
                    "            @Override\n" +
                    "            public Key getKey() {\n" +
                    "                return %Key%;\n" +
                    "            }\n" +
                    "\n" +
                    "            @Override\n" +
                    "            public DataManipulator<?, ?> constructDataManipulator() {\n" +
                    "                List<?> list = node.getList(TypeToken.of(ItemEffectBean.class));\n" +
                    "                return new %DataManiulator%(list);\n" +
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
