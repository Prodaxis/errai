package org.jboss.errai.ioc.rebind.ioc.codegen;

import org.jboss.errai.ioc.rebind.ioc.codegen.builder.impl.DeclareAssignmentBuilder;
import org.jboss.errai.ioc.rebind.ioc.codegen.exception.InvalidTypeException;
import org.jboss.errai.ioc.rebind.ioc.codegen.meta.MetaClass;

import javax.enterprise.util.TypeLiteral;

/**
 * This class represents a variable.
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class Variable extends AbstractStatement {
    private String name;
    private MetaClass type;
    private Statement value;

    private Variable(String name, MetaClass type) {
        this.name = name;
        this.type = type;
    }

    private Variable(String name, MetaClass type, Object initialization) {
        this(name, type);
        initialize(initialization);
    }

    public void initialize(Object initialization) {
        this.type = (type == null) ? inferType(initialization) : type;
        value = GenUtil.convert(getContext(), initialization, type);
    }

    private MetaClass inferType(Object initialization) {
        Statement initStatement = GenUtil.generate(getContext(), initialization);
        MetaClass inferredType = (initStatement != null) ? initStatement.getType() : null;
        if (inferredType == null) {
            throw new InvalidTypeException("No type specified and no initialization provided to infer the type.");
        }

        return inferredType;
    }

    public static Variable create(String name, Class<?> type) {
        return new Variable(name, MetaClassFactory.get(type));
    }

    public static Variable create(String name, TypeLiteral<?> type) {
        return new Variable(name, MetaClassFactory.get(type));
    }

    public static Variable create(String name, MetaClass type) {
        return new Variable(name, type);
    }

    public static Variable create(String name, Object initialization) {
        return new Variable(name, null, initialization);
    }

    public static Variable create(String name, Class<?> type, Object initialization) {
        return new Variable(name, MetaClassFactory.get(type), initialization);
    }

    public static Variable create(String name, TypeLiteral<?> type, Object initialization) {
        return new Variable(name, MetaClassFactory.get(type), initialization);
    }

    public static Variable create(String name, MetaClass type, Object initialization) {
        return new Variable(name, type, initialization);
    }

    public static VariableReference get(final String name) {
        return new VariableReference() {
            public String getName() {
                return name;
            }

            public Statement getValue() {
                return null;
            }
        };
    }

    public VariableReference getReference() {
        return new VariableReference() {
            public String getName() {
                return name;
            }

            public MetaClass getType() {
                return type;
            }

            public Statement getValue() {
                return value;
            }
        };
    }

    public String getName() {
        return name;
    }

    public MetaClass getType() {
        return type;
    }

    private String hashString;

    private String hashString() {
        if (hashString == null) {
            hashString = Variable.class.getName() + ":" + name + ":" + type.getFullyQualifedName();
        }
        return hashString;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Variable
                && hashString().equals(Variable.class.getName() + ":" + name + ":" + ((Variable) o).type.getFullyQualifedName());
    }

    @Override
    public int hashCode() {
        return hashString().hashCode();
    }

    @Override
    public String toString() {
        return "Variable [name=" + name + ", type=" + type + "]";
    }

    public String generate(Context context) {
        return new DeclareAssignmentBuilder(getReference(), value).generate(context) + ";";
    }
}
