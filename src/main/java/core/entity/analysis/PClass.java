package core.entity.analysis;

import lombok.*;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.List;

/**
 * client class
 */
//@Data //may cause stackoverflow
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"tree"})
public class PClass {
    private String cid;
    private String className;
    private String typeName;
    private int startIndex;
    private int endIndex;
    private List<String> fieldDeclarations;
    private List<String> superClassNames;
    private List<PClass> superClasses;

    private ParseTree tree;

    private List<PMethod> pMethodList;

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public List<String> getFieldDeclarations() {
        return fieldDeclarations;
    }

    public void setFieldDeclarations(List<String> fieldDeclarations) {
        this.fieldDeclarations = fieldDeclarations;
    }

    public List<String> getSuperClassNames() {
        return superClassNames;
    }

    public void setSuperClassNames(List<String> superClassNames) {
        this.superClassNames = superClassNames;
    }

    public List<PClass> getSuperClasses() {
        return superClasses;
    }

    public void setSuperClasses(List<PClass> superClasses) {
        this.superClasses = superClasses;
    }

    public ParseTree getTree() {
        return tree;
    }

    public void setTree(ParseTree tree) {
        this.tree = tree;
    }

    public List<PMethod> getPMethodList() {
        return pMethodList;
    }

    public void setPMethodList(List<PMethod> pMethodList) {
        this.pMethodList = pMethodList;
    }
}

