package cz.neumimto.rpg;

import com.sun.source.tree.MethodTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import java.util.ArrayList;
import java.util.List;

public class ConstructorScanner  extends TreePathScanner<List<MethodTree>, Trees> {
    private List<MethodTree> methodTrees = new ArrayList<>();

    public MethodTree scan(ExecutableElement methodElement, Trees trees) {
        assert methodElement.getKind() == ElementKind.CONSTRUCTOR;

        List<MethodTree> methodTrees = this.scan(trees.getPath(methodElement), trees);
        assert methodTrees.size() == 1;

        return methodTrees.get(0);
    }

    @Override
    public List<MethodTree> scan(TreePath treePath, Trees trees) {
        super.scan(treePath, trees);
        return this.methodTrees;
    }

    @Override
    public List<MethodTree> visitMethod(MethodTree methodTree, Trees trees) {
        this.methodTrees.add(methodTree);
        return super.visitMethod(methodTree, trees);
    }
}