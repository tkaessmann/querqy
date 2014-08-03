package querqy.rewrite.commonrules.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import querqy.model.Term;
import static org.hamcrest.Matchers.*;

public class RulesCollectionTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testSingeInputSingleInstruction() {
        
        RulesCollectionBuilder builder = new RulesCollectionBuilder();
        
        String s1 = "test";
        
        Input input = new Input(inputTerms(null, s1));
        
        Instructions instructions = instructions("instruction1");
        builder.addRule(input, instructions);
        
        RulesCollection rulesCollection = builder.build();
        TermPositionSequence sequence = new TermPositionSequence();
        sequence.nextPosition();
        sequence.addTerm(new Term(null, s1.toCharArray()));
        
        List<Action> actions = rulesCollection.getRewriteActions(sequence);
        assertThat(actions, contains( new Action(Arrays.asList(instructions), terms(s1), 0, 1)) );
        
    }
    
    @Test
    public void testSingleInputTwoInstructionsFromSameRule() {
        
        RulesCollectionBuilder builder = new RulesCollectionBuilder();
        
        String s1 = "test";
        
        Input input = new Input(inputTerms(null, s1));
        
        Instructions instructions = instructions("instruction1", "instruction2");
        builder.addRule(input, instructions);
        
        RulesCollection rulesCollection = builder.build();
        TermPositionSequence sequence = new TermPositionSequence();
        sequence.nextPosition();
        sequence.addTerm(new Term(null, s1.toCharArray()));
        
        List<Action> actions = rulesCollection.getRewriteActions(sequence);
        assertThat(actions, contains( new Action(Arrays.asList(instructions), terms(s1), 0, 1)) );
        
    }
    
    @Test
    public void testSameInputTwoInstructionsFromDiffentRules() {
        
        RulesCollectionBuilder builder = new RulesCollectionBuilder();
        
        String s1 = "test";
        
        Input input = new Input(inputTerms(null, s1));
        
        Instructions instructions1 = instructions("instruction1");
        builder.addRule(input, instructions1);
        
        Instructions instructions2 = instructions("instruction2");
        builder.addRule(input, instructions2);
        
        RulesCollection rulesCollection = builder.build();
        TermPositionSequence sequence = new TermPositionSequence();
        sequence.nextPosition();
        sequence.addTerm(new Term(null, s1.toCharArray()));
        
        List<Action> actions = rulesCollection.getRewriteActions(sequence);
        assertThat(actions, contains( new Action(Arrays.asList(instructions1, instructions2), terms(s1), 0, 1)) );
        
    }
    
    @Test
    public void testTwoInputsOneInstructionsPerInput() {
        
        RulesCollectionBuilder builder = new RulesCollectionBuilder();
        
        String s1 = "test1";
        String s2 = "test2";
        
        Input input1 = new Input(inputTerms(null, s1));
        Input input2 = new Input(inputTerms(null, s2));
        
        Instructions instructions1 = instructions("instruction1");
        builder.addRule(input1, instructions1);
        
        Instructions instructions2 = instructions("instruction2");
        builder.addRule(input2, instructions2);
        
        // Input is just s1
        RulesCollection rulesCollection = builder.build();
        TermPositionSequence sequence = new TermPositionSequence();
        sequence.nextPosition();
        sequence.addTerm(new Term(null, s1.toCharArray()));
        
        List<Action> actions = rulesCollection.getRewriteActions(sequence);
        assertThat(actions, contains( new Action(Arrays.asList(instructions1), terms(s1), 0, 1)) );
        
        // Input is just s2
        sequence = new TermPositionSequence();
        sequence.nextPosition();
        sequence.addTerm(new Term(null, s2.toCharArray()));
        
        actions = rulesCollection.getRewriteActions(sequence);
        assertThat(actions, contains( new Action(Arrays.asList(instructions2), terms(s2), 0, 1)) );

        // Input is s2 s1
        sequence.nextPosition();
        sequence.addTerm(new Term(null, s1.toCharArray()));
        
        actions = rulesCollection.getRewriteActions(sequence);
        assertThat(actions, contains( 
                new Action(Arrays.asList(instructions2), terms(s2), 0, 1),
                new Action(Arrays.asList(instructions1), terms(s1), 1, 2)) 
                
                );
        
        
    }
    
    @Test
    public void testCompoundAndInterlacedInput() throws Exception {
        RulesCollectionBuilder builder = new RulesCollectionBuilder();
        
        String s1 = "test1";
        String s2 = "test2";
        String s3 = "test3";
        
        Input input1 = new Input(inputTerms(null, s1, s2));
        Input input2 = new Input(inputTerms(null, s2, s3));
        
        Instructions instructions1 = instructions("instruction1");
        builder.addRule(input1, instructions1);
        
        Instructions instructions2 = instructions("instruction2");
        builder.addRule(input2, instructions2);
        
        // Input is just s1
        RulesCollection rulesCollection = builder.build();
        TermPositionSequence sequence = new TermPositionSequence();
        sequence.nextPosition();
        sequence.addTerm(new Term(null, s1.toCharArray()));
        
        List<Action> actions = rulesCollection.getRewriteActions(sequence);
        assertTrue(actions.isEmpty());
        
        // Input is s1 s2
        sequence.nextPosition();
        sequence.addTerm(new Term(null, s2.toCharArray()));
        
        actions = rulesCollection.getRewriteActions(sequence);
        assertThat(actions, contains( 
                new Action(Arrays.asList(instructions1), terms(s1, s2), 0, 2)) 
                
                );
        
        // Input is s1 s2 s3
        sequence.nextPosition();
        sequence.addTerm(new Term(null, s3.toCharArray()));
        actions = rulesCollection.getRewriteActions(sequence);
        assertThat(actions, contains( 
                new Action(Arrays.asList(instructions1), terms(s1, s2), 0, 2),
                new Action(Arrays.asList(instructions2), terms(s2, s3), 1, 3)
                ) 
                
                );
        
        
    }
    
    @Test
    public void testTwoMatchingInputsOnePartial() throws Exception {
        RulesCollectionBuilder builder = new RulesCollectionBuilder();
        
        String s1 = "test1";
        String s2 = "test2";
        
        Input input1 = new Input(inputTerms(null, s1, s2));
        Input input2 = new Input(inputTerms(null, s2));
        
        Instructions instructions1 = instructions("instruction1");
        Instructions instructions2 = instructions("instruction2");
        
        builder.addRule(input2, instructions2);
        builder.addRule(input1, instructions1);
        
        // Input is s1 s2
        RulesCollection rulesCollection = builder.build();
        TermPositionSequence sequence = new TermPositionSequence();
        sequence.nextPosition();
        sequence.addTerm(new Term(null, s1.toCharArray()));
        
        sequence.nextPosition();
        sequence.addTerm(new Term(null, s2.toCharArray()));
        
        List<Action> actions = rulesCollection.getRewriteActions(sequence);
        assertThat(actions, contains( 
                new Action(Arrays.asList(instructions1), terms(s1, s2), 0, 2),
                new Action(Arrays.asList(instructions2), terms(s2), 1, 2)
                
                ) 
                
                );
        
       
        
    }
    
    @Test
    public void testMultipleTermsPerPosition() throws Exception {
        RulesCollectionBuilder builder = new RulesCollectionBuilder();
        
        String s1 = "test1";
        String s2 = "test2";
        
        Input input1 = new Input(inputTerms(null, s1));
        Input input2 = new Input(inputTerms(null, s2));
        
        Instructions instructions1 = instructions("instruction1");
        builder.addRule(input1, instructions1);
        
        Instructions instructions2 = instructions("instruction2");
        builder.addRule(input2, instructions2);
        
        // Input is just s1
        RulesCollection rulesCollection = builder.build();
        TermPositionSequence sequence = new TermPositionSequence();
        sequence.nextPosition();
        sequence.addTerm(new Term(null, s1.toCharArray()));
        
        List<Action> actions = rulesCollection.getRewriteActions(sequence);
        assertThat(actions, contains( new Action(Arrays.asList(instructions1), terms(s1), 0, 1)) );
        
        sequence.addTerm(new Term(null, s2.toCharArray()));
        
        actions = rulesCollection.getRewriteActions(sequence);
        assertThat(actions, contains( new Action(Arrays.asList(instructions1), terms(s1), 0, 1),
                new Action(Arrays.asList(instructions2), terms(s2), 0, 1))
                );
        
    }
    @Test
    public void testMultipleTermsWithFieldNamesPerPosition() throws Exception {
        RulesCollectionBuilder builder = new RulesCollectionBuilder();
        
        String s1 = "test1";
        String s2 = "test2";
        
        Input input1 = new Input(
                Arrays.asList(
                        new querqy.rewrite.commonrules.model.Term(s1.toCharArray(), 0, s1.length(), Arrays.asList("f11", "f12")),
                        new querqy.rewrite.commonrules.model.Term(s2.toCharArray(), 0, s2.length(), Arrays.asList("f21", "f22"))
                        
                        ));
                
        
        Instructions instructions1 = instructions("instruction1");
        builder.addRule(input1, instructions1);
        
        Term term11 = new Term(null, "f11", s1.toCharArray());
        Term term12 = new Term(null, "f12", s1.toCharArray());
        Term term21 = new Term(null, "f21", s2.toCharArray());
        Term term22 = new Term(null, "f22", s2.toCharArray());
        
        
        // Input is just s1
        RulesCollection rulesCollection = builder.build();
        TermPositionSequence sequence = new TermPositionSequence();
        sequence.nextPosition();
        sequence.addTerm(term11);
        sequence.addTerm(term21);
        
        List<Action> actions = rulesCollection.getRewriteActions(sequence);
        assertTrue(actions.isEmpty());
        
        sequence.nextPosition();
        sequence.addTerm(term12);
        actions = rulesCollection.getRewriteActions(sequence);
        assertTrue(actions.isEmpty());
        
        sequence.addTerm(term22);
        actions = rulesCollection.getRewriteActions(sequence);
        
        assertThat(actions, contains( new Action(Arrays.asList(instructions1), 
                                        Arrays.asList(term11, term22), 0, 2)));
        sequence.clear();
        
        actions = rulesCollection.getRewriteActions(sequence);
        assertTrue(actions.isEmpty());
        sequence.nextPosition();
        sequence.addTerm(term12);
        sequence.nextPosition();
        sequence.addTerm(term21);
        actions = rulesCollection.getRewriteActions(sequence);
        
        assertThat(actions, contains( new Action(Arrays.asList(instructions1), Arrays.asList(term12, term21), 0, 2)) );
        
        
        
        
    }
    
    
    List<querqy.rewrite.commonrules.model.Term> inputTerms(List<String> fieldNames, String...values) {
        List<querqy.rewrite.commonrules.model.Term> result = new LinkedList<>();
        for (String value: values) {
            char[] chars = value.toCharArray();
            result.add(new querqy.rewrite.commonrules.model.Term(chars, 0, chars.length, fieldNames));
        }
        return result;
    }

    
    List<Term> terms(String...values) {
        List<Term> result = new LinkedList<>();
        for (String value: values) {
            char[] chars = value.toCharArray();
            result.add(new Term(null, chars));
        }
        return result;
    }
    
    List<Term> termsWithFieldname(String fieldName, String...values) {
        List<Term> result = new LinkedList<>();
        for (String value: values) {
            char[] chars = value.toCharArray();
            result.add(new Term(null, fieldName, chars));
        }
        return result;
    }
    
    Instructions instructions(String...names) {
        List<Instruction> instructions = new LinkedList<>();
        for (String name: names) {
            instructions.add(new SimpleInstruction(name));
        }
        return new Instructions(instructions);
    }

    static class SimpleInstruction implements Instruction {
        
        final String name;
        
        public SimpleInstruction(String name) {
            this.name = name;
        }
        
        
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            return result;
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            SimpleInstruction other = (SimpleInstruction) obj;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            return true;
        }


        @Override
        public String toString() {
            return "SimpleInstruction [name=" + name + "]";
        }


        @Override
        public void apply(TermPositionSequence sequence,
                List<Term> matchedTerms, int startPosition, int endPosition) {
        }
        
       
        
    }
}
