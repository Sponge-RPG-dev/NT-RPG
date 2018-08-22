package cz.neumimto.rpg;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.io.File;
import java.util.Collections;
import java.util.Set;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class GlobalEffectAnnotationProcessor extends AbstractProcessor {

    private Messager messager;
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();

    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(ClassGenerator.Generate.class)) {
            ElementKind kind = element.getKind();
            if (kind == ElementKind.CLASS) {
                ClassGenerator.Generate annotation = element.getAnnotation(ClassGenerator.Generate.class);
                String id = annotation.id();

            }
        }
        return false;
    }


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(ClassGenerator.Generate.class.getCanonicalName());
    }
}
