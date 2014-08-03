package querqy.antlr.rewrite.commonrules;

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;

import org.junit.Test;

import querqy.model.BooleanQuery;
import querqy.model.DisjunctionMaxQuery;
import querqy.model.Query;
import querqy.rewrite.commonrules.CommonRulesRewriter;
import querqy.rewrite.commonrules.model.DeleteInstruction;
import querqy.rewrite.commonrules.model.Input;
import querqy.rewrite.commonrules.model.Instruction;
import querqy.rewrite.commonrules.model.Instructions;
import querqy.rewrite.commonrules.model.RulesCollection;
import querqy.rewrite.commonrules.model.RulesCollectionBuilder;

public class DeleteInstructionTest extends AbstractCommonRulesTest {

    
    @Test
    public void testThatNothingIsDeletedIfWeWouldEndUpWithAnEmptyQuery() {
        
        RulesCollectionBuilder builder = new RulesCollectionBuilder();
        DeleteInstruction delete = new DeleteInstruction(Arrays.asList(mkTerm("a")));
        builder.addRule(new Input(Arrays.asList(mkTerm("a"))), new Instructions(Arrays.asList((Instruction) delete)));
        RulesCollection rules = builder.build();
        CommonRulesRewriter rewriter = new CommonRulesRewriter(rules);
        
        Query query = makeQuery("a");
        Query rewritten = rewriter.rewrite(query);
        
        assertThat(rewritten, 
                bq(
                        bq(
                                dmq(
                                        term("a")
                                )
                )));
        
        
        
    }
    
    @Test
    public void testThatTermIsRemovedIfThereIsAnotherTermInTheSameDMQ() throws Exception {
        RulesCollectionBuilder builder = new RulesCollectionBuilder();
        DeleteInstruction delete = new DeleteInstruction(Arrays.asList(mkTerm("a")));
        builder.addRule(new Input(Arrays.asList(mkTerm("a"))), new Instructions(Arrays.asList((Instruction) delete)));
        RulesCollection rules = builder.build();
        CommonRulesRewriter rewriter = new CommonRulesRewriter(rules);
        
        Query query = makeQuery("a");
        DisjunctionMaxQuery dmq = query.getClauses(BooleanQuery.class).get(0)
            .getClauses(DisjunctionMaxQuery.class).get(0);
        querqy.model.Term termB = new querqy.model.Term(dmq, null, "b".toCharArray());
        
        dmq.addClause(termB);
        
        Query rewritten = rewriter.rewrite(query);
        
        assertThat(rewritten, 
                bq(
                        bq(
                                dmq(
                                        term("b")
                                )
                )));
    }
    
    
    @Test
    public void testThatTermIsRemovedOnceIfItExistsTwiceInSameDMQAndNoOtherTermExistsInQuery() throws Exception {
        RulesCollectionBuilder builder = new RulesCollectionBuilder();
        DeleteInstruction delete = new DeleteInstruction(Arrays.asList(mkTerm("a")));
        builder.addRule(new Input(Arrays.asList(mkTerm("a"))), new Instructions(Arrays.asList((Instruction) delete)));
        RulesCollection rules = builder.build();
        CommonRulesRewriter rewriter = new CommonRulesRewriter(rules);
        
        Query query = makeQuery("a");
        DisjunctionMaxQuery dmq = query.getClauses(BooleanQuery.class).get(0)
            .getClauses(DisjunctionMaxQuery.class).get(0);
        querqy.model.Term termB = new querqy.model.Term(dmq, null, "a".toCharArray());
        
        dmq.addClause(termB);
        
        Query rewritten = rewriter.rewrite(query);
        
        assertThat(rewritten, 
                bq(
                        bq(
                                dmq(
                                        term("a")
                                )
                )));
    }
    
    @Test
    public void testThatTermIsRemovedIfThereASecondDMQWithoutTheTerm() throws Exception {
        RulesCollectionBuilder builder = new RulesCollectionBuilder();
        DeleteInstruction delete = new DeleteInstruction(Arrays.asList(mkTerm("a")));
        builder.addRule(new Input(Arrays.asList(mkTerm("a"))), new Instructions(Arrays.asList((Instruction) delete)));
        RulesCollection rules = builder.build();
        CommonRulesRewriter rewriter = new CommonRulesRewriter(rules);
        
        Query query = makeQuery("a b");
        
        Query rewritten = rewriter.rewrite(query);
        
        assertThat(rewritten, 
                bq(
                        bq(
                                dmq(
                                        term("b")
                                )
                )));
    }
    
    @Test
    public void testThatTermIsNotRemovedOnceIfThereASecondDMQWithTheSameTermAndNoOtherTermExists() throws Exception {
        RulesCollectionBuilder builder = new RulesCollectionBuilder();
        DeleteInstruction delete = new DeleteInstruction(Arrays.asList(mkTerm("a")));
        builder.addRule(new Input(Arrays.asList(mkTerm("a"))), new Instructions(Arrays.asList((Instruction) delete)));
        RulesCollection rules = builder.build();
        CommonRulesRewriter rewriter = new CommonRulesRewriter(rules);
        
        Query query = makeQuery("a a");
        
        Query rewritten = rewriter.rewrite(query);
        
        assertThat(rewritten, 
                bq(
                        bq(
                                dmq(
                                        term("a")
                                )
                )));
    }

}
