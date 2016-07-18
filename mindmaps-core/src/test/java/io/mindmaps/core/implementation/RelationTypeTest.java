package io.mindmaps.core.implementation;

import io.mindmaps.core.exceptions.ConceptException;
import io.mindmaps.core.exceptions.ErrorMessage;
import io.mindmaps.core.model.RelationType;
import io.mindmaps.core.model.RoleType;
import io.mindmaps.factory.MindmapsTinkerGraphFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Collection;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

public class RelationTypeTest {

    private MindmapsTransactionImpl mindmapsGraph;
    private RelationType relationType;
    private RoleType role1;
    private RoleType role2;
    private RoleType role3;

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws ConceptException {
        mindmapsGraph = (MindmapsTransactionImpl) MindmapsTinkerGraphFactory.getInstance().newGraph().newTransaction();
        mindmapsGraph.initialiseMetaConcepts();

        //Building
        relationType = mindmapsGraph.putRelationType("relationType");
        role1 = mindmapsGraph.putRoleType("role1");
        role2 = mindmapsGraph.putRoleType("role2");
        role3 = mindmapsGraph.putRoleType("role3");

        relationType.hasRole(role1);
        relationType.hasRole(role2);
        relationType.hasRole(role3);
    }
    @After
    public void destroyGraphAccessManager() throws Exception {
        mindmapsGraph.close();
    }

    @Test
    public void updateBaseTypeCheck(){
        RelationType relationType = mindmapsGraph.putRelationType("Test");
        RelationType relationType2 = mindmapsGraph.putRelationType("Test");
        assertEquals(relationType, relationType2);
    }

    @Test
    public void overrideFail(){
        RoleType original = mindmapsGraph.putRoleType("Role Type");

        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage(allOf(
                containsString(ErrorMessage.ID_ALREADY_TAKEN.getMessage(original.getId(), original.toString()))
        ));

        mindmapsGraph.putRelationType(original.getId());
    }

    @Test
    public void testGetRoles() throws Exception {
        Collection<RoleType> roles = relationType.hasRoles();
        assertEquals(3, roles.size());
        assertTrue(roles.contains(role1));
        assertTrue(roles.contains(role2));
        assertTrue(roles.contains(role3));
    }

    @Test
    public void testRoleType(){
        RelationType c1 = mindmapsGraph.putRelationType("c1");
        RoleType c2 = mindmapsGraph.putRoleType("c2");
        RoleType c3 = mindmapsGraph.putRoleType("c3");
        assertNull(c2.relationType());

        c1.hasRole(c2);
        c1.hasRole(c3);
        assertTrue(c1.hasRoles().contains(c2));
        assertTrue(c1.hasRoles().contains(c3));

        c1.deleteHasRole(c2);
        assertFalse(c1.hasRoles().contains(c2));
        assertTrue(c1.hasRoles().contains(c3));
    }


}