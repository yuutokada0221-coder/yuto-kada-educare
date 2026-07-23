-- 夜のジャーナル：今日の気分の横に選べる、任意の天気記録（SUNNY / CLOUDY / RAINY）
ALTER TABLE public.daily_journal ADD COLUMN weather character varying(255);
