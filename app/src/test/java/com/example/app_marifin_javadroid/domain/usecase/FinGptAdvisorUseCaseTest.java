package com.example.app_marifin_javadroid.domain.usecase;

import com.example.app_marifin_javadroid.domain.model.ChatMessage;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit Tests for FinGptAdvisorUseCase.
 */
public class FinGptAdvisorUseCaseTest {

    private FinGptAdvisorUseCase advisor;

    @Before
    public void setUp() {
        advisor = new FinGptAdvisorUseCase();
    }

    @Test
    public void testProcessTransactionPromptCreatesDraft() {
        ChatMessage message = advisor.processUserPrompt("Beli kopi 25rb di Starbucks");

        assertNotNull(message);
        assertEquals(ChatMessage.Sender.FINGPT, message.getSender());
        assertTrue(message.hasDraftTransaction());
        assertNotNull(message.getDraftTransaction());
        assertEquals("Makanan & Minuman", message.getDraftTransaction().getPredictedCategoryName());
    }

    @Test
    public void testProcessCashflowAdvicePrompt() {
        ChatMessage message = advisor.processUserPrompt("Bagaimana kondisi arus kas saya?");

        assertNotNull(message);
        assertEquals(ChatMessage.Sender.FINGPT, message.getSender());
        assertTrue(message.getContent().contains("Arus Kas"));
    }

    @Test
    public void testProcessBudgetTipsPrompt() {
        ChatMessage message = advisor.processUserPrompt("Beri saya tips hemat uang");

        assertNotNull(message);
        assertEquals(ChatMessage.Sender.FINGPT, message.getSender());
        assertTrue(message.getContent().contains("Tips Cerdas"));
    }
}
