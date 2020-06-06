package gov.noaa.pmel.tmap.las.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * An event for adding a value for the list of active constraints for a SelectionConstraint.  If the is not display panel, receivers should build the panel in which the values is to be added.
 * @author rhs
 *
 */
public class VariableConstraintEvent extends GwtEvent<VariableConstraintEvent.Handler> {
    private String lhs;
    private String op1;
    private String variable;
    private String op2;
    private String rhs;
    private boolean apply;
    private String dsid;
    private String varid;
    
    public static final Type<VariableConstraintEvent.Handler> TYPE = new Type<VariableConstraintEvent.Handler>();
    /**
     * Generate a constraint of the form LHS op Variable op RHS, usually 21. < sst <= 30. or similar, but either side can be null to get sst <=30. or 21. < sst. for example.
     * @param the value on the left of the inequality
     * @param the first operator for "between" type constraints
     * @param the variable
     * @param the second operator for "between" type constraints
     * @param the value o the right of the inequality
     */
    public VariableConstraintEvent(String dsid, String varid, String lhs, String op1, String variable, String rhs, String op2, boolean apply) {
        super();
        this.dsid = dsid;
        this.varid = varid;
        this.lhs = lhs;
        this.op1 = op1;
        this.variable = variable;
        this.op2 = op2;
        this.rhs = rhs;
        this.apply = apply;
    }

    public interface Handler extends EventHandler {
        void onChange(VariableConstraintEvent event);
    }

    @Override
    protected void dispatch(Handler handler) {
        handler.onChange(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<Handler> getAssociatedType() {
        return TYPE;
    }

    public String getLhs() {
        return lhs;
    }

    public void setLhs(String lhs) {
        this.lhs = lhs;
    }

    public String getOp1() {
        return op1;
    }

    public void setOp1(String op1) {
        this.op1 = op1;
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    public String getOp2() {
        return op2;
    }

    public void setOp2(String op2) {
        this.op2 = op2;
    }

    public String getRhs() {
        return rhs;
    }

    public void setRhs(String rhs) {
        this.rhs = rhs;
    }

    public boolean isApply() {
        return apply;
    }

    public void setApply(boolean apply) {
        this.apply = apply;
    }
    public String getDsid() {
        return dsid;
    }

    public void setDsid(String dsid) {
        this.dsid = dsid;
    }

    public String getVarid() {
        return varid;
    }

    public void setVarid(String varid) {
        this.varid = varid;
    }

    public static Type<VariableConstraintEvent.Handler> getType() {
        return TYPE;
    }

    
}
