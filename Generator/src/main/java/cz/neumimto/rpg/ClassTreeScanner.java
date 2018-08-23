package cz.neumimto.rpg;


import com.sun.source.tree.ClassTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import java.util.ArrayList;
import java.util.List;

public class ClassTreeScanner extends TreePathScanner<ClassTree, Trees> {
    private ClassTree classTree;

    public ClassTree scan(ExecutableElement element, Trees trees) {
        assert element.getKind() == ElementKind.CLASS;

        ClassTree methodTrees = this.scan(trees.getPath(element), trees);
        return methodTrees;
    }

    @Override
    public ClassTree scan(TreePath treePath, Trees trees) {
        super.scan(treePath, trees);
        return classTree;
    }

    @Override
    public ClassTree visitClass(ClassTree classTree, Trees trees) {
        this.classTree = classTree;
        return super.visitClass(classTree, trees);
    }

}