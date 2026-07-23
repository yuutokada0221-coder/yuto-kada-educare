-- 達成率の分母を日をまたいでも保持するためのスナップショット（未設定の過去分はNULLのままでよい）
ALTER TABLE public.daily_task ADD COLUMN input_count integer;
