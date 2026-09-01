-- ============================================================================
-- MARIFIN SUPABASE POSTGRESQL SCHEMA & ROW LEVEL SECURITY (RLS)
-- Product: MariFin ("Kelola Keuangan, Lebih Cerdas")
-- Product Owner: Mari Partner
-- Target: PostgreSQL 15+ on Supabase
-- ============================================================================

-- Enable required extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================================================
-- 1. PROFILES TABLE (Linked with Supabase Auth users)
-- ============================================================================
CREATE TABLE IF NOT EXISTS public.profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    full_name VARCHAR(255) NOT NULL,
    avatar_url TEXT,
    phone VARCHAR(50),
    currency VARCHAR(10) NOT NULL DEFAULT 'IDR',
    timezone VARCHAR(50) NOT NULL DEFAULT 'Asia/Jakarta',
    onboarding_completed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================================
-- 2. FINANCIAL ACCOUNTS TABLE (Bank, E-Wallet, Cash, etc.)
-- ============================================================================
CREATE TABLE IF NOT EXISTS public.financial_accounts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    name VARCHAR(150) NOT NULL,
    institution_name VARCHAR(150) NOT NULL,
    account_type VARCHAR(50) NOT NULL, -- 'Bank', 'E-Wallet', 'Cash', 'Credit Card', 'Investment', 'Other'
    account_number_masked VARCHAR(100),
    currency VARCHAR(10) NOT NULL DEFAULT 'IDR',
    initial_balance NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    current_balance NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    icon_url TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================================
-- 3. CATEGORIES TABLE (System default & Custom user categories)
-- ============================================================================
CREATE TABLE IF NOT EXISTS public.categories (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES auth.users(id) ON DELETE CASCADE, -- NULL for system default
    parent_id UUID REFERENCES public.categories(id) ON DELETE SET NULL,
    name VARCHAR(150) NOT NULL,
    icon VARCHAR(100) NOT NULL DEFAULT 'ic_category_default',
    color VARCHAR(20) NOT NULL DEFAULT '#1E56A0',
    type VARCHAR(30) NOT NULL, -- 'expense', 'income', 'both'
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    is_favorite BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================================
-- 4. TRANSACTIONS TABLE (Income, Expense, Transfer, Bill)
-- ============================================================================
CREATE TABLE IF NOT EXISTS public.transactions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    account_id UUID NOT NULL REFERENCES public.financial_accounts(id) ON DELETE RESTRICT,
    category_id UUID REFERENCES public.categories(id) ON DELETE SET NULL,
    type VARCHAR(30) NOT NULL, -- 'expense', 'income', 'transfer_in', 'transfer_out', 'bill'
    amount NUMERIC(15,2) NOT NULL CHECK (amount > 0),
    currency VARCHAR(10) NOT NULL DEFAULT 'IDR',
    merchant VARCHAR(255),
    description TEXT,
    transaction_date TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    source VARCHAR(50) NOT NULL DEFAULT 'manual', -- 'manual', 'ai', 'recurring', 'import'
    attachment_count INT NOT NULL DEFAULT 0,
    transfer_group_id UUID, -- Links paired transfer_in and transfer_out
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ
);

-- ============================================================================
-- 5. TRANSACTION TRANSFERS AUDIT (Explicit Pair Relationship)
-- ============================================================================
CREATE TABLE IF NOT EXISTS public.transaction_transfers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    transfer_group_id UUID NOT NULL UNIQUE,
    source_account_id UUID NOT NULL REFERENCES public.financial_accounts(id) ON DELETE RESTRICT,
    destination_account_id UUID NOT NULL REFERENCES public.financial_accounts(id) ON DELETE RESTRICT,
    amount NUMERIC(15,2) NOT NULL CHECK (amount > 0),
    fee NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    transfer_date TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================================
-- 6. BUDGETS TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS public.budgets (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    name VARCHAR(150) NOT NULL,
    amount NUMERIC(15,2) NOT NULL CHECK (amount > 0),
    period_type VARCHAR(30) NOT NULL DEFAULT 'monthly', -- 'weekly', 'monthly', 'yearly', 'custom'
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    alert_threshold INT NOT NULL DEFAULT 80, -- percentage (50, 75, 80, 90, 100)
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================================
-- 7. BUDGET CATEGORIES (Many-to-Many Budget to Category)
-- ============================================================================
CREATE TABLE IF NOT EXISTS public.budget_categories (
    budget_id UUID NOT NULL REFERENCES public.budgets(id) ON DELETE CASCADE,
    category_id UUID NOT NULL REFERENCES public.categories(id) ON DELETE CASCADE,
    allocated_amount NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    PRIMARY KEY (budget_id, category_id)
);

-- ============================================================================
-- 8. BILLS TABLE (Tagihan Rutin)
-- ============================================================================
CREATE TABLE IF NOT EXISTS public.bills (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    name VARCHAR(150) NOT NULL,
    amount NUMERIC(15,2) NOT NULL CHECK (amount > 0),
    category_id UUID REFERENCES public.categories(id) ON DELETE SET NULL,
    account_id UUID REFERENCES public.financial_accounts(id) ON DELETE SET NULL,
    due_date DATE NOT NULL,
    recurrence VARCHAR(30) NOT NULL DEFAULT 'monthly', -- 'once', 'weekly', 'monthly', 'yearly'
    status VARCHAR(30) NOT NULL DEFAULT 'upcoming', -- 'upcoming', 'due', 'paid', 'overdue'
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================================
-- 9. FINANCIAL GOALS TABLE (Target Finansial)
-- ============================================================================
CREATE TABLE IF NOT EXISTS public.financial_goals (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    name VARCHAR(150) NOT NULL,
    target_amount NUMERIC(15,2) NOT NULL CHECK (target_amount > 0),
    current_amount NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    deadline DATE,
    account_id UUID REFERENCES public.financial_accounts(id) ON DELETE SET NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'in_progress', -- 'in_progress', 'achieved', 'cancelled'
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================================
-- 10. GOAL CONTRIBUTIONS
-- ============================================================================
CREATE TABLE IF NOT EXISTS public.goal_contributions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    goal_id UUID NOT NULL REFERENCES public.financial_goals(id) ON DELETE CASCADE,
    amount NUMERIC(15,2) NOT NULL CHECK (amount > 0),
    transaction_id UUID REFERENCES public.transactions(id) ON DELETE SET NULL,
    contribution_date TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================================
-- 11. DOCUMENTS TABLE (Struk, Invoice, Bukti Transaksi)
-- ============================================================================
CREATE TABLE IF NOT EXISTS public.documents (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    transaction_id UUID REFERENCES public.transactions(id) ON DELETE SET NULL,
    storage_path TEXT NOT NULL,
    original_name VARCHAR(255) NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    file_size BIGINT NOT NULL,
    document_type VARCHAR(50) NOT NULL DEFAULT 'receipt', -- 'receipt', 'invoice', 'bank_statement', 'other'
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================================
-- 12. NOTIFICATIONS TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS public.notifications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50) NOT NULL, -- 'budget_alert', 'bill_due', 'goal_milestone', 'ai_insight'
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    payload JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================================
-- 13. AI CONVERSATIONS & MESSAGES (FinGPT)
-- ============================================================================
CREATE TABLE IF NOT EXISTS public.ai_conversations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL DEFAULT 'FinGPT Chat',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.ai_messages (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    conversation_id UUID NOT NULL REFERENCES public.ai_conversations(id) ON DELETE CASCADE,
    sender VARCHAR(20) NOT NULL, -- 'user', 'assistant', 'system'
    content TEXT NOT NULL,
    structured_data JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================================
-- 14. AI TRANSACTION DRAFTS (Draft sebelum dikonfirmasi oleh user)
-- ============================================================================
CREATE TABLE IF NOT EXISTS public.ai_transaction_drafts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    raw_prompt TEXT NOT NULL,
    suggested_type VARCHAR(30) NOT NULL,
    suggested_amount NUMERIC(15,2) NOT NULL,
    suggested_category_id UUID REFERENCES public.categories(id) ON DELETE SET NULL,
    suggested_merchant VARCHAR(255),
    suggested_date TIMESTAMPTZ,
    status VARCHAR(30) NOT NULL DEFAULT 'pending', -- 'pending', 'confirmed', 'rejected'
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================================
-- 15. USER PREFERENCES
-- ============================================================================
CREATE TABLE IF NOT EXISTS public.user_preferences (
    user_id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    theme VARCHAR(20) NOT NULL DEFAULT 'system',
    biometric_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    budget_notifications BOOLEAN NOT NULL DEFAULT TRUE,
    bill_notifications BOOLEAN NOT NULL DEFAULT TRUE,
    insight_notifications BOOLEAN NOT NULL DEFAULT TRUE,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================================
-- 16. AUDIT LOGS
-- ============================================================================
CREATE TABLE IF NOT EXISTS public.audit_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES auth.users(id) ON DELETE SET NULL,
    action VARCHAR(100) NOT NULL,
    entity_name VARCHAR(100) NOT NULL,
    entity_id UUID,
    details JSONB,
    ip_address VARCHAR(50),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================================
-- INDEXES FOR PERFORMANCE
-- ============================================================================
CREATE INDEX IF NOT EXISTS idx_transactions_user_date ON public.transactions(user_id, transaction_date DESC);
CREATE INDEX IF NOT EXISTS idx_transactions_user_type ON public.transactions(user_id, type);
CREATE INDEX IF NOT EXISTS idx_transactions_category ON public.transactions(user_id, category_id);
CREATE INDEX IF NOT EXISTS idx_transactions_account ON public.transactions(user_id, account_id);
CREATE INDEX IF NOT EXISTS idx_transactions_transfer_group ON public.transactions(transfer_group_id);
CREATE INDEX IF NOT EXISTS idx_categories_user_default ON public.categories(user_id, is_default);
CREATE INDEX IF NOT EXISTS idx_financial_accounts_user ON public.financial_accounts(user_id);
CREATE INDEX IF NOT EXISTS idx_bills_user_due ON public.bills(user_id, due_date);
CREATE INDEX IF NOT EXISTS idx_budgets_user_date ON public.budgets(user_id, start_date, end_date);
CREATE INDEX IF NOT EXISTS idx_documents_user ON public.documents(user_id);

-- ============================================================================
-- ROW LEVEL SECURITY (RLS) POLICIES
-- ============================================================================
ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.financial_accounts ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.categories ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.transactions ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.transaction_transfers ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.budgets ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.budget_categories ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.bills ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.financial_goals ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.goal_contributions ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.documents ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.notifications ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.ai_conversations ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.ai_messages ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.ai_transaction_drafts ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.user_preferences ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.audit_logs ENABLE ROW LEVEL SECURITY;

-- Standard User Isolation Policies (auth.uid() = user_id)
CREATE POLICY "Users can manage own profile" ON public.profiles FOR ALL USING (auth.uid() = id);
CREATE POLICY "Users can manage own accounts" ON public.financial_accounts FOR ALL USING (auth.uid() = user_id);
CREATE POLICY "Users can manage own transactions" ON public.transactions FOR ALL USING (auth.uid() = user_id);
CREATE POLICY "Users can manage own transfers" ON public.transaction_transfers FOR ALL USING (auth.uid() = user_id);
CREATE POLICY "Users can manage own budgets" ON public.budgets FOR ALL USING (auth.uid() = user_id);
CREATE POLICY "Users can manage own budget categories" ON public.budget_categories FOR ALL 
    USING (EXISTS (SELECT 1 FROM public.budgets WHERE budgets.id = budget_categories.budget_id AND budgets.user_id = auth.uid()));
CREATE POLICY "Users can manage own bills" ON public.bills FOR ALL USING (auth.uid() = user_id);
CREATE POLICY "Users can manage own goals" ON public.financial_goals FOR ALL USING (auth.uid() = user_id);
CREATE POLICY "Users can manage own goal contributions" ON public.goal_contributions FOR ALL 
    USING (EXISTS (SELECT 1 FROM public.financial_goals WHERE financial_goals.id = goal_contributions.goal_id AND financial_goals.user_id = auth.uid()));
CREATE POLICY "Users can manage own documents" ON public.documents FOR ALL USING (auth.uid() = user_id);
CREATE POLICY "Users can manage own notifications" ON public.notifications FOR ALL USING (auth.uid() = user_id);
CREATE POLICY "Users can manage own AI conversations" ON public.ai_conversations FOR ALL USING (auth.uid() = user_id);
CREATE POLICY "Users can manage own AI messages" ON public.ai_messages FOR ALL 
    USING (EXISTS (SELECT 1 FROM public.ai_conversations WHERE ai_conversations.id = ai_messages.conversation_id AND ai_conversations.user_id = auth.uid()));
CREATE POLICY "Users can manage own AI drafts" ON public.ai_transaction_drafts FOR ALL USING (auth.uid() = user_id);
CREATE POLICY "Users can manage own preferences" ON public.user_preferences FOR ALL USING (auth.uid() = user_id);
CREATE POLICY "Users can view own audit logs" ON public.audit_logs FOR SELECT USING (auth.uid() = user_id);

-- Category Specific RLS: Public Default (is_default = true) or User Owned
CREATE POLICY "Users can read default and own categories" ON public.categories FOR SELECT 
    USING (is_default = TRUE OR auth.uid() = user_id);
CREATE POLICY "Users can insert own categories" ON public.categories FOR INSERT 
    WITH CHECK (auth.uid() = user_id AND is_default = FALSE);
CREATE POLICY "Users can update own categories" ON public.categories FOR UPDATE 
    USING (auth.uid() = user_id AND is_default = FALSE);
CREATE POLICY "Users can delete own categories" ON public.categories FOR DELETE 
    USING (auth.uid() = user_id AND is_default = FALSE);

-- ============================================================================
-- STORED PROCEDURE (RPC): EXECUTE TRANSFER ATOMICALLY
-- ============================================================================
CREATE OR REPLACE FUNCTION public.execute_transfer(
    p_source_account_id UUID,
    p_dest_account_id UUID,
    p_amount NUMERIC(15,2),
    p_fee NUMERIC(15,2) DEFAULT 0.00,
    p_transaction_date TIMESTAMPTZ DEFAULT NOW(),
    p_description TEXT DEFAULT 'Transfer Saldo'
) RETURNS UUID AS $$
DECLARE
    v_user_id UUID := auth.uid();
    v_transfer_group_id UUID := uuid_generate_v4();
    v_source_balance NUMERIC(15,2);
BEGIN
    -- Check user authorization
    IF v_user_id IS NULL THEN
        RAISE EXCEPTION 'Unauthorized transfer attempt';
    END IF;

    -- Lock and verify source account
    SELECT current_balance INTO v_source_balance
    FROM public.financial_accounts
    WHERE id = p_source_account_id AND user_id = v_user_id
    FOR UPDATE;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Source account not found';
    END IF;

    -- Update balances
    UPDATE public.financial_accounts
    SET current_balance = current_balance - (p_amount + p_fee),
        updated_at = NOW()
    WHERE id = p_source_account_id AND user_id = v_user_id;

    UPDATE public.financial_accounts
    SET current_balance = current_balance + p_amount,
        updated_at = NOW()
    WHERE id = p_dest_account_id AND user_id = v_user_id;

    -- Insert transfer pair transactions
    INSERT INTO public.transactions (
        user_id, account_id, type, amount, description, transaction_date, transfer_group_id
    ) VALUES (
        v_user_id, p_source_account_id, 'transfer_out', p_amount + p_fee, p_description, p_transaction_date, v_transfer_group_id
    );

    INSERT INTO public.transactions (
        user_id, account_id, type, amount, description, transaction_date, transfer_group_id
    ) VALUES (
        v_user_id, p_dest_account_id, 'transfer_in', p_amount, p_description, p_transaction_date, v_transfer_group_id
    );

    -- Insert into transfer audit log
    INSERT INTO public.transaction_transfers (
        user_id, transfer_group_id, source_account_id, destination_account_id, amount, fee, transfer_date, notes
    ) VALUES (
        v_user_id, v_transfer_group_id, p_source_account_id, p_dest_account_id, p_amount, p_fee, p_transaction_date, p_description
    );

    RETURN v_transfer_group_id;
END;
$$ LANGUAGE plpgsql SECURITY INVOKER SET search_path = public, pg_temp;

-- Secure function execution permissions (Invoker context)
REVOKE EXECUTE ON FUNCTION public.execute_transfer(UUID, UUID, NUMERIC, NUMERIC, TIMESTAMPTZ, TEXT) FROM PUBLIC;
REVOKE EXECUTE ON FUNCTION public.execute_transfer(UUID, UUID, NUMERIC, NUMERIC, TIMESTAMPTZ, TEXT) FROM anon;
GRANT EXECUTE ON FUNCTION public.execute_transfer(UUID, UUID, NUMERIC, NUMERIC, TIMESTAMPTZ, TEXT) TO authenticated;

-- ============================================================================
-- TRIGGER: AUTO-CREATE PROFILE & PREFERENCES ON SIGNUP
-- ============================================================================
CREATE OR REPLACE FUNCTION public.handle_new_user() 
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO public.profiles (id, full_name, avatar_url)
    VALUES (
        NEW.id,
        COALESCE(NEW.raw_user_meta_data->>'full_name', 'Pengguna MariFin'),
        NEW.raw_user_meta_data->>'avatar_url'
    )
    ON CONFLICT (id) DO NOTHING;

    INSERT INTO public.user_preferences (user_id)
    VALUES (NEW.id)
    ON CONFLICT (user_id) DO NOTHING;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER SET search_path = public, pg_temp;

-- Revoke public & external REST API execution on trigger function
REVOKE EXECUTE ON FUNCTION public.handle_new_user() FROM PUBLIC;
REVOKE EXECUTE ON FUNCTION public.handle_new_user() FROM anon;
REVOKE EXECUTE ON FUNCTION public.handle_new_user() FROM authenticated;

DROP TRIGGER IF EXISTS on_auth_user_created ON auth.users;
CREATE TRIGGER on_auth_user_created
    AFTER INSERT ON auth.users
    FOR EACH ROW EXECUTE FUNCTION public.handle_new_user();

-- ============================================================================
-- DEFAULT CATEGORIES SEED DATA
-- ============================================================================
INSERT INTO public.categories (name, icon, color, type, is_default, is_favorite) VALUES
('Makanan & Minuman', 'ic_category_food', '#EF4444', 'expense', TRUE, TRUE),
('Transportasi', 'ic_category_transport', '#3AB4F2', 'expense', TRUE, TRUE),
('Kebutuhan Rumah', 'ic_category_home', '#10B981', 'expense', TRUE, FALSE),
('Tagihan & Utilitas', 'ic_category_bills', '#F59E0B', 'expense', TRUE, TRUE),
('Shopping', 'ic_category_shopping', '#EC4899', 'expense', TRUE, FALSE),
('Kesehatan & Olahraga', 'ic_category_health', '#06B6D4', 'expense', TRUE, FALSE),
('Hiburan', 'ic_category_entertainment', '#8B5CF6', 'expense', TRUE, FALSE),
('Edukasi', 'ic_category_education', '#3B82F6', 'expense', TRUE, FALSE),
('Investasi', 'ic_category_investment', '#10B981', 'expense', TRUE, FALSE),
('Donasi & Hadiah', 'ic_category_gift', '#F43F5E', 'expense', TRUE, FALSE),
('Gaji & Upah', 'ic_category_salary', '#10B981', 'income', TRUE, TRUE),
('Bonus & THR', 'ic_category_bonus', '#34D399', 'income', TRUE, FALSE),
('Pendapatan Bisnis', 'ic_category_business', '#6366F1', 'income', TRUE, FALSE),
('Dividen & Bunga', 'ic_category_dividend', '#14B8A6', 'income', TRUE, FALSE),
('Lainnya', 'ic_category_other', '#64748B', 'both', TRUE, FALSE)
ON CONFLICT DO NOTHING;
