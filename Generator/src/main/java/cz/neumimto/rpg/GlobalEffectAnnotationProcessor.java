package cz.neumimto.rpg;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
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
    private String template =  //fuck it
            "\n" +
            "import cz.neumimto.rpg.effects.IEffectConsumer;\n" +
            "import cz.neumimto.rpg.effects.IGlobalEffect;\n" +
            "import %import.effect%;\n" +
            "import cz.neumimto.rpg.effects.model.EffectModelFactory;\n" +
            "\n" +
            "import java.util.Map;\n" +
            "\n" +
            "public class %effect%Global implements IGlobalEffect<%effect%> {\n" +
            "\tpublic DamageBonusGlobal() {\n" +
            "\t}\n" +
            "\n" +
            "\t@Override\n" +
            "\tpublic %effect% construct(IEffectConsumer consumer, long duration, Map<String, String> value) {\n" +
            "\t\treturn new %effect%(consumer, duration, EffectModelFactory.create(%effect%.class, value, %model%.class));\n" +
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
                    TypeElement enclosingClass = (TypeElement) element;
                    String classname = "Global" + enclosingClass.getQualifiedName().toString();
                    JavaFileObject javaFileObject = filerUtils.createSourceFile(classname);
                    try (BufferedWriter writer = new BufferedWriter(javaFileObject.openWriter())) {
                        System.out.println("Generating source code for Global" + classname);
                        if (elementUtils.getPackageOf(enclosingClass).getQualifiedName().length() > 0) {
                            writer.write("package " + elementUtils.getPackageOf(enclosingClass).getQualifiedName() + ";");
                            writer.newLine();
                        }
                        writer.write(template
                                    .replaceAll("%effect%", enclosingClass.getSimpleName().toString())
                                    .replaceAll("%import\\.effect%", enclosingClass.getEnclosingElement().getSimpleName().toString()));
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
